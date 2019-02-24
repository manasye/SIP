package com.example.sip;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.sip.stepcounter.StepCounter;
import com.example.sip.stepcounter.StepCounterListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StepService extends Service implements StepCounterListener {

    public static final String START_SERVICE = "start";
    public static final String STOP_SERVICE = "stop";
    public static boolean isServiceRunning = false;
    private static final String NOTIF_CHANNEL = "[SIPFORGNDNOTIF]";
    private static final int FOREGROUND_SERVICE_ID = 1;
    public static com.example.sip.StepCounter callback = null;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference("users").child(user.getUid()).child("stepdata");
    private String currentDate;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    private int currentStepCount;
    private StepCounter counter;
    private NotificationManager nm;

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

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(NOTIF_CHANNEL, "S.I.P. Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(nc);
        }
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

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        int sensorType = Integer.parseInt(sp.getString(getString(R.string.sensor_select_pref),"0"));
        switch (sensorType) {
            case 1:
                counter.useAccelerometer();
                break;
            case 2:
                counter.useNativeStep();
                break;
            default:
                // TO-DO : Error handling
                counter.useAccelerometer();
                break;
        }
        counter.start();
        currentDate = sdf.format(Calendar.getInstance().getTime());

        /* Saves today's target to Firebase */
        final DatabaseReference prevData = ref.child(currentDate);
        prevData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("target").exists()) {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String temp = sp.getString(getString(R.string.step_goal_pref),"0");
                    int target = Integer.parseInt(temp);
                    prevData.child("target").setValue(target);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        isServiceRunning = true;
    }

    public void stop() {
        isServiceRunning = false;
        counter.stop();
        stopForeground(true);

        /* Updates value to Firebase */
        final DatabaseReference prevData = ref.child(currentDate);
        prevData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count;
                if (dataSnapshot.child("count").exists()) {
                    count = dataSnapshot.child("count").getValue(Integer.class);
                } else {
                    count = 0;
                }
                count += currentStepCount;
                prevData.child("count").setValue(count);

                Intent intent = new Intent(History.HISTORY_REFRESH_EVENT);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (callback != null) {
            callback.resetStartButton();
        }
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

        Notification notification = rebuildNotification();
        nm.notify(FOREGROUND_SERVICE_ID, notification);
        if (callback != null) {
            callback.onStepDetected(currentStepCount);
        }
    }

}
