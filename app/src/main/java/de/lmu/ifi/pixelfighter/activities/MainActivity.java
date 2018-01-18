package de.lmu.ifi.pixelfighter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.PixelModification;
import de.lmu.ifi.pixelfighter.models.Player;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.models.callbacks.Callback;
import de.lmu.ifi.pixelfighter.models.game.Board;
import de.lmu.ifi.pixelfighter.models.game.Game;
import de.lmu.ifi.pixelfighter.models.game.GamePlayer;
import de.lmu.ifi.pixelfighter.services.android.Settings;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;
import de.lmu.ifi.pixelfighter.services.firebase.AuthenticationService;
import de.lmu.ifi.pixelfighter.services.firebase.game.GamePlayerService;
import de.lmu.ifi.pixelfighter.services.firebase.game.GameService;
import de.lmu.ifi.pixelfighter.services.firebase.game.GameWrapperService;
import de.lmu.ifi.pixelfighter.services.firebase.game.GamesService;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    Settings settings;

    @BindView(R.id.usernameEditText)
    EditText usernameTextView;
    @BindView(R.id.btnRegister)
    Button btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        {
            GamesService serv = new GamesService();
            final String gameKey = serv.createGame();
            final String playerKey = "adfasdflk24";
            serv.joinGame(gameKey, playerKey);

            final GameWrapperService.Callback callback = new GameWrapperService.Callback() {
                @Override
                public void onGameChanged(Game game) {
                    Log.d("GameWrapper", game.toString());
                }

                @Override
                public void onBoardChanged(Board board) {
                    Log.d("GameWrapper", board.toString());
                }

                @Override
                public void onPlayerChanged(GamePlayer player) {
                    Log.d("GameWrapper", player.toString());
                }
            };
            GameWrapperService gameServ = new GameWrapperService(gameKey, playerKey, callback);

            gameServ.onResume();

        }


        {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            Log.d("Firebase", "Sign in ");

            if(currentUser == null) {
                FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user = task.getResult().getUser();
                        Log.d("Firebase", "User = " + user.getUid());
                    }
                });
            } else {
                Log.d("Firebase", "Signd in " + currentUser.getUid());
            }
        }

        // check for settings
        settings = new Settings();

        String key = settings.getPlayerKey();
        if(key == null || key.isEmpty()) {
            Log.d("MainActivity", "Please register");
            showRegister();
        } else {
            showLogin();
            login(key);
        }
    }

    private void showRegister() {
        usernameTextView.setVisibility(View.VISIBLE);
        btnRegister.setVisibility(View.VISIBLE);
    }

    private void showLogin() {
        usernameTextView.setVisibility(View.GONE);
        btnRegister.setVisibility(View.GONE);
    }


    @OnClick(R.id.btnRegister)
    public void register() {

        String username = usernameTextView.getText().toString();

        AuthenticationService.getInstance().register(username, new Callback<Player>() {
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
                Toast.makeText(MainActivity.this, "Login failed. Please register with Username", Toast.LENGTH_LONG).show();
                settings.setPlayerKey("");
                showRegister();
                register();
            }
        });
    }

    private void step1(final Player player) {
        Pixelfighter.getInstance().setPlayer(player);
        new Settings().setPlayerKey(player.getKey());
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
