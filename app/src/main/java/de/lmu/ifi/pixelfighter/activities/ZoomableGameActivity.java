package de.lmu.ifi.pixelfighter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.activities.game.GameView;
import de.lmu.ifi.pixelfighter.activities.game.PendingClick;
import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.GamePlayer;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;
import de.lmu.ifi.pixelfighter.services.firebase.BoardService;
import de.lmu.ifi.pixelfighter.services.firebase.GameService;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.UpdateCallback;

/**
 * Created by michael on 09.01.18.
 */

public class ZoomableGameActivity extends AppCompatActivity implements UpdateCallback<Pixel>, GameService.Callback, GameView.OnClickListener {

    private static final String TAG = "ZoomableGameActivity";

    private BoardService boardService;
    private GameService gameService;

    private GameView gameView;

    @BindView(R.id.bombToggle)
    ToggleButton bombToggle;
    @BindView(R.id.bombCountView)
    TextView bombCounterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomable_game);

        ButterKnife.bind(this);

        this.boardService = new BoardService(Pixelfighter.getInstance().getGame(), bombToggle, this);
        this.gameService = new GameService(
                Pixelfighter.getInstance().getGame(),
                Pixelfighter.getInstance().getPlayer().getKey(),
                this);
        final Board board = this.boardService.getBoard();
        this.gameView = findViewById(R.id.gameView);
        this.gameView.setBoard(board);
        this.gameView.setOnClickListener(this);

        // bei 20 Pixel
        // Scale=5 => 4 Pixel
        // => Pixels / Scale = #Pixel
        // Scale = #PixelsWidth/maxPixel

        float scale = board.getWidth() / 7.0f;
        this.gameView.setMaxScale(scale);

        updateBombView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.boardService.register();
        this.gameService.register();
        this.gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.boardService.unregister();
        this.gameService.unregister();
        this.gameView.pause();
    }

    @Override
    public void onUpdate(Pixel pixel) {

    }

    @Override
    public void onGameOver() {
        Toast.makeText(this, "Game is over", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ChooseTeamActivity.class);
        startActivity(intent);
    }

    @Override
    public void onGamePlayerChanged(GamePlayer gamePlayer) {
        updateBombView();
    }

    @Override
    public void onClick(int x, int y) {

        final PendingClick click = new PendingClick(x, y);
        this.gameView.addPendingClick(click);
        this.boardService.setPixel(x, y, gameService, new ServiceCallback<Pixel>() {
            @Override
            public void success(Pixel pixel) {
                Log.d(TAG, "Successfully set pixel " + pixel.toString());
                // remove from pending
                gameView.removePendingClick(click);

                //Die Umgebung auf Gegner überprüfen, die umgefärbt werden müssen
                Log.d(TAG, "Running enemy check now");
                ArrayList<Pixel> pixelsToUpdate = boardService.checkForEnemiesToConvert(pixel.getX(), pixel.getY());
                for (Pixel newPixel : pixelsToUpdate) {
                    Log.d(TAG, "updating Pixel: " + pixel);
                    boardService.updatePixel(newPixel, customCallback);
                }
            }

            @Override
            public void failure(String message) {
                Log.d(TAG, message);
                // remove from pending
                gameView.removePendingClick(click);
            }
        });
    }

    private ServiceCallback<Pixel> customCallback = new ServiceCallback<Pixel>() {
        @Override
        public void success(Pixel pixel) {
            Log.d(TAG, "Successfully changed pixel " + pixel.toString());

            //Wiederum die Umgebung auf Gegner überprüfen, die umgefärbt werden müssen
            Log.d(TAG, "Running deeper level enemy check now");
            ArrayList<Pixel> pixelsToUpdate = boardService.checkForEnemiesToConvert(pixel.getX(), pixel.getY());
            for (Pixel newPixel : pixelsToUpdate) {
                Log.d(TAG, "(deeper level) updating Pixel: " + pixel);
                boardService.updatePixel(newPixel, customCallback);
            }
        }

        @Override
        public void failure(String message) {
            Log.d(TAG, message);
        }
    };

    @OnCheckedChanged(R.id.bombToggle)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) { //on
            if (gameService.getBombCount() > 0) {
                if (!boardService.isBombActive()) {
                    boardService.activateBombForNextClick();
                }
            } else {
                bombToggle.setChecked(false);
            }
        } else { //off
            boardService.deactivateBombForNextClick();
        }
    }

    

    private void updateBombView() {
        bombCounterView.setText("x" + gameService.getBombCount());
    }
}
