package de.lmu.ifi.pixelfighter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.models.GamePlayer;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.models.UserData;
import de.lmu.ifi.pixelfighter.models.callbacks.GameCallback;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;
import de.lmu.ifi.pixelfighter.services.firebase.GamesService;
import de.lmu.ifi.pixelfighter.utils.StartActivityHelper;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_desc)
    public void onClickDescription() {
        Intent intentDesc = new Intent(MenuActivity.this, OnboardingActivity.class);
        startActivity(intentDesc);
    }

    @OnClick(R.id.button_stats)
    public void onClickStats() {
        Intent intentStats = new Intent(MenuActivity.this, StatisticsActivity.class);
        startActivity(intentStats);
    }

    @OnClick(R.id.button_game)
    public void onClickGame() {


        UserData userData = Pixelfighter.getInstance().getUserData();
        String gameKey = userData.getGameKey();

        if(gameKey == null || gameKey.isEmpty()) {
            // ToDo: choose team!

            Intent intent = new Intent(MenuActivity.this, ChooseTeamActivity.class);
            startActivity(intent);

        } else {
            GamesService.getInstance().loadGame(gameKey, new GameCallback() {
                @Override
                public void onClosed() {
                    Log.d("Toast", "Your Game was already closed");
                    // Search for new game?
                    Intent intent = new Intent(MenuActivity.this, ChooseTeamActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onModelNotExists() {
                    Intent intent = new Intent(MenuActivity.this, ChooseTeamActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onLoaded(Game game) {
                    Log.d("Toast", "You loaded Game " + game.getKey());

                    for(Map.Entry<String, Map<String, GamePlayer>> team : game.getPlayers().entrySet()) {
                        if(team.getValue().containsKey(Pixelfighter.getInstance().getUserData().getUid())) {
                            Pixelfighter.getInstance().setTeam(Team.valueOf(team.getKey()));
                            break;
                        }
                    }


                    StartActivityHelper.start(MenuActivity.this).gameActivity(game.getKey());

                }

                @Override
                public void onError(String message) {
                    Toast.makeText(MenuActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();

                }
            });
        }
    }


}
