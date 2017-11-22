package de.lmu.ifi.pixelfighter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.lmu.ifi.pixelfighter.demo.view.GameSurface;
import de.lmu.ifi.pixelfighter.demo2.GameView;
import de.lmu.ifi.pixelfighter.demo3.Board;
import de.lmu.ifi.pixelfighter.demo3.Game;
import de.lmu.ifi.pixelfighter.demo3.GameActivity;
import de.lmu.ifi.pixelfighter.demo3.Pixel;
import de.lmu.ifi.pixelfighter.demo3.Team;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    //private GameView gameSurface;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set fullscreen
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set No Title
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //gameSurface = new GameView(this);
        //this.setContentView(gameSurface);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void startGame(View view) {

        final String username = ((EditText)findViewById(R.id.userName)).getText().toString();

        final String teamName;
        if(view.getId() == R.id.buttonRed) {
            teamName = "red";
        } else if (view.getId() == R.id.buttonGreen) {
            teamName = "green";
        } else {
            return;
        }
        Log.d(TAG, "Start Game with Team " + teamName);

        DatabaseReference ref = database.getReference().child("teams").child(teamName);
        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Team team = mutableData.getValue(Team.class);
                if (team == null) {
                    return Transaction.success(mutableData);
                }
                team.addPlayer(username);
                mutableData.setValue(team);
                return Transaction.success(mutableData);

            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d("MainActivity", "postTransaction:onComplete:" + databaseError);
                if(databaseError == null) {

                    database.getReference("board").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final Board board = dataSnapshot.getValue(Board.class);

                            game = new Game(board, teamName);

                            final Intent intent = new Intent(MainActivity.this, GameActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

    }

    public void resetGameBoard(View view) {

        Board board = new Board();
        board.reset();

        DatabaseReference myRef = database.getReference("board");
        myRef.setValue(board);
    }

}
