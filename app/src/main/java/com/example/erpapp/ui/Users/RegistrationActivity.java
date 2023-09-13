package com.example.erpapp.ui.Users;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.erpapp.Classes.User;
import com.example.erpapp.MainActivity;
import com.example.erpapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;



public class RegistrationActivity extends AppCompatActivity {


    private TextInputEditText emailEditText, passwordEditText, companyNameEditText;
    private TextView alreadyHaveAccount;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private String companyName, role,email,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        companyNameEditText = findViewById(R.id.companyNameEditText);
        registerButton = findViewById(R.id.registerButton);
        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                registerUser();
            }
        });

    }

    private void registerUser() {
        // Initialize Firebase Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Get the values from the EditText fields
        
        companyName = companyNameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        role = "admin"; // Replace with the selected role
        
        
        if (email.isEmpty()){
            emailEditText.setError("Enter email address");
            emailEditText.requestFocus();
        } else if (companyName.isEmpty()) {
            companyNameEditText.setError("please enter company name");
            companyNameEditText.requestFocus();
            
        } else if (password.isEmpty()) {
            passwordEditText.setError("please enter password");
            passwordEditText.requestFocus();
        }else{
            // Create a User object
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(role);
            user.setCompanyName(companyName);


            // Register the user with email and password
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Registration success, get the userId
                                String userId = mAuth.getCurrentUser().getUid();
                                // Add the userId to the User object
                                user.setUserId(userId);

                                // Add the user data to Firestore
                                firestore.collection("users")
                                        .document(userId) // Use userId as the document ID
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // User data added successfully
                                                Toast.makeText(RegistrationActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                                // You can navigate to another activity or perform any other action here
                                                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Error occurred while adding user data
                                                Toast.makeText(RegistrationActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                // Registration failed
                                Toast.makeText(RegistrationActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }



}

