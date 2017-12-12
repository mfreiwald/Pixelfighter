package de.lmu.ifi.pixelfighter.models;

import com.google.firebase.database.Exclude;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by michael on 23.11.17.
 */

public class Game extends BaseKeyModel {

    private Board board = new Board();
    private Map<String, List<String>> players = new HashMap<>();
    private boolean isActive = true;
    private String startTime;
    private String endTime;

    public Game() {
        this.players.put(Team.Blue.name(), new ArrayList<String>());
        this.players.put(Team.Green.name(), new ArrayList<String>());
        this.players.put(Team.Red.name(), new ArrayList<String>());
        this.players.put(Team.Yellow.name(), new ArrayList<String>());
    }

    public Game(Board board) {
        this();
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Map<String, List<String>> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, List<String>> players) {
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

    @Exclude
    public DateTime getTimeRemaining() {
        DateTime now = DateTime.now(DateTimeZone.UTC);
        DateTime end = new DateTime(endTime);
        long remainingMillis = end.getMillis() - now.getMillis();
        DateTime remaining = new DateTime(remainingMillis);
        return remaining;
    }

    @Override
    public String toString() {
        return "Game{" +
                "board=" + board +
                ", players=" + players +
                ", isActive=" + isActive +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
