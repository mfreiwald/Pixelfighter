package de.lmu.ifi.pixelfighter.utils;

import android.app.Activity;
import android.content.Intent;

import de.lmu.ifi.pixelfighter.activities.MenuActivity;
import de.lmu.ifi.pixelfighter.activities.RegisterActivity;
import de.lmu.ifi.pixelfighter.activities.ZoomableGameActivity;
import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;
import de.lmu.ifi.pixelfighter.services.android.Settings;

/**
 * Created by michael on 16.01.18.
 */

public class StartActivityHelper {

    private Activity activity;

    public StartActivityHelper(Activity activity) {
        this.activity = activity;
    }

    public static StartActivityHelper start(Activity activity) {
        return new StartActivityHelper(activity);
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
    }

    public void registerActivity() {
        startActivity(RegisterActivity.class);
    }

    public void menuActivity() {
        startActivity(MenuActivity.class);
    }

    public void gameActivity(Game game) {
        if(game == null) return;
        Settings settings = new Settings();
        settings.setActiveGameKey(game.getKey());
        Pixelfighter.getInstance().setGame(game);
        startActivity(ZoomableGameActivity.class);
    }

}
