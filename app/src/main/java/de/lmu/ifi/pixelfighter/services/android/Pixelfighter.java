package de.lmu.ifi.pixelfighter.services.android;

import de.lmu.ifi.pixelfighter.DefaultApp;
import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.models.UserData;

/**
 * Created by michael on 11.12.17.
 */

public class Pixelfighter {

    private static Pixelfighter INSTANCE = new Pixelfighter();
    private UserData userData;
    private boolean useDark = false;

    private Pixelfighter() {

    }

    public static Pixelfighter getInstance() {
        return INSTANCE;
    }

    public boolean isUseDark() {
        return useDark;
    }

    public void setUseDark(boolean useDark) {
        this.useDark = useDark;
    }

    public int getTeamColor(Team team) {
        if (useDark) {
            switch (team) {
                case Red:
                    return DefaultApp.getAppContext().getColor(R.color.btn_red_dark);
                case Blue:
                    return DefaultApp.getAppContext().getColor(R.color.btn_blue_dark);
                case Yellow:
                    return DefaultApp.getAppContext().getColor(R.color.btn_yellow_dark);
                case Green:
                    return DefaultApp.getAppContext().getColor(R.color.btn_green_dark);
                default:
                    return DefaultApp.getAppContext().getColor(R.color.btn_none_dark);
            }
        } else {
            switch (team) {
                case Red:
                    return DefaultApp.getAppContext().getColor(R.color.btn_red);
                case Blue:
                    return DefaultApp.getAppContext().getColor(R.color.btn_blue);
                case Yellow:
                    return DefaultApp.getAppContext().getColor(R.color.btn_yellow);
                case Green:
                    return DefaultApp.getAppContext().getColor(R.color.btn_green);
                default:
                    return DefaultApp.getAppContext().getColor(R.color.btn_none);
            }
        }
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }
}
