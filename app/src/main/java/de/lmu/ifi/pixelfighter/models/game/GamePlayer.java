package de.lmu.ifi.pixelfighter.models.game;

import java.util.HashMap;
import java.util.Map;

import de.lmu.ifi.pixelfighter.models.PixelModification;
import de.lmu.ifi.pixelfighter.models.Team;

/**
 * Created by michael on 17.01.18.
 */

public class GamePlayer {

    private String playerKey;
    private Team team;
    private Map<String, Integer> weaponAmount;

    public GamePlayer() {
        weaponAmount = new HashMap<>();
        weaponAmount.put(PixelModification.Bomb.name(), 0);
    }

    public GamePlayer(String playerKey, Team team) {
        this();
        this.playerKey = playerKey;
        this.team = team;
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

    public Map<String, Integer> getWeaponAmount() {
        return weaponAmount;
    }

    public void setWeaponAmount(Map<String, Integer> weaponAmount) {
        this.weaponAmount = weaponAmount;
    }

    @Override
    public String toString() {
        return "GamePlayer{" +
                "playerKey='" + playerKey + '\'' +
                ", team=" + team +
                ", weaponAmount=" + weaponAmount +
                '}';
    }
}
