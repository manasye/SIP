package com.example.sip;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.takisoft.fix.support.v7.preference.EditTextPreference;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

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

        @Override
        public void onCreatePreferencesFix(Bundle bundle, String s) {
            setPreferencesFromResource(R.xml.preferences, s);
            EditTextPreference stepGoal = (EditTextPreference) findPreference(getString(R.string.step_goal_pref));
            ListPreference sensorType = (ListPreference) findPreference(getString(R.string.sensor_select_pref));
            Preference logoutButton = findPreference(getString(R.string.logout_pref));

            // Loads previously saved values
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getContext());
            String steps = sp.getString(stepGoal.getKey(), "0");
            stepGoal.setSummary(steps + " steps");
            String sensor = sp.getString(sensorType.getKey(), "undefined");
            sensorType.setSummary("Currently using " + getSensorDescription(Integer.parseInt(sensor)));
            logoutButton.setSummary("Currently logged in as " + authFirebase.getCurrentUser().getEmail());

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

        /* If the user changed a setting value, the summary will be updated to reflect the changes */
        public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
            Preference pref = findPreference(key);
            if (pref.getKey().equals(getString(R.string.step_goal_pref))) {
                EditTextPreference etp = (EditTextPreference) pref;
                String value = sp.getString(key,"0");
                etp.setSummary(value + " steps");
            } else if (pref.getKey().equals(getString(R.string.sensor_select_pref))) {
                ListPreference lp = (ListPreference) pref;
                String value = sp.getString(key,"-1");
                lp.setSummary("Currently using " + getSensorDescription(Integer.parseInt(value)));
            }
        }

        public static String getSensorDescription(int sensorId) {
            switch(sensorId) {
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
