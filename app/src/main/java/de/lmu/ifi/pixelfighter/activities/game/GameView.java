package de.lmu.ifi.pixelfighter.activities.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.models.PixelModification;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;

/**
 * Created by michael on 15.01.18.
 */

public class GameView extends ZoomableSurfaceView implements Runnable {

    private static final int OFFSET = 10;

    private Board board;
    private OnClickListener onClickListener;

    private SurfaceHolder surfaceHolder;
    private boolean running;
    private Thread gameThread;

    private long startTime;
    private long endTime;
    public final int TARGET_FPS = 40;
    public long ACTUAL_FPS = 0;

    private CopyOnWriteArrayList<PendingClick> pendingClicks = new CopyOnWriteArrayList<>();

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        surfaceHolder = getHolder();
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void run() {
        Canvas canvas;

        while (running) {
            if (surfaceHolder.getSurface().isValid()) {

                canvas = surfaceHolder.lockCanvas();

                canvas.save();
                startTime = System.currentTimeMillis();

                //Log.d("GameView", "draw on canvas");

                updateFrame(canvas);

                canvas.restore();
                surfaceHolder.unlockCanvasAndPost(canvas);

                endTime = System.currentTimeMillis();
                long delta = endTime - startTime;

                if (delta < 1000) {
                    long interval = (1000 - delta) / TARGET_FPS;
                    ACTUAL_FPS = TARGET_FPS;
                    try {
                        Thread.sleep(interval);
                    } catch (Exception ex) {
                    }
                } else {
                    ACTUAL_FPS = 1000 / delta;
                }

                //Log.d("FPS", ACTUAL_FPS + " ");

            }
        }
    }


    private void updateFrame(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        if (board == null) return;

        float boxSize = calculateBoxSize();



        float offsetX = calculateOffsetX();
        float offsetY = calculateOffsetY();


        for (int x = 0; x < this.board.getWidth(); x++) {
            for (int y = 0; y < this.board.getHeight(); y++) {

                float left = OFFSET + offsetX + x * boxSize;
                float top = OFFSET + offsetY + y * boxSize;
                float right = left + boxSize;
                float bottom = top + boxSize;

                Pixel pixel = this.board.getPixels().get(x).get(y);
                Team team = pixel.getTeam();

                Paint mFillPaint = new Paint();
                Paint mStrokePaint = new Paint();
                RectF mRect = new RectF(left, top, right, bottom);
                mFillPaint.setStyle(Paint.Style.FILL);

                int color = Pixelfighter.getTeamColor(team);

                ListIterator<PendingClick> iterator = pendingClicks.listIterator();
                while(iterator.hasNext()){
                    PendingClick click = iterator.next();

                    if(click.getX() == x && click.getY() == y) {
                        color = click.getColor();
/*
                        if(color == getContext().getColor(R.color.btn_none)) {
                        } else {
                            color = Color.LTGRAY;
                        }
                        */
                    }
                }

                mFillPaint.setColor(color);

                mStrokePaint.setStyle(Paint.Style.STROKE);
                mStrokePaint.setColor(Color.BLACK);
                mStrokePaint.setStrokeWidth(2);
                canvas.drawRect(mRect, mFillPaint);
                canvas.drawRect(mRect, mStrokePaint);

                Team playerTeam = Pixelfighter.getInstance().getTeam();
                if(pixel.getPixelMod() == PixelModification.Bomb && team.equals(playerTeam)) {
                    Drawable d = getResources().getDrawable(R.drawable.ic_bomb, null);
                    d.setBounds((int)Math.ceil(mRect.left), (int)Math.ceil(mRect.top), (int)Math.floor(mRect.right), (int)Math.floor(mRect.bottom));
                    d.draw(canvas);
                }
            }
        }

    }

    private float calculateBoxSize() {
        return (
                Math.min(
                        (this.getHeight()-OFFSET*2) / new Float(this.board.getHeight()),
                        (this.getWidth()-OFFSET*2) / new Float(this.board.getWidth())));
    }

    private float calculateOffsetX() {
        return (this.getWidth()-this.board.getWidth()*calculateBoxSize()-OFFSET*2)/2.0f;
    }

    private float calculateOffsetY() {
        return (this.getHeight()-this.board.getHeight()*calculateBoxSize()-OFFSET*2)/2.0f;
    }

    public void pause() {
        running = false;
        try {
            // Stop the thread (rejoin the main thread)
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private int CLICK_ACTION_THRESHOLD = 200;
    private float startX;
    private float startY;

    private PendingClick downEventClick;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                downEventClick = new PendingClick(getCoordinateX(startX), getCoordinateY(startY));
                pendingClicks.add(downEventClick);
                break;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                pendingClicks.remove(downEventClick);
                downEventClick = null;
                if (isAClick(startX, endX, startY, endY)) {
                    click(startX, startY);
                }
                break;
        }
        return true;
    }

    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return !(differenceX > CLICK_ACTION_THRESHOLD/* =5 */ || differenceY > CLICK_ACTION_THRESHOLD);
    }

    private void click(float x, float y) {
        float boxSize = calculateBoxSize();
        int pX = getCoordinateX(x);
        int pY = getCoordinateY(y);
        Log.d("GameView", "Click at (" + pX + ", " + pY + ")");
        if (pX < 0 || pX >= this.board.getWidth()) return;
        if (pY < 0 || pY >= this.board.getHeight()) return;

        if (onClickListener == null) return;
        onClickListener.onClick(pX, pY);
    }

    private int getCoordinateX(float x) {
        return (int) ((x-calculateOffsetX()-OFFSET) / new Float(calculateBoxSize()));
    }
    private int getCoordinateY(float x) {
        return (int) ((x-calculateOffsetY()-OFFSET) / new Float(calculateBoxSize()));
    }

    public void addPendingClick(PendingClick click) {
        this.pendingClicks.add(click);
    }

    public void removePendingClick(PendingClick click) {
        this.pendingClicks.remove(click);
    }

    public interface OnClickListener {
        void onClick(int x, int y);
    }
}
