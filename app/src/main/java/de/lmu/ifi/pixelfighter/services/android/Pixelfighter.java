package de.lmu.ifi.pixelfighter.services.android;

import android.graphics.Color;

import de.lmu.ifi.pixelfighter.DefaultApp;
import de.lmu.ifi.pixelfighter.R;
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

    private boolean useDark = false;

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

    public boolean isUseDark() {
        return useDark;
    }

    public void setUseDark(boolean useDark) {
        this.useDark = useDark;
    }

    public int getTeamColor(Team team) {
        if(useDark) {
            switch(team) {
                case Red: return DefaultApp.getAppContext().getColor(R.color.btn_red_dark);
                case Blue: return DefaultApp.getAppContext().getColor(R.color.btn_blue_dark);
                case Yellow: return DefaultApp.getAppContext().getColor(R.color.btn_yellow_dark);
                case Green: return DefaultApp.getAppContext().getColor(R.color.btn_green_dark);
                default: return DefaultApp.getAppContext().getColor(R.color.btn_none_dark);
            }
        } else {
            switch(team) {
                case Red: return DefaultApp.getAppContext().getColor(R.color.btn_red);
                case Blue: return DefaultApp.getAppContext().getColor(R.color.btn_blue);
                case Yellow: return DefaultApp.getAppContext().getColor(R.color.btn_yellow);
                case Green: return DefaultApp.getAppContext().getColor(R.color.btn_green);
                default: return DefaultApp.getAppContext().getColor(R.color.btn_none);
            }
        }
    }

    public int getMyTeamColor() {
        Team team = getTeam();
        if(team == null) {
            return Color.WHITE;
        }
        return getTeamColor(team);
    }

}
