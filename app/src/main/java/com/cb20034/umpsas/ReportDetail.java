package com.cb20034.umpsas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
public class ReportDetail extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private Button editReportButton,deleteReportButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        Toolbar toolbar = findViewById(R.id.toolbarReportDetail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        isUserAdmin();
        // Retrieve summon details from the intent
        Report selectedReport = (Report) getIntent().getSerializableExtra("selectedReport");

        // Display summon details in TextViews
        if (selectedReport != null) {
            TextView reportID = findViewById(R.id.reportIDTV);
            TextView reportDetail = findViewById(R.id.reportDetailTV);
            TextView date = findViewById(R.id.dateTV);
            ImageView reportImageView = findViewById(R.id.reportImageView);


            reportID.setText("Report ID: " + selectedReport.getReportId());
            reportDetail.setText("Detail: " + selectedReport.getReportDetail());
            date.setText("Date: " + selectedReport.getDate());

            String imagePath = selectedReport.getImagePath();

            if (imagePath != null && !imagePath.isEmpty()) {
                // You may need to adjust the RequestOptions based on your requirements
                RequestOptions requestOptions = new RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background) // Placeholder image while loading
                        .error(R.drawable.ic_launcher_foreground); // Image to display in case of error

                Glide.with(this)
                        .load(imagePath)
                        .apply(requestOptions)
                        .into(reportImageView);
            } else {
                // Handle the case where the imagePath is empty or null
                reportImageView.setImageResource(R.drawable.blue_box);
            }

            editReportButton = findViewById(R.id.editReportButton);
            deleteReportButton = findViewById(R.id.deleteReportButton);
            editReportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the "Edit Summon" button click
                    navigateToEditReport(selectedReport);
                }
            });

            deleteReportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the "Edit Summon" button click
                    deleteReport(selectedReport.getUserId(), selectedReport.getReportId(), selectedReport.getImagePath());
                }
            });

            // Add more TextViews or UI elements to display other summon details
        } else {
            // Handle the case where the selectedSummon is null
            finish();
        }
    }
    private void navigateToEditReport(Report selectedReport) {
        if (selectedReport != null) {
            Intent intent = new Intent(ReportDetail.this, ReportEdit.class);
            intent.putExtra("selectedReport", selectedReport);
            startActivity(intent);
        }
    }
    private void deleteReport(String userId, String reportID, String oldImagePath) {
        deleteOldImage(oldImagePath);
        firestore.collection("users").document(userId).collection("reports")
                .document(reportID)
                .delete()
                .addOnSuccessListener(aVoid -> {

                    StorageReference storageRef = storage.getReference();
                    StorageReference imageRef = storageRef.child("summon_images/" + reportID);


                    // Vehicle deleted successfully
                    new Handler().postDelayed(() -> {
                        // Finish the current activity (vehicle registration)
                        finish();

                        // Start the vehicle menu activity after a 1-second delay
                        Intent intent = new Intent(ReportDetail.this, ReportList.class);
                        startActivity(intent);
                    }, 1000); // 1000 milliseconds = 1 second// 1000 milliseconds = 1 second
                    // Handle any additional logic or UI updates after deletion
                })
                .addOnFailureListener(e -> {
                    // Handle the exception or show an error message
                    Toast.makeText(this, "Failed to delete vehicle", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteOldImage(String oldImagePath) {
        if (oldImagePath != null && !oldImagePath.isEmpty()) {
            StorageReference storageRef = storage.getReferenceFromUrl(oldImagePath);
            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Old image deleted successfully
                    Log.d("TAG", "Old image deleted successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    // Handle failure
                    Log.e("TAG", "Failed to delete old image: " + e.getMessage());
                }
            });
        }
    }
    private void handleUserType(String userType) {
        Summon selectedSummon = (Summon) getIntent().getSerializableExtra("selectedSummon");
        // Implement your logic based on the retrieved user type
        if ("Admin".equals(userType)) {
            editReportButton.setVisibility(View.INVISIBLE);
        } else {
            deleteReportButton.setVisibility(View.INVISIBLE);

        }
    }
    private boolean isUserAdmin() {
        String userId = getUserId();

        firestore.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful()) {
                        User user = userTask.getResult().toObject(User.class);
                        if (user != null) {
                            String userType = user.getUserType();
                            // Now you can use the userType as needed
                            handleUserType(userType);
                        }
                    } else {
                        // Handle the exception or show an error message
                        Toast.makeText(this, "Error loading user data: " + userTask.getException(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Return a default value or handle asynchronously if needed
        return false;
    }
    private String getUserId() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : "";
    }
}