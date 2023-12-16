package com.cb20034.umpsas;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class VehicleEdit extends AppCompatActivity {

    private Button cancelEditButton;
    private Button confirmEditButton;
    private EditText licenseValidDateText;
    private String userId; // Store the user ID here
    private String vehicleId; // Store the vehicle ID here
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_edit);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Get the user ID from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            checkVehicleRegistration(currentUser.getUid());
            userId = currentUser.getUid();

        } else {
            // Handle the case when the user is not logged in
            // You might want to navigate the user back to the login screen or take appropriate action
            finish();
            return;
        }

        cancelEditButton = findViewById(R.id.cancelEditButton);
        confirmEditButton = findViewById(R.id.confirmEditButton);
        licenseValidDateText = findViewById(R.id.LicenseValidDateText);

        // Set up click listeners
        licenseValidDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        cancelEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancel editing and go back
                finish();
            }
        });

        confirmEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve values from the widgets
                Spinner vehicleTypeSpinner = findViewById(R.id.spinnerVehicleType);
                EditText brandText = findViewById(R.id.brandText);
                EditText modelText = findViewById(R.id.modelText);
                EditText colorText = findViewById(R.id.ColorText);
                EditText plateNoText = findViewById(R.id.plateNoText);
                Spinner academicYearSpinner = findViewById(R.id.spinnerAcademicYear);

                String selectedVehicleType = vehicleTypeSpinner.getSelectedItem().toString();
                String brand = brandText.getText().toString();
                String model = modelText.getText().toString();
                String color = colorText.getText().toString();
                String licenseValidDate = licenseValidDateText.getText().toString();
                String plateNo = plateNoText.getText().toString();
                String selectedAcademicYear = academicYearSpinner.getSelectedItem().toString();

                // TODO: Validate the input fields, handle errors if needed

                // Update the Firestore document with the edited values
                updateFirestoreDocument(selectedVehicleType, brand, model, color, licenseValidDate, plateNo, selectedAcademicYear);

                // Close the activity after updating
                finish();
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

    private void updateFirestoreDocument(String vehicleType, String brand, String model, String color,
                                         String licenseValidDate, String plateNo, String academicYear) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference vehicleRef = db.collection("users").document(userId).collection("vehicles").document(vehicleId);

        vehicleRef.update(
                "vehicleType", vehicleType,
                "brand", brand,
                "model", model,
                "color", color,
                "licenseValidDate", licenseValidDate,
                "plateNo", plateNo,
                "academicYear", academicYear
        ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Update successful! You can handle it here (e.g., show a toast, close the activity)
                Toast.makeText(VehicleEdit.this, "Vehicle updated successfully!", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> {
                    // Finish the current activity (vehicle registration)
                    finish();

                    // Start the vehicle menu activity after a 1-second delay
                    Intent intent = new Intent(VehicleEdit.this, VehicleMenu.class);
                    startActivity(intent);
                }, 1000); // Close the activity after successful update
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Update failed! You can handle the error here (e.g., show a toast)
                Toast.makeText(VehicleEdit.this, "Error updating vehicle", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkVehicleRegistration(String userId) {
        firestore.collection("users").document(userId).collection("vehicles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Check if the user has a registered vehicle
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            // User has a registered vehicle, show the details
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            vehicleId = documentSnapshot.getId(); // Get ID from the first document

                        } else {
                            Toast.makeText(VehicleEdit.this, "No vehicle", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle the exception or show an error message
                    }
                });
    }
}
