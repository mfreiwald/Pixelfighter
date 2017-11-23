package de.lmu.ifi.pixelfighter.demo.demo3.board;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by michael on 23.11.17.
 */

public class Board {

    private ArrayList<ArrayList<PixelValue>> pixels;

    public Board() {
    }

    public void reset(int size_x, int size_y) {
        pixels = new ArrayList<>();
        for(int x=0; x<size_x; x++) {
            ArrayList<PixelValue> row = new ArrayList();
            for(int y=0; y<size_y; y++) {
                row.add(new PixelValue(Team.None, ""));
            }
            pixels.add(row);
        }
    }

    public ArrayList<ArrayList<PixelValue>> getPixels() {
        return pixels;
    }

    public void setPixels(ArrayList<ArrayList<PixelValue>> pixels) {
        this.pixels = pixels;
    }

    public void setPixel(int x, int y, final Team team, final String player, final Callback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference("board3").child("pixels").child(Integer.toString(x)).child(Integer.toString(y));
        Log.d("Board", "Ref for transcation = " + ref);
        /*
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PixelValue value = dataSnapshot.getValue(PixelValue.class);
                if(value == null) {
                    Log.d("Board", "Can not read dataSnapshot. " + dataSnapshot);
                } else {
                    Log.d("Board", "PixelValue = " + value);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                PixelValue value = mutableData.getValue(PixelValue.class);
                Log.d("Board", "Transaction Value = " + value);)
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
        */
        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                PixelValue value = mutableData.getValue(PixelValue.class);
                if(value == null) {
                    Log.d("Board", "Can not read mutableData. " + mutableData);
                    return Transaction.success(mutableData);
                }

                if(value.getTeam() == Team.None) {
                    Log.d("Board", "Set Pixel");
                    value.setTeam(team);
                    value.setPlayer(player);
                    mutableData.setValue(value);
                    return Transaction.success(mutableData);
                } else {
                    Log.d("Board", "Pixel already set");
                    return Transaction.abort();
                }
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d("setPixel", ""+databaseError);
                Log.d("setPixel", ""+b);
                Log.d("setPixel", ""+dataSnapshot);
                if(databaseError == null) {
                    if (b) {
                        callback.success();
                    } else {
                        callback.failure(Callback.Error.ALREADY_SET, null);
                    }
                } else {
                    callback.failure(Callback.Error.ERROR, databaseError);
                }
            }
        });

    }

    public static Board initToFB() {
        Board board = new Board();
        board.reset(6,10);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("board3").setValue(board);
        return board;
    }

    public static void loadBoardFromFB(final Result<Board> result) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("board3").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Board board = dataSnapshot.getValue(Board.class);
                result.value(board);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void test2() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("board3").child("pixels").child("0").child("0").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Board", "New value " + dataSnapshot);
                PixelValue value = dataSnapshot.getValue(PixelValue.class);
                Log.d("Board", "Pixel set from Team " + value.getTeam() + " placed by Player " + value.getPlayer());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public enum Team {
        None,
        Red,
        Blue,
        Green,
        Yellow;

        public void test() {

        }
    }

    public interface Callback {
        public enum Error {
            ALREADY_SET,
            ERROR
        }
        void success();
        void failure(Error error, DatabaseError databaseError);
    }

    public interface Result<E> {
        void value(E result);
    }
}
