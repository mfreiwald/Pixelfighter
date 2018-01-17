package de.lmu.ifi.pixelfighter.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.GamePlayer;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.models.PixelModification;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;
import de.lmu.ifi.pixelfighter.services.firebase.BoardService;
import de.lmu.ifi.pixelfighter.services.firebase.GameService;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.UpdateCallback;

public class GameActivity extends AppCompatActivity implements UpdateCallback<Pixel>, GameService.Callback, View.OnClickListener {


    private BoardService boardService;
    private GameService gameService;
    private ArrayList<ArrayList<Button>> buttons;

    @BindView(R.id.bombButton)
    Button bombButton;
    @BindView(R.id.bombCountView)
    TextView bombCounterView;
    private int bombCharges = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2);

        ButterKnife.bind(this);
        updateBombView(0);

        this.boardService = new BoardService(Pixelfighter.getInstance().getGame(), this);
        this.gameService = new GameService(Pixelfighter.getInstance().getGame(), Pixelfighter.getInstance().getPlayer().getKey(), this);
        final Board board = this.boardService.getBoard();

        GridLayout layout = findViewById(R.id.layout);
        layout.setColumnCount(board.getWidth());

        buttons = new ArrayList<>();

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int dpInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, dm);
        for (int y = 0; y < board.getHeight(); y++) {
            ArrayList<Button> column = new ArrayList<>();
            for (int x = 0; x < board.getWidth(); x++) {
                Button button = new Button(this);
                button.setHeight(dpInPx);
                button.setWidth(dpInPx);
                button.setTag(x + "," + y);
                button.setOnClickListener(this);
                layout.addView(button);
                column.add(button);
            }
            buttons.add(column);
        }

        updateBoard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boardService.register();
        gameService.register();
        updateBoard();
    }

    @Override
    protected void onPause() {
        super.onPause();
        boardService.unregister();
        gameService.unregister();
    }

//    @Override
//    public void onBackPressed() {
//
//    }

    @Override
    public void onUpdate(Pixel pixel) {
        Log.d("GameActivity", "Pixel Update " + pixel);
        updateButton(pixel);
    }

    @Override
    public void onGameOver() {

        Log.d("DEBUG Game over", "onGameOvercalled");
       /*Toast toast=  Toast.makeText(GameActivity.this, "The game is over!",
                Toast.LENGTH_LONG);
       toast.show();

        Intent intent = new Intent(GameActivity.this, GameEndActivity.class);
        intent.putExtra("board", boardService.getBoard().getPixels());
        startActivity(intent);*/
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GameActivity.this);
        dialogBuilder.setTitle("GAME IS OVER!");
        dialogBuilder.setMessage("The Game is over.");
        dialogBuilder.setPositiveButton("See statistics", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(GameActivity.this, GameEndActivity.class);
                intent.putExtra("board", boardService.getBoard().getPixels());
                startActivity(intent);
            }
        });
        /*dialogBuilder.setNegativeButton("Back to Main Menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(GameActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });*/

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    public void onGamePlayerChanged(GamePlayer gamePlayer) {

    }

    @Override
    public void onClick(View view) {
        String tag = (String) view.getTag();

        String[] split = tag.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);

        Log.d("Click", "On view (" + x + "," + y + ")");
        this.boardService.setPixel(x, y, new ServiceCallback<Pixel>() {
            @Override
            public void success(Pixel pixel) {
                Log.d("GameActivity", "Successfully set pixel " + pixel.toString());
                updateButton(pixel);

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
            }
        });
    }

    private ServiceCallback<Pixel> customCallback = new ServiceCallback<Pixel>() {
        @Override
        public void success(Pixel pixel) {
            Log.d("GameActivity", "Successfully changed pixel " + pixel.toString());
            updateButton(pixel);

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
                updateBombView(-1);
                Log.d("GameService", "Activated bomb for next click");
            } else {
                boardService.deactivateBombForNextClick();
                updateBombView(1);
            }
        }
    }

    private void updateBombView(int amount) {
        bombCharges += amount;
        if (bombCharges < 0) bombCharges = 0;

        bombCounterView.setText("x" + bombCharges);
    }

    private void updateBoard() {
        for (int x = 0; x < this.boardService.getBoard().getWidth(); x++) {
            for (int y = 0; y < this.boardService.getBoard().getHeight(); y++) {
                Pixel pixel = this.boardService.getBoard().getPixels().get(x).get(y);
                Log.d("GameActivity", "Pixel: " + pixel);
                updateButton(pixel);
            }
        }
    }

    private void updateButton(Pixel pixel) {
        Team team = pixel.getTeam();
        Team playerTeam = Pixelfighter.getInstance().getTeam();
        Button button = this.buttons.get(pixel.getY()).get(pixel.getX());
        switch (team) {
            case Red:
                button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.btn_red)));
                break;
            case Blue:
                button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.btn_blue)));
                break;
            case Green:
                button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.btn_green)));
                break;
            case Yellow:
                button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.btn_yellow)));
                break;
            default:
                button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.btn_none)));
                break;
        }

        if (pixel.getPixelMod() != PixelModification.None && team.equals(playerTeam)) {
            button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bomb, 0, 0, 0);
        } else if (pixel.getPixelMod() != PixelModification.None && team.equals(Team.None)) {
            button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bomb_transparent, 0, 0, 0);
        }
    }
}
