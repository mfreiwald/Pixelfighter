package de.lmu.ifi.pixelfighter.activities.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

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

public class ZoomableGameActivity extends AppCompatActivity implements UpdateCallback<Pixel>, GameService.Callback, GameView.OnClickListener {

    private static final String TAG = "ZoomableGameActivity";

    GameSettings gameSettings;

    //private BoardService boardService;
    //private GameService gameService;

    @BindView(R.id.gameView)
    GameView gameView;
    @BindView(R.id.bombToggle)
    ToggleButton bombToggle;
    @BindView(R.id.bombCountView)
    TextView bombCounterView;

    private LightSensor lightSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomable_game);

        ButterKnife.bind(this);



        // Get GameKey from Intent
        this.gameSettings = new GameSettings(getIntent().getExtras().getString("gameKey"));

        // Load Game
        Database.Game(this.gameSettings.getGameKey()).Game().addSingleListener(new GenericReference.ValueListener<Game>() {
            @Override
            public void onData(Game object) {
                gameSettings.setBoard(object.getBoard());
            }

            @Override
            public void onError(GenericReference.Error error) {

            }
        });

        this.lightSensor = new LightSensor();


        //this.boardService = new BoardService(Pixelfighter.getInstance().getGame(), bombToggle, this);
        /*
        this.gameService = new GameService(
                Pixelfighter.getInstance().getGame(),
                Pixelfighter.getInstance().getUserData().getUid(),
                this);
                */
        this.gameView.setOnClickListener(this);
        this.gameView.setGameSettings(gameSettings);

        //updateBombView();
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
        //this.gameService.register();
        this.gameView.resume();
        this.lightSensor.onResume();

        Database.Game(gameSettings.gameKey).Board().addListener(boardListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //this.boardService.unregister();
        //this.gameService.unregister();
        this.gameView.pause();
        this.lightSensor.onPause();

        Database.Game(gameSettings.gameKey).Board().removeListener(boardListener);
    }

    @Override
    public void onUpdate(Pixel pixel) {

    }

    @Override
    public void onGameOver() {
        Log.d("DEBUG Game over", "onGameOvercalled");

        Toast.makeText(this, "Game is over", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ZoomableGameActivity.this, GameEndActivity.class);
        intent.putExtra("board", gameSettings.getBoard().getPixels());
        String key = Pixelfighter.getInstance().getUserData().getGameKey();
        intent.putExtra("gamekey",key);
        Log.d("D/gameOver: ", key);
        startActivity(intent);
    }

    @Override
    public void onGamePlayerChanged(GamePlayer gamePlayer) {
        //updateBombView();
    }

    @Override
    public void onClick(int x, int y) {

        final PendingClick click = new PendingClick(x, y);
        this.gameView.addPendingClick(click);


        new BoardHandling(gameSettings).placePixel(
                gameSettings.getBoard(),
                x, y,
                Pixelfighter.getInstance().getUserData().getUid(),
                Pixelfighter.getInstance().getTeam(),
                PixelModification.None,
                new ServiceCallback<Pixel>() {
                    @Override
                    public void success(Pixel pixel) {
                        gameView.removePendingClick(click);

                    }

                    @Override
                    public void failure(String message) {
                        gameView.removePendingClick(click);

                    }
                });

/*
        this.boardService.setPixel(x, y, gameService, new ServiceCallback<Pixel>() {
            @Override
            public void success(Pixel pixel) {
                Log.d(TAG, "Successfully set pixel " + pixel.toString());
                // remove from pending
                gameView.removePendingClick(click);

                //Die Umgebung auf Gegner überprüfen, die umgefärbt werden müssen
                Log.d(TAG, "Running enemy check now");
                ArrayList<Pixel> pixelsToUpdate = boardService.checkForEnemiesToConvert(gameService, pixel.getX(), pixel.getY());
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
 */
    }
/*
    private ServiceCallback<Pixel> customCallback = new ServiceCallback<Pixel>() {
        @Override
        public void success(Pixel pixel) {
            Log.d(TAG, "Successfully changed pixel " + pixel.toString());

            //Wiederum die Umgebung auf Gegner überprüfen, die umgefärbt werden müssen
            Log.d(TAG, "Running deeper level enemy check now");
            ArrayList<Pixel> pixelsToUpdate = boardService.checkForEnemiesToConvert(gameService, pixel.getX(), pixel.getY());
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
*/
/*
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
*/
/*
    private void updateBombView() {
        bombCounterView.setText("x" + gameService.getBombCount());
    }
*/

    public static class GameSettings {
        private final String gameKey;
        private Board board;

        public GameSettings(String gameKey) {
            this.gameKey = gameKey;
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
    }
}