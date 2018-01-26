package de.lmu.ifi.pixelfighter.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.Statistics;
import de.lmu.ifi.pixelfighter.services.firebase.Database;

public class StatisticsActivity extends AppCompatActivity {

    final String TAG = "StatisticsActivity";

    TextView gamesView;
    TextView myScore;
    TextView wonGames;
    FirebaseUser user;
    DatabaseReference rootRef;
    DatabaseReference gamesRef;
    DatabaseReference scoreRef;
    DatabaseReference wonRef;

    int gamesCount;
    int score;
    int gamesWon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        ButterKnife.bind(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        gamesRef = rootRef.child("games");
        scoreRef = rootRef.child("score");
        wonRef = rootRef.child("won");

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
                        gamesRef.setValue(0);
                        scoreRef.setValue(0);
                        wonRef.setValue(0);
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

        gamesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String gamesStr = dataSnapshot.getValue().toString();
                    gamesCount = Integer.parseInt(gamesStr);
                    String gamesText = "You played " + String.valueOf(gamesCount);
                    gamesView.setText(gamesText);
                } else {
                    gamesView.setText("You haven't played any games yet.");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {            }
        });

        wonRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String wonStr = dataSnapshot.getValue().toString();
                    gamesWon = Integer.parseInt(wonStr);
                    String gamesText = "games and won " + String.valueOf(gamesWon) + ".";
                    wonGames.setText(gamesText);
                } else {
                    wonGames.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {            }
        });


        scoreRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String scoreStr = dataSnapshot.getValue().toString();
                    score = Integer.parseInt(scoreStr);
                    String scoreText = "Score: " + String.valueOf(score);
                    myScore.setText(scoreText);
                    Log.d("DEBUG STATS", scoreStr );
                } else {
                    myScore.setText("Score: 0");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {      }
        });
    }
}
