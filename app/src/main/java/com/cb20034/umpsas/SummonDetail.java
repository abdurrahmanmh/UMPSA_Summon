package com.cb20034.umpsas;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SummonDetail extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summon_detail);

        // Retrieve summon details from the intent
        Summon selectedSummon = (Summon) getIntent().getSerializableExtra("selectedSummon");

        // Display summon details in TextViews
        if (selectedSummon != null) {
            TextView plateNoTextView = findViewById(R.id.plateNoTextView);
            TextView offenceTextView = findViewById(R.id.offenceTextView);
            TextView locationTextView = findViewById(R.id.locationTextView);
            TextView fineAmountTextView = findViewById(R.id.fineAmountTextView);

            plateNoTextView.setText("Plate Number: " + selectedSummon.getPlateNumber());
            offenceTextView.setText("Offence: " + selectedSummon.getOffence());
            locationTextView.setText("Location: " + selectedSummon.getLocation());
            fineAmountTextView.setText("Fine Amount: " + selectedSummon.getFineAmount());

            // Add more TextViews or UI elements to display other summon details
        } else {
            // Handle the case where the selectedSummon is null
            finish();
        }
    }
}
