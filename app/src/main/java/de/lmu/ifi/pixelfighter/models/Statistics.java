package de.lmu.ifi.pixelfighter.models;

/**
 * Created by F on 19.01.2018.
 */

public class Statistics {

    int gamesCount;
    int Score;

    public Statistics(int gamesCount, int score) {
        this.gamesCount = gamesCount;
        Score = score;
    }

    public int getGamesCount() {
        return gamesCount;
    }

    public void setGamesCount(int gamesCount) {
        this.gamesCount = gamesCount;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }
}
