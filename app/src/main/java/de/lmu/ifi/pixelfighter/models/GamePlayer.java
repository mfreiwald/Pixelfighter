package de.lmu.ifi.pixelfighter.models;

/**
 * Created by michael on 17.01.18.
 */

public class GamePlayer extends BaseModel {
    private String playerKey;
    private int bombAmount = 0;

    public GamePlayer() {
    }

    public String getPlayerKey() {
        return playerKey;
    }

    public void setPlayerKey(String playerKey) {
        this.playerKey = playerKey;
    }

    public int getBombAmount() {
        return bombAmount;
    }

    public void setBombAmount(int bombAmount) {
        if(bombAmount < 0) bombAmount = 0;
        this.bombAmount = bombAmount;
    }
}
