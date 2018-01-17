package de.lmu.ifi.pixelfighter.services.android;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import de.lmu.ifi.pixelfighter.DefaultApp;

/**
 * Created by michael on 17.01.18.
 */

public class LightSensor implements SensorEventListener {
    public static final float LUX_THRESHOLD = 4;

    private SensorManager sensorManager;
    private Sensor lightSensor;

    public LightSensor() {
        sensorManager = (SensorManager) DefaultApp.getAppContext().getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

    }

    public void onResume() {
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        sensorManager.unregisterListener(this, lightSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float light = event.values[0];
        if(light < LUX_THRESHOLD) {
            Pixelfighter.getInstance().setUseDark(true);
        } else {
            Pixelfighter.getInstance().setUseDark(false);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
