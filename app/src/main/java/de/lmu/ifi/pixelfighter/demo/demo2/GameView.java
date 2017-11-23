package de.lmu.ifi.pixelfighter.demo.demo2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by michael on 20.11.17.
 */

public class GameView extends SurfaceView {

    private SurfaceHolder holder;
    private GameLoopThread gameLoopThread;
    private Board gameBoard;

    public GameView(Context context) {
        super(context);

        gameBoard = new Board(this, 5);
        this.gameLoopThread = new GameLoopThread(this);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                gameBoard.reset();
                gameLoopThread.setRunning(true);
                gameLoopThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = false;
                gameLoopThread.setRunning(false);
                while(retry) {
                    try {
                        gameLoopThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.BLACK);
        this.gameBoard.draw(canvas);
    }
}
