package de.lmu.ifi.pixelfighter.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.services.android.Singleton;
import de.lmu.ifi.pixelfighter.services.firebase.BoardService;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.UpdateCallback;

public class GameActivity extends AppCompatActivity implements UpdateCallback<Pixel>, View.OnClickListener {


    private BoardService boardService;

    private ArrayList<ArrayList<Button>> buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2);

        this.boardService = new BoardService(Singleton.getInstance().getGame(), this);
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
        updateBoard();
    }

    @Override
    protected void onPause() {
        super.onPause();
        boardService.unregister();
    }

//    @Override
//    public void onBackPressed() {
//
//    }

    @Override
    public void onUpdate(Pixel pixel) {
        Log.d("GameActivity", "Pixel Update " + pixel);
        updateButton(pixel.getTeam(), pixel.getX(), pixel.getY());
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
                updateButton(pixel.getTeam(), pixel.getX(), pixel.getY());

                //Die Umgebung auf Gegner überprüfen, die umgefärbt werden müssen
                Log.d("GameActivity", "Running enemy check now");
                runEnemyCheck(pixel.getX(), pixel.getY());
            }

            @Override
            public void failure(String message) {
                Log.d("GameActivity", message);
            }
        });


    }

    private void runEnemyCheck(int x, int y) {
        this.boardService.runEnemyCheck(x, y, new ServiceCallback<List<Pixel>>() {
            @Override
            public void success(List<Pixel> updateList) {
                Log.d("GameActivity", "Successfully ran enemy check. Amount of pixels to update: " + updateList.size());
                for (Pixel pixel : updateList) {
                    updateButton(pixel.getTeam(), pixel.getX(), pixel.getY());
                }
            }

            @Override
            public void failure(String message) {
                Log.d("GameActivity", message);
            }
        });
    }

    private void updateBoard() {
        for (int x = 0; x < this.boardService.getBoard().getWidth(); x++) {
            for (int y = 0; y < this.boardService.getBoard().getHeight(); y++) {
                Pixel pixel = this.boardService.getBoard().getPixels().get(x).get(y);
                Log.d("GameActivity", "Pixel: " + pixel);
                updateButton(pixel.getTeam(), pixel.getX(), pixel.getY());
            }
        }
    }

    private void updateButton(Team team, int x, int y) {
        Button button = this.buttons.get(y).get(x);
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
    }
}
