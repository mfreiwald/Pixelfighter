package de.lmu.ifi.pixelfighter.services.firebase;

import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.pixelfighter.activities.game.ZoomableGameActivity;
import de.lmu.ifi.pixelfighter.game.Rules;
import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.models.PixelModification;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;

/**
 * Created by michael on 19.01.18.
 */

public class BoardHandling {

    private final String gameKey;
    private final ZoomableGameActivity.GameSettings gameSettings;

    public BoardHandling(ZoomableGameActivity.GameSettings gameSettings) {
        this.gameKey = gameSettings.getGameKey();
        this.gameSettings = gameSettings;
    }

    public void placePixel(final GameService gameService, final Board board, final int x, final int y, final String uid, final Team team, final PixelModification modification, final ServiceCallback<Pixel> callback) {
        Database.Game(gameKey).Pixel(x, y).runTransaction(new GenericReference.Handler<Pixel>() {
            @Override
            public Pixel doTransaction(Pixel mutable) {
                Rules rules = new Rules(board, team, x, y);

                // Check if Pixel can be set
                if (!rules.isFree()) return null;

                // Check if Pixel is at own Team
                // need current board
                if (!rules.isAtOwnTeam()) return null;

                Rules.checkForLootModification(gameService ,board, mutable);

                // Check if we can replace a neighbour
                for(Pixel pixel : Rules.checkForEnemiesToConvert(gameSettings.getBoard(), team, x, y)) {
                    executeReplacing(pixel.getX(), pixel.getY(), team, uid);
                }

                mutable.setPlayerKey(uid);
                mutable.setTeam(team);
                mutable.setPixelMod(modification);
                return mutable;
            }

            @Override
            public void onComplete(boolean changed, Pixel object) {
                if (changed) {
                    callback.success(object);
                } else {
                    callback.failure("Not valid to set");
                }
            }
        });
    }

    private void checkReplacingFrom(int x, int y) {
        // get a list will pixel to replacing
        // and call @executeReplacing(x, y, ownTeam, uid);
    }

    public void executeReplacing(final int x, final int y, final Team ownTeam, final String uid) {
        Database.Game(gameKey).Pixel(x, y).runTransaction(new GenericReference.Handler<Pixel>() {
            @Override
            public Pixel doTransaction(Pixel mutable) {

                // Check if replaced Pixel has a Bomb
                if(mutable.getPixelMod() == PixelModification.Bomb) {
                    executeBombFrom(x, y);
                    mutable.setTeam(Team.None);
                    mutable.setPlayerKey("");
                    mutable.setPixelMod(PixelModification.None);
                    return mutable;
                } else { // Simple replacing
                    mutable.setTeam(ownTeam);
                    mutable.setPlayerKey(uid);
                    mutable.setPixelMod(PixelModification.None);
                    // Check if we can replace a neighbour
                    for(Pixel pixel : Rules.checkForEnemiesToConvert(gameSettings.getBoard(), ownTeam, x, y)) {
                        executeReplacing(pixel.getX(), pixel.getY(), ownTeam, uid);
                    }
                    return mutable;
                }

            }

            @Override
            public void onComplete(boolean changed, Pixel object) {
                // do nothing
            }
        });
    }

    public void executeBombFrom(final int x, final int y) {
        // Get all neighboars
        for(Pixel pixel : getNeighbour(x, y)) {
            overridePixel(pixel.getX(), pixel.getY(), Team.None, "", PixelModification.None, null);
        }
    }

    private List<Pixel> getNeighbour(int x, int y) {
        List<Pixel> result = new ArrayList<>();
        for (int _x = x - 1; _x <= x + 1; _x++) {
            for (int _y = y - 1; _y <= y + 1; _y++) {
                if (_x < 0 || _x >= this.gameSettings.getBoard().getWidth())
                    continue;
                if (_y < 0 || _y >= this.gameSettings.getBoard().getHeight())
                    continue;

                if(_x == x && _y == y)
                    continue;

                result.add(this.gameSettings.getBoard().getPixels().get(_x).get(_y));
            }
        }
        return result;
    }

    private List<Pixel> getOwnTeamNeighbour(int x, int y, Team team) {
        List<Pixel> result = new ArrayList<>();
        for(Pixel pixel : getNeighbour(x ,y)) {
            if(pixel.getTeam() == team) {
                result.add(pixel);
            }
        }
        return result;
    }

    private List<Pixel> getEnemyNeighbour(int x, int y, Team team) {
        List<Pixel> result = new ArrayList<>();
        for(Pixel pixel : getNeighbour(x ,y)) {
            if(pixel.getTeam() != team && pixel.getTeam() != Team.None) {
                result.add(pixel);
            }
        }
        return result;
    }



    public void overridePixel(int x, int y, final Team team, final String uid, final PixelModification modification, final ServiceCallback<Pixel> callback) {
        Database.Game(this.gameKey).Pixel(x, y).runTransaction(new GenericReference.Handler<Pixel>() {
            @Override
            public Pixel doTransaction(Pixel mutable) {
                mutable.setTeam(team);
                mutable.setPlayerKey(uid);
                mutable.setPixelMod(modification);
                return mutable;
            }

            @Override
            public void onComplete(boolean changed, Pixel object) {
                // nothing to do
                if(callback == null) return;
                callback.success(object);
            }
        });
    }


}
