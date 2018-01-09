package de.lmu.ifi.pixelfighter.services.firebase;

import android.util.Log;

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
import de.lmu.ifi.pixelfighter.models.PixelModification;
import de.lmu.ifi.pixelfighter.models.Player;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.services.android.Singleton;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.UpdateCallback;

public class BoardService extends BaseService<Board> {

    private final Board board;
    private final UpdateCallback<Pixel> updateCallback;
    private final ValueEventListener pixelListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Pixel pixel = dataSnapshot.getValue(Pixel.class);
            if (pixel == null)
                return;
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

    //Specifically for updating Pixels instead of placing new ones
    public void changePixel(final int x, final int y,
                            final ServiceCallback<Pixel> callback) {
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
        newPixel.setPixelMod(PixelModification.None);

        dbRef.child("pixels").child(Integer.toString(x)).child(Integer.toString(y)).runTransaction(new Transaction.Handler() {
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
        final Team team = Singleton.getInstance().getTeam();
        ArrayList<Pixel> pixelsToUpdate = Rules.checkForEnemiesToConvert(board, team, x, y);
        Log.d("RULES", "amount of pixels to update: " + pixelsToUpdate.size());

        return pixelsToUpdate;

        //statt in einer neuen Transaction Rules.checkForEnemiesToConvert(board, team, x, y); laufen zu lassen
        // lieber hier. Dann die entstandene pixelsToUpdate Liste durchlaufen udn für jeden zu verändernden
        // Pixel die setPixel Methode aufrufen -> jeder "converted enemy" wird wie ein neuer Pixel gesetzt statt eine
        // Liste mit allen zu übergeben
        // (bzw. neue setPixel Methode schreiben, damit trotz einer Farbe was verändert)

//        dbRef.child("pixels").child(Integer.toString(x)).child(Integer.toString(y)).runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                Pixel currentPixel = mutableData.getValue(Pixel.class);
//                if (currentPixel == null) {
//                    return Transaction.success(mutableData);
//                }
//
//                if (!pixelsToUpdate.isEmpty()) {
//                    mutableData.setValue(pixelsToUpdate);
//                    return Transaction.success(mutableData);
//                } else {
//                    return Transaction.abort();
//                }
//            }

//            @Override
//            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
//                if (databaseError == null) {
//                    if (b) {
//                        GenericTypeIndicator<List<Pixel>> t = new GenericTypeIndicator<List<Pixel>>() {
//                        };
//                        List<Pixel> updates = dataSnapshot.getValue(t);
//                        callback.success(updates);
//                    } else {
//                        callback.failure("No valid results for enemy check");
//                    }
//                } else {
//                    callback.failure(databaseError.getMessage());
//                }
//            }
//        });
    }
}
