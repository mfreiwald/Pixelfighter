package de.lmu.ifi.pixelfighter.activities.game;

import android.graphics.Color;

import de.lmu.ifi.pixelfighter.DefaultApp;
import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;

/**
 * Created by michael on 16.01.18.
 */

public class PendingClick {
    private int x;
    private int y;
    private int color = getTeamPendingColor();

    public PendingClick(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getColor() {
        return color;
    }

    static int getTeamPendingColor() {
        Team team = Pixelfighter.getInstance().getTeam();
        if(team == null) {
            return Color.BLACK;
        }
        switch(team) {
            case Red: return DefaultApp.getAppContext().getColor(R.color.btn_red_pending);
            case Blue: return DefaultApp.getAppContext().getColor(R.color.btn_blue_pending);
            case Yellow: return DefaultApp.getAppContext().getColor(R.color.btn_yellow_pending);
            case Green: return DefaultApp.getAppContext().getColor(R.color.btn_green_pending);
            default: return Color.GRAY;
        }
    }
}
