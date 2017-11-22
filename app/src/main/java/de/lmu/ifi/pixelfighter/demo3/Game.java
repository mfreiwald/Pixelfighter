package de.lmu.ifi.pixelfighter.demo3;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by michael on 20.11.17.
 */

public class Game {

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private Board board;
    private String teamName;

    public Game(Board board, String teamName) {
        this.board = board;
        this.teamName = teamName;

        database.getReference("board").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Game.this.board = dataSnapshot.getValue(Board.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public Board getBoard() {
        return board;
    }

    public void setPixel(final int x, final int y, final Callback callback) {
        database.getReference("board").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Board board = mutableData.getValue(Board.class);
                if (board == null) {
                    return Transaction.success(mutableData);
                }
                board.setPixel(x, y, teamName);
                mutableData.setValue(board);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if(databaseError == null) {
                    Board board = dataSnapshot.getValue(Board.class);
                    Game.this.board = board;
                    callback.success();
                }
            }
        });
    }

    public interface Callback {
        void success();
    }

}
