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
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SummonAdd extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private EditText plateNumberEditText, offenceEditText, locationEditText, fineAmountEditText, dateSummon;
    private ImageView addedPictureImageView;
    private Button addSummonButton,cancelSummonButton;
    private FloatingActionButton fabAddPicture;

    private Uri imageUri;

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summon_add);

        Toolbar toolbar = findViewById(R.id.toolbarSummonAdd);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        plateNumberEditText = findViewById(R.id.plateNoTXT);
        offenceEditText = findViewById(R.id.offenceTXT);
        locationEditText = findViewById(R.id.locationTXT);
        fineAmountEditText = findViewById(R.id.fineAmount);
        addedPictureImageView = findViewById(R.id.addedPicture);
        addSummonButton = findViewById(R.id.confirmSummon);
        cancelSummonButton = findViewById(R.id.cancelSummon);
        fabAddPicture = findViewById(R.id.fabAddPictureSummon);


        dateSummon = findViewById(R.id.dateSummon);

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
                        addedPictureImageView.setImageURI(imageUri);
                    }
                });


        fabAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showImagePickerDialog();
            }
        });

        addSummonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if the plate number exists in the vehicles collection
                getVehicleUserID();


            }
        });
        cancelSummonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
        dateSummon.setOnClickListener(new View.OnClickListener() {
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
                        dateSummon.setText(selectedDate);
                    }
                },
                year, month, day
        );

        // Show the date picker dialog
        datePickerDialog.show();
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



    private void getVehicleUserID() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String plateNumber = plateNumberEditText.getText().toString();

        Query vehiclesQuery = db.collectionGroup("vehicles");
        vehiclesQuery
                .whereEqualTo("plateNo", plateNumber)  // Filter by plateNumber instead of document ID
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot vehicleDoc = queryDocumentSnapshots.getDocuments().get(0);
                        String userId = vehicleDoc.getString("userId");
                        // ... retrieve other vehicle details
                        Toast.makeText(SummonAdd.this, "Summon added successfully!"+userId, Toast.LENGTH_SHORT).show();
                        addSummon(userId);
                    } else {
                        // Handle no matching vehicle
                        Toast.makeText(SummonAdd.this, "Plate Number Does not exist!", Toast.LENGTH_SHORT).show();
                    }
                })

                .addOnFailureListener(e -> {
                    Log.e("TAG", "Query failed: " + e.getMessage());
                    Toast.makeText(SummonAdd.this, "rosak", Toast.LENGTH_SHORT).show();
                });
    }

    public void addSummon(String userId) {
        String offence = offenceEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String fineAmount = fineAmountEditText.getText().toString();
        String plateNumber = plateNumberEditText.getText().toString().trim();
        String date = dateSummon.getText().toString();
        String status = "Unpaid";

        String timestamp = generateTimestamp();
        Summon summon = new Summon(plateNumber, offence, location, fineAmount, "", userId,status,date,timestamp);

        firestore.collection("users").document(userId).collection("summons").document(timestamp)
                .set(summon)
                .addOnSuccessListener(aVoid ->
                {
                    sendFCMNotification(userId);
                    uploadImage(userId, timestamp);

                }) .addOnFailureListener(e -> {
                    Toast.makeText(SummonAdd.this, "Failed to add summon", Toast.LENGTH_SHORT).show();
                });
    }
    private String generateTimestamp() {
        // Get the current date and time
        Date date = new Date();

        // Specify the format for the timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

        // Format the date and time to create the timestamp
        return dateFormat.format(date);
    }
    private void uploadImage(final String userId, String timestamp) {
        if (imageUri != null) {
            StorageReference storageReference = storage.getReference().child("summon_images/" + System.currentTimeMillis() + ".jpg");

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
                            Toast.makeText(SummonAdd.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(SummonAdd.this, "Please add a picture", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(SummonAdd.this, "Summon added successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                    // You can also navigate to another activity, finish the current activity, etc.
                                    // Example: Navigate to the main activity
                                    Intent intent = new Intent(SummonAdd.this, SummonList.class);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(SummonAdd.this, "Summon update failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
    private void sendFCMNotification(String userId) {
        FirebaseFirestore.getInstance().collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userFCMToken = documentSnapshot.getString("fcmToken");
                        Log.d("TAG", "User FCM Token: " + userFCMToken);
                        if (userFCMToken != null && !userFCMToken.isEmpty()) {
                            // Create a notification payload
                            Map<String, String> data = new HashMap<>();
                            data.put("title", "Summon Notification");
                            data.put("body", "You have received a new summon.");
                            data.put("plateNumber", plateNumberEditText.getText().toString());
                            data.put("offense", offenceEditText.getText().toString());
                            // Add other summon details as needed

                            // Send the FCM message
                            FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(userFCMToken)
                                    .setData(data)
                                    .build());
                        } else {
                            // Log tag for the case where userFCMToken is empty
                            Log.e("TAG", "Empty or null FCM token for user with ID: " + userId);
                        }
                    } else {
                        // Log tag for the case where the document does not exist
                        Log.e("TAG", "Document does not exist for user with ID: " + userId);
                    }
                })
                .addOnFailureListener(e -> {
                    // Log tag for the failure to get user data
                    Log.e("TAG", "Failed to get user data: " + e.getMessage());
                });
    }




}

