package com.cb20034.umpsas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPassword extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputEditText emailText;
    private Button bReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        emailText = findViewById(R.id.emailForgot);
        bReset = findViewById(R.id.bResetPassword);

        bReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailText.getText().toString();
                resetPassword(email);
            }
        });
    }

    private void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Password reset email sent successfully

                            showAlertDialog("Success", "Password reset email sent successfully");
                            // You can provide additional feedback to the user or navigate to another screen
                            // For example, you can open a SuccessActivity
                            // Consider adding UI logic in this part
                        } else {
                            // If password reset fails, display a message to the user.
                            // You can also handle different failure cases here
                            // For example, email doesn't exist, or network error
                            // Show an error pop-up dialog
                            showAlertDialog("Failed", "Failed to send password reset email");
                            // Consider adding UI logic in this part
                        }
                    }
                });
    }
    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // You can add additional actions on OK button click if needed
                        dialog.dismiss(); // Dismiss the dialog
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}