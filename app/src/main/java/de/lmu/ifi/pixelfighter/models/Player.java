package de.lmu.ifi.pixelfighter.models;

/**
 * Created by michael on 23.11.17.
 */

public class Player extends BaseKeyModel {

    private String username;

    public Player() {
    }

    public Player(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
