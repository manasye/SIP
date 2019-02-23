package com.example.sip;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.sip.stepcounter.StepCounter;
import com.example.sip.stepcounter.StepCounterListener;

public class StepService extends Service implements StepCounterListener {

    public static final String START_SERVICE = "start";
    public static final String STOP_SERVICE = "stop";
    public static boolean isServiceRunning = false;
    private static final String NOTIF_CHANNEL = "[SIPFORGNDNOTIF]";
    private static final int FOREGROUND_SERVICE_ID = 1;
    public static com.example.sip.StepCounter callback = null;

    private int currentStepCount;
    private StepCounter counter;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        currentStepCount = 0;
        counter = new StepCounter(this);
        counter.registerListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            switch(intent.getAction()) {
                case START_SERVICE:
                    start();
                    break;
                case STOP_SERVICE:
                    stop();
                    break;
            }
        }
        return super.onStartCommand(intent,flags,startId);
    }

    public void start() {
        Notification notification = rebuildNotification();
        startForeground(FOREGROUND_SERVICE_ID, notification);
        counter.start();
        isServiceRunning = true;
    }

    public void stop() {
        isServiceRunning = false;
        counter.stop();
        stopForeground(true);
        stopSelf();
    }

    private Notification rebuildNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIF_CHANNEL);
        builder.setContentTitle("Counting steps...");
        builder.setContentText(currentStepCount + " steps");
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);

        Intent stopIntent = new Intent(this, StepService.class);
        stopIntent.setAction(STOP_SERVICE);
        PendingIntent pendingStop = PendingIntent.getService(this, 0, stopIntent, 0);
        NotificationCompat.Action stopAction = new NotificationCompat.Action(R.drawable.ic_stop_black_24dp,"Stop",pendingStop);
        builder.addAction(stopAction);

        return builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStepDetected(int stepCount) {
        currentStepCount = stepCount;
        Log.d("[FORGNDSRV]","step detected!");
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = rebuildNotification();
        nm.notify(FOREGROUND_SERVICE_ID, notification);
        if (callback != null) {
            callback.onStepDetected(currentStepCount);
        }
    }
}
