package com.cb20034.umpsas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class VehicleList extends AppCompatActivity {

    private ListView listViewVehicles;
    private SearchView searchView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private List<Vehicle> vehicleList;
    private ArrayAdapter<Vehicle> vehicleAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        listViewVehicles = findViewById(R.id.listViewVehicles);
        searchView = findViewById(R.id.searchView);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        vehicleList = new ArrayList<>();

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
        for (Vehicle vehicle : vehicleList) {
            // Adjust the condition based on your filtering criteria
            if (vehicle.getPlateNo().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(vehicle);
            }
        }

        // Update the adapter with the filtered list
        vehicleAdapter.clear();
        vehicleAdapter.addAll(filteredList);
        vehicleAdapter.notifyDataSetChanged();
    }

    private void showVehicleDetails(Vehicle selectedVehicle) {
        Intent intent = new Intent(this, VehicleDetails.class);
        intent.putExtra("selectedVehicle", selectedVehicle);
        startActivity(intent);
    }


    private void loadAllVehicles() {
        firestore.collection("users")
                .document(getUserId())
                .collection("vehicles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        vehicleList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Vehicle vehicle = document.toObject(Vehicle.class);
                            if (vehicle != null) {
                                vehicleList.add(vehicle);
                            }
                        }
                        vehicleAdapter.notifyDataSetChanged();
                    } else {
                        // Handle the exception or show an error message
                    }
                });
    }

    private String getUserId() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : "";
    }
}
