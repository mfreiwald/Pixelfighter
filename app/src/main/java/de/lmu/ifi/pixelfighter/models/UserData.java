package de.lmu.ifi.pixelfighter.models;

/**
 * Created by michael on 18.01.18.
 */

public class UserData {

    private String uid;
    private String username;
    private String gameKey;
    private int games;
    private int won;
    private int score;

    public UserData() {
    }

    public UserData(String uid, String username, Integer score, Integer games, Integer won) {
        this.uid = uid;
        this.username = username;
        this.gameKey = "";
        this.score = score;
        this.games = games;
        this.won = won;

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGameKey() {
        return gameKey;
    }

    public void setGameKey(String gameKey) {
        this.gameKey = gameKey;
    }

    public int getGames() {
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }

    public int getWon() {
        return won;
    }

    public void setWon(int won) {
        this.won = won;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", games='" + games + '\'' +
                ", score='" + score + '\'' +
                ", won='" + won + '\'' +
                ", gameKey='" + gameKey + '\'' +
                '}';
    }
}
