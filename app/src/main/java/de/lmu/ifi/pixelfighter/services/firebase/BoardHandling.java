package de.lmu.ifi.pixelfighter.services.firebase;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.pixelfighter.activities.ZoomableGameActivity;
import de.lmu.ifi.pixelfighter.activities.game.GameSettings;
import de.lmu.ifi.pixelfighter.game.Rules;
import de.lmu.ifi.pixelfighter.models.GamePlayer;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.models.PixelModification;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;

/**
 * Created by michael on 19.01.18.
 */

public class BoardHandling {

    private final GameSettings gameSettings;
    private ZoomableGameActivity zoomActivity;

    public BoardHandling(ZoomableGameActivity zoomActivity, GameSettings gameSettings) {
        this.zoomActivity = zoomActivity;
        this.gameSettings = gameSettings;
    }

    private boolean placeModification(Pixel mutable, PixelModification modification) {
        if (modification == PixelModification.None) return false;
        GamePlayer player = gameSettings.getGamePlayer();
        if (player.getModificationAmount().get(modification.name()) < 1) return false;
        player.getModificationAmount().put(modification.name(), player.getModificationAmount().get(modification.name()) - 1);
        Database.Game(gameSettings.getGameKey()).GamePlayer(gameSettings.getUid(), gameSettings.getTeam()).setValue(player);
        mutable.setPixelMod(modification);
        return true;
    }

    private boolean pickModification(Pixel mutable) {
        if (mutable.getPixelMod() == PixelModification.None) return false;
        GamePlayer player = gameSettings.getGamePlayer();
        player.getModificationAmount().put(mutable.getPixelMod().name(), player.getModificationAmount().get(mutable.getPixelMod().name()) + 1);
        Database.Game(gameSettings.getGameKey()).GamePlayer(gameSettings.getUid(), gameSettings.getTeam()).setValue(player);
        mutable.setPixelMod(PixelModification.None);
        return true;
    }

    public void placePixel(final int x, final int y, final PixelModification modification, final ServiceCallback<Pixel> callback) {
        Database.Game(gameSettings.getGameKey()).Pixel(x, y).runTransaction(new GenericReference.Handler<Pixel>() {

            @Override
            public Pixel doTransaction(Pixel mutable) {
                Rules rules = new Rules(gameSettings.getBoard(), gameSettings.getTeam(), x, y);

                // Clicked on own Team
                if (mutable.getTeam() == gameSettings.getTeam() && mutable.getPixelMod() == PixelModification.None && modification != PixelModification.None) {
                    if (placeModification(mutable, modification)) return mutable;
                    else return null;
                }

                // Check if Pixel can be set
                if (!rules.isFree()) return null;

                // Check if Pixel is at own Team
                // need current board
                if (!rules.isAtOwnTeam()) return null;

                //Rules.checkForLootModification(gameService ,board, mutable);

                pickModification(mutable);
                mutable.setPlayerKey(gameSettings.getUid());
                mutable.setTeam(gameSettings.getTeam());
                placeModification(mutable, modification);

                return mutable;
            }

            @Override
            public void onComplete(boolean changed, Pixel object) {
                if (changed) {
                    callback.success(object);

                    // run replacing
                    // Check if we can replace a neighbour
                    List<Pixel> enemiesToConvert = Rules.checkForEnemiesToConvert(gameSettings.getBoard(), gameSettings.getTeam(), x, y);
                    enemiesToConvertCount += enemiesToConvert.size();
                    for (Pixel pixel : enemiesToConvert) {
                        executeReplacing(pixel.getX(), pixel.getY(), gameSettings.getTeam(), gameSettings.getUid());
                    }
                    // after that, run bombs

                } else {
                    callback.failure("Not valid to set");
                }
            }
        });
    }

    private volatile int enemiesToConvertCount = 0;
    private volatile int enemiesConverted = 0;
    private List<BombCount> bombsToExecute = new ArrayList<>();

    private static class BombCount {
        int x;
        int y;
        Team team;
        String uid;
    }

