package de.lmu.ifi.pixelfighter.demo.demo3;

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
        if(this.getPixelList().get(y*7+x).teamName.isEmpty()) {
            this.getPixelList().get(y*7+x).teamName = teamName;
        }
    }

    public List<Pixel> getPixelList() {
        return pixelList;
    }

    public void reset() {
        int id = 0;
        for(int y=0; y<10; y++) {
            for(int x=0; x<7; x++) {
                pixelList.add(new Pixel(id, x, y));
                id++;
            }
        }
    }
}
