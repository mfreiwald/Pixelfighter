package de.lmu.ifi.pixelfighter.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.activities.game.GameSettings;
import de.lmu.ifi.pixelfighter.activities.game.GameUpdate;
import de.lmu.ifi.pixelfighter.activities.game.GameView;
import de.lmu.ifi.pixelfighter.activities.game.OnGameUpdateCallback;
import de.lmu.ifi.pixelfighter.activities.game.PendingClick;
import de.lmu.ifi.pixelfighter.models.GamePlayer;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.models.PixelModification;
import de.lmu.ifi.pixelfighter.services.android.LightSensor;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;
import de.lmu.ifi.pixelfighter.services.firebase.BoardHandling;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;

/**
 * Created by michael on 09.01.18.
 */

public class ZoomableGameActivity extends AppCompatActivity implements OnGameUpdateCallback, GameView.OnClickListener {

    private static final String TAG = "ZoomableGameActivity";

    @BindView(R.id.gameView)
    GameView gameView;
    @BindView(R.id.bombToggle)
    ToggleButton bombToggle;
    @BindView(R.id.bombCountView)
    TextView bombCounterView;
    @BindView(R.id.protectionToggle)
    ToggleButton protectionToggle;
    @BindView(R.id.protectionCountView)
    TextView protectionCountView;

    private GameSettings gameSettings;
    private GameUpdate gameUpdate;
    private PixelModification currentModification = PixelModification.None;
    private LightSensor lightSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomable_game);

        ButterKnife.bind(this);

        // Get GameKey from Intent
        String gameKey = getIntent().getExtras().getString("gameKey");
        String uid = Pixelfighter.getInstance().getUserData().getUid();

        this.gameUpdate = new GameUpdate(gameKey, uid, this);
        this.gameUpdate.load();

        this.lightSensor = new LightSensor();

        updateBombView(0);
        updateProtectionView(0);

        BroadcastReceiver br = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter("de.lmu.ifi.pixelfighter.MY_NOTIFICATION");
        LocalBroadcastManager.getInstance(this).registerReceiver(br, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.gameView.resume();
        this.lightSensor.onResume();
        this.gameUpdate.addListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.gameView.pause();
        this.lightSensor.onPause();
        this.gameUpdate.removeListeners();
    }

    @Override
    public void onGameReady(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
        this.gameView.setGameSettings(gameSettings);
        this.gameView.setOnClickListener(ZoomableGameActivity.this);
        this.gameUpdate.addListeners();
    }

    @Override
    public void onGameOver() {
        Log.d("DEBUG Game over", "onGameOvercalled");

        Toast.makeText(this, "Game is over", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ZoomableGameActivity.this, GameEndActivity.class);
        intent.putExtra("board", gameSettings.getBoard().getPixels());
        String key = gameSettings.getGameKey();
        intent.putExtra("gamekey",key);
        Log.d("D/gameOver: ", key);
        startActivity(intent);
    }

    @Override
    public void onGamePlayerChanged(GamePlayer gamePlayer) {
        updateBombView(gamePlayer.getModificationAmount().get(PixelModification.Bomb.name()));
        updateProtectionView(gamePlayer.getModificationAmount().get(PixelModification.Protection.name()));
    }

    @Override
    public void onClick(int x, int y) {

        final PendingClick click = new PendingClick(x, y, gameSettings.getTeam());
        this.gameView.addPendingClick(click);


        // Check what to do
        BoardHandling handling = new BoardHandling(gameSettings);
        handling.placePixel(x ,y, currentModification, new ServiceCallback<Pixel>() {
            @Override
            public void success(Pixel pixel) {
                gameView.removePendingClick(click);
                updateToogles();
            }

            @Override
            public void failure(String message) {
                gameView.removePendingClick(click);
                updateToogles();
            }
        });

        bombToggle.setChecked(false);
        protectionToggle.setChecked(false);
        currentModification = PixelModification.None;

    }

    private void updateToogles() {

    }

    @OnCheckedChanged(R.id.bombToggle)
    public void onCheckedChangedBomb(CompoundButton buttonView, final boolean isChecked) {
        if(isChecked) {
            currentModification = PixelModification.Bomb;
        } else {
            currentModification = PixelModification.None;
        }
    }

    @OnCheckedChanged(R.id.protectionToggle)
    public void onCheckedChangedProtection(CompoundButton buttonView, final boolean isChecked) {
        if(isChecked) {
            currentModification = PixelModification.Protection;
        } else {
            currentModification = PixelModification.None;
        }
    }


    private void updateBombView(int amount) {
        if(amount < 1) {
            bombToggle.setEnabled(false);
        } else {
            bombToggle.setEnabled(true);
        }
        bombCounterView.setText("x" + amount);
    }

    private void updateProtectionView(int amount) {
        if(amount < 1) {
            protectionToggle.setEnabled(false);
        } else {
            protectionToggle.setEnabled(true);
        }
        protectionCountView.setText("x" + amount);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received broadcast");
            int x = intent.getIntExtra("x", 0);
            int y = intent.getIntExtra("y", 0);

            final FrameLayout fl = findViewById(R.id.zoomLayout);
            final TextView textView = new TextView(context);
            FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT, 10);
            int left = (int)(10 + gameView.calculateOffsetX() + x * gameView.calculateBoxSize());
            int top = (int)(10 + gameView.calculateOffsetY() + y * gameView.calculateBoxSize());
            p.setMargins(left, top, 0, 0);
            textView.setLayoutParams(p);
            textView.setText("BOMBO");
            final float[] size = {5.0f};
            textView.setTextSize(size[0]);
            fl.addView(textView);


            new CountDownTimer(1000, 100) {
                public void onTick(long millisUntilFinished) {
                    size[0] += 0.8f;
                    textView.setTextSize(size[0]);
                }

                public void onFinish() {
                    fl.removeView(textView);
                }
            }.start();
        }
    }
}
