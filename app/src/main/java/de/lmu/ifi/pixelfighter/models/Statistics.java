package de.lmu.ifi.pixelfighter.models;

import java.util.ArrayList;

/**
 * Created by F on 09.01.2018.
 */

public class Statistics {

    Board board;
    ArrayList<ArrayList<Pixel>> pixels;


    public Statistics(Board board) {
        this.board = board;
    }

    public int[] getStats(Board board) {
        pixels = board.getPixels();
        int none = 0; //index 0
        int red = 0; //index 1
        int blue = 0; //index 2
        int green = 0; //index 3
        int yellow = 0; //index 4
        int [] stats = {1,1,1,1,1};

        for (int i=0; i<pixels.size(); i++) {
            for (int j=0; j<pixels.size(); j++){
                switch (pixels.get(i).get(j).getTeam()) {
                    case None:
                        none = none + 1;
                        stats[0] = none;
                        break;
                    case Red:
                        red = red +1;
                        stats[1] = red;
                        break;
                    case Blue:
                        blue++;
                        stats[2] = blue;
                        break;
                    case Green:
                        green++;
                        stats[3] = green;
                        break;
                    case Yellow:
                        yellow++;
                        stats[4] = yellow;
                        break;
                }

            }
        }

        return stats;
    }
}
