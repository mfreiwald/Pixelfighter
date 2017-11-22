package de.lmu.ifi.pixelfighter.demo;

/**
 * Created by michael on 20.11.17.
 */

public class Player {

    private final PixelType pixelType;
    private final String name;

    public Player(PixelType pixelType, String name) {
        this.pixelType = pixelType;
        this.name = name;
    }

    public PixelType getPixelType() {
        return pixelType;
    }

    public String getName() {
        return name;
    }
}
