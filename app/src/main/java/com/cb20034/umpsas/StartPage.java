package com.cb20034.umpsas;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class StartPage extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123; // Choose any code you prefer


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // If the user is already logged in, skip the login activity
            startActivity(new Intent(StartPage.this, MainMenu.class));
            finish();
        }

        Button bLogin = findViewById(R.id.bLogin);
        Button bRegister = findViewById(R.id.bRegister);
        askPermissions();
        bLogin.setOnClickListener(n->{
            Intent intent = new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
        });
        bRegister.setOnClickListener(n->{
            Intent intent = new Intent(getApplicationContext(),Register.class);
            startActivity(intent);
        });
    }
    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void askPermissions() {
        // Check if the device is running on TIRAMISU (API level >= 33)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            List<String> permissionsToRequest = new ArrayList<>();

            // Check for POST_NOTIFICATIONS permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
            }

            // Check for READ_EXTERNAL_STORAGE permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            // Check for WRITE_EXTERNAL_STORAGE permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            // Check for CAMERA permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.CAMERA);
            }

            if (!permissionsToRequest.isEmpty()) {
                // Some permissions are not granted, request them
                String[] permissionsArray = permissionsToRequest.toArray(new String[0]);

                // Check each permission individually
                for (String permission : permissionsArray) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        // TODO: Display an educational UI explaining to the user the features that will be enabled
                        //       by them granting the permissions. This UI should provide the user
                        //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                        //       If the user selects "No thanks," allow the user to continue without permissions.
                    }
                }

                // Directly ask for the permissions
                ActivityCompat.requestPermissions(this, permissionsArray, PERMISSION_REQUEST_CODE);
            }
        }
    }

}