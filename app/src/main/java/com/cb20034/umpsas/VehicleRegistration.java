package com.cb20034.umpsas;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class VehicleRegistration extends AppCompatActivity {

    private EditText licenseValidDateText;
    private Spinner spinnerVehicleType, spinnerAcademicYear;
    private TextInputEditText brandText, modelText, colorText, plateNoText;
    private Button cancelButton, confirmButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_registration);

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        licenseValidDateText = findViewById(R.id.LicenseValidDateText);
        spinnerVehicleType = findViewById(R.id.spinnerVehicleType);
        spinnerAcademicYear = findViewById(R.id.spinnerAcademicYear);
        brandText = findViewById(R.id.brandText);
        modelText = findViewById(R.id.modelText);
        colorText = findViewById(R.id.ColorText);
        plateNoText = findViewById(R.id.plateNoText);
        cancelButton = findViewById(R.id.cancelButton);
        confirmButton = findViewById(R.id.confirmButton);

        // Set up a click listener for the date text field
        licenseValidDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

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
            String vehicleType = spinnerVehicleType.getSelectedItem().toString();
            String brand = brandText.getText().toString();
            String model = modelText.getText().toString();
            String color = colorText.getText().toString();
            String licenseValidDate = licenseValidDateText.getText().toString();
            String plateNo = plateNoText.getText().toString();
            String academicYear = spinnerAcademicYear.getSelectedItem().toString();

            // Create a Map to store vehicle details
            Map<String, Object> vehicleData = new HashMap<>();
            vehicleData.put("vehicleType", vehicleType);
            vehicleData.put("brand", brand);
            vehicleData.put("model", model);
            vehicleData.put("color", color);
            vehicleData.put("licenseValidDate", licenseValidDate);
            vehicleData.put("plateNo", plateNo);
            vehicleData.put("academicYear", academicYear);

            // Add the vehicle details to Firestore
            firestore.collection("users").document(userId).collection("vehicles")
                    .add(vehicleData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(VehicleRegistration.this, "Vehicle registered successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(VehicleRegistration.this, "Failed to register vehicle", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}