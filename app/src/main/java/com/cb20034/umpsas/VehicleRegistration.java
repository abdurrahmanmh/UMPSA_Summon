package com.cb20034.umpsas;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class VehicleRegistration extends AppCompatActivity {

    private TextInputEditText brandText, modelText, colorText, plateNoText, licenseValidDateText;
    private Button cancelButton, confirmButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private Spinner spinnerAcademicYear,spinnerVehicleType ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_registration);

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        brandText = findViewById(R.id.brandText);
        modelText = findViewById(R.id.modelText);
        colorText = findViewById(R.id.colorText);
        plateNoText = findViewById(R.id.plateNoText);
        licenseValidDateText = findViewById(R.id.licenseValidDate);
        cancelButton = findViewById(R.id.cancelButton);
        confirmButton = findViewById(R.id.confirmButton);
        spinnerVehicleType = findViewById(R.id.spinnerVehicleType);
        spinnerAcademicYear = findViewById(R.id.spinnerAcademicYear);



        // Set up button click listeners
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to go to the MainMenu activity
                Intent intent = new Intent(VehicleRegistration.this, MainMenu.class);
                // Add any additional flags or data you may need
                // For example, if you want to clear the back stack:
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // Start the MainMenu activity
                startActivity(intent);
                // Close the current activity
                finish();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerVehicle(); // Perform vehicle registration when confirm button is clicked
            }
        });

        // Set up a click listener for the date text field
        licenseValidDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Set the selected date to the text field
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        licenseValidDateText.setText(selectedDate);
                    }
                },
                year, month, day
        );

        // Show the date picker dialog
        datePickerDialog.show();
    }

    private void registerVehicle() {
        // Get the currently logged-in user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            // Get user ID
            String userId = currentUser.getUid();

            // Get values from UI elements
            String brand = brandText.getText().toString();
            String model = modelText.getText().toString();
            String color = colorText.getText().toString();
            String plateNo = plateNoText.getText().toString();
            String licenseValidDate = licenseValidDateText.getText().toString();
            String academicYear = spinnerAcademicYear.getSelectedItem().toString();
            String vehicleType = spinnerVehicleType.getSelectedItem().toString();

            // Check if plateNo is not empty
            if (TextUtils.isEmpty(plateNo)) {
                Toast.makeText(this, "Please enter the plate number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a Map to store vehicle details
            Vehicle vehicle = new Vehicle();

            if ("Please choose the vehicle type".equals(vehicleType)) {
                // User has not selected a valid type, show an error message
                Toast.makeText(this, "Please choose a valid vehicle type", Toast.LENGTH_SHORT).show();
                return; // Exit the method, do not proceed with registration
            }
            if ("Please choose the Academic Year".equals(academicYear)) {
                // User has not selected a valid type, show an error message
                Toast.makeText(this, "Please choose a valid vehicle type", Toast.LENGTH_SHORT).show();
                return; // Exit the method, do not proceed with registration
            }

            vehicle.setUserId(userId);
            vehicle.setVehicleType(vehicleType);
            vehicle.setBrand(brand);
            vehicle.setModel(model);
            vehicle.setColor(color);
            vehicle.setPlateNo(plateNo);
            vehicle.setLicenseValidDate(licenseValidDate);
            vehicle.setAcademicYear(academicYear);

            // Add the vehicle details to Firestore with plateNo as the document ID
            firestore.collection("users").document(userId).collection("vehicles")
                    .document(plateNo)
                    .set(vehicle)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(VehicleRegistration.this, "Vehicle registered successfully", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> {
                            // Finish the current activity (vehicle registration)
                            finish();

                            // Start the vehicle menu activity after a 1-second delay
                            Intent intent = new Intent(VehicleRegistration.this, VehicleMenu.class);
                            startActivity(intent);
                        }, 1000); // 1000 milliseconds = 1 second
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(VehicleRegistration.this, "Failed to register vehicle", Toast.LENGTH_SHORT).show();
                    });
        }
    }

}
