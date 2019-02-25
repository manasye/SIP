package com.example.sip;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class NotificationService extends FirebaseMessagingService {

    private static final String TAG_LOG = "[NOTIFSERVICE]";
    private static final String CHANNEL_ID = "[SIPNOTIF]";

    /* Handles any message received from Firebase Server */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG_LOG,"Message received from : " + remoteMessage.getFrom());
        Map<String,String> data = remoteMessage.getData();

        if (data.size() > 0) {
            String type = data.get("type");
            if (type != null) {
                switch(type) {
                    case "walk_reminder":
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        boolean showNotification = sp.getBoolean(getString(R.string.reminder_pref),true);
                        if (showNotification) {
                            Log.d(TAG_LOG,"Walk reminder job received, firing notification...");
                            fireWalkReminder();
                        } else {
                            Log.d(TAG_LOG,"Walk reminder received, but set to not notify, ignoring...");
                        }
                        break;
                    default:
                        Log.d(TAG_LOG,"Received invalid payload!");
                }
            }
        }

    }

    /* Show a notification to remind the user to walk (and meet it's daily target)
    *  TO_DO: Check whether the user already passed it's target or not */
    public void fireWalkReminder() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentTitle("Get up and go!");
        builder.setContentText("Walk around and be healthier!");
        builder.setAutoCancel(true);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        /* For Oreo and above : need to create a notification channel first */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(CHANNEL_ID, "S.I.P. Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(nc);
            v.vibrate(VibrationEffect.createOneShot(200,VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(200);
        }

        nm.notify(0, builder.build());

    }
}
