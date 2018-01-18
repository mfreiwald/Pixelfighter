package de.lmu.ifi.pixelfighter.models;

import de.lmu.ifi.pixelfighter.services.firebase.Database;

/**
 * Created by michael on 18.01.18.
 */

public class UserData {
    public static Class clazz = UserData.class;

    private String uid;
    private String username;
    private String gameKey;

    public UserData() {}

    public UserData(String uid, String username) {
        this.uid = uid;
        this.username = username;
        this.gameKey = "";
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGameKey() {
        return gameKey;
    }

    public void setGameKey(String gameKey) {
        this.gameKey = gameKey;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", gameKey='" + gameKey + '\'' +
                '}';
    }
}
