package de.lmu.ifi.pixelfighter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import butterknife.ButterKnife;
import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.game.RandomTeam;
import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.models.callbacks.Callback;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;
import de.lmu.ifi.pixelfighter.services.firebase.GamesService;
import de.lmu.ifi.pixelfighter.utils.StartActivityHelper;

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


        GamesService.getInstance().searchAndJoinGame(Pixelfighter.getInstance().getUserData().getUid(), selectedTeam, new Callback<Game>() {
            @Override
            public void onLoaded(Game game) {
                Log.d("Toast", "Your are playing now on Game " + game.getKey());
                StartActivityHelper.start(ChooseTeamActivity.this).gameActivity(game.getKey());
            }

            @Override
            public void onError(String message) {
                Log.d("Toast", "Error: " + message);
            }
        });
    }
}
