package de.lmu.ifi.pixelfighter.models;

/**
 * Created by michael on 23.11.17.
 */

public class Pixel {

    private Team team;
    private String playerKey;
    private int x;
    private int y;

    public Pixel() {
    }

    public Pixel(int x, int y) {
        this.x = x;
        this.y = y;
        this.team = Team.None;
        this.playerKey = "";
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getPlayerKey() {
        return playerKey;
    }

    public void setPlayerKey(String playerKey) {
        this.playerKey = playerKey;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Pixel{" +
                "team=" + team +
                ", playerKey='" + playerKey + '\'' +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
