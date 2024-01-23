package com.cb20034.umpsas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class VehicleDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

        Toolbar toolbar = findViewById(R.id.toolbarVehicleDetail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent.hasExtra("selectedVehicle")) {
            Vehicle selectedVehicle = (Vehicle) intent.getSerializableExtra("selectedVehicle");

            // Display details about the selected vehicle and its owner
            TextView plateNoTextView = findViewById(R.id.plateNoTextView);
            TextView brandTextView = findViewById(R.id.brandTextView);
            TextView modelTextView = findViewById(R.id.modelTextView);
            TextView userNameTextView = findViewById(R.id.userNameTextView);
            TextView userIdTextView = findViewById(R.id.userIdTextView);
            TextView userTypeView = findViewById(R.id.userTypeView);
            TextView userPhoneTextView = findViewById(R.id.userPhoneTextView);
            TextView userIdEmailView = findViewById(R.id.userIdEmailView);

            if (selectedVehicle != null) {
                plateNoTextView.setText("Plate Number: " + selectedVehicle.getPlateNo());
                brandTextView.setText("Brand : " + selectedVehicle.getBrand());
                modelTextView.setText("Model : "+selectedVehicle.getModel());

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Reference to the "users" collection (replace with your collection name)
                db.collection("users").document(selectedVehicle.getUserId()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        // User data retrieved successfully
                                        User user = document.toObject(User.class);
                                        String userType = user.getUserType();

                                        // Set the welcome message in the TextView
                                        userNameTextView.setText("Owner : " + user.getName());
                                        userIdTextView.setText("ID : "+user.getId());
                                        userTypeView.setText("User : "+user.getUserType());
                                        userPhoneTextView.setText("Phone : "+user.getPhoneNo());
                                        userIdEmailView.setText("Email : "+ user.getEmail());

                                    }
                                } else {

                                    // Handle failures
                                }
                            }
                        });

                // Add more TextViews or UI elements to display other details about the vehicle and its owner
            } else {
                // Handle the case where the selectedVehicle is null
                finish();
            }
        } else {
            // Handle the case where the intent does not contain the expected data
            finish();
        }
    }
}