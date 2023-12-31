package com.cb20034.umpsas;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SummonEdit extends AppCompatActivity {

    private EditText plateNumberEditText, offenceEditText, locationEditText, fineAmountEditText;
    private ImageView editedAddedPictureImageView;
    private Button confirmEditSummonButton, cancelEditSummonButton;
    private FloatingActionButton editFabAddPictureSummon;

    private Uri imageUri;

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summon_edit);

        Summon selectedSummon = (Summon) getIntent().getSerializableExtra("selectedSummon");


        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        plateNumberEditText = findViewById(R.id.editPlateNoTXT);
        offenceEditText = findViewById(R.id.editOffenceTXT);
        locationEditText = findViewById(R.id.editLocationTXT);
        fineAmountEditText = findViewById(R.id.editFineAmount);
        editedAddedPictureImageView = findViewById(R.id.editedAddedPicture);
        confirmEditSummonButton = findViewById(R.id.confirmEditSummon);
        cancelEditSummonButton = findViewById(R.id.cancelEditSummon);
        editFabAddPictureSummon = findViewById(R.id.editFabAddPictureSummon);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Handle the captured image here
                        Log.d("TAG", "Captured image successfully!");
                    } else {
                        Log.e("TAG", "Camera launch failed.");
                    }
                });
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        // Handle the selected image from gallery
                        imageUri = result;
                        editedAddedPictureImageView.setImageURI(imageUri);
                    }
                });

        editFabAddPictureSummon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickerDialog();
            }
        });

        confirmEditSummonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmEditSummon();
            }
        });

        cancelEditSummonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Close the activity
            }
        });
    }

    private void confirmEditSummon() {
        String plateNumber = plateNumberEditText.getText().toString().trim();
        String offence = offenceEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String fineAmount = fineAmountEditText.getText().toString().trim();

        if (plateNumber.isEmpty() || offence.isEmpty() || location.isEmpty() || fineAmount.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        // Additional validations if needed

        // Update summon details in Firestore
        updateSummonDetails(plateNumber, offence, location, fineAmount);
    }

    private void updateSummonDetails(String plateNumber, String offence, String location, String fineAmount) {
        // Assuming you pass the selected summon's ID and user ID from the previous activity
        String summonId = getIntent().getStringExtra("selectedSummonId");
        String selectedUserId = getIntent().getStringExtra("selectedUserId");

        if (summonId != null && selectedUserId != null) {
            DocumentReference summonRef = firestore.collection("users").document(selectedUserId)
                    .collection("summons").document(summonId);

            summonRef.update(
                    "plateNumber", plateNumber,
                    "offence", offence,
                    "location", location,
                    "fineAmount", fineAmount
                    // Add more fields as needed
            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    // Update successful
                    uploadImage(summonId); // Upload image after updating summon details
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    // Handle failure
                    Toast.makeText(SummonEdit.this, "Failed to update summon details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    private void uploadImage(String summonId) {
        if (imageUri != null) {
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child("summon_images/" + summonId);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = imageRef.putBytes(data);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Image upload successful
                            Toast.makeText(SummonEdit.this, "Summon details updated successfully", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity
                        } else {
                            // Handle failure
                            Toast.makeText(SummonEdit.this, "Failed to upload image: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // No image selected, just finish the activity
            Toast.makeText(this, "Summon details updated successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
        }
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == 0) {
                    openCamera();
                } else {
                    openGallery();
                }
            }
        });
        builder.show();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }

    private void openGallery() {
        galleryLauncher.launch("image/*");
    }
}
