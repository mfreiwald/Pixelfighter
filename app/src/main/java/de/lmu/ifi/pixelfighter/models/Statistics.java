package de.lmu.ifi.pixelfighter.models;

import java.util.ArrayList;

/**
 * Created by F on 09.01.2018.
 */

public class Statistics {

    Board board;
    String teams;
    ArrayList<ArrayList<Pixel>> pixels;


    public Statistics(ArrayList<String> team) {
        this.teams = teams;
    }

    public int[] getStats(ArrayList<String> teams) {
        int none = 0; //index 0
        int red = 0; //index 1
        int blue = 0; //index 2
        int green = 0; //index 3
        int yellow = 0; //index 4
        int [] stats = {1,1,1,1,1};

        for (int i=0; i<teams.size(); i++) {
            switch (teams.get(i)) {
                case "None":
                    none = none + 1;
                    stats[0] = none;
                    break;
                case "Red":
                    red = red +1;
                    stats[1] = red;
                    break;
                case "Blue":
                    blue++;
                    stats[2] = blue;
                    break;
                case "Green":
                    green++;
                    stats[3] = green;
                    break;
                case "Yellow":
                    yellow++;
                    stats[4] = yellow;
                    break;
            }
        }

        return stats;
    }
}
