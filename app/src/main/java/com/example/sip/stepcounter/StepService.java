package com.example.sip.stepcounter;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class StepService extends IntentService implements StepCounterListener {

    public StepService() {
        super("StepService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        
    }

    @Override
    public void onStepDetected(int stepCount) {

    }
}
