package de.lmu.ifi.pixelfighter.services.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.pixelfighter.game.Rules;
import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.models.Player;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.services.android.Singleton;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.UpdateCallback;

/**
 * Created by michael on 28.11.17.
 */

public class BoardService extends BaseService<Board> {

    private final Board board;
    private final UpdateCallback<Pixel> updateCallback;
    private final ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Pixel pixel = dataSnapshot.getValue(Pixel.class);
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
                dbRef.child("pixels").child(Integer.toString(x)).child(Integer.toString(y)).addValueEventListener(listener);
            }
        }
    }

    public void unregister() {
        for (int x = 0; x < this.board.getWidth(); x++) {
            for (int y = 0; y < this.board.getHeight(); y++) {
                dbRef.child("pixels").child(Integer.toString(x)).child(Integer.toString(y)).removeEventListener(listener);
            }
        }
    }

    public void setPixel(final int x, final int y, final ServiceCallback<Pixel> callback) {
        // get current user & team
        // ToDo: get current user data
        Player player = Singleton.getInstance().getPlayer();
        final Team team = Singleton.getInstance().getTeam();
        if (player == null || team == null) {
            callback.failure("Player or Team is null");
            return;
        }

        final Pixel newPixel = new Pixel();
        newPixel.setPlayerKey(player.getKey());
        newPixel.setTeam(team);
        newPixel.setX(x);
        newPixel.setY(y);

        dbRef.child("pixels").child(Integer.toString(x)).child(Integer.toString(y)).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Pixel currentPixel = mutableData.getValue(Pixel.class);
                if (currentPixel == null) {
                    return Transaction.success(mutableData);
                }

                // ToDo: Run Game Validation
                // ToDo: Problem, Board ist nicht aktuell !!!
                if (Rules.validate(board, team, x, y)) {
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

    public void runEnemyCheck(final int x, final int y, final ServiceCallback<List<Pixel>> callback) {
        final Team team = Singleton.getInstance().getTeam();

        dbRef.child("pixels").child(Integer.toString(x)).child(Integer.toString(y)).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Pixel currentPixel = mutableData.getValue(Pixel.class);
                if (currentPixel == null) {
                    return Transaction.success(mutableData);
                }

                // ToDo: Run Game Validation
                // ToDo: Problem, Board ist nicht aktuell !!!
                if (Rules.validate(board, team, x, y)) {
                    ArrayList<Pixel> pixelsToUpdate = Rules.checkForEnemiesToConvert(board, team, x, y);
                    mutableData.setValue(pixelsToUpdate);
                    return Transaction.success(mutableData);
                } else {
                    return Transaction.abort();
                }
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError == null) {
                    if (b) {
                        GenericTypeIndicator<List<Pixel>> t = new GenericTypeIndicator<List<Pixel>>() {
                        };
                        List<Pixel> updates = dataSnapshot.getValue(t);
                        callback.success(updates);
                    } else {
                        callback.failure("Not valid to set");
                    }
                } else {
                    callback.failure(databaseError.getMessage());
                }
            }
        });
    }
}
