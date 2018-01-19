package de.lmu.ifi.pixelfighter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;
import de.lmu.ifi.pixelfighter.services.firebase.Database;
import de.lmu.ifi.pixelfighter.services.firebase.GenericReference;

public class GameEndActivity extends AppCompatActivity {

    final String TAG = "GameEndActiv";

    TextView teamWon;
    TextView blueText;
    TextView redText;
    TextView greenText;
    TextView yellowText;
    Button btnMain;
    String winner;

    int red;
    int blue;
    int green;
    int yellow;
    int index;


    Board board;
    ArrayList<ArrayList<Pixel>> pixelDoublelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        Database.Game(Pixelfighter.getInstance().getUserData().getGameKey()).Board().addSingleListener(new GenericReference.ValueListener<Board>() {
            @Override
            public void onData(Board object) {
                board = object;

                init();

                teamWon = (TextView) findViewById(R.id.teamWonTextview);
                blueText = (TextView) findViewById(R.id.blue);
                redText = (TextView) findViewById(R.id.red);
                greenText = (TextView) findViewById(R.id.green);
                yellowText = (TextView) findViewById(R.id.yellow);


                teamWon.setText(winner);
                String redStr = " Team Red filled " + String.valueOf(red) + " pixel(s). ";
                String blueStr = " Team Blue filled " + String.valueOf(blue) + " pixel(s). ";
                String greenStr = " Team Green filled " + String.valueOf(green) + " pixel(s). ";
                String yellowStr = " Team Yellow filled " + String.valueOf(yellow) + " pixel(s). ";

                redText.setText(redStr);
                blueText.setText(blueStr);
                greenText.setText(greenStr);
                yellowText.setText(yellowStr);

                btnMain = (Button) findViewById(R.id.btnMainMenu);
                btnMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentDesc = new Intent(GameEndActivity.this, MenuActivity.class);
                        startActivity(intentDesc);

                    }
                });
            }

            @Override
            public void onError(GenericReference.Error error) {

            }
        });

    }

    public void init() {
        Intent intent = getIntent();
        String key = intent.getStringExtra("gamekey");
        getPixellist(key);
        getStats(pixelDoublelist);
        getHighest();
        setWinner(index);

    }

    public void getPixellist(String key){
        pixelDoublelist = this.board.getPixels();
        Log.d(TAG + "Board", pixelDoublelist.toString());
    }

    public void getStats(ArrayList<ArrayList<Pixel>> pixels) {
        try {
            for (int i=0; i<pixels.size(); i++) {
                for (int j= 0; j<pixels.size(); j++) {
                    Log.d("Statistics", pixels.get(i).get(j).getTeam().toString());
                    switch (pixels.get(i).get(j).getTeam()) {
                        case None:
                            break;
                        case Red:
                            red = red + 1;
                            break;
                        case Blue:
                            blue++;
                            break;
                        case Green:
                            green++;
                            break;
                        case Yellow:
                            yellow++;
                            break;
                    }
                }
            }

        } catch (RuntimeException r) {
            System.out.print(r);
        }


    }

    private void getHighest() {
        int []stats = {0,red, blue, green, yellow};
        //gibt den Index des Teams zurück, welches die meisten Pixel gefüllt hat, NICHT die Anzahl der meisten Pixel
        int highest = 0;

        for(int i=0;i<stats.length;i++){
            if(stats[i]>highest){
                highest=stats[i];
                index = i;
            }
        }
    }

    private void setWinner(int winIndex){
        winner = "The winner is...";
        switch (winIndex) {
            case 0:
                winner = "There is no winner...try again.";
                break;
            case 1:
                winner = "The winner is RED!";
                break;
            case 2:
                winner = "The winner is BLUE!";
                break;
            case 3:
                winner = "The winner is GREEN!";
                break;
            case 4:
                winner = "The winner is YELLOW!";
                break;
        }
    }
}
