package de.lmu.ifi.pixelfighter.demo;

/**
 * Created by michael on 20.11.17.
 */

public class Field {

    private final int x;
    private final int y;
    private PixelType type = PixelType.GRAY;


    public Field(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean setType(PixelType type) {
        if(this.type == PixelType.GRAY) {
            this.type = type;
            return true;
        }
        return false;
    }

    public PixelType getType() {
        return type;
    }
}


