package com.cb20034.umpsas;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextInputEditText emailText;
    private TextInputEditText passwordText;
    private Button bLogin;
    private TextView TVforgotPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the Toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        bLogin = findViewById(R.id.bLogin);
        TVforgotPass = findViewById(R.id.TVforgotPass);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                signIn(email, password);
            }
        });

        TVforgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            obtainAndStoreFCMToken();
                            updateUI(user);
                        } else {
                            updateUI(null);
                        }
                    }
                });
    }
    private void obtainAndStoreFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> {
                    updateFCMTokenInFirestore(token);
                })
                .addOnFailureListener(e -> {
                    // Handle failure to obtain FCM token
                });
    }

    private void updateFCMTokenInFirestore(String token) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Map<String, Object> data = new HashMap<>();
            data.put("fcmToken", token);

            FirebaseFirestore.getInstance().collection("users").document(userId)
                    .update(data)
                    .addOnSuccessListener(aVoid -> {
                        // FCM token successfully updated in Firestore
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to update FCM token
                    });
            // Update the FCM token in Firestore
            // Add the necessary Firestore update logic here
        }
    }
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(Login.this, MainMenu.class);
            startActivity(intent);
            finish();
        } else {
            showToast("Login failed. Please check your email or password.");
        }
    }

    private void showToast(String message) {
        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // Handle the back button press (e.g., finish the current activity)
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
