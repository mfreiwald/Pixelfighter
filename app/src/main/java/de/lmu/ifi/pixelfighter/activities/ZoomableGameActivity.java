package de.lmu.ifi.pixelfighter.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Map;
import java.util.Objects;

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
import de.lmu.ifi.pixelfighter.models.Team;
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
    @BindView(R.id.buttonsLayout)
    ConstraintLayout buttonsLayout;
    @BindView(R.id.bombToggle)
    ToggleButton bombToggle;
    @BindView(R.id.bombCountView)
    TextView bombCounterView;
    @BindView(R.id.bombImage)
    ImageView bombImage;

    @BindView(R.id.protectionToggle)
    ToggleButton protectionToggle;
    @BindView(R.id.protectionCountView)
    TextView protectionCountView;
    @BindView(R.id.freePixel)
    TextView freePixel;
    @BindView(R.id.redPixel)
    TextView redPixel;
    @BindView(R.id.bluePixel)
    TextView bluePixel;
    @BindView(R.id.greenPixel)
    TextView greenPixel;
    @BindView(R.id.yellowPixel)
    TextView yellowPixel;
    @BindView(R.id.protectionImage)
    ImageView protectionImage;

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

        this.lightSensor = new LightSensor(new LightSensor.LightListener() {
            @Override
            public void onChanged(boolean useDark) {
                if(useDark) {
                    buttonsLayout.setBackgroundColor(getColor(R.color.game_background_dark));
                    bombImage.setColorFilter(getColor(R.color.game_background));
                    protectionImage.setColorFilter(getColor(R.color.game_background));
                } else {
                    buttonsLayout.setBackgroundColor(getColor(R.color.game_background));
                    bombImage.setColorFilter(getColor(R.color.game_background_dark));
                    protectionImage.setColorFilter(getColor(R.color.game_background_dark));
                }
            }
        });

        updateBombView(0);
        updateProtectionView(0);

        BroadcastReceiver br = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("de.lmu.ifi.pixelfighter.BOMB_WAS_EXECUTED");
        filter.addAction("de.lmu.ifi.pixelfighter.PROTECTION_WAS_EXECUTED");
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
        setStatistics(gameSettings.getStatics());
    }

    @Override
    public void onGameOver() {
        Log.d("GameOver: ", "OnGameOver called");
        Toast.makeText(this, "Game is over", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ZoomableGameActivity.this, GameEndActivity.class);
        String team = gameSettings.getTeam().toString();
        intent.putExtra("team", team);
        startActivity(intent);
        lightSensor.onPause();
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
        BoardHandling handling = new BoardHandling(this, gameSettings);
        handling.placePixel(x ,y, currentModification, new ServiceCallback<Pixel>() {
            @Override
            public void success(Pixel pixel) {
                gameView.removePendingClick(click);
                updateToggles();
            }

            @Override
            public void failure(String message) {
                gameView.removePendingClick(click);
                updateToggles();
            }
        });

        bombToggle.setChecked(false);
        protectionToggle.setChecked(false);
        currentModification = PixelModification.None;

        setStatistics(gameSettings.getStatics());

    }

    private void updateToggles() {

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
        bombCounterView.setText(""+amount);
    }

    private void updateProtectionView(int amount) {
        if(amount < 1) {
            protectionToggle.setEnabled(false);
        } else {
            protectionToggle.setEnabled(true);
        }
        protectionCountView.setText(""+amount);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        public final static int EXPLOSION = 1;
        public final static int PROTECTION = 2;

        @Override
        public void onReceive(Context context, Intent intent) {
            if(Objects.equals(intent.getAction(), "de.lmu.ifi.pixelfighter.BOMB_WAS_EXECUTED")) {
                Log.d(TAG, "received bomb broadcast");
                Log.d(TAG, "action: " + intent.getAction());
                int x = intent.getIntExtra("x", 0);
                int y = intent.getIntExtra("y", 0);

                animateIcon(x, y, context, MyBroadcastReceiver.EXPLOSION);

            } else if(Objects.equals(intent.getAction(), "de.lmu.ifi.pixelfighter.PROTECTION_WAS_EXECUTED")) {
                Log.d(TAG, "received protection broadcast");
                Log.d(TAG, "action: " + intent.getAction());
                int x = intent.getIntExtra("x", 0);
                int y = intent.getIntExtra("y", 0);

                animateIcon(x, y, context, MyBroadcastReceiver.PROTECTION);
            }
        }
    }

    private void animateIcon(int x, int y, Context context, int typeOfAnimation) {
        final FrameLayout fl = findViewById(R.id.zoomLayout);
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, 10);
        int left = (int)(gameView.calculateOffsetX() + x * gameView.calculateBoxSize() - 40);
        int top = (int)(gameView.calculateOffsetY() + y * gameView.calculateBoxSize() - 40);
        p.setMargins(left, top, 0, 0);

        final ImageView iconImgView = new ImageView(context);
        switch (typeOfAnimation) {

            case MyBroadcastReceiver.EXPLOSION:
                iconImgView.setImageResource(R.drawable.explosion);
                break;
            case MyBroadcastReceiver.PROTECTION:
                iconImgView.setImageResource(R.drawable.shield);
                break;
            default:
                iconImgView.setImageResource(R.drawable.explosion);
                break;
        }
        iconImgView.setLayoutParams(p);

        final float[] scale = {0.2f};
        iconImgView.setScaleX(scale[0]);
        iconImgView.setScaleY(scale[0]);
        fl.addView(iconImgView);

        new CountDownTimer(850, 100) {
            public void onTick(long millisUntilFinished) {
                scale[0] += 0.04f;
                iconImgView.setScaleX(scale[0]);
                iconImgView.setScaleY(scale[0]);
            }

            public void onFinish() {
                fl.removeView(iconImgView);
            }
        }.start();
    }


    private void setStatistics(Map<Team, Integer> statics ) {
        int full = findViewById(android.R.id.content).getWidth();

        int red = statics.get(Team.Red);
        int blue = statics.get(Team.Blue);
        int green = statics.get(Team.Green);
        int yellow = statics.get(Team.Yellow);
        int free = statics.get(Team.None);
        int pixels = red + blue + green + yellow + free;

        if (red!=0) {
            redPixel.setVisibility(View.VISIBLE);
            red = (statics.get(Team.Red)*full/pixels) ;
        }
        if(blue!=0){
            bluePixel.setVisibility(View.VISIBLE);
            blue = (statics.get(Team.Blue)*full/pixels);

        }
        if(green!=0 ) {
            greenPixel.setVisibility(View.VISIBLE);
            green = (statics.get(Team.Green)*full/pixels);
        }
        if(yellow!=0) {
            yellowPixel.setVisibility(View.VISIBLE);
            yellow = (statics.get(Team.Yellow)*full/pixels);
        }
        if(free!=0) {
            free = (statics.get(Team.None)*full/pixels);
        }

        LinearLayout.LayoutParams lpNone = (LinearLayout.LayoutParams) freePixel.getLayoutParams();
        LinearLayout.LayoutParams lpRed = (LinearLayout.LayoutParams) redPixel.getLayoutParams();
        LinearLayout.LayoutParams lpBlue = (LinearLayout.LayoutParams) bluePixel.getLayoutParams();
        LinearLayout.LayoutParams lpGreen = (LinearLayout.LayoutParams) greenPixel.getLayoutParams();
        LinearLayout.LayoutParams lpYellow = (LinearLayout.LayoutParams) yellowPixel.getLayoutParams();

        lpNone.width = free;
        lpRed.width = red;
        lpBlue.width = blue;
        lpGreen.width = green;
        lpYellow.width = yellow;

        freePixel.setLayoutParams(lpNone);
        redPixel.setLayoutParams(lpRed);
        bluePixel.setLayoutParams(lpBlue);
        greenPixel.setLayoutParams(lpGreen);
        yellowPixel.setLayoutParams(lpYellow);
    }

}
