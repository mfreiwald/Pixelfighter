package de.lmu.ifi.pixelfighter.models;

import java.util.ArrayList;

/**
 * Created by michael on 23.11.17.
 */

public class Board extends BaseModel {

    private static int[][] WORLD = {
            {0,0,1,1,1,1,1,1,0,0,0,1,1,0,1,1,1,0,0,0},
            {0,0,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0},
            {0,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0},
            {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
            {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0},
            {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
            {0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0},
            {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0},
            {0,0,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,0,0},
            {0,0,1,1,1,1,0,1,1,0,0,0,1,1,1,1,1,1,1,0},
            {1,1,1,1,1,1,0,1,1,0,0,0,1,1,1,1,1,1,0,0}
    };

    private int width;
    private int height;
    private ArrayList<ArrayList<Pixel>> pixels;

    public Board() {
        this.width = WORLD[0].length;
        this.height = WORLD.length;
        reset(width, height);
    }

    private void reset(int size_x, int size_y) {
        pixels = new ArrayList<>();
        for(int x=0; x<size_x; x++) {
            ArrayList<Pixel> column = new ArrayList();
            for(int y=0; y<size_y; y++) {
                int a = WORLD[y][x];
                if(a == 0) {
                    column.add(new Pixel(x, y, true));
                } else {
                    column.add(new Pixel(x, y, false));
                }
            }
            pixels.add(column);
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public ArrayList<ArrayList<Pixel>> getPixels() {
        return pixels;
    }

    public void setPixels(ArrayList<ArrayList<Pixel>> pixels) {
        this.pixels = pixels;
    }
}
