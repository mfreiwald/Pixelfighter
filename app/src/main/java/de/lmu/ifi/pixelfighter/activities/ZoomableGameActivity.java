package de.lmu.ifi.pixelfighter.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.activities.game.GameView;
import de.lmu.ifi.pixelfighter.activities.game.PendingClick;
import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.GamePlayer;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.models.PixelModification;
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

    @BindView(R.id.bombButton)
    Button bombButton;
    @BindView(R.id.bombCountView)
    TextView bombCounterView;
    private int bombCharges = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomable_game);

        ButterKnife.bind(this);

        updateBombView(0);


        this.boardService = new BoardService(Pixelfighter.getInstance().getGame(), this);
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

        float scale = board.getWidth()/7.0f;
        this.gameView.setMaxScale(scale);

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
        int amount = gamePlayer.getBombAmount();
        updateBombView(amount);

    }

    @Override
    public void onClick(int x, int y) {
        
        final PendingClick click = new PendingClick(x, y);
        this.gameView.addPendingClick(click);
        this.boardService.setPixel(x, y, new ServiceCallback<Pixel>() {
            @Override
            public void success(Pixel pixel) {
                Log.d("GameActivity", "Successfully set pixel " + pixel.toString());
                // remove from pending
                gameView.removePendingClick(click);

                if (pixel.getPixelMod().equals(PixelModification.Bomb))
                    updateBombView(1);

                //Die Umgebung auf Gegner überprüfen, die umgefärbt werden müssen
                Log.d("GameActivity", "Running enemy check now");
                ArrayList<Pixel> pixelsToUpdate = boardService.checkForEnemiesToConvert(pixel.getX(), pixel.getY());
                for (Pixel newPixel : pixelsToUpdate) {
                    Log.d("GameActivity", "updating Pixel: " + pixel);
                    boardService.changePixel(newPixel, customCallback);
                }
            }

            @Override
            public void failure(String message) {
                Log.d("GameActivity", message);
                // remove from pending
                gameView.removePendingClick(click);
            }
        });
    }

    private ServiceCallback<Pixel> customCallback = new ServiceCallback<Pixel>() {
        @Override
        public void success(Pixel pixel) {
            Log.d("GameActivity", "Successfully changed pixel " + pixel.toString());

            //Wiederum die Umgebung auf Gegner überprüfen, die umgefärbt werden müssen
            Log.d("GameActivity", "Running deeper level enemy check now");
            ArrayList<Pixel> pixelsToUpdate = boardService.checkForEnemiesToConvert(pixel.getX(), pixel.getY());
            for (Pixel newPixel : pixelsToUpdate) {
                Log.d("GameActivity", "(deeper level) updating Pixel: " + pixel);
                boardService.changePixel(newPixel, customCallback);
            }
        }

        @Override
        public void failure(String message) {
            Log.d("GameActivity", message);
        }
    };

    @OnClick(R.id.bombButton)
    public void placeBomb() {
        if (bombCharges > 0) {
            if (!boardService.isBombActive()) {
                boardService.activateBombForNextClick();
                bombCharges--;
                Log.d("GameService", "Activated bomb for next click");
            } else {
                boardService.deactivateBombForNextClick();
                bombCharges++;
            }
        }
        bombCounterView.setText("x" + bombCharges);
    }

    private void updateBombView(int amount) {
        bombCounterView.setText(amount + " Bombs");
    }
}
