package de.lmu.ifi.pixelfighter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Map;

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
    @BindView(R.id.bombToggle)
    ToggleButton bombToggle;
    @BindView(R.id.bombCountView)
    TextView bombCounterView;
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
                setStatistics(gameSettings.getStatics());
            }

            @Override
            public void failure(String message) {
                gameView.removePendingClick(click);
            }
        });

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
        bombCounterView.setText("x" + amount);
    }

    private void updateProtectionView(int amount) {
        protectionCountView.setText("x" + amount);
    }

    private void setStatistics(Map<Team, Integer> statics ) {
        int full = gameView.getWidth();

        int red = statics.get(Team.Red);
        int blue = statics.get(Team.Blue);
        int green = statics.get(Team.Green);
        int yellow = statics.get(Team.Yellow);
        int free = statics.get(Team.None);
        int pixels = red + blue + green + yellow + free;
        Log.d("Statistics", "Gesamt: " + pixels);

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
        Log.d("Statics", "Full: " + full);
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

        Log.d("Statics", "Red: " + red);
        Log.d("Statics", "Blue: " + blue);
        Log.d("Statics", "Green: " + green);
        Log.d("Statics", "Yellow: " + yellow);
        Log.d("Statics", "None: " + free);

    }

}
