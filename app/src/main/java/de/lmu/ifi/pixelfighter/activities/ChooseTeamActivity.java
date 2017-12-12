package de.lmu.ifi.pixelfighter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.game.RandomTeam;
import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.services.android.Singleton;

public class ChooseTeamActivity extends AppCompatActivity {

    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_team);
        ButterKnife.bind(this);
    }

    public void selectedTeam(View view) {
        Team selectedTeam;
        switch(view.getId()) {
            case R.id.btnRandom: selectedTeam = RandomTeam.evaluateRandomTeam(); break;
            case R.id.btnRed: selectedTeam = Team.Red; break;
            case R.id.btnBlue: selectedTeam = Team.Blue; break;
            case R.id.btnGreen: selectedTeam = Team.Green; break;
            case R.id.btnYellow: selectedTeam = Team.Yellow; break;
            default: selectedTeam = RandomTeam.evaluateRandomTeam();
        }

        Singleton.getInstance().setTeam(selectedTeam);
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
