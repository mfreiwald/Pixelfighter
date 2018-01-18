package de.lmu.ifi.pixelfighter.models.game;

/**
 * Created by michael on 17.01.18.
 */

public class Game {

    private boolean active = true;

    public Game() {
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Game{" +
                "active=" + active +
                '}';
    }
}
