package com.cb20034.umpsas;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import java.util.Calendar;
import java.util.Date;

public class SummonEdit extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private EditText plateNumberEditText, offenceEditText, locationEditText, fineAmountEditText, editDateSummon;
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
        editDateSummon = findViewById(R.id.editDateSummon);

        //old field to edit
        plateNumberEditText.setText(selectedSummon.getPlateNumber());
        offenceEditText.setText(selectedSummon.getOffence());
        locationEditText.setText(selectedSummon.getLocation());
        fineAmountEditText.setText(selectedSummon.getFineAmount());


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
        editDateSummon.setOnClickListener(new View.OnClickListener() {
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
                        editDateSummon.setText(selectedDate);
                    }
                },
                year, month, day
        );

        // Show the date picker dialog
        datePickerDialog.show();
    }
    private void confirmEditSummon() {
        String plateNumber = plateNumberEditText.getText().toString().trim();
        String offence = offenceEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String fineAmount = fineAmountEditText.getText().toString().trim();
        String date = editDateSummon.getText().toString().trim();

        if (plateNumber.isEmpty() || offence.isEmpty() || location.isEmpty() || fineAmount.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        // Additional validations if needed

        // Update summon details in Firestore
        updateSummonDetails(plateNumber, offence, location, fineAmount, date);
    }

    private void updateSummonDetails(String plateNumber, String offence, String location, String fineAmount,String date) {
        Summon selectedSummon = (Summon) getIntent().getSerializableExtra("selectedSummon");

        String summonId = selectedSummon.getTimestamp();
        String selectedUserId = selectedSummon.getUserId();
        String oldImagePath = selectedSummon.getImagePath();

        if (summonId != null && selectedUserId != null) {
            DocumentReference summonRef = firestore.collection("users").document(selectedUserId)
                    .collection("summons").document(summonId);

            summonRef.update(
                    "plateNumber", plateNumber,
                    "offence", offence,
                    "location", location,
                    "fineAmount", fineAmount,
                    "date", date
                    // Add more fields as needed
            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    // Update successful
                    uploadImage(summonId, oldImagePath, selectedUserId); // Upload image after updating summon details
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



    private void uploadImage(String summonId, String oldImagePath, String userId) {
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
                            deleteOldImage(oldImagePath);
                            getDownloadUrlAndSaveSummon(userId, summonId, imageRef);
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
    private void getDownloadUrlAndSaveSummon(final String userId, final String timestamp, StorageReference storageReference) {
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Update the summon with the image path
                firestore.collection("users").document(userId)
                        .collection("summons").document(timestamp)
                        .update("imagePath", uri.toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SummonEdit.this, "Summon Edited successfully!", Toast.LENGTH_SHORT).show();

                                    // You can also navigate to another activity, finish the current activity, etc.
                                    // Example: Navigate to the main activity
                                    Intent intent = new Intent(SummonEdit.this, SummonList.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SummonEdit.this, "Summon update failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
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
