package com.cb20034.umpsas;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

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
            TextView userid = findViewById(R.id.userIdTextView);
            ImageView summonImageView = findViewById(R.id.summonImageView);

            plateNoTextView.setText("Plate Number: " + selectedSummon.getPlateNumber());
            offenceTextView.setText("Offence: " + selectedSummon.getOffence());
            locationTextView.setText("Location: " + selectedSummon.getLocation());
            fineAmountTextView.setText("Fine Amount: " + selectedSummon.getFineAmount());
            fineAmountTextView.setText("Userid: " + selectedSummon.getUserId());

            String imagePath = selectedSummon.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                // You may need to adjust the RequestOptions based on your requirements
                RequestOptions requestOptions = new RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background) // Placeholder image while loading
                        .error(R.drawable.ic_launcher_foreground); // Image to display in case of error

                Glide.with(this)
                        .load(imagePath)
                        .apply(requestOptions)
                        .into(summonImageView);
            } else {
                // Handle the case where the imagePath is empty or null
                summonImageView.setImageResource(R.drawable.blue_box);
            }
            // Add more TextViews or UI elements to display other summon details
        } else {
            // Handle the case where the selectedSummon is null
            finish();
        }
    }
}
