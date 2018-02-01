package de.lmu.ifi.pixelfighter.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import butterknife.ButterKnife;
import butterknife.OnClick;
import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.UserData;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;
import de.lmu.ifi.pixelfighter.services.firebase.Database;

public class StatisticsActivity extends AppCompatActivity {

    final String TAG = "StatisticsActivity";

    TextView gamesView;
    TextView myScore;
    TextView wonGames;

    UserData userData;

    int gamesCount;
    int score;
    int gamesWon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        ButterKnife.bind(this);

        userData = Pixelfighter.getInstance().getUserData();

        gamesView = (TextView) findViewById(R.id.gamesTextView);
        myScore = (TextView) findViewById(R.id.statsScore);
        wonGames = (TextView) findViewById(R.id.wonTextView);

        getStats();

    }

    @OnClick(R.id.button_reset)
    public void onClickReset() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);

        builder.setTitle("Delete statistics")
                .setMessage("Are you sure you want to delete your statistics?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        userData.setGames(0);
                        userData.setScore(0);
                        userData.setWon(0);
                        Database.UserData(userData.getUid()).setValue(userData);
                        Pixelfighter.getInstance().setUserData(userData);
                        getStats();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void getStats() {
        Log.d("getStats", "UserData: " + userData.getGameKey());

        gamesCount = userData.getGames();
        if (gamesCount!=0) {
            String gamesText = "Games played: " + String.valueOf(gamesCount);
            gamesView.setText(gamesText);
        } else {
            gamesView.setText("You haven't played any games yet.");
        }

        gamesWon = userData.getWon();
        String gamesText = "Games won: " + String.valueOf(gamesWon);
        wonGames.setText(gamesText);

        score = userData.getScore();
        String scoreText = "Score: " + String.valueOf(score);
        myScore.setText(scoreText);
    }
}
