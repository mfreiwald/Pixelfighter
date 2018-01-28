package de.lmu.ifi.pixelfighter.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 17.01.18.
 */

public class GamePlayer extends BaseModel {
    private String playerKey;
    private Map<String, Integer> modificationAmount = new HashMap<>();


    public GamePlayer() {
        modificationAmount.put(PixelModification.Bomb.name(), 0);
        modificationAmount.put(PixelModification.Protection.name(), 0);
    }

    public String getPlayerKey() {
        return playerKey;
    }

    public void setPlayerKey(String playerKey) {
        this.playerKey = playerKey;
    }

    public Map<String, Integer> getModificationAmount() {
        return modificationAmount;
    }

    public void setModificationAmount(Map<String, Integer> modificationAmount) {
        this.modificationAmount = modificationAmount;
    }
}
