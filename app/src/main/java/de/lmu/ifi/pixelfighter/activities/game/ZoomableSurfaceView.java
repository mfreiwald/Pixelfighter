package de.lmu.ifi.pixelfighter.activities.game;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by arch on 1/16/17.
 * https://github.com/m-damavandi/ZoomableSurfaceView
 */

public class ZoomableSurfaceView extends SurfaceView {

    int left, top, right, bottom;
    private ScaleGestureDetector SGD;
    private Context context;
    private boolean isSingleTouch;
    private float width, height = 0;
    private float scale = 1f;
    private float minScale = 1f;
    private float maxScale = 5f;

    public ZoomableSurfaceView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ZoomableSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public ZoomableSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        setOnTouchListener(new MyTouchListeners());
        SGD = new ScaleGestureDetector(context, new ScaleListener());
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (width == 0 && height == 0) {
            width = ZoomableSurfaceView.this.getWidth();
            height = ZoomableSurfaceView.this.getHeight();
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
        }

    }

    public void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
    }

    private void checkDimension(View vi) {

        if (vi.getX() > left) {
            vi.animate()
                    .x(left)
                    .y(vi.getY())
                    .setDuration(0)
                    .start();
        }

        if ((vi.getWidth() + vi.getX()) < right) {
            vi.animate()
                    .x(right - vi.getWidth())
                    .y(vi.getY())
                    .setDuration(0)
                    .start();
        }

        if (vi.getY() > top) {
            vi.animate()
                    .x(vi.getX())
                    .y(top)
                    .setDuration(0)
                    .start();
        }

        if ((vi.getHeight() + vi.getY()) < bottom) {
            vi.animate()
                    .x(vi.getX())
                    .y(bottom - vi.getHeight())
                    .setDuration(0)
                    .start();
        }
    }

    public float getScale() {
        return scale;
    }

    private class MyTouchListeners implements View.OnTouchListener {

        float dX, dY;

        MyTouchListeners() {
            super();
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            SGD.onTouchEvent(event);
            if (event.getPointerCount() > 1) {
                isSingleTouch = false;
            } else {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    isSingleTouch = true;
                }
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dX = ZoomableSurfaceView.this.getX() - event.getRawX();
                    dY = ZoomableSurfaceView.this.getY() - event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (isSingleTouch) {
                        ZoomableSurfaceView.this.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        checkDimension(ZoomableSurfaceView.this);
                    }
                    break;
                default:
                    return false;
            }
            return false;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d("onGlobalLayout: ", scale + " " + width + " " + height);
            scale *= detector.getScaleFactor();
            scale = Math.max(minScale, Math.min(scale, maxScale));

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    (int) (width * scale), (int) (height * scale));
            ZoomableSurfaceView.this.setLayoutParams(params);

            Log.d("Move to ", detector.getFocusX() + ", " + detector.getFocusY());
            checkDimension(ZoomableSurfaceView.this);
            return true;
        }
    }
}