package de.lmu.ifi.pixelfighter.activities;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.models.Statistics;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;

public class GameEndActivity extends AppCompatActivity {

    final String TAG = "GameEndActivity";

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
    ArrayList<ArrayList<Pixel>> pixels;
    int[] stats = new int[5];
    DatabaseReference dbRootRef;
    ArrayList <String> teams;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        board = Pixelfighter.getInstance().getGame().getBoard();

        teams = new ArrayList<>();
        statistics = new Statistics(teams);


        teamWon = (TextView) findViewById(R.id.teamWonTextview);
        blueText = (TextView) findViewById(R.id.blue);
        redText = (TextView) findViewById(R.id.red);
        greenText = (TextView) findViewById(R.id.green);
        yellowText = (TextView) findViewById(R.id.yellow);

        Intent intent = getIntent();
        String key = intent.getStringExtra("gamekey");
        Log.d(TAG, key);

        getTeams(key);

        stats = statistics.getStats(teams);

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

    public void getTeams(String key){
        dbRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = dbRootRef.child("games").child(key).child("board").child("pixels");


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for(int i = 0; i<board.getHeight(); i++) {
                        for (int j = 0; j<board.getWidth(); j++) {
                            teams.add(snapshot.child(Integer.toString(i)).child(Integer.toString(j)).child("team").getValue().toString());

                        }
                    }
                    Log.d("DEBUG Teams", teams.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
