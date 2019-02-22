package com.example.sip;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    private EditText password, email, confPass;
    private FirebaseAuth authFirebase = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in
        FirebaseUser currentUser = authFirebase.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(Register.this, HomePage.class));
        }
    }

    public void goToLogin(View view) {
        startActivity(new Intent(Register.this, Login.class));
    }

    public void register(View view) {
        // Get all register data
        email = findViewById(R.id.usernameField);
        String emailText = email.getText().toString();
        password = findViewById(R.id.passwordField);
        String passText = password.getText().toString();
        confPass = findViewById(R.id.confPasswordField);
        String confPassText = confPass.getText().toString();

        if (emailText.isEmpty()) {
            email.requestFocus();
            Toast.makeText(Register.this,"Please fill in the email!",Toast.LENGTH_SHORT).show();
        } else if (passText.isEmpty()) {
            password.requestFocus();
            Toast.makeText(Register.this,"Please fill in the password!",Toast.LENGTH_SHORT).show();
        } else if (confPassText.isEmpty()) {
            confPass.requestFocus();
            Toast.makeText(Register.this,"Please type in the password again!",Toast.LENGTH_SHORT).show();
        } else if (!passText.equals(confPassText)) {
            Toast.makeText(Register.this, "Password and confirm password is not the same.",
            Toast.LENGTH_SHORT).show();
        } else {
            authFirebase.createUserWithEmailAndPassword(emailText, passText)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign up success
                                Log.d("Register", "createUserWithEmail:success");
                                goToMainPage();
                            } else {
                                // Sign up fails
                                Log.w("Register", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(Register.this, "Register failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }

    private void goToMainPage() {
        startActivity(new Intent(Register.this, HomePage.class));
    }
}
