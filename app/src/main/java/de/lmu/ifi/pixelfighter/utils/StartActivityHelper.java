package de.lmu.ifi.pixelfighter.utils;

import android.app.Activity;
import android.content.Intent;

import de.lmu.ifi.pixelfighter.activities.ChooseTeamActivity;
import de.lmu.ifi.pixelfighter.activities.ZoomableGameActivity;
import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;
import de.lmu.ifi.pixelfighter.services.android.Settings;

/**
 * Created by michael on 16.01.18.
 */

public class StartActivityHelper {

    public static void startGameActivity(Activity activity, Game game) {
        if(game == null) return;
        Settings settings = new Settings();
        settings.setActiveGameKey(game.getKey());
        Pixelfighter.getInstance().setGame(game);
        Intent intent = new Intent(activity, ZoomableGameActivity.class);
        activity.startActivity(intent);
    }

}
