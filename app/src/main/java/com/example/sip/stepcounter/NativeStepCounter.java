package com.example.sip.stepcounter;

import android.hardware.SensorEvent;

public class NativeStepCounter implements StepProcessor {

    private boolean isInitialized = false;
    private float lastStepCount;

    @Override
    public int process(SensorEvent in, int currentStepCount) {
        int tempCount = currentStepCount;

        if (!isInitialized) {
            lastStepCount = in.values[0];
            isInitialized = true;
        } else {
            float diff = in.values[0] - lastStepCount;
            tempCount += diff;
            lastStepCount = in.values[0];
        }

        return tempCount;
    }
}
