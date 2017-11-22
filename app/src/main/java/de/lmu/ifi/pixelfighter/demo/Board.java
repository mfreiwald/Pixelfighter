package de.lmu.ifi.pixelfighter.demo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import de.lmu.ifi.pixelfighter.demo.view.GameSurface;

/**
 * Created by michael on 20.11.17.
 */

public class Board {

    public static final int SIZE_X = 5;
    public static final int SIZE_Y = 7;

    private final Field[][] pixels = initialize_board();

    private GameSurface gameSurface;
    private Paint paint;

    public Board(GameSurface gameSurface) {
        this.gameSurface = gameSurface;
        paint = new Paint();
    }

    public void update() {

    }

    public void draw(Canvas canvas) {
        Path path = new Path();
        path.moveTo(18,10);
        path.lineTo(1, 0);
        path.lineTo(0, 20);

        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);


        canvas.drawPath(path, paint);
    }

    public Field getField(int x, int y) {
        return pixels[x][y];
    }

    private static Field[][] initialize_board() {
        Field[][] result = new Field[SIZE_X][SIZE_Y];
        for(int y=0; y<SIZE_Y; y++) {
            for(int x=0; x<SIZE_X; x++) {
                Field field = new Field(x, y);
                result[x][y] = field;
            }
        }
        return result;
    }
}
