package de.lmu.ifi.pixelfighter.demo.demo3;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 20.11.17.
 */

public class Board {

    List<Pixel> pixelList = new ArrayList<>();

    public Board() {

    }

    public void setPixel(int x, int y, String teamName) {
        if (this.getPixelList().get(y * 7 + x).teamName.isEmpty()) {
            this.getPixelList().get(y * 7 + x).teamName = teamName;
        }
    }

    public List<Pixel> getPixelList() {
        return pixelList;
    }

    public List<Pixel> getAdjacentPixelsFor(int x, int y) {
        List<Pixel> adjacentList = new ArrayList<>();

        int xN = x + 1, yN = y - 1; //Start with top right neighbor
        final int maxX = 6;
        final int maxY = 9;

        for (int i = 7; i >= 0; i--) {
            if (i > 5) {
                if ((xN <= maxX && xN >= 0) && (yN <= maxY && yN >= 0)) {
                    adjacentList.add(pixelList.get(getListPosFromCoords(xN, yN)));
                }
                yN++;
            } else if (i <= 5 && i > 3) {
                if ((xN <= maxX && xN >= 0) && (yN <= maxY && yN >= 0)) {
                    adjacentList.add(pixelList.get(getListPosFromCoords(xN, yN)));
                }
                xN--;
            } else if (i <= 3 && i > 1) {
                if ((xN <= maxX && xN >= 0) && (yN <= maxY && yN >= 0)) {
                    adjacentList.add(pixelList.get(getListPosFromCoords(xN, yN)));
                }
                yN--;
            } else if (i <= 1) {
                if ((xN <= maxX && xN >= 0) && (yN <= maxY && yN >= 0)) {
                    adjacentList.add(pixelList.get(getListPosFromCoords(xN, yN)));
                }
                xN++;
            }
        }

        Log.d("BOARD", "adj. Pixel amount: " + adjacentList.size());

        return adjacentList;
    }

    public static int getListPosFromCoords(int x, int y) {
        return (y * 7) + x;
    }

    public void reset() {
        int id = 0;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 7; x++) {
                pixelList.add(new Pixel(id, x, y));
                id++;
            }
        }
    }
}
