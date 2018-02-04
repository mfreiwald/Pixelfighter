package de.lmu.ifi.pixelfighter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.models.UserData;
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
    TextView ownTeamWon;
    LinearLayout linTeamWon;
    Button btnMain;
    String winner;
    UserData userData;

    int red;
    int blue;
    int green;
    int yellow;
    int index;
    int color;
    int width;
    int height;

    Boolean won = false;
    String playerTeam;

    Board board;
    ArrayList<ArrayList<Pixel>> pixelDoublelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        userData = Pixelfighter.getInstance().getUserData();

        Database.Game(Pixelfighter.getInstance().getUserData().getGameKey()).Board().addSingleListener(new GenericReference.ValueListener<Board>() {
            @Override
            public void onData(Board object) {
                board = object;
                width = board.getWidth();
                height = board.getHeight();
                pixelDoublelist = board.getPixels();
                init();

                teamWon = (TextView) findViewById(R.id.teamWonTextview);
                //ownTeamWon = (TextView) findViewById(R.id.ownTeamWonTv);
                linTeamWon = (LinearLayout) findViewById(R.id.linTeamWon);
                blueText = (TextView) findViewById(R.id.blue);
                redText = (TextView) findViewById(R.id.red);
                greenText = (TextView) findViewById(R.id.green);
                yellowText = (TextView) findViewById(R.id.yellow);


                teamWon.setText(winner);
                linTeamWon.setBackgroundColor(color);
                if (won) {
                    //ownTeamWon.setVisibility(View.VISIBLE);
                }
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
        playerTeam = intent.getStringExtra("team");
        getStats(pixelDoublelist);
        getHighest();
        setWinner(index);
        saveStats(pixelDoublelist);
    }

    public void getStats(ArrayList<ArrayList<Pixel>> pixels) {
        Log.d(TAG, "getStats");
        try {
            for (int i = 0; i < width - 1; i++) {
                for (int j = 0; j < height - 1; j++) {
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
        Log.d(TAG, "getHighest");
        int[] stats = {0, red, blue, green, yellow};
        Log.d(TAG + " Stats", Integer.toString(red) + Integer.toString(blue)
                + Integer.toString(green) + Integer.toString(yellow));
        //gibt den Index des Teams zurück, welches die meisten Pixel gefüllt hat, NICHT die Anzahl der meisten Pixel
        int highest = 0;

        for (int i = 0; i < stats.length; i++) {
            if (stats[i] > highest) {
                highest = stats[i];
                index = i;
                Log.d(TAG + "Index", Integer.toString(index));

            }
        }
    }

    private void setWinner(int winIndex) {
        Log.d(TAG, "setWinner");
        String team = "None";
        winner = "The winner is...";
        switch (winIndex) {
            case 0:
                winner = "There is no winner...try again.";
                break;
            case 1:
                team = "Red";
                winner = "The winner is RED!";
                color = ContextCompat.getColor(this, R.color.btn_red);
                break;
            case 2:
                team = "Blue";
                winner = "The winner is BLUE!";
                color = ContextCompat.getColor(this, R.color.btn_blue);
                break;
            case 3:
                team = "Green";
                winner = "The winner is GREEN!";
                color = ContextCompat.getColor(this, R.color.btn_green);
                break;
            case 4:
                team = "Yellow";
                winner = "The winner is YELLOW!";
                color = ContextCompat.getColor(this, R.color.btn_yellow);
                break;
        }
        if (team.equals(playerTeam)) {
            won = true;
        }
    }

    private void saveStats(ArrayList<ArrayList<Pixel>> pixels) {
        Log.d(TAG, "saveStatsCalled");
        int score = 0;
        for (int i = 0; i < width - 1; i++) {
            for (int j = 0; j < height - 1; j++) {
                Log.d(TAG + " i", Integer.toString(i));
                Log.d(TAG + " j", Integer.toString(j));
                if (pixels.get(i).get(j).getPlayerKey().equals(userData.getUid())) {
                    score = score + 2;
                }
            }
        }
        userData.setScore(userData.getScore() + score);
        userData.setGames(userData.getGames() + 1);
        if (won) {
            userData.setWon(userData.getWon() + 1);
        }
        Database.UserData(userData.getUid()).setValue(userData);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
}
