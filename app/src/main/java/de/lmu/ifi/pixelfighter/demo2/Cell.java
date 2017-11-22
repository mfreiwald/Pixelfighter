package de.lmu.ifi.pixelfighter.demo2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.demo.PixelType;

/**
 * Created by michael on 20.11.17.
 */

public class Cell extends GameObject {

    private PixelType type = PixelType.GRAY;

    public Cell(GameView gameView, int x, int y) {
        super(gameView,
                BitmapFactory.decodeResource(gameView.getContext().getResources(),
                        R.drawable.gray),
                x,
                y,
                50,
                50);
    }

    public void set(PixelType type) {
        if(this.type == PixelType.GRAY)
            this.type = type;
    }
}
