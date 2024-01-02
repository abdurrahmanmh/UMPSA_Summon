package com.cb20034.umpsas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReceiptList extends AppCompatActivity {

    private ListView listViewReceipts;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private List<Receipt> receiptList;
    private List<Receipt> originalReceiptList;
    private ArrayAdapter<Receipt> receiptAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_list);

        Toolbar toolbar = findViewById(R.id.toolbarReceiptList);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        listViewReceipts = findViewById(R.id.listViewReceipts);
        receiptList = new ArrayList<>();
        originalReceiptList = new ArrayList<>();


        receiptAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, receiptList);
        listViewReceipts.setAdapter(receiptAdapter);

        listViewReceipts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Receipt selectedReceipt = receiptList.get(position);
                showReceiptDetails(selectedReceipt);
            }
        });

        loadAllReceipts();


    }

    private void loadAllReceipts() {
        String userId = getUserId();


        firestore.collection("users")
                .document(userId)
                .collection("receipts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        receiptList.clear();
                        originalReceiptList.clear();  // Clear the original list before updating

                        for (QueryDocumentSnapshot receiptDocument : task.getResult()) {
                            Receipt receipt = receiptDocument.toObject(Receipt.class);
                            if (receipt != null) {
                                receiptList.add(receipt);
                                originalReceiptList.add(receipt);  // Update the original list
                            }
                        }
                        receiptAdapter.notifyDataSetChanged();
                    } else {
                        // Handle the exception or show an error message
                        Toast.makeText(this, "Error loading Receipt: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showReceiptDetails(Receipt selectedReceipt) {
        if (selectedReceipt != null) {
            Intent intent = new Intent(this, ReceiptDetail.class);
            intent.putExtra("selectedReceipt", selectedReceipt);
            startActivity(intent);
        } else {
            // Handle the case where the selectedSummon is null
            Toast.makeText(this, "Selected receipt is null", Toast.LENGTH_SHORT).show();
        }
    }
    private String getUserId() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : "";
    }
}