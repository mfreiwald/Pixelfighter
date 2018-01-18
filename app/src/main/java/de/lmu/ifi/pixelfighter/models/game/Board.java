package de.lmu.ifi.pixelfighter.models.game;

import java.util.ArrayList;

import de.lmu.ifi.pixelfighter.models.Pixel;

/**
 * Created by michael on 17.01.18.
 */

public class Board {

    private int width;
    private int height;
    private ArrayList<ArrayList<Pixel>> pixels;

    public Board() {
        this(0, 0);
    }

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        reset(width, height);
    }

    private void reset(int size_x, int size_y) {
        pixels = new ArrayList<>();
        for(int x=0; x<size_x; x++) {
            ArrayList<Pixel> column = new ArrayList();
            for(int y=0; y<size_y; y++) {
                column.add(new Pixel(x, y));
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

    @Override
    public String toString() {
        return "Board{" +
                "width=" + width +
                ", height=" + height +
                ", pixels=" + pixels +
                '}';
    }
}
