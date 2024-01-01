package com.cb20034.umpsas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentGetaway extends AppCompatActivity {
    private Button buttonPay;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_getaway);
        Summon selectedSummon = (Summon) getIntent().getSerializableExtra("selectedSummon");

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        buttonPay  =findViewById(R.id.buttonPay);


        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentUpdate();
            }
        });
    }

    private void paymentUpdate() {
        Summon selectedSummon = (Summon) getIntent().getSerializableExtra("selectedSummon");

        String summonId = selectedSummon.getTimestamp();
        String selectedUserId = selectedSummon.getUserId();

        String updatedStatus = "Paid";

        if (summonId != null && selectedUserId != null) {
            DocumentReference summonRef = firestore.collection("users").document(selectedUserId)
                    .collection("summons").document(summonId);

            summonRef.update(
                    "status", updatedStatus
                    // Add more fields as needed
            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    addReceipt(selectedUserId,selectedSummon );
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    // Handle failure
                    Toast.makeText(PaymentGetaway.this, "Failed to pay summon: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void addReceipt(String userId, Summon selectedSummon) {


        String offence = selectedSummon.getOffence();
        String offenceLocation = selectedSummon.getLocation();
        String offenceDate = selectedSummon.getDate();
        String plateNo = selectedSummon.getPlateNumber();
        String summonId = selectedSummon.getTimestamp();
        String paymentMethod = "FPX";
        String amountPaid = selectedSummon.getFineAmount();
        String receiptId= generateTimestamp();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String payDate = dateFormat.format(calendar.getTime());


        Receipt receipt = new Receipt(plateNo, summonId, offenceDate, offenceLocation, payDate,offence,paymentMethod,amountPaid,userId,receiptId);

        firestore.collection("users").document(userId).collection("receipts").document(receiptId)
                .set(receipt)
                .addOnSuccessListener(aVoid -> {

                    Toast.makeText(PaymentGetaway.this, "Receipt Generated!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PaymentGetaway.this, MainMenu.class);
                    startActivity(intent);

                    finish();
                }) .addOnFailureListener(e -> {
                    Toast.makeText(PaymentGetaway.this, "Failed to Pay", Toast.LENGTH_SHORT).show();
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

}