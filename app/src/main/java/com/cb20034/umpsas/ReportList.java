package com.cb20034.umpsas;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReportList extends AppCompatActivity {
    private Button addReportButton;
    private ListView listViewReports;
    private FirebaseFirestore firestore;
    private SearchView searchView;
    private FirebaseAuth firebaseAuth;
    private List<Report> reportList;
    private List<Report> originalReportList;
    private ArrayAdapter<Report> reportAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbarReportList);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewReports = findViewById(R.id.listViewReports);
        searchView = findViewById(R.id.searchViewReport);
        reportList = new ArrayList<>();
        originalReportList = new ArrayList<>();

        addReportButton = findViewById(R.id.addReportButton);
        reportAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reportList);
        listViewReports.setAdapter(reportAdapter);
        isUserAdmin();
        listViewReports.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Report selectedReport = reportList.get(position);
                showReportDetails(selectedReport);
            }
        });
        addReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start SummonsManagement.class or Summons.class based on user type
                Intent intent = new Intent(ReportList.this, ReportAdd.class);
                startActivity(intent);
            }
        });

        loadAllReports();
        setupSearchView();

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
    private void handleUserType(String userType) {
        // Implement your logic based on the retrieved user type
        if ("Admin".equals(userType)) {
            addReportButton.setVisibility(View.INVISIBLE);
        } else {

            searchView.setVisibility(View.INVISIBLE);
        }
    }
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform the final search (if needed)
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the list based on the entered text
                filterReportList(newText);
                return true;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // The search view gained focus, show the date picker
                    showDatePickerDialog();
                }
            }
        });
    }
    private void filterReportList(String query) {
        ArrayList<Report> filteredList = new ArrayList<>();
        for (Report report : originalReportList) {
            // Adjust the condition based on your filtering criteria
            if (report.getDate().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(report);
            }
        }// Update the adapter with the filtered list
        reportAdapter.clear();

        if (query.isEmpty()) {
            reportAdapter.addAll(originalReportList);
        } else {
            // Otherwise, add the filtered list
            reportAdapter.addAll(filteredList);
        }

        reportAdapter.notifyDataSetChanged();
    }
    private void loadAllReports() {
        String userId = getUserId();

        firestore.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful()) {
                        User user = userTask.getResult().toObject(User.class);
                        if (user != null) {
                            String userType = user.getUserType();

                            if ("Admin".equals(userType)) {
                                // Admin users can see all summons
                                loadAllReportsForAdmin();
                            } else {
                                // Non-admin users can see only their summons
                                loadUserReports(userId);
                            }
                        }
                    } else {
                        // Handle the exception or show an error message
                        Toast.makeText(this, "Error loading user data: " + userTask.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void loadAllReportsForAdmin() {
        firestore.collectionGroup("reports")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reportList.clear();
                        originalReportList.clear();  // Clear the original list before updating

                        for (QueryDocumentSnapshot reportDocument : task.getResult()) {
                            Report report = reportDocument.toObject(Report.class);
                            if (report != null) {
                                reportList.add(report);
                                originalReportList.add(report);  // Update the original list
                            }
                        }
                        reportAdapter.notifyDataSetChanged();
                    } else {
                        // Handle the exception or show an error message
                        Toast.makeText(this, "Error loading Summon: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void loadUserReports(String userId) {
        firestore.collection("users")
                .document(userId)
                .collection("reports")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reportList.clear();
                        originalReportList.clear();  // Clear the original list before updating

                        for (QueryDocumentSnapshot reportDocument : task.getResult()) {
                            Report report = reportDocument.toObject(Report.class);
                            if (report != null) {
                                reportList.add(report);
                                originalReportList.add(report);  // Update the original list
                            }
                        }
                        reportAdapter.notifyDataSetChanged();
                    } else {
                        // Handle the exception or show an error message
                        Toast.makeText(this, "Error loading Summon: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showReportDetails(Report selectedReport) {
        if (selectedReport != null) {
            Intent intent = new Intent(this, ReportDetail.class);
            intent.putExtra("selectedReport", selectedReport);
            startActivity(intent);
        } else {
            // Handle the case where the selectedSummon is null
            Toast.makeText(this, "Selected report is null", Toast.LENGTH_SHORT).show();
        }
    }
    private String getUserId() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : "";
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
                        searchView.setQuery(selectedDate, false);
                    }
                },
                year, month, day
        );

        // Show the date picker dialog
        datePickerDialog.show();
    }
}