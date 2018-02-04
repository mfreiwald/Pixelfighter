package de.lmu.ifi.pixelfighter.activities.game;

import android.graphics.Color;

import de.lmu.ifi.pixelfighter.DefaultApp;
import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.Team;

/**
 * Created by michael on 16.01.18.
 */

public class PendingClick {
    private int x;
    private int y;
    private int color;

    public PendingClick(int x, int y, Team team) {
        this.x = x;
        this.y = y;
        this.color = getTeamPendingColor(team);
    }

    static int getTeamPendingColor(Team team) {
        if (team == null) {
            return Color.BLACK;
        }
        switch (team) {
            case Red:
                return DefaultApp.getAppContext().getColor(R.color.btn_red_pending);
            case Blue:
                return DefaultApp.getAppContext().getColor(R.color.btn_blue_pending);
            case Yellow:
                return DefaultApp.getAppContext().getColor(R.color.btn_yellow_pending);
            case Green:
                return DefaultApp.getAppContext().getColor(R.color.btn_green_pending);
            default:
                return Color.GRAY;
        }
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
}
