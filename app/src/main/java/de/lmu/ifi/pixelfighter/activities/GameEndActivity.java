package de.lmu.ifi.pixelfighter.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;

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
    int[] stats = new int[5];
    DatabaseReference dbRootRef;
    //ArrayList<Pixel> pixelList = new ArrayList<>();
    ArrayList<ArrayList<Pixel>> pixelDoublelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        board = Pixelfighter.getInstance().getGame().getBoard();
        Intent intent = getIntent();
        String key = intent.getStringExtra("gamekey");
        //pixelList = getPixellist(key);
        //Log.d(TAG, pixelList.toString());
        getPixellist(key);
        Log.d(TAG, pixelDoublelist.toString());

        teamWon = (TextView) findViewById(R.id.teamWonTextview);
        blueText = (TextView) findViewById(R.id.blue);
        redText = (TextView) findViewById(R.id.red);
        greenText = (TextView) findViewById(R.id.green);
        yellowText = (TextView) findViewById(R.id.yellow);



        //getPixellist(key);
        //getStats(pixelDoublelist);

        //teamWon.setText(setWinner(index));

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

    public void init() {

    }

    public void getPixellist(String key){

        pixelDoublelist = this.board.getPixels();
        Log.d(TAG + "Board", pixelDoublelist.toString());

        getStats(pixelDoublelist);

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
            Log.d("Teams: ", Integer.toString(red) + Integer.toString(blue) + Integer.toString(green) + Integer.toString(yellow));
            getHighest();

        } catch (RuntimeException r) {
            System.out.print(r);
        }


    }

    private void getHighest() {
        int []stats = {0,red, blue, green, yellow};
        //gibt den Index des Teams zurück, welches die meisten Pixel gefüllt hat, NICHT die Anzahl der meisten Pixel
        int wonIndex = 0;
        int highest = 0;

        for(int i=0;i<stats.length;i++){
            if(stats[i]>highest){
                highest=stats[i];
                wonIndex = i;
            }
        }
        setWinner(wonIndex);
    }

    private String setWinner(int winIndex){
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
        return winner;
    }
}
