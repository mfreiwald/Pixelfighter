package de.lmu.ifi.pixelfighter.demo.demo3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 21.11.17.
 */

public class Team {

    public String teamName;
    public List<String> players = new ArrayList<>();

    public Team() {
    }

    private Team(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public void addPlayer(String player) {
        players.add(player);
    }

    public static Team CREATE_TEAM_RED() {
        return new Team("RED");
    }

    public static Team CREATE_TEAM_GREEN() {
        return new Team("GREEN");
    }
}
