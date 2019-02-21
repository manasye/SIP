package com.example.sip.stepcounter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StepCounter implements SensorEventListener {

    private int sensorType = -999;
    private int stepCount;
    private SensorManager sm;
    private StepCounterListener caller; // Callback whenever StepCounter detects a step
    private StepProcessor sp = null;
    private Sensor usedSensor;

    private boolean nativeStepPresent = false; // true if a native pedometer/step counter sensor available
    private boolean accelPresent = false; // true if an accelerometer available

    /* Ctor
     * StepCounter will automatically use native step counter if available.
     */
    public StepCounter(Context context) {
        // Detects sensor type and applies better sensor if available
        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if ((usedSensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)) != null) {
            nativeStepPresent = true;
            sensorType = Sensor.TYPE_STEP_COUNTER;
            sp = new NativeStepCounter();
        } else if ((usedSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)) != null) {
            accelPresent = true;
            sensorType = Sensor.TYPE_ACCELEROMETER;
            sp = new AccelStepCounter();
        }

    }

    /*
     *  Register a listener for this counter
     *  Listener will be notified if this counter detects a step
     */
    public void registerListener(StepCounterListener caller) {
        this.caller = caller;
    }

    /* Make the counter use native (dedicated hardware) step counter */
    public void useNativeStep() {
        this.sensorType = Sensor.TYPE_STEP_COUNTER;
        usedSensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    /* Make the counter use accelerometer to count steps (not accurate) */
    public void useAccelerometer() {
        this.sensorType = Sensor.TYPE_ACCELEROMETER;
        usedSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    /* Returns the type of sensor this counter use.
     * Returns -999 if no suitable sensor can be found on this device.
     * Do a checking by comparing it with the constant "Sensor.TYPE_STEP_COUNTER" or "Sensor.TYPE_ACCELEROMETER"
     */
    public int getSensorType() {
        return sensorType;
    }

    /* Check if a specialized pedometer sensor is available on this phone */
    public boolean isNativeStepAvailable() {
        return nativeStepPresent;
    }

    /* Check if accelerometer is available on this phone */
    public boolean isAccelAvailable() {
        return accelPresent;
    }

    /* Start counting steps */
    public void start() {
        stepCount = 0;
        sm.registerListener(this,usedSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    /* Stop counting */
    public void stop() {
        sm.unregisterListener(this);
    }

    /* Implementing SensorListener method.
     * Responds if sensor sends new data.
     * */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // If data from step counter
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            stepCount = sp.process(event, stepCount);
            caller.onStepDetected(stepCount);
            // If data from accelerometer
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int temp = sp.process(event, stepCount);
            if (temp > stepCount) {
                stepCount = temp;
                caller.onStepDetected(stepCount);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // no need to implement this
    }
}
