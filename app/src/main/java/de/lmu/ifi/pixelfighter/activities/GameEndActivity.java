package de.lmu.ifi.pixelfighter.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.Statistics;
import de.lmu.ifi.pixelfighter.services.android.Singleton;

public class GameEndActivity extends AppCompatActivity {

    TextView teamWon;
    TextView blueText;
    TextView redText;
    TextView greenText;
    TextView yellowText;
    Button btnMain;
    Statistics statistics;

    int red;
    int blue;
    int green;
    int yellow;
    int index;


    Board board;
    int[] stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        board = Singleton.getInstance().getGame().getBoard();
        stats = statistics.getStats(board);

        teamWon = (TextView) findViewById(R.id.teamWonTextview);
        blueText = (TextView) findViewById(R.id.blue);
        redText = (TextView) findViewById(R.id.red);
        greenText = (TextView) findViewById(R.id.green);
        yellowText = (TextView) findViewById(R.id.yellow);

        stats[0] = 0;
        red = stats[1];
        blue = stats[2];
        green = stats[3];
        yellow = stats[4];
        index = getHighest(stats);
        teamWon.setText(setWinner(index));

        String redStr = " Team Red filled " + String.valueOf(red) + " pixels. ";
        String blueStr = " Team Blue filled " + String.valueOf(blue) + " pixels. ";
        String greenStr = " Team Green filled " + String.valueOf(green) + " pixels. ";
        String yellowStr = " Team Yellow filled " + String.valueOf(yellow) + " pixels. ";

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

    private int getHighest(int [] stats) {
        //gibt den Index des Teams zurück, welches die meisten Pixel gefüllt hat, NICHT die Anzahl der meisten Pixel
        int wonIndex = 0;
        int highest = 0;

        for(int i=0;i<stats.length;i++){
            if(stats[i]>highest){
                highest=stats[i];
                wonIndex = i;
            }
        }
        return wonIndex;
    }

    private String setWinner(int winIndex){
        String winner = "The winner is...";
        switch (winIndex) {
            case 0:
                winner = "There is no winner...try again.";
                break;

            case 1:
                winner = "The winner ist RED!";
                break;
            case 2:
                winner = "The winner ist BLUE!";
                break;
            case 3:
                winner = "The winner ist GREEN!";
                break;
            case 4:
                winner = "The winner ist YELLOW!";
                break;
        }
        return winner;
    }
}
