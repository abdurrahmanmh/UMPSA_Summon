package com.cb20034.umpsas;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Register extends AppCompatActivity {

    private static final String TAG = "Register";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbarReg);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input from the registration form
                String name = ((TextInputEditText) findViewById(R.id.nameText)).getText().toString();
                String id = ((TextInputEditText) findViewById(R.id.idText)).getText().toString();
                String password = ((TextInputEditText) findViewById(R.id.passwordText)).getText().toString();
                String phoneNo = ((TextInputEditText) findViewById(R.id.phoneNoText)).getText().toString();
                String email = ((TextInputEditText) findViewById(R.id.emailText)).getText().toString();
                String icNumber = ((TextInputEditText) findViewById(R.id.icNumberText)).getText().toString();

                RadioGroup userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup);
                int selectedRadioButtonId = userTypeRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                String userType = selectedRadioButton != null ? selectedRadioButton.getText().toString() : "";

                String adminKey = null;

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(id) || TextUtils.isEmpty(password) ||
                        TextUtils.isEmpty(phoneNo) || TextUtils.isEmpty(email) || TextUtils.isEmpty(icNumber) || TextUtils.isEmpty(userType)) {
                    // Display a message indicating that all fields must be filled
                    Toast.makeText(Register.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return; // Stop further processing
                }

                // Check if the user selected Admin, then get the admin key
                if ("Admin".equals(userType)) {
                    adminKey = ((TextInputEditText) findViewById(R.id.adminKeyText)).getText().toString();
                    if ("123456".equals(adminKey)) {
                        registerUserAndStoreData(name, id, password, phoneNo, email, icNumber, userType);
                    } else {
                        Toast.makeText(Register.this, "Wrong Key.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Register the user and store data in Firestore
                    registerUserAndStoreData(name, id, password, phoneNo, email, icNumber, userType);
                }
            }
        });

        // Set up a RadioGroup.OnCheckedChangeListener
        RadioGroup userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup);
        userTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                TextInputLayout adminKeyLayout = findViewById(R.id.adminKeyLayout);
                // TextInputLayout ID is adminKeyLayout

                if (checkedId == R.id.rbAdmin) {
                    // If the admin radio button is selected, show the admin key field
                    adminKeyLayout.setVisibility(View.VISIBLE);
                } else {
                    // If another radio button is selected, hide the admin key field
                    adminKeyLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void registerUserAndStoreData(String name, String id, String password, String phoneNo, String email, String icNumber, String userType) {
        // Check if both ID and IC number are unique
        checkIfUserExists(id, icNumber, (isIdUnique, isIcNumberUnique) -> {
            if (isIdUnique && isIcNumberUnique) {
                // If both ID and IC number are unique, proceed with user registration
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // User registration is successful
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        // Store user data in Firestore
                                        storeUserData(user.getUid(), name, id, phoneNo, email, icNumber, userType);
                                    }
                                } else {
                                    // If registration fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    showToast("Registration failed. Please try again.");
                                }
                            }
                        });
            } else {
                // Inform the user which field(s) already exist
                if (!isIdUnique && !isIcNumberUnique) {
                    showToast("ID and IC number already exist. Please choose different credentials.");
                } else if (!isIdUnique && isIcNumberUnique) {
                    showToast("ID already exists. Please choose a different ID.");
                } else if (isIdUnique && !isIcNumberUnique) {
                    showToast("IC number already exists. Please choose a different IC number.");
                }
            }
        });
    }

    private void storeUserData(String userId, String name, String id, String phoneNo, String email, String icNumber, String userType) {
        // Create a new user object with the provided data
        User newUser;

        newUser = new User(userId, name, id, phoneNo, email, icNumber, userType);


        // Store the user data in Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Registration and data storage are successful
                        showToast("Registration successful!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle errors during data storage
                        Log.w(TAG, "Error writing document", e);
                        // Additional logic for failure if needed
                        showToast("Registration failed. Please try again.");
                    }
                });
    }
    private void showToast(String message) {
        Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
    }
    private void checkIfUserExists(String userId, String icNumber, OnIdAndIcNumberCheckListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("id", userId)
                .whereEqualTo("icNumber", icNumber)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean isIdUnique  = task.getResult().isEmpty();
                            boolean isIcNumberUnique = task.getResult().isEmpty();
                            listener.onIdAndIcNumberCheckComplete(isIdUnique, isIcNumberUnique);
                        } else {
                            // Handle the exception or error
                            listener.onIdAndIcNumberCheckComplete(false, false);  // Assume non-unique in case of an error
                        }
                    }
                });
    }
    public interface OnIdAndIcNumberCheckListener {
        void onIdAndIcNumberCheckComplete(boolean isIdUnique, boolean isIcNumberUnique);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // Handle the back button press (e.g., finish the current activity)
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}