package com.cb20034.umpsas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainMenu extends AppCompatActivity {
    private TextView bSummons, bVehicle, bReceipt, bReport, bNotification;
    private TextView welcomeTextView;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (doubleBackToExitPressedOnce) {
            // If double back pressed, exit the app
            finish();
        } else {
            // Show a toast message indicating that the user should press back again to exit
            showToast("Press again to exit");
            doubleBackToExitPressedOnce = true;

            // Reset the doubleBackToExitPressedOnce variable after a delay
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = findViewById(R.id.toolbarMenu);
        setSupportActionBar(toolbar);

        welcomeTextView = findViewById(R.id.welcome);

        // Initialize buttons
        bSummons = findViewById(R.id.bSummons);
        bVehicle = findViewById(R.id.bVehicle);
        bReceipt = findViewById(R.id.bReceipt);
        bReport = findViewById(R.id.bReport);
        bNotification = findViewById(R.id.bNotification);


        // Get the current Firebase user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // If the user is logged in, retrieve additional data from Firestore
            String userId = currentUser.getUid();
            // Reference to Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Reference to the "users" collection (replace with your collection name)
            db.collection("users").document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // User data retrieved successfully
                                    User user = document.toObject(User.class);
                                    String userType = user.getUserType();
                                    handleUserType(userType);

                                    // Set the welcome message in the TextView
                                    welcomeTextView.setText("Welcome " + user.getName());
                                }
                            } else {
                                startActivity(new Intent(MainMenu.this, StartPage.class));
                                finish();
                                // Handle failures
                            }
                        }
                    });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            logout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    private void handleUserType(String userType) {
        if ("Admin".equals(userType)) {
            // If userType is Admin, modify text and visibility
            bSummons.setText("Summons Management");
            bVehicle.setText("Report Management");
            bReceipt.setText("Search Vehicle");
            bReport.setVisibility(View.INVISIBLE);
            bNotification.setVisibility(View.INVISIBLE);
        }else {bNotification.setVisibility(View.INVISIBLE);}
        bSummons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start SummonsManagement.class or Summons.class based on user type
                startActivity(getIntentForActivity(userType, SummonList.class, SummonList.class));
            }
        });

        bVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start ReportManagement.class or Vehicle.class based on user type
                startActivity(getIntentForActivity(userType, ReportList.class, VehicleMenu.class));
            }
        });

        bReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start SearchVehicle.class or ReceiptList.class based on user type
                startActivity(getIntentForActivity(userType, VehicleList.class, ReceiptList.class));
            }
        });

        bReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start ReportList.class when bReport is clicked
                startActivity(new Intent(MainMenu.this, ReportList.class));
            }
        });

        bNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start Notification.class when bNotification is clicked
                startActivity(new Intent(MainMenu.this, Notification.class));
            }
        });
    }

    private Intent getIntentForActivity(String userType, Class<?> adminActivity, Class<?> regularActivity) {
        if ("Admin".equals(userType)) {
            return new Intent(MainMenu.this, adminActivity);
        } else {
            return new Intent(MainMenu.this, regularActivity);
        }
    }
    private void logout() {
        FirebaseAuth.getInstance().signOut();

        // Redirect to the login screen or any other desired activity
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish(); // Close the current activity
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}