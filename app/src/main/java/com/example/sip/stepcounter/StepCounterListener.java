package com.example.sip.stepcounter;

// Implement this interface to be notified by your StepCounter.
// Don't forget to register your listener to your StepCounter instance.
public interface StepCounterListener {

    // Step count = number of steps detected by StepCounter on "one session"
    public void onStepDetected(int stepCount);

}
