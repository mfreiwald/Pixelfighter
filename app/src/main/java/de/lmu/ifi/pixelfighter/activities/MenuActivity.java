package de.lmu.ifi.pixelfighter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.models.callbacks.GameCallback;
import de.lmu.ifi.pixelfighter.services.android.Settings;
import de.lmu.ifi.pixelfighter.services.android.Singleton;
import de.lmu.ifi.pixelfighter.services.firebase.GamesService;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_desc)
    public void onClickDescription() {
        Intent intentDesc = new Intent(MenuActivity.this, GameDescActivity.class);
        startActivity(intentDesc);
    }

    @OnClick(R.id.button_stats)
    public void onClickStats() {
        Intent intentStats = new Intent(MenuActivity.this, StatisticsActivity.class);
        startActivity(intentStats);
    }

    @OnClick(R.id.button_game)
    public void onClickGame() {

        Settings settings = new Settings();
        String gameKey = settings.getActiveGameKey();
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
                public void onLoaded(Game game) {
                    Log.d("Toast", "You loaded Game " + game.getKey());
                    Singleton.getInstance().setGame(game);

                    for(Map.Entry<String, List<String>> teams : game.getPlayers().entrySet()) {
                        if(teams.getValue().contains(Singleton.getInstance().getPlayer().getKey())) {
                            Singleton.getInstance().setTeam(Team.valueOf(teams.getKey()));
                            break;
                        }
                    }



                    Intent intent = new Intent(MenuActivity.this, GameActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onError(String message) {
                    Log.d("Toast", "Error: " + message);

                }
            });
        }
    }

}
