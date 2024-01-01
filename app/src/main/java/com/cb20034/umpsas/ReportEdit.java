package com.cb20034.umpsas;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;
public class ReportEdit extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private EditText reportDetailEdit;
    private ImageView editedPictureReport;
    private Button cancelEditReport,confirmEditReport;
    private FloatingActionButton fabEditPictureReport;
    private Uri imageUri;

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_edit);
        Report selectedReport = (Report) getIntent().getSerializableExtra("selectedReport");

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        reportDetailEdit = findViewById(R.id.reportDetailEdit);
        editedPictureReport = findViewById(R.id.editedPictureReport);
        confirmEditReport = findViewById(R.id.confirmEditReport);
        cancelEditReport = findViewById((R.id.cancelEditReport));
        fabEditPictureReport = findViewById(R.id.fabEditPictureReport);
        editedPictureReport = findViewById(R.id.editedPictureReport);

        reportDetailEdit.setText(selectedReport.getReportDetail());


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
                        editedPictureReport.setImageURI(imageUri);
                    }
                });


        fabEditPictureReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showImagePickerDialog();
            }
        });

        confirmEditReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if the plate number exists in the vehicles collection
                EditReport();
            }
        });

        cancelEditReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the plate number exists in the vehicles collection
                finish();
            }
        });

    }
    private void EditReport() {
        String reportDetail = reportDetailEdit.getText().toString().trim();

        if (reportDetail.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }
        // Update summon details in Firestore
        updateReportDetails(reportDetail);
    }
    private void updateReportDetails(String reportDetail) {
        Report selectedReport = (Report) getIntent().getSerializableExtra("selectedReport");

        String reportId = selectedReport.getReportId();
        String selectedUserId = selectedReport.getUserId();
        String oldImagePath = selectedReport.getImagePath();

        if (reportId != null && selectedUserId != null) {
            DocumentReference summonRef = firestore.collection("users").document(selectedUserId)
                    .collection("reports").document(reportId);

            summonRef.update(
                    "reportDetail", reportDetail
                    // Add more fields as needed
            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    // Update successful
                    uploadImage(reportId, oldImagePath, selectedUserId); // Upload image after updating summon details
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    // Handle failure
                    Toast.makeText(ReportEdit.this, "Failed to update summon details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadImage(String reportId, String oldImagePath, String userId) {
        if (imageUri != null) {
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child("report_images/" + reportId);

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
                            Toast.makeText(ReportEdit.this, "Summon details updated successfully", Toast.LENGTH_SHORT).show();
                            deleteOldImage(oldImagePath);
                            getDownloadUrlAndSaveSummon(userId, reportId, imageRef);
                            finish(); // Close the activity
                        } else {
                            // Handle failure
                            Toast.makeText(ReportEdit.this, "Failed to upload image: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

    private void getDownloadUrlAndSaveSummon(final String userId, final String reportId, StorageReference storageReference) {
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Update the summon with the image path
                firestore.collection("users").document(userId)
                        .collection("reports").document(reportId)
                        .update("imagePath", uri.toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ReportEdit.this, "Report added successfully!", Toast.LENGTH_SHORT).show();

                                    // You can also navigate to another activity, finish the current activity, etc.
                                    // Example: Navigate to the main activity
                                    Intent intent = new Intent(ReportEdit.this, MainMenu.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(ReportEdit.this, "Report image failed!", Toast.LENGTH_SHORT).show();
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
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        openCamera();
                        break;
                    case 1:
                        openGallery();
                        break;
                }
            }
        });
        builder.show();
    }
    private void openCamera() {
        try {
            Log.d("TAG", "Entering openCamera() method.");

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                Log.d("TAG", "Camera app found, launching intent.");
                cameraLauncher.launch(takePictureIntent);
            } else {
                Log.w("TAG", "No camera app found on device.");
            }
        } catch (Exception e) {
            Log.e("TAG", "Error opening camera: " + e.getMessage());
        }
    }
    private void openGallery() {
        galleryLauncher.launch("image/*");
    }


    private String getUserId() {
        FirebaseUser currentUser = auth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : "";
    }
}