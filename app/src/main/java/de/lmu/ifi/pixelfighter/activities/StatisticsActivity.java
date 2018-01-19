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

        String gamesStr = "Played games: " + String.valueOf(games);
        String scoreStr = myScore.getText() + String.valueOf(score);

        if (games == 0) {
            gamesCount.setText("You haven't played any games yet.");
        } else {
            gamesCount.setText(gamesStr);
        }

        myScore.setText(scoreStr);

    }

    @OnClick(R.id.button_reset)
    public void onClickReset() {
        //Delete Statistics from Firebase
    }

    public void getStats() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference statsRef = rootRef.child("users").child(user.getUid()).child("stats");

        statsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Statistics statistics = dataSnapshot.getValue(Statistics.class);
                if (statistics== null) {
                    Log.d(TAG, "statistics is null");
                    return;
                } else {
                    games = statistics.getGamesCount();
                    score = statistics.getScore();
                    Log.d(TAG,": games: " + games);
                    Log.d(TAG,": score: " + score);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
