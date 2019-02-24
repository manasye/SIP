package com.example.sip;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.sip.stepcounter.StepCounter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.takisoft.fix.support.v7.preference.EditTextPreference;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }

        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        private FirebaseAuth authFirebase = FirebaseAuth.getInstance();
        private boolean somethingChanged;
        private SharedPreferences sp;


        @Override
        public void onCreatePreferencesFix(Bundle bundle, String s) {
            setPreferencesFromResource(R.xml.preferences, s);
            EditTextPreference stepGoal = (EditTextPreference) findPreference(getString(R.string.step_goal_pref));
            ListPreference sensorType = (ListPreference) findPreference(getString(R.string.sensor_select_pref));
            Preference connectButton = findPreference(getString(R.string.integrate_pref));
            Preference logoutButton = findPreference(getString(R.string.logout_pref));
            sp = PreferenceManager.getDefaultSharedPreferences(this.getContext());

            // Loads previously saved values
            String steps = sp.getString(stepGoal.getKey(), "0");
            stepGoal.setSummary(steps + " steps");
            String sensor = sp.getString(sensorType.getKey(), "undefined");
            sensorType.setSummary("Currently using " + getSensorDescription(Integer.parseInt(sensor)));
            logoutButton.setSummary("Currently logged in as " + authFirebase.getCurrentUser().getEmail());

            // Disable sensor choice if no sensor cannot be found
            com.example.sip.stepcounter.StepCounter temp = new com.example.sip.stepcounter.StepCounter(getContext());
            if (!temp.isAccelAvailable() && !temp.isNativeStepAvailable()) {
                sensorType.setEnabled(false);
                sensorType.setSummary("No suitable sensor found on your device");
            }

            // Connect button listener
            connectButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(getContext(),"COMING SOON...",Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            // Logout button listener
            logoutButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(true);
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are you sure you want to logout?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            authFirebase.signOut();
                            Intent intent = new Intent(getContext(), Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }

            });

            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            // Backing up preferences to Firebase
            if (somethingChanged) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("users").child(authFirebase.getCurrentUser().getUid());
                DatabaseReference pref = ref.child("preferences");

                int dailyGoal = Integer.parseInt(sp.getString(getString(R.string.step_goal_pref),"0"));
                int sensorType = Integer.parseInt(sp.getString(getString(R.string.sensor_select_pref), "1"));

                pref.child("dailyGoal").setValue(dailyGoal);
                pref.child("sensorType").setValue(sensorType);

                // Also updates today's stepdata target
                String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
                ref.child("stepdata").child(date).child("target").setValue(dailyGoal);

                Intent intent = new Intent(History.HISTORY_REFRESH_EVENT);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
            }
        }

        /* If the user changed a setting value, the summary will be updated to reflect the changes */
        public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
            Preference pref = findPreference(key);
            if (pref.getKey().equals(getString(R.string.step_goal_pref))) {
                EditTextPreference etp = (EditTextPreference) pref;
                String value = sp.getString(key, "0");
                etp.setSummary(value + " steps");
                somethingChanged = true;
            } else if (pref.getKey().equals(getString(R.string.sensor_select_pref))) {
                ListPreference lp = (ListPreference) pref;
                String value = sp.getString(key, "-1");
                if (value.equals("2")) {
                    StepCounter temp = new StepCounter(getContext());
                    if (!temp.isNativeStepAvailable()) {
                        value = "1";
                        Toast.makeText(getContext(),"That sensor is not available on this device!",Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(key,value);
                        editor.apply();
                    }
                }
                lp.setSummary("Currently using " + getSensorDescription(Integer.parseInt(value)));
                somethingChanged = true;
            }
        }

        public static String getSensorDescription(int sensorId) {
            switch (sensorId) {
                case 1:
                    return "Accelerometer";
                case 2:
                    return "Native Pedometer";
                default:
                    return "undefined";
            }
        }
    }
}
