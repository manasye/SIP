package com.example.sip.stepcounter;

import android.hardware.SensorEvent;

public interface StepProcessor {

    public void refresh();
    public int process(SensorEvent in, int currentStepCount);

}
