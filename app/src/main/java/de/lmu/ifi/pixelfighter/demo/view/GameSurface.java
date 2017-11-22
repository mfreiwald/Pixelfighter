package de.lmu.ifi.pixelfighter.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import de.lmu.ifi.pixelfighter.demo.Board;

/**
 * Created by michael on 20.11.17.
 */

public class GameSurface extends SurfaceView implements Runnable{

    volatile boolean playing;
    private Thread gameThread = null;

    private Board board;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    public GameSurface(Context context) {
        super(context);

        board = new Board(this);
        surfaceHolder = getHolder();
    }



    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void update() {
        //updating player position
        board.update();
    }

    private void draw() {
        //checking if surface is valid
        if (surfaceHolder.getSurface().isValid()) {
            //locking the canvas
            canvas = surfaceHolder.lockCanvas();
            //drawing a background color for canvas
            canvas.drawColor(Color.BLUE);
            //Drawing the player
            board.draw(canvas);

            //Unlocking the canvas
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}
