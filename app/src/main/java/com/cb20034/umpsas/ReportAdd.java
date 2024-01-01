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

public class ReportAdd extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private EditText reportDetailEditText;
    private ImageView addedPictureReport;
    private Button cancelReport,confirmReport;
    private FloatingActionButton fabAddPictureReport;
    private Uri imageUri;

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_add);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        reportDetailEditText = findViewById(R.id.reportDetailEditText);
        addedPictureReport = findViewById(R.id.addedPictureReport);
        confirmReport = findViewById(R.id.confirmReport);
        cancelReport = findViewById((R.id.cancelReport));

        fabAddPictureReport = findViewById(R.id.fabAddPictureReport);


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
                        addedPictureReport.setImageURI(imageUri);
                    }
                });


        fabAddPictureReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showImagePickerDialog();
            }
        });

        confirmReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if the plate number exists in the vehicles collection
                addReport();


            }
        });


    }
    public void addReport() {
        String reportDetail = reportDetailEditText.getText().toString();

        String userId = getUserId();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String date = dateFormat.format(calendar.getTime());

        String timestamp = generateTimestamp();
        Report report = new Report(userId, timestamp, reportDetail, "", date);

        firestore.collection("users").document(userId).collection("reports").document(timestamp)
                .set(report)
                .addOnSuccessListener(aVoid -> {
                    uploadImage(userId, timestamp);
                }) .addOnFailureListener(e -> {
                    Toast.makeText(ReportAdd.this, "Failed to add report", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImage(final String userId, String timestamp) {
        if (imageUri != null) {
            StorageReference storageReference = storage.getReference().child("report_images/" + System.currentTimeMillis() + ".jpg");

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos); // Compress the image

                byte[] data = baos.toByteArray();

                UploadTask uploadTask = storageReference.putBytes(data);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Image uploaded successfully
                            getDownloadUrlAndSaveSummon(userId, timestamp, storageReference);
                        } else {
                            // Handle upload failure
                            Toast.makeText(ReportAdd.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(ReportAdd.this, "Please add a picture", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDownloadUrlAndSaveSummon(final String userId, final String timestamp, StorageReference storageReference) {
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Update the summon with the image path
                firestore.collection("users").document(userId)
                        .collection("reports").document(timestamp)
                        .update("imagePath", uri.toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ReportAdd.this, "Report added successfully!", Toast.LENGTH_SHORT).show();

                                    // You can also navigate to another activity, finish the current activity, etc.
                                    // Example: Navigate to the main activity
                                    Intent intent = new Intent(ReportAdd.this, MainMenu.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(ReportAdd.this, "Report image failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
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

    private String generateTimestamp() {
        // Get the current date and time
        Date date = new Date();

        // Specify the format for the timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

        // Format the date and time to create the timestamp
        return dateFormat.format(date);
    }
    private String getUserId() {
        FirebaseUser currentUser = auth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : "";
    }
}