package de.lmu.ifi.pixelfighter.models;

import java.io.Serializable;

/**
 * Created by michael on 23.11.17.
 */

public class Pixel implements Serializable{

    private boolean invalid = false;
    private Team team;
    private String playerKey;
    private int x;
    private int y;
    private PixelModification pixelMod;

    public Pixel() {
    }

    public Pixel(int x, int y) {
        this.x = x;
        this.y = y;
        this.team = Team.None;
        this.playerKey = "";
        this.pixelMod = PixelModification.None;
    }

    public Pixel(int x, int y, boolean invalid) {
        this(x, y);
        this.invalid = invalid;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
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

    public PixelModification getPixelMod() {
        return pixelMod;
    }

    public void setPixelMod(PixelModification pixelMod) {
        this.pixelMod = pixelMod;
    }

    @Override
    public String toString() {
        return "Pixel{" +
                "team=" + team +
                ", playerKey='" + playerKey + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", PixelMod=" + pixelMod +
                '}';
    }
}
