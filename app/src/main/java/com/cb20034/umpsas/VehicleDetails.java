package com.cb20034.umpsas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class VehicleDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

        Intent intent = getIntent();
        if (intent.hasExtra("selectedVehicle")) {
            Vehicle selectedVehicle = (Vehicle) intent.getSerializableExtra("selectedVehicle");

            // Display details about the selected vehicle and its owner
            TextView plateNoTextView = findViewById(R.id.plateNoTextView);
            TextView brandModelTextView = findViewById(R.id.brandModelTextView);

            if (selectedVehicle != null) {
                plateNoTextView.setText("Plate Number: " + selectedVehicle.getPlateNo());
                String vehicleBrandText = selectedVehicle.getBrand() + " " + selectedVehicle.getModel();
                brandModelTextView.setText("Brand and Model: " + vehicleBrandText);

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