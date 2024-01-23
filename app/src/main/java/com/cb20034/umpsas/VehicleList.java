package com.cb20034.umpsas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.List;

public class VehicleList extends AppCompatActivity {

    private ListView listViewVehicles;
    private SearchView searchView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private List<Vehicle> vehicleList;
    private List<Vehicle> originalVehicleList;  // New list to store unfiltered data
    private ArrayAdapter<Vehicle> vehicleAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        Toolbar toolbar = findViewById(R.id.toolbarVehicleList);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewVehicles = findViewById(R.id.listViewVehicles);
        searchView = findViewById(R.id.searchViewVehicleList);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        vehicleList = new ArrayList<>();
        originalVehicleList = new ArrayList<>();  // Initialize the new list

        // Set up the ListView and adapter
        vehicleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, vehicleList);
        listViewVehicles.setAdapter(vehicleAdapter);

        // Set up item click listener
        listViewVehicles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click, e.g., show details or perform an action
                Vehicle selectedVehicle = vehicleList.get(position);
                showVehicleDetails(selectedVehicle);
            }
        });

        // Set up search functionality
        setupSearchView();

        // Load user's vehicles from Firestore
        loadAllVehicles();
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
                filterVehicleList(newText);
                return true;
            }
        });
    }

    private void filterVehicleList(String query) {
        ArrayList<Vehicle> filteredList = new ArrayList<>();
        for (Vehicle vehicle : originalVehicleList) {
            // Adjust the condition based on your filtering criteria
            if (vehicle.getPlateNo().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(vehicle);
            }
        }

        // Update the adapter with the filtered list
        vehicleAdapter.clear();

        if (query.isEmpty()) {
            // If the query is empty, restore the original list of all vehicles
            vehicleAdapter.addAll(originalVehicleList);
        } else {
            // Otherwise, add the filtered list
            vehicleAdapter.addAll(filteredList);
        }

        vehicleAdapter.notifyDataSetChanged();
    }

    private void showVehicleDetails(Vehicle selectedVehicle) {
        Intent intent = new Intent(this, VehicleDetails.class);
        intent.putExtra("selectedVehicle", selectedVehicle);
        startActivity(intent);
    }

    private void loadAllVehicles() {
        firestore.collectionGroup("vehicles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        vehicleList.clear();
                        originalVehicleList.clear();  // Clear the original list before updating

                        for (QueryDocumentSnapshot vehicleDocument : task.getResult()) {
                            Vehicle vehicle = vehicleDocument.toObject(Vehicle.class);
                            if (vehicle != null) {
                                vehicleList.add(vehicle);
                                originalVehicleList.add(vehicle);  // Update the original list
                            }
                        }
                        vehicleAdapter.notifyDataSetChanged();
                    } else {
                        // Handle the exception or show an error message
                        Toast.makeText(this, "Error loading vehicles: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getUserId() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : "";
    }
}

