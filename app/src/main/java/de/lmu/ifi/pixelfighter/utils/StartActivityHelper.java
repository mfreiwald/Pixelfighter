package de.lmu.ifi.pixelfighter.utils;

import android.app.Activity;
import android.content.Intent;

import de.lmu.ifi.pixelfighter.activities.GameActivity;
import de.lmu.ifi.pixelfighter.activities.MenuActivity;
import de.lmu.ifi.pixelfighter.activities.RegisterActivity;
import de.lmu.ifi.pixelfighter.models.UserData;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;
import de.lmu.ifi.pixelfighter.services.firebase.Database;

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

    public void gameActivity(String gameKey) {
        UserData userData = Pixelfighter.getInstance().getUserData();
        userData.setGameKey(gameKey);
        Database.UserData(userData.getUid()).setValue(userData);

        Intent intent = new Intent(activity, GameActivity.class);
        intent.putExtra("gameKey", gameKey);
        activity.startActivity(intent);
    }

}
