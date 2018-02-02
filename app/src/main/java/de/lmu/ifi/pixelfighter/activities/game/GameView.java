package de.lmu.ifi.pixelfighter.activities.game;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.activities.ZoomableGameActivity;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.models.PixelModification;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;

/**
 * Created by michael on 15.01.18.
 */

public class GameView extends ZoomableSurfaceView implements Runnable {

    private static final int OFFSET = 10;

    private OnClickListener onClickListener;

    private SurfaceHolder surfaceHolder;
    private boolean running;
    private Thread gameThread;

    private long startTime;
    private long endTime;
    public final int TARGET_FPS = 40;
    public long ACTUAL_FPS = 0;

    private CopyOnWriteArrayList<PendingClick> pendingClicks = new CopyOnWriteArrayList<>();
    private GameSettings gameSettings;

    ArrayList<ArrayList<Pixel>> oldPixels;
    ArrayList<int[]> triggeredProtections = new ArrayList<>();

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

        if (Pixelfighter.getInstance().isUseDark()) {
            canvas.drawColor(getContext().getColor(R.color.game_background_dark));
        } else {
            canvas.drawColor(getContext().getColor(R.color.game_background));
        }

        if (gameSettings == null) return;
        if (gameSettings.getBoard() == null) return;

        float boxSize = calculateBoxSize();

        // bei 20 Pixel
        // Scale=5 => 4 Pixel
        // => Pixels / Scale = #Pixel
        // Scale = #PixelsWidth/maxPixel
        float scale = gameSettings.getBoard().getWidth() / 7.0f;
        this.setMaxScale(scale);

        float offsetX = calculateOffsetX();
        float offsetY = calculateOffsetY();

        Map<Team, Integer> statistics = new HashMap<>();
        statistics.put(Team.None, 0);
        statistics.put(Team.Blue, 0);
        statistics.put(Team.Green, 0);
        statistics.put(Team.Red, 0);
        statistics.put(Team.Yellow, 0);


        if (oldPixels == null) {
            oldPixels = new ArrayList<>();
            for (int x = 0; x < this.gameSettings.getBoard().getWidth(); x++) {
                oldPixels.add(x, new ArrayList<Pixel>());
                for (int y = 0; y < this.gameSettings.getBoard().getHeight(); y++) {
                    oldPixels.get(x).add(y, null);
                }
            }
        }

        for (int x = 0; x < this.gameSettings.getBoard().getWidth(); x++) {
            for (int y = 0; y < this.gameSettings.getBoard().getHeight(); y++) {

                float left = OFFSET + offsetX + x * boxSize;
                float top = OFFSET + offsetY + y * boxSize;
                float right = left + boxSize;
                float bottom = top + boxSize;

                Pixel pixel = this.gameSettings.getBoard().getPixels().get(x).get(y);

                if (pixel.isInvalid()) continue;

                Pixel oldPixel = oldPixels.get(x).get(y);
                if (oldPixel != null) {
                    if (oldPixel.getPixelMod() == PixelModification.Bomb && oldPixel.getTeam() != Team.None && pixel.getPixelMod() == PixelModification.None) {
                        Log.d("Bomb", "bomb exploeded at " + pixel.toString());
                        //Toast.makeText(getContext(), "Bomb exploded", Toast.LENGTH_SHORT).show();
                        sendBroadcastToUI(x, y);
                    }
                }

//                for (int[] coords : triggeredProtections) {
//                    sendBroadcastToUI(coords[0], coords[1], ZoomableGameActivity.MyBroadcastReceiver.PROTECTION);
//                }
//                triggeredProtections.clear();


                oldPixels.get(x).set(y, pixel);


                Team team = pixel.getTeam();
                statistics.put(team, statistics.get(team) + 1);

                Paint mFillPaint = new Paint();
                Paint mStrokePaint = new Paint();
                RectF mRect = new RectF(left, top, right, bottom);
                mFillPaint.setStyle(Paint.Style.FILL);

                int color = Pixelfighter.getInstance().getTeamColor(team);

                ListIterator<PendingClick> iterator = pendingClicks.listIterator();
                while (iterator.hasNext()) {
                    PendingClick click = iterator.next();

                    if (click.getX() == x && click.getY() == y) {
                        color = click.getColor();
                    }
                }

                mFillPaint.setColor(color);

                mStrokePaint.setStyle(Paint.Style.STROKE);
                mStrokePaint.setColor(Color.BLACK);
                mStrokePaint.setStrokeWidth(2);
                canvas.drawRect(mRect, mFillPaint);
                canvas.drawRect(mRect, mStrokePaint);

                Team playerTeam = this.gameSettings.getTeam();
                if (pixel.getPixelMod() == PixelModification.Bomb && team.equals(playerTeam)) {
                    drawMod(mRect, canvas, R.drawable.ic_bomb);
                } else if (pixel.getPixelMod() == PixelModification.Bomb && team.equals(Team.None)) {
                    drawMod(mRect, canvas, R.drawable.ic_bomb_transparent);
                } else if (pixel.getPixelMod() == PixelModification.Protection && team.equals(playerTeam)) {
                    drawMod(mRect, canvas, R.drawable.ic_indicator_selected);
                } else if (pixel.getPixelMod() == PixelModification.Protection && team.equals(Team.None)) {
                    drawMod(mRect, canvas, R.drawable.ic_indicator_unselected);
                }

            }
        }

