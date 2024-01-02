package com.cb20034.umpsas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class SummonList extends AppCompatActivity {
    private Button addSummonButton;
    private SearchView searchView;
    private ListView listViewSummons;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private List<Summon> summonList;
    private List<Summon> originalSummonList;
    private ArrayAdapter<Summon> summonAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summon_list);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbarSummonList);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewSummons = findViewById(R.id.listViewSummons);
        searchView = findViewById(R.id.searchViewSummon);
        summonList = new ArrayList<>();
        originalSummonList = new ArrayList<>();

        addSummonButton = findViewById(R.id.addSummonButton);
        summonAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, summonList);
        listViewSummons.setAdapter(summonAdapter);

        isUserAdmin();
        // Check user type and set visibility of addSummonButton
            addSummonButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Start SummonsManagement.class or Summons.class based on user type
                    Intent intent = new Intent(SummonList.this, SummonAdd.class);
                    startActivity(intent);
                }
            });

        listViewSummons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Summon selectedSummon = summonList.get(position);
                showSummonDetails(selectedSummon);
            }
        });

        loadAllSummons();
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
            addSummonButton.setVisibility(View.VISIBLE);
        } else {
            addSummonButton.setVisibility(View.INVISIBLE);
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
                filterSummonList(newText);
                return true;
            }
        });
    }

    private void filterSummonList(String query) {
        ArrayList<Summon> filteredList = new ArrayList<>();
        for (Summon summon : originalSummonList) {
            // Adjust the condition based on your filtering criteria
            if (summon.getPlateNumber().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(summon);
            }
        }

        // Update the adapter with the filtered list
        summonAdapter.clear();

        if (query.isEmpty()) {
            summonAdapter.addAll(originalSummonList);
        } else {
            // Otherwise, add the filtered list
            summonAdapter.addAll(filteredList);
        }

        summonAdapter.notifyDataSetChanged();
    }

    private void loadAllSummons() {
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
                                loadAllSummonsForAdmin();
                            } else {
                                // Non-admin users can see only their summons
                                loadUserSummons(userId);
                            }
                        }
                    } else {
                        // Handle the exception or show an error message
                        Toast.makeText(this, "Error loading user data: " + userTask.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadAllSummonsForAdmin() {
        firestore.collectionGroup("summons")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        summonList.clear();
                        originalSummonList.clear();  // Clear the original list before updating

                        for (QueryDocumentSnapshot summonDocument : task.getResult()) {
                            Summon summon = summonDocument.toObject(Summon.class);
                            if (summon != null) {
                                summonList.add(summon);
                                originalSummonList.add(summon);  // Update the original list
                            }
                        }
                        summonAdapter.notifyDataSetChanged();
                    } else {
                        // Handle the exception or show an error message
                        Toast.makeText(this, "Error loading Summon: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUserSummons(String userId) {
        firestore.collection("users")
                .document(userId)
                .collection("summons")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        summonList.clear();
                        originalSummonList.clear();  // Clear the original list before updating

                        for (QueryDocumentSnapshot summonDocument : task.getResult()) {
                            Summon summon = summonDocument.toObject(Summon.class);
                            if (summon != null) {
                                summonList.add(summon);
                                originalSummonList.add(summon);  // Update the original list
                            }
                        }
                        summonAdapter.notifyDataSetChanged();
                    } else {
                        // Handle the exception or show an error message
                        Toast.makeText(this, "Error loading Summon: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showSummonDetails(Summon selectedSummon) {
        if (selectedSummon != null) {
            Intent intent = new Intent(this, SummonDetail.class);
            intent.putExtra("selectedSummon", selectedSummon);
            startActivity(intent);
        } else {
            // Handle the case where the selectedSummon is null
            Toast.makeText(this, "Selected summon is null", Toast.LENGTH_SHORT).show();
        }
    }

    private String getUserId() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : "";
    }
}
