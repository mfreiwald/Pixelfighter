package de.lmu.ifi.pixelfighter.services.android;

import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.models.Player;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.models.callbacks.Callback;
import de.lmu.ifi.pixelfighter.services.firebase.AuthenticationService;
import de.lmu.ifi.pixelfighter.services.firebase.GameService;

/**
 * Created by michael on 11.12.17.
 */

public class Pixelfighter {

    private static Pixelfighter INSTANCE = new Pixelfighter();

    public static Pixelfighter getInstance() {
        return INSTANCE;
    }



    private Player player;
    private Game game;
    private Team team;

    private Pixelfighter() {

    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }


}
