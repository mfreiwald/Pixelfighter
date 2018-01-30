package de.lmu.ifi.pixelfighter.activities.game;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.activities.GameEndActivity;
import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.models.GamePlayer;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.models.PixelModification;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.models.UserData;
import de.lmu.ifi.pixelfighter.services.android.LightSensor;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;
import de.lmu.ifi.pixelfighter.services.firebase.BoardHandling;
import de.lmu.ifi.pixelfighter.services.firebase.Database;
import de.lmu.ifi.pixelfighter.services.firebase.GameService;
import de.lmu.ifi.pixelfighter.services.firebase.GenericReference;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.UpdateCallback;

/**
 * Created by michael on 09.01.18.
 */

public class ZoomableGameActivity extends AppCompatActivity implements GameService.Callback, GameView.OnClickListener {

    private static final String TAG = "ZoomableGameActivity";

    GameSettings gameSettings;

    GenericReference<Board> boardReference;

    //private BoardService boardService;
    private GameService gameService;

    @BindView(R.id.gameView)
    GameView gameView;
    @BindView(R.id.bombToggle)
    ToggleButton bombToggle;
    @BindView(R.id.bombCountView)
    TextView bombCounterView;

    private LightSensor lightSensor;

    private PixelModification currentModification = PixelModification.None;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomable_game);

        ButterKnife.bind(this);

        // Get GameKey from Intent
        this.gameSettings = new GameSettings(getIntent().getExtras().getString("gameKey"), Pixelfighter.getInstance().getUserData().getUid());

        boardReference = Database.Game(gameSettings.gameKey).Board();

        // Load Game
        Database.Game(this.gameSettings.getGameKey()).Game().addSingleListener(new GenericReference.ValueListener<Game>() {
            @Override
            public void onData(Game object) {
                gameSettings.setBoard(object.getBoard());

                // search for my team
                for (Map.Entry<String, Map<String, GamePlayer>> playersInTeam : object.getPlayers().entrySet()) {
                    if (playersInTeam.getValue().containsKey(gameSettings.getUid())) {
                        gameSettings.setTeam(Team.valueOf(playersInTeam.getKey()));
                        break;
                    }
                }
                gameView.setGameSettings(gameSettings);
                Log.d("GameSettings", gameSettings.toString());
                gameView.setOnClickListener(ZoomableGameActivity.this);

                gameService = new GameService(
                        object,
                        gameSettings.getGameKey(),
                        gameSettings.getUid(),
                        ZoomableGameActivity.this);
                gameService.register();

            }

            @Override
            public void onError(GenericReference.Error error) {

            }
        });

        this.lightSensor = new LightSensor();

        //this.boardService = new BoardService(Pixelfighter.getInstance().getGame(), bombToggle, this);

        updateBombView();

        BroadcastReceiver br = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter("de.lmu.ifi.pixelfighter.MY_NOTIFICATION");
        LocalBroadcastManager.getInstance(this).registerReceiver(br, filter);
    }

    private GenericReference.ValueListener<Board> boardListener = new GenericReference.ValueListener<Board>() {
        @Override
        public void onData(Board board) {
            gameSettings.setBoard(board);
        }

        @Override
        public void onError(GenericReference.Error error) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //this.boardService.register();
        if (gameService != null) this.gameService.register();
        this.gameView.resume();
        this.lightSensor.onResume();

        boardReference.addListener(boardListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //this.boardService.unregister();
        if (gameService != null) this.gameService.unregister();
        this.gameView.pause();
        this.lightSensor.onPause();

        boardReference.removeListener(boardListener);
    }

    @Override
    public void onGameOver() {
        Log.d("DEBUG Game over", "onGameOvercalled");

        Toast.makeText(this, "Game is over", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ZoomableGameActivity.this, GameEndActivity.class);
        intent.putExtra("board", gameSettings.getBoard().getPixels());
        String key = Pixelfighter.getInstance().getUserData().getGameKey();
        intent.putExtra("gamekey", key);
        Log.d("D/gameOver: ", key);
        startActivity(intent);
    }

    @Override
    public void onGamePlayerChanged(GamePlayer gamePlayer) {
        updateBombView();
    }

    @Override
    public void onClick(int x, int y) {

        final PendingClick click = new PendingClick(x, y, gameSettings.getTeam());
        this.gameView.addPendingClick(click);


        new BoardHandling(this).placePixel(
                gameService,
                gameSettings.getBoard(),
                x, y,
                gameSettings.getUid(),
                gameSettings.getTeam(),
                currentModification,
                new ServiceCallback<Pixel>() {
                    @Override
                    public void success(Pixel pixel) {
                        gameView.removePendingClick(click);

                        if (currentModification == PixelModification.Bomb) {
                            gameService.placedBomb();
                            if (gameService.getBombCount() <= 0) {
                                currentModification = PixelModification.None;
                            }
                        }
                    }

                    @Override
                    public void failure(String message) {
                        gameView.removePendingClick(click);

                    }
                });
    }


    @OnCheckedChanged(R.id.bombToggle)
    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
        if (gameService == null) return;
        if (gameService.getBombCount() <= 0) {
            currentModification = PixelModification.None;
            bombToggle.post(new Runnable() {
                @Override
                public void run() {
                    bombToggle.setChecked(false);
                }
            });
        }
        if (isChecked) {
            currentModification = PixelModification.Bomb;
        } else {
            currentModification = PixelModification.None;
        }
    }

    private void updateBombView() {
        if (gameService == null) return;
        bombCounterView.setText("x" + gameService.getBombCount());
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }


    public static class GameSettings {
        private final String gameKey;
        private String uid;
        private Team team;
        private Board board;

        public GameSettings(String gameKey, String uid) {
            this.gameKey = gameKey;
            this.uid = uid;
        }

        public String getGameKey() {
            return gameKey;
        }

        public Board getBoard() {
            return board;
        }

        private void setBoard(Board board) {
            this.board = board;
        }

        public Team getTeam() {
            return this.team;
        }

        private void setTeam(Team team) {
            this.team = team;
        }

        public String getUid() {
            return uid;
        }

        @Override
        public String toString() {
            return "GameSettings{" +
                    "gameKey='" + gameKey + '\'' +
                    ", uid='" + uid + '\'' +
                    ", team=" + team +
                    ", board=" + board +
                    '}';
        }
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int x = intent.getIntExtra("x", 0);
            int y = intent.getIntExtra("y", 0);

            final FrameLayout fl = findViewById(R.id.zoomLayout);
            final TextView textView = new TextView(context);
            FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT, 10);
            int left = x * 60;
            int top = y * 60;
            p.setMargins(left, top, 0, 0);
            textView.setLayoutParams(p);
            textView.setText("BOMBO");
            final float[] size = {5.0f};
            textView.setTextSize(size[0]);
            fl.addView(textView);

            new CountDownTimer(1000, 100) {
                public void onTick(long millisUntilFinished) {
                    size[0] += 0.2f;
                    textView.setTextSize(size[0]);
                }

                public void onFinish() {
                    fl.removeView(textView);
                }
            }.start();

        }
    }
}