        if (gameSettings != null) gameSettings.setStatics(statistics);

    }

    private void drawMod(RectF mRect, Canvas canvas, int drawable) {
        Drawable d = getResources().getDrawable(drawable, null);
        d.setBounds((int) Math.ceil(mRect.left), (int) Math.ceil(mRect.top), (int) Math.floor(mRect.right), (int) Math.floor(mRect.bottom));
        d.draw(canvas);
    }

    public float calculateBoxSize() {
        if (gameSettings == null) return 1.0f;
        return (
                Math.min(
                        (this.getHeight() - OFFSET * 2) / new Float(this.gameSettings.getBoard().getHeight()),
                        (this.getWidth() - OFFSET * 2) / new Float(this.gameSettings.getBoard().getWidth())));
    }

    public float calculateOffsetX() {
        if (gameSettings == null) return 1.0f;
        return (this.getWidth() - this.gameSettings.getBoard().getWidth() * calculateBoxSize() - OFFSET * 2) / 2.0f;
    }

    public float calculateOffsetY() {
        if (gameSettings == null) return 1.0f;
        return (this.getHeight() - this.gameSettings.getBoard().getHeight() * calculateBoxSize() - OFFSET * 2) / 2.0f;
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
        if (gameSettings == null) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                downEventClick = new PendingClick(getCoordinateX(startX), getCoordinateY(startY), gameSettings.getTeam());
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
        if (this.gameSettings == null) return;

        float boxSize = calculateBoxSize();
        int pX = getCoordinateX(x);
        int pY = getCoordinateY(y);
        Log.d("GameView", "Click at (" + pX + ", " + pY + ")");
        if (pX < 0 || pX >= this.gameSettings.getBoard().getWidth()) return;
        if (pY < 0 || pY >= this.gameSettings.getBoard().getHeight()) return;
        if (this.gameSettings.getBoard().getPixels().get(pX).get(pY).isInvalid()) return;

        if (onClickListener == null) return;
        onClickListener.onClick(pX, pY);
    }

    private int getCoordinateX(float x) {
        return (int) ((x - calculateOffsetX() - OFFSET) / new Float(calculateBoxSize()));
    }

    private int getCoordinateY(float x) {
        return (int) ((x - calculateOffsetY() - OFFSET) / new Float(calculateBoxSize()));
    }

    public void addPendingClick(PendingClick click) {
        this.pendingClicks.add(click);
    }

    public void removePendingClick(PendingClick click) {
        this.pendingClicks.remove(click);
    }

    public void setGameSettings(GameSettings gameSettings) {
        if (gameSettings == null) return;
        this.gameSettings = gameSettings;

//        BroadcastReceiver br = new MyBroadcastReceiver();
//        IntentFilter filter = new IntentFilter("de.lmu.ifi.pixelfighter.PROTECTION_TRIGGERED");
//        LocalBroadcastManager.getInstance(getContext()).registerReceiver(br, filter);
    }

    private void sendBroadcastToUI(int x, int y) {
        Intent intent = new Intent();
        intent.putExtra("x", x);
        intent.putExtra("y", y);

//        switch (typeOfAction) {
//            case ZoomableGameActivity.MyBroadcastReceiver.EXPLOSION:
//                intent.setAction("de.lmu.ifi.pixelfighter.BOMB_WAS_EXECUTED");
//            case ZoomableGameActivity.MyBroadcastReceiver.PROTECTION:
//                intent.setAction("de.lmu.ifi.pixelfighter.PROTECTION_WAS_EXECUTED");
//        }

        intent.setAction("de.lmu.ifi.pixelfighter.BOMB_WAS_EXECUTED");
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        Log.d("GAMEVIEW", "sent broadcast to UI");
    }

//    private class MyBroadcastReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            int x = intent.getIntExtra("x", 0);
//            int y = intent.getIntExtra("y", 0);
//
//            triggeredProtections.add(new int[]{x, y});
//        }
//    }

    public interface OnClickListener {
        void onClick(int x, int y);
    }
}
