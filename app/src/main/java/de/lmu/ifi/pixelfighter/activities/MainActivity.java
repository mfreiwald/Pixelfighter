package de.lmu.ifi.pixelfighter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.models.Player;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.models.callbacks.Callback;
import de.lmu.ifi.pixelfighter.models.callbacks.GameCallback;
import de.lmu.ifi.pixelfighter.services.android.Settings;
import de.lmu.ifi.pixelfighter.services.android.Singleton;
import de.lmu.ifi.pixelfighter.services.firebase.AuthenticationService;
import de.lmu.ifi.pixelfighter.services.firebase.GameService;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // check for settings
        settings = new Settings();
        String key = settings.getPlayerKey();
        if(key == null || key.isEmpty()) {
            register();
        } else {
            login(key);
        }
    }







    private void register() {
        AuthenticationService.getInstance().register("michael", new Callback<Player>() {
            @Override
            public void onLoaded(Player player) {
                Log.d("Toast", "Successful registed. Your Key="+player.getKey());

                settings.setPlayerKey(player.getKey());
                step1(player);

            }

            @Override
            public void onError(String message) {
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
                Log.d("Toast", "Error: " + message);
                settings.setPlayerKey("");
                register();
            }
        });
    }

    private void step1(final Player player) {

        final Intent intent = new Intent(this, de.lmu.ifi.pixelfighter.activities.GameActivity.class);

        Singleton.getInstance().setPlayerKey(player.getKey());
        Singleton.getInstance().setTeam(Team.Red);
        String gameKey = settings.getActiveGameKey();
        if(gameKey == null || gameKey.isEmpty()) {
            // ToDo: choose team!
            GameService.getInstance().searchAndJoinGame(player, Team.Red, new Callback<Game>() {
                @Override
                public void onLoaded(Game game) {
                    Log.d("Toast", "Your are playing now on Game " + game.getKey());
                    settings.setActiveGameKey(game.getKey());
                    Singleton.getInstance().setGame(game);
                    startActivity(intent);
                }

                @Override
                public void onError(String message) {
                    Log.d("Toast", "Error: " + message);
                }
            });
        } else {
            GameService.getInstance().loadGame(gameKey, new GameCallback() {
                @Override
                public void onClosed() {
                    Log.d("Toast", "Your Game was already closed");
                    // Search for new game?
                    GameService.getInstance().searchAndJoinGame(player, Team.Red, new Callback<Game>() {
                        @Override
                        public void onLoaded(Game game) {
                            Log.d("Toast", "Your are playing now on Game " + game.getKey());
                            settings.setActiveGameKey(game.getKey());
                            Singleton.getInstance().setGame(game);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(String message) {
                            Log.d("Toast", "Error: " + message);
                        }
                    });
                }

                @Override
                public void onLoaded(Game game) {
                    Log.d("Toast", "You loaded Game " + game.getKey());
                    Singleton.getInstance().setGame(game);
                    startActivity(intent);
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


        /*
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
        */
    }

    public void resetGameBoard(View view) {
        /*
        Board board = new Board();
        board.reset();

        DatabaseReference myRef = database.getReference("board");
        myRef.setValue(board);
        */
    }

}
