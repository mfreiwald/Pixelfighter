package de.lmu.ifi.pixelfighter.demo;

/**
 * Created by michael on 20.11.17.
 */

public class Gameplay {

    private Board board;
    private Player[] players;


    public void setPixel(Player player, int x, int y) {
        Field field = board.getField(x, y);

        if(field.getType() != PixelType.GRAY) {
            // field already set
            return;
        }

        field.setType(player.getPixelType());

    }

}
