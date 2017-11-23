package de.lmu.ifi.pixelfighter.demo.demo3.board;

/**
 * Created by michael on 23.11.17.
 */

public class PixelValue {

    private Board.Team team;
    private String player;

    public PixelValue() {
    }

    public PixelValue(Board.Team team, String player) {
        this.team = team;
        this.player = player;
    }

    public Board.Team getTeam() {
        return team;
    }

    public void setTeam(Board.Team team) {
        this.team = team;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }
}
