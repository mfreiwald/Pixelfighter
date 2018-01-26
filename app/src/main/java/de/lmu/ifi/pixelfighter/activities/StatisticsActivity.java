package de.lmu.ifi.pixelfighter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    TextView gamesCount;
    TextView myScore;

    int games;
    int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        ButterKnife.bind(this);

        gamesCount = (TextView) findViewById(R.id.gamesTextView);
        myScore = (TextView) findViewById(R.id.statsScore);

        getStats();

    }

    @OnClick(R.id.button_reset)
    public void onClickReset() {
        //Delete Statistics from Firebase
    }

    public void getStats() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference gamesRef = rootRef.child("users").child(user.getUid()).child("stats").child("games");

        gamesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String gamesStr = dataSnapshot.getValue().toString();
                games = Integer.parseInt(gamesStr);
                if (games == 0) {
                    gamesCount.setText("You haven't played any games yet.");
                } else {
                    String gamesText = "Played games: " + String.valueOf(games);
                    gamesCount.setText(gamesText);
                }

                Log.d("DEBUG STATS", gamesStr );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference scoreRef = rootRef.child("users").child(user.getUid()).child("stats").child("score");

        scoreRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!= null) {
                    String scoreStr = dataSnapshot.getValue().toString();
                    score = Integer.parseInt(scoreStr);
                    String scoreText = myScore.getText() + String.valueOf(score);
                    myScore.setText(scoreText);
                    Log.d("DEBUG STATS", scoreStr );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
