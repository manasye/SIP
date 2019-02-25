package com.example.sip.stepcounter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {

    public static final String PREFERENCE_ID = "preferences";
    public static final String STEP_DATA_ID = "stepdata";

    private static Database instance;
    private DatabaseReference prefData;
    private DatabaseReference stepData;

    private Database() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        this.prefData = rootRef.child(PREFERENCE_ID);
        this.prefData.keepSynced(true);
        this.stepData = rootRef.child(STEP_DATA_ID);
        this.stepData.keepSynced(true);
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public DatabaseReference getPreferences() {
        return prefData;
    }

    public DatabaseReference getStepData() {
        return stepData;
    }
}
