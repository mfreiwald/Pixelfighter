package de.lmu.ifi.pixelfighter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;

import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.activities.game.ZoomableSurfaceView;

/**
 * Created by michael on 09.01.18.
 */

public class ZoomableGameActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    ZoomableSurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomable_game);

        surfaceView = (ZoomableSurfaceView) findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
