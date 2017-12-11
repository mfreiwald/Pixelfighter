package de.lmu.ifi.pixelfighter.services.android;

import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.models.Team;

/**
 * Created by michael on 11.12.17.
 */

public class Singleton {

    private static Singleton INSTANCE = new Singleton();

    public static Singleton getInstance() {
        return INSTANCE;
    }

    private Game game;
    private String playerKey;
    private Team team;

    private Singleton() {

    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getPlayerKey() {
        return playerKey;
    }

    public void setPlayerKey(String playerKey) {
        this.playerKey = playerKey;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
