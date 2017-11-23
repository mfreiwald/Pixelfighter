package de.lmu.ifi.pixelfighter.demo.demo2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by michael on 20.11.17.
 */

public class GameObject {

    private GameView gameView;
    private Bitmap bitmap;
    private int x;
    private int y;
    private int width;
    private int height;

    public GameObject(GameView gameView, Bitmap bitmap, int x, int y, int width, int height) {
        this.gameView = gameView;
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void onDraw(Canvas canvas, int column, int row) {
        Rect src = new Rect((column * this.width), (row * this.height), (column * this.width) + width, (row * this.height) + height);
        Rect dst = new Rect(this.x, this.y, this.x + this.width, this.y + this.height);
        canvas.drawBitmap(this.bitmap, src, dst, null);
    }

    public boolean hasCollided(float otherX, float otherY) {
        return this.x < otherX && this.y < otherY && this.x + this.width > otherX && this.y + this.height > otherY;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
