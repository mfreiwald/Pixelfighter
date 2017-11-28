package de.lmu.ifi.pixelfighter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import de.lmu.ifi.pixelfighter.demo.demo3.Board;
import de.lmu.ifi.pixelfighter.demo.demo3.GameActivity;
import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.models.Player;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.models.callbacks.Callback;
import de.lmu.ifi.pixelfighter.models.callbacks.GameCallback;
import de.lmu.ifi.pixelfighter.services.android.Settings;
import de.lmu.ifi.pixelfighter.services.firebase.AuthenticationService;
import de.lmu.ifi.pixelfighter.services.firebase.GameService;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    //private GameView gameSurface;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static de.lmu.ifi.pixelfighter.demo.demo3.Game game;

    Settings settings;

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

        settings = new Settings();
        String key = settings.getPlayerKey();
        if(key == null || key.isEmpty()) {
            register();
        } else {
            login(key);
        }





        de.lmu.ifi.pixelfighter.demo.demo3.board.Board.loadBoardFromFB(new de.lmu.ifi.pixelfighter.demo.demo3.board.Board.Result<de.lmu.ifi.pixelfighter.demo.demo3.board.Board>() {
            @Override
            public void value(de.lmu.ifi.pixelfighter.demo.demo3.board.Board result) {
                Log.d("Main", "Board = " + result);


                result.setPixel(0, 0, de.lmu.ifi.pixelfighter.demo.demo3.board.Board.Team.Yellow, "miexec", new de.lmu.ifi.pixelfighter.demo.demo3.board.Board.Callback() {
                    @Override
                    public void success() {
                        Log.d("Main", "Set pixel was successfull");
                    }

                    @Override
                    public void failure(Error error, DatabaseError databaseError) {
                        Log.d("Main", "Set pixel failure. " + error);

                    }

                });
            }
        });


    }

    private void register() {
        String android_id = android.provider.Settings.Secure.getString(this.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        AuthenticationService.getInstance().register("michael", new Callback<Player>() {
            @Override
            public void onLoaded(Player player) {
                Toast.makeText(MainActivity.this, "Successful registed. Your Key="+player.getKey(), Toast.LENGTH_LONG).show();
                Log.d("Toast", "Successful registed. Your Key="+player.getKey());

                settings.setPlayerKey(player.getKey());
                step1(player);

            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                Log.d("Toast", "Error: " + message);

            }
        });
    }

    private void login(String key) {
        AuthenticationService.getInstance().login(key, new Callback<Player>() {
            @Override
            public void onLoaded(Player player) {
                Log.d("Toast", "Successful login. Your Key="+player.getKey());

                step1(player);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                Log.d("Toast", "Error: " + message);
                settings.setPlayerKey("");
                register();
            }
        });
    }

    private void step1(final Player player) {

        String gameKey = settings.getActiveGameKey();
        if(gameKey == null || gameKey.isEmpty()) {
            GameService.getInstance().searchAndJoinGame(player, Team.Red, new Callback<Game>() {
                @Override
                public void onLoaded(Game game) {
                    Toast.makeText(MainActivity.this, "Your are playing now on Game " + game.getKey(), Toast.LENGTH_LONG).show();
                    Log.d("Toast", "Your are playing now on Game " + game.getKey());
                    settings.setActiveGameKey(game.getKey());
                }

                @Override
                public void onError(String message) {
                    Log.d("Toast", "Error: " + message);
                    Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            GameService.getInstance().loadGame(gameKey, new GameCallback() {
                @Override
                public void onClosed() {
                    Log.d("Toast", "Your Game was already closed");
                    // Search for new game?
                }

                @Override
                public void onLoaded(Game game) {
                    Log.d("Toast", "You loaded Game " + game.getKey());
                }

                @Override
                public void onError(String message) {
                    Log.d("Toast", "Error: " + message);
                    settings.setActiveGameKey("");
                    step1(player);
                }
            });
        }


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
                de.lmu.ifi.pixelfighter.demo.demo3.Team team = mutableData.getValue(de.lmu.ifi.pixelfighter.demo.demo3.Team.class);
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

                            game = new de.lmu.ifi.pixelfighter.demo.demo3.Game(board, teamName);

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
