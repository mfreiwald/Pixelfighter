package de.lmu.ifi.pixelfighter.services.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import de.lmu.ifi.pixelfighter.game.Rules;
import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.models.PixelModification;
import de.lmu.ifi.pixelfighter.models.Player;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.UpdateCallback;

/**
 * Created by michael on 28.11.17.
 */

public class BoardService extends BaseService<Board> {

    private boolean isBombActive = false;

    private final Board board;
    private final UpdateCallback<Pixel> updateCallback;
    private final ValueEventListener pixelListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Pixel pixel = dataSnapshot.getValue(Pixel.class);
            if (pixel == null)
                return;
            Log.d("BoardService", "Pixel update " + pixel.toString());
            board.getPixels().get(pixel.getX()).set(pixel.getY(), pixel);
            if (updateCallback != null)
                updateCallback.onUpdate(pixel);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public BoardService(Game game, UpdateCallback<Pixel> updateCallback) {
        super("games/" + game.getKey() + "/board");
        this.board = game.getBoard();
        this.updateCallback = updateCallback;
    }

    @Override
    protected Board wrap(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(Board.class);
    }

    public Board getBoard() {
        return board;
    }

    public void register() {
        for (int x = 0; x < this.board.getWidth(); x++) {
            for (int y = 0; y < this.board.getHeight(); y++) {
                dbRef.child("pixels").child(Integer.toString(x)).child(Integer.toString(y)).addValueEventListener(pixelListener);
            }
        }
    }

    public void unregister() {
        for (int x = 0; x < this.board.getWidth(); x++) {
            for (int y = 0; y < this.board.getHeight(); y++) {
                dbRef.child("pixels").child(Integer.toString(x)).child(Integer.toString(y)).removeEventListener(pixelListener);
            }
        }
    }

    public void setPixel(final int x, final int y, final ServiceCallback<Pixel> callback) {
        // get current user & team
        // ToDo: get current user data
        Player player = Pixelfighter.getInstance().getPlayer();
        final Team team = Pixelfighter.getInstance().getTeam();
        if (player == null || team == null) {
            callback.failure("Player or Team is null");
            return;
        }

        final Pixel newPixel = new Pixel();
        newPixel.setPlayerKey(player.getKey());
        newPixel.setTeam(team);
        newPixel.setX(x);
        newPixel.setY(y);
        newPixel.setPixelMod(PixelModification.None);

        dbRef.child("pixels").child(Integer.toString(x)).child(Integer.toString(y)).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Pixel currentPixel = mutableData.getValue(Pixel.class);
                if (currentPixel == null) {
                    return Transaction.success(mutableData);
                }

                // ToDo: Run Game Validation
                // ToDo: Problem, Board ist nicht aktuell !!!
                if (isBombActive && Rules.validateForPixelModification(team, currentPixel)) {
                    currentPixel.setPixelMod(PixelModification.Bomb);
                    mutableData.setValue(currentPixel);
                    Log.d("BoardService", "Bomb successfully set on: " + currentPixel.getX() + ", " + currentPixel.getY());
                    return Transaction.success(mutableData);
                } else if (Rules.validate(board, team, x, y)) {
                    Log.d("BoardService", "running checkForLootModification");
                    newPixel.setPixelMod(Rules.checkForLootModification(board, currentPixel));
                    mutableData.setValue(newPixel);
                    return Transaction.success(mutableData);
                } else {
                    return Transaction.abort();
                }
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError == null) {
                    if (b) {
                        Pixel pixel = dataSnapshot.getValue(Pixel.class);
                        callback.success(pixel);
                    } else {
                        callback.failure("Not valid to set");
                    }
                } else {
                    callback.failure(databaseError.getMessage());
                }
            }
        });
    }

    //Specifically for updating Pixels instead of placing new ones
    public void changePixel(final Pixel newPixel,
                            final ServiceCallback<Pixel> callback) {
        Player player = Pixelfighter.getInstance().getPlayer();
        final Team team = Pixelfighter.getInstance().getTeam();
        if (player == null || team == null) {
            callback.failure("Player or Team is null");
            return;
        }

        dbRef.child("pixels").child(Integer.toString(newPixel.getX())).child(Integer.toString(newPixel.getY())).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Pixel currentPixel = mutableData.getValue(Pixel.class);
                if (currentPixel == null) {
                    return Transaction.success(mutableData);
                }

                mutableData.setValue(newPixel);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError == null) {
                    if (b) {
                        Pixel pixel = dataSnapshot.getValue(Pixel.class);
                        callback.success(pixel);
                    } else {
                        callback.failure("Not valid to set");
                    }
                } else {
                    callback.failure(databaseError.getMessage());
                }
            }
        });
    }

    public ArrayList<Pixel> checkForEnemiesToConvert(final int x, final int y) {
        final Team team = Pixelfighter.getInstance().getTeam();
        ArrayList<Pixel> pixelsToUpdate = Rules.checkForEnemiesToConvert(board, team, x, y);
        Log.d("BOARDSERVICE", "amount of pixels to update: " + pixelsToUpdate.size());

        return pixelsToUpdate;
    }

    public void activateBombForNextClick() {
        isBombActive = true;
    }

    public void deactivateBombForNextClick() {
        isBombActive = false;
    }

    public boolean isBombActive() {
        return isBombActive;
    }
}
