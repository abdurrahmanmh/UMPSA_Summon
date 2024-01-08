package com.cb20034.umpsas;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;

public class ReceiptDetail extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton fabDownloadPDF;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_detail);
        Toolbar toolbar = findViewById(R.id.toolbarReceiptDetail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Receipt selectedReceipt = (Receipt) getIntent().getSerializableExtra("selectedReceipt");

        if (selectedReceipt != null) {
            TextView receiptId = findViewById(R.id.textViewReceiptId);
            TextView payDate = findViewById(R.id.textViewPayDate);
            TextView summonId = findViewById(R.id.textViewSummonId);
            TextView offenceDate = findViewById(R.id.textViewOffenceDate);
            TextView offence = findViewById(R.id.textViewOffence);
            TextView paymentMethod = findViewById(R.id.textViewPaymentMethod);
            TextView amountPaid = findViewById(R.id.textViewAmountPaid);

            FloatingActionButton fabDownloadPDF = findViewById(R.id.fabDownloadPDF);

            receiptId.setText("ID : "+selectedReceipt.getReceiptId());
            payDate.setText("Payment Date : " + selectedReceipt.getPayDate());
            summonId.setText("Summon ID : "+selectedReceipt.getSummonId());
            offenceDate.setText("Date of Summon : "+selectedReceipt.getOffenceDate());
            offence.setText("Offence : "+ selectedReceipt.getOffence());
            paymentMethod.setText("Payment Method : "+selectedReceipt.getPaymentMethod());
            amountPaid.setText("Amount Paid : RM"+selectedReceipt.getAmountPaid());


            fabDownloadPDF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the "Edit Summon" button click
                    saveAsPDF(selectedReceipt);
                }
            });
        }else{
        finish();
        }
    }
    private void saveAsPDF(Receipt selectedReceipt) {
        try {
            // Get the external files directory
            File externalFilesDir = getExternalFilesDir(null);

            if (externalFilesDir != null) {
                // Create a PdfDocument instance
                String pdfPath = externalFilesDir.getAbsolutePath() + "/receipt.pdf";
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(pdfPath));

                // Create a Document instance
                Document document = new Document(pdfDocument);

                // Add receipt details to the PDF
                document.add(new Paragraph("Receipt ID: " + selectedReceipt.getReceiptId()));
                document.add(new Paragraph("Payment Date: " + selectedReceipt.getPayDate()));
                document.add(new Paragraph("Summon ID: " + selectedReceipt.getSummonId()));
                document.add(new Paragraph("Payment Method : " + selectedReceipt.getPaymentMethod()));
                document.add(new Paragraph("Amount Paid : RM" + selectedReceipt.getAmountPaid()));
                // Add other receipt details as needed

                // Close the Document
                document.close();

                // Show a Toast indicating successful PDF creation
                Toast.makeText(this, "PDF saved successfully at" +pdfPath, Toast.LENGTH_SHORT).show();
            } else {
                // Handle the case where externalFilesDir is null
                Toast.makeText(this, "Error: External storage not available", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Show a Toast indicating an error
            Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show();
        }
    }

}