package com.cb20034.umpsas;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class VehicleMenu extends AppCompatActivity {

    private TextView carDetailText;
    private FloatingActionButton fabAddVehicle;
    private FloatingActionButton fabEditVehicle;
    private FloatingActionButton fabDeleteVehicle;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        Toolbar toolbar = findViewById(R.id.toolbarVehicle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        carDetailText = findViewById(R.id.carDetailText);
        fabAddVehicle = findViewById(R.id.fabAddVehicle);
        fabEditVehicle = findViewById(R.id.fabEditVehicle);
        fabDeleteVehicle = findViewById(R.id.fabDeleteVehicle);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Check if the user is logged in
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // User is logged in, check if they have a registered vehicle
            checkVehicleRegistration(currentUser.getUid());

            // Set up the floating action button click listener
            fabAddVehicle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open the VehicleRegistration activity
                    startActivity(VehicleRegistration.class);
                }
            });
            fabEditVehicle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open the VehicleEdit activity
                    startActivity(VehicleEdit.class);
                }
            });
        } else {
            // User is not logged in, handle accordingly (e.g., redirect to login)
        }
    }

    private void checkVehicleRegistration(String userId) {
        firestore.collection("users").document(userId).collection("vehicles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            // User has a registered vehicle, show the details
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String vehicleDetails = getVehicleDetails(documentSnapshot);
                            carDetailText.setText(vehicleDetails);

                            // Show the delete vehicle button
                            fabDeleteVehicle.setVisibility(View.VISIBLE);

                            // Set up the delete button click listener
                            fabDeleteVehicle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Call a method to handle vehicle deletion
                                    deleteVehicle(userId, documentSnapshot.getId());
                                }
                            });

                            // Hide the add vehicle button
                            fabAddVehicle.setVisibility(View.GONE);

                        } else {
                            // No registered vehicle, show the appropriate message
                            carDetailText.setText("No car registered");
                            fabEditVehicle.setVisibility(View.GONE);
                            fabDeleteVehicle.setVisibility(View.GONE);
                        }
                    } else {
                        // Handle the exception or show an error message
                    }
                });
    }

    private String getVehicleDetails(DocumentSnapshot documentSnapshot) {
        // Customize this method based on your Firestore document structure
        // This is just a basic example, adjust it according to your data model
        String vehicleType = documentSnapshot.getString("vehicleType");
        String brand = documentSnapshot.getString("brand");
        String model = documentSnapshot.getString("model");
        String color = documentSnapshot.getString("color");
        String licenseValidDate = documentSnapshot.getString("licenseValidDate");
        String plateNo = documentSnapshot.getString("plateNo");
        String academicYear = documentSnapshot.getString("academicYear");

        // Format the details as needed
        return "Vehicle Type: " + vehicleType +
                "\nBrand: " + brand +
                "\nModel: " + model +
                "\nColor: " + color +
                "\nLicense Valid Date: " + licenseValidDate +
                "\nPlate No: " + plateNo +
                "\nAcademic Year: " + academicYear;
    }

    private void deleteVehicle(String userId, String vehicleId) {
        firestore.collection("users").document(userId).collection("vehicles")
                .document(vehicleId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Vehicle deleted successfully
                    new Handler().postDelayed(() -> {
                        // Finish the current activity (vehicle registration)
                        finish();

                        // Start the vehicle menu activity after a 1-second delay
                        Intent intent = new Intent(VehicleMenu.this, VehicleMenu.class);
                        startActivity(intent);
                    }, 1000); // 1000 milliseconds = 1 second// 1000 milliseconds = 1 second
                    // Handle any additional logic or UI updates after deletion
                })
                .addOnFailureListener(e -> {
                    // Handle the exception or show an error message
                    Toast.makeText(this, "Failed to delete vehicle", Toast.LENGTH_SHORT).show();
                });
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}