    public void executeReplacing(final int x, final int y, final Team ownTeam, final String uid) {
        Database.Game(gameSettings.getGameKey()).Pixel(x, y).runTransaction(new GenericReference.Handler<Pixel>() {
            @Override
            public Pixel doTransaction(Pixel mutable) {

                if (mutable.getPixelMod() == PixelModification.Protection) {
//                    sendBroadcastToGameView(x, y);
                    return null;
                }
                // Check if replaced Pixel has a Bomb
                if (mutable.getPixelMod() == PixelModification.Bomb) {
                    Log.d("Replace&Bomb", "Execute Bomb at " + mutable.toString());
                    BombCount bc = new BombCount();
                    bc.x = mutable.getX();
                    bc.y = mutable.getY();
                    bc.team = mutable.getTeam();
                    bc.uid = mutable.getPlayerKey();
                    bombsToExecute.add(bc);
                    //executeBombFrom(x, y, mutable.getTeam());
                }
                mutable.setTeam(ownTeam);
                mutable.setPlayerKey(uid);
                mutable.setPixelMod(PixelModification.None);

                // Check if we can replace a neighbour
                List<Pixel> enemiesToConvert = Rules.checkForEnemiesToConvert(gameSettings.getBoard(), ownTeam, x, y);
                enemiesToConvertCount += enemiesToConvert.size();
                for (Pixel pixel : enemiesToConvert) {
                    executeReplacing(pixel.getX(), pixel.getY(), ownTeam, uid);
                }
                return mutable;
            }

            @Override
            public void onComplete(boolean changed, Pixel object) {
                // do nothing
                enemiesConverted++;
                convertingEnemiesFinished();
            }
        });
    }

    private void convertingEnemiesFinished() {
        Log.d("Replace&Bomb", enemiesConverted + " == " + enemiesToConvertCount);
        if (enemiesConverted == enemiesToConvertCount) {
            Log.d("Replace&Bomb", "all the fiels replaced. Start bombing");
            // excute bombs
            for (BombCount bc : bombsToExecute) {
                executeBombFrom(bc.x, bc.y, bc.team, bc.uid);
            }
        }
    }


    public void executeBombFrom(final int x, final int y, Team team, String uid) {
        // Get all neighbors
        overridePixel(x, y, team, uid, PixelModification.None, null);
        for (Pixel pixel : getNeighbours(x, y)) {
            overridePixel(pixel.getX(), pixel.getY(), team, uid, PixelModification.None, null);
        }
    }


    private List<Pixel> getNeighbours(int x, int y) {
        List<Pixel> result = new ArrayList<>();
        for (int _x = x - 1; _x <= x + 1; _x++) {
            for (int _y = y - 1; _y <= y + 1; _y++) {
                if (_x < 0 || _x >= this.gameSettings.getBoard().getWidth())
                    continue;
                if (_y < 0 || _y >= this.gameSettings.getBoard().getHeight())
                    continue;

                if (_x == x && _y == y)
                    continue;

                result.add(this.gameSettings.getBoard().getPixels().get(_x).get(_y));
            }
        }
        return result;
    }

    private List<Pixel> getOwnTeamNeighbours(int x, int y, Team team) {
        List<Pixel> result = new ArrayList<>();
        for (Pixel pixel : getNeighbours(x, y)) {
            if (pixel.getTeam() == team) {
                result.add(pixel);
            }
        }
        return result;
    }

    private List<Pixel> getEnemyNeighbours(int x, int y, Team team) {
        List<Pixel> result = new ArrayList<>();
        for (Pixel pixel : getNeighbours(x, y)) {
            if (pixel.getTeam() != team && pixel.getTeam() != Team.None) {
                result.add(pixel);
            }
        }
        return result;
    }


    public void overridePixel(int x, int y, final Team team, final String uid, final PixelModification modification, final ServiceCallback<Pixel> callback) {
        Database.Game(this.gameSettings.getGameKey()).Pixel(x, y).runTransaction(new GenericReference.Handler<Pixel>() {
            @Override
            public Pixel doTransaction(Pixel mutable) {
                if (mutable.getPixelMod() == PixelModification.Protection) return null;
                mutable.setTeam(team);
                mutable.setPlayerKey(uid);
                mutable.setPixelMod(modification);
                return mutable;
            }

            @Override
            public void onComplete(boolean changed, Pixel object) {
                // nothing to do
                if (callback == null) return;
                callback.success(object);
            }
        });
    }

    private void sendBroadcastToGameView(int x, int y) {
        Intent intent = new Intent();
        intent.setAction("de.lmu.ifi.pixelfighter.PROTECTION_TRIGGERED");
        intent.putExtra("x", x);
        intent.putExtra("y", y);
        LocalBroadcastManager.getInstance(zoomActivity).sendBroadcast(intent);
        Log.d("BOARDHANDLING", "sent protection broadcast to GameView");
    }


}
