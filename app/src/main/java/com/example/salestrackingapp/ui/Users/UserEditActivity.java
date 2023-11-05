package com.example.salestrackingapp.ui.Users;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.salestrackingapp.R;
import com.example.salestrackingapp.ui.settings.SettingsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

public class UserEditActivity extends AppCompatActivity {

    TextInputEditText fullname, emailEdt,oldPassword,newPassword,confirmNewPassword;
    Button save,cancelBtn;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference usersRef = firestore.collection("users");
    private String currentUserId;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        fullname = findViewById(R.id.fullNameEditText);
        emailEdt = findViewById(R.id.emailEditText);
        oldPassword = findViewById(R.id.oldPasswordEditText);
        newPassword = findViewById(R.id.passwordEditText);
        MaterialToolbar toolbar;
        //        toolbar
        toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.sign_out){
                logout();
                return true;
            } else if (item.getItemId() == R.id.settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);

            }
            return false;
        });
        cancelBtn = findViewById(R.id.cancelButton);
        save = findViewById(R.id.saveButton);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            // Now you have the current user's ID (currentUserId).
            // You can use it to fetch the user data from Firestore.
            fetchUserDataFromFirestore(currentUserId);
        } else {
            // Handle the case when the user is not signed in.
            Toast.makeText(this, "User Id is null please login again to edit your profile", Toast.LENGTH_SHORT).show();
        }

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser();
            }
        });
    }

    private void fetchUserDataFromFirestore(String currentUserId) {

        Source source = Source.CACHE;
        usersRef
                .whereEqualTo("userId",currentUserId)
                .get(source)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Clear existing data if needed
                        // Iterate through the documents and add user data to the userList
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String email = document.getString("email");
                            String fullName = document.getString("fullName");
                            fullname.setText(fullName);
                            emailEdt.setText(email);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(UserEditActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void validate(){
        if (emailEdt.getText().toString().isEmpty()){
            emailEdt.setError("Email cannot be empty");
        } else if (fullname.getText().toString().isEmpty()) {
            fullname.setError("Fullname cannot be empty");
        }
    }

    private void updateUser(){

        String updatedFullName = fullname.getText().toString();
        String updatedEmail = emailEdt.getText().toString();

        if (currentUser != null) {
            // Update email and full name in Firebase Authentication
            currentUser.updateEmail(updatedEmail)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(updatedFullName)
                                    .build();

                            currentUser.updateProfile(profileUpdates)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Email and full name updated in Firebase Authentication
                                            // Now, update the Firestore document in the "users" collection
                                            updateFirestoreUserData(currentUser.getUid(), updatedFullName, updatedEmail);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UserEditActivity.this, "Error updating full name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserEditActivity.this, "Error updating email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        if (!newPassword.getText().toString().isEmpty() && !confirmNewPassword.getText().toString().isEmpty()) {
            if (newPassword.getText().toString().equals(confirmNewPassword.getText().toString())) {
                // Call a method to change the password
                changePassword(newPassword.getText().toString());
            } else {
                Toast.makeText(UserEditActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void changePassword(String newPassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            currentUser.updatePassword(newPassword)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(UserEditActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserEditActivity.this, "Error changing password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void updateFirestoreUserData(String userId, String updatedFullName, String updatedEmail) {
        // Update the Firestore document in the "users" collection
        DocumentReference userRef = usersRef.document(userId);
        userRef.update("fullName", updatedFullName, "email", updatedEmail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UserEditActivity.this, "User information updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserEditActivity.this, "Error updating user information in Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        // you can navigate to the login screen or perform any cleanup
        // After sign-out, you can navigate to the LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close the current activity
    }
}