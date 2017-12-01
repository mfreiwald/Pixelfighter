package de.lmu.ifi.pixelfighter.demo.demo3;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 20.11.17.
 */

public class Game {

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private Board board;
    private String teamName;
    private Callback updateCallback;


    public Game(Board board, String teamName) {
        this.board = board;
        this.teamName = teamName;

        database.getReference("board").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Game.this.board = dataSnapshot.getValue(Board.class);
                if (updateCallback != null)
                    updateCallback.success();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setUpdateCallback(Callback updateCallback) {
        this.updateCallback = updateCallback;
    }


    public Board getBoard() {
        return board;
    }

    public void setPixel(final int x, final int y, final Callback callback) {
        board.setPixel(x, y, teamName);
        callback.success();
        database.getReference("board").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Board board = mutableData.getValue(Board.class);
                if (board == null) {
                    return Transaction.success(mutableData);
                }
                board.setPixel(x, y, teamName);
                board = checkSurroundingPixels(board, x, y);
                mutableData.setValue(board);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError == null) {
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

    private Board checkSurroundingPixels(Board board, int x, int y) {
        Log.d("GAME", "check START");
        List<Pixel> adjacentPixelList = board.getAdjacentPixelsFor(x, y);
        List<Pixel> adjacentEnemies = new ArrayList<>();

        for (Pixel pixel : adjacentPixelList) {
            String name = pixel.getTeamName();
            if (!name.equals(teamName) && !name.isEmpty()) {
                adjacentEnemies.add(pixel);
            }
        }

        Log.d("GAME", "adj. enemies amount:" + adjacentEnemies.size());

        //If there is at least one enemy, check this enemy's surrounding pixels,
        // to see if there are 3 or more ally pixels -> would turn this pixel into ally
        if (adjacentEnemies.size() > 0) {
            for (Pixel enemy : adjacentEnemies) {
                String enemyTeamName = enemy.teamName;
                List<Pixel> adjacentAllies = new ArrayList<>();

                for (Pixel surroundingPixel : board.getAdjacentPixelsFor(enemy.x, enemy.y)) {
                    //Allies of the initial pixel coming into checkSurroundingPixels
                    String name = surroundingPixel.getTeamName();
                    if (!name.equals(enemyTeamName) && !name.isEmpty()) {
                        adjacentAllies.add(surroundingPixel);
//                        Log.d("GAME", "Added ally for x: " + surroundingPixel.x + ", y: " + surroundingPixel.y);
                    }
                }

                Log.d("GAME", "Allies amount: " + adjacentAllies.size());

                //Turn this enemy into an ally
                if (adjacentAllies.size() >= 3) {
                    enemy.teamName = teamName;
                    board.pixelList.set(Board.getListPosFromCoords(enemy.x, enemy.y), enemy);
//                    Log.d("GAME", "Turned this enemy into ally");
                }
            }
        }

        return board;
    }

}
