package com.example.sip.stepcounter;

public interface StepCounterListener {

    // Step count = number of steps detected by StepCounter on "one session"
    public void onStepDetected(int stepCount);

}
