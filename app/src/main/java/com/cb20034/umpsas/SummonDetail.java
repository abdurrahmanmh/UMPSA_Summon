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


import androidx.annotation.Nullable;
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

public class SummonDetail extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private Button editSummonButton,deleteSummonButton,paySummonButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summon_detail);

        Toolbar toolbar = findViewById(R.id.toolbarSummonDetail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Retrieve summon details from the intent
        Summon selectedSummon = (Summon) getIntent().getSerializableExtra("selectedSummon");

        isUserAdmin();
        // Display summon details in TextViews
        if (selectedSummon != null) {
            TextView plateNoTextView = findViewById(R.id.plateNoSummonDetail);
            TextView offenceTextView = findViewById(R.id.offenceSummonDetail);
            TextView locationTextView = findViewById(R.id.locationSummonDetail);
            TextView fineAmountTextView = findViewById(R.id.fineAmountSummonDetail);
            TextView status = findViewById(R.id.statusSummonDetail);
            ImageView summonImageView = findViewById(R.id.summonImageView);


            plateNoTextView.setText("Plate Number: " + selectedSummon.getPlateNumber());
            offenceTextView.setText("Offence: " + selectedSummon.getOffence());
            locationTextView.setText("Location: " + selectedSummon.getLocation());
            fineAmountTextView.setText("Fine Amount: RM" + selectedSummon.getFineAmount());
            status.setText("Status: " + selectedSummon.getStatus());

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

            editSummonButton = findViewById(R.id.editSummonButton);
            deleteSummonButton = findViewById(R.id.deleteSummonButton);
            paySummonButton = findViewById(R.id.paySummonButton);
            editSummonButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the "Edit Summon" button click
                    navigateToEditSummon(selectedSummon);
                }
            });

            deleteSummonButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the "Edit Summon" button click
                    deleteSummon(selectedSummon.getUserId(), selectedSummon.getTimestamp(), selectedSummon.getImagePath());
                }
            });
            paySummonButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the "Edit Summon" button click
                    navigateToPayment(selectedSummon);
                }
            });

            // Add more TextViews or UI elements to display other summon details
        } else {
            // Handle the case where the selectedSummon is null
            finish();
        }
    }
    private void navigateToEditSummon(Summon selectedSummon) {
        if (selectedSummon != null) {
            Intent intent = new Intent(SummonDetail.this, SummonEdit.class);
            intent.putExtra("selectedSummon", selectedSummon);
            startActivity(intent);
        }
    }
    private void navigateToPayment(Summon selectedSummon) {
        if (selectedSummon != null) {
            Intent intent = new Intent(SummonDetail.this, PaymentGetaway.class);
            intent.putExtra("selectedSummon", selectedSummon);
            startActivity(intent);
        }
    }
    private void deleteSummon(String userId, String summonId, String oldImagePath) {
        deleteOldImage(oldImagePath);
        firestore.collection("users").document(userId).collection("summons")
                .document(summonId)
                .delete()
                .addOnSuccessListener(aVoid -> {

                    StorageReference storageRef = storage.getReference();
                    StorageReference imageRef = storageRef.child("summon_images/" + summonId);


                    // Vehicle deleted successfully
                    new Handler().postDelayed(() -> {
                        // Finish the current activity (vehicle registration)
                        finish();

                        // Start the vehicle menu activity after a 1-second delay
                        Intent intent = new Intent(SummonDetail.this, SummonList.class);
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
            paySummonButton.setVisibility(View.INVISIBLE);
        } else {
            editSummonButton.setVisibility(View.INVISIBLE);
            deleteSummonButton.setVisibility(View.INVISIBLE);

            if("Paid".equals(selectedSummon.getStatus())){
                paySummonButton.setVisibility(View.INVISIBLE);
            }
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
