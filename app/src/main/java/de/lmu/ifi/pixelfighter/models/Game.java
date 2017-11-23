package de.lmu.ifi.pixelfighter.models;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lmu.ifi.pixelfighter.models.callbacks.Callback;
import de.lmu.ifi.pixelfighter.models.callbacks.GameCallback;
import de.lmu.ifi.pixelfighter.services.firebase.GameService;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;

/**
 * Created by michael on 23.11.17.
 */

public class Game extends BaseKeyModel {

    private Board board = new Board();
    private Map<String, List<Player>> players = new HashMap<>();
    private boolean isActive = true;
    private String startTime;
    private String endTime;

    public Game() {
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Map<String, List<Player>> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, List<Player>> players) {
        this.players = players;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public DateTime getTimeRemaining() {
        DateTime now = DateTime.now(DateTimeZone.UTC);
        DateTime end = new DateTime(endTime);
        long remainingMillis = end.getMillis() - now.getMillis();
        DateTime remaining = new DateTime(remainingMillis);
        return remaining;
    }
}
