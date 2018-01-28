package de.lmu.ifi.pixelfighter.activities.game;

import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.GamePlayer;
import de.lmu.ifi.pixelfighter.models.Team;

/**
 * Created by michael on 28.01.18.
 */

public class GameSettings {
    private final String gameKey;
    private final String uid;
    private Team team;
    private Board board;
    private GamePlayer gamePlayer;

    public GameSettings(String gameKey, String uid) {
        this.gameKey = gameKey;
        this.uid = uid;
    }

    public String getGameKey() {
        return gameKey;
    }

    public Board getBoard() {
        return board;
    }

    protected void setBoard(Board board) {
        this.board = board;
    }

    public Team getTeam() {
        return this.team;
    }

    protected void setTeam(Team team) {
        this.team = team;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    protected void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public String getUid() {
        return uid;
    }

    @Override
    public String toString() {
        return "GameSettings{" +
                "gameKey='" + gameKey + '\'' +
                ", uid='" + uid + '\'' +
                ", team=" + team +
                ", board=" + board +
                '}';
    }
}
