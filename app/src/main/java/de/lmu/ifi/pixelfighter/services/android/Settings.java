package de.lmu.ifi.pixelfighter.services.android;

import android.content.Context;
import android.content.SharedPreferences;

import de.lmu.ifi.pixelfighter.DefaultApp;

/**
 * Created by michael on 23.11.17.
 */

public class Settings {

    private String playerKey;
    private String activeGameKey;

    public Settings() {
        SharedPreferences sharedPref = DefaultApp.getAppContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        this.playerKey = sharedPref.getString("playerKey", "");
        this.activeGameKey = sharedPref.getString("activeGameKey", "");
    }

    public String getPlayerKey() {
        return playerKey;
    }

    public void setPlayerKey(String playerKey) {
        this.playerKey = playerKey;
        save();
    }

    public String getActiveGameKey() {
        return activeGameKey;
    }

    public void setActiveGameKey(String activeGameKey) {
        this.activeGameKey = activeGameKey;
        save();
    }

    public void save() {
        SharedPreferences sharedPref = DefaultApp.getAppContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("playerKey", this.getPlayerKey());
        editor.putString("activeGameKey", this.getActiveGameKey());
        editor.commit();
    }

}
