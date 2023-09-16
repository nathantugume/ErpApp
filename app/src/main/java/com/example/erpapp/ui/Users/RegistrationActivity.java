package com.example.erpapp.ui.Users;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.erpapp.Classes.Company;
import com.example.erpapp.Classes.User;
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

    private TextInputEditText emailEditText, passwordEditText, confirmPassword, companyNameEditText, fullNameEditText;
    private FirebaseAuth mAuth;
    private String companyName;
    private String role;
    private String email;
    private String password;
    private String fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        companyNameEditText = findViewById(R.id.companyNameEditText);
        Button registerButton = findViewById(R.id.registerButton);
        TextView alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        confirmPassword = findViewById(R.id.confirmPasswordEditText);
        fullNameEditText = findViewById(R.id.fullNameEditText);

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
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        companyName = companyNameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        role = "admin"; // Replace with the selected role
        String confirmPass = confirmPassword.getText().toString();
        fullName = fullNameEditText.getText().toString();

        if (email.isEmpty()) {
            emailEditText.setError("Enter email address");
            emailEditText.requestFocus();
        } else if (companyName.isEmpty()) {
            companyNameEditText.setError("Please enter company name");
            companyNameEditText.requestFocus();
        } else if (password.isEmpty()) {
            passwordEditText.setError("Please enter password");
            passwordEditText.requestFocus();
        } else if (confirmPass.isEmpty()) {
            confirmPassword.setError("Please confirm your password");
            confirmPassword.requestFocus();
        } else if (!password.equals(confirmPass)) {
            passwordEditText.setError("Passwords do not match!!");
            confirmPassword.setError("Passwords do not match!!");
            confirmPassword.requestFocus();
        } else if (fullName.isEmpty()) {
            fullNameEditText.setError("Please enter Full Name");
            fullNameEditText.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String userId = mAuth.getCurrentUser().getUid();
                                // Create a new company document in the "companies" collection
                                createCompanyAndAssociateUser(firestore, userId, companyName);
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void createCompanyAndAssociateUser(FirebaseFirestore firestore, String userId, String companyName) {
        // Create a new company document in the "companies" collection
        Company company = new Company();
        company.setCompanyName(companyName);
        // Generate a unique company ID and set it
        String companyId = firestore.collection("companies").document().getId();
        company.setCompanyId(companyId);

        firestore.collection("companies")
                .document(companyId)
                .set(company)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Company document added successfully
                        // Store the companyId in SharedPreferences for later use
                        saveCompanyIDToSharedPreferences(companyId);

                        // Store the companyId in the user's document in the "users" collection
                        User user = new User();
                        user.setUserId(userId);
                        user.setEmail(email);
                        user.setPassword(password);
                        user.setRole(role);
                        user.setFullName(fullName);
                        user.setCompanyName(companyName);
                        user.setCompanyId(companyId);

                        firestore.collection("users")
                                .document(userId) // Use userId as the document ID
                                .set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // User data added successfully
                                        Toast.makeText(RegistrationActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
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
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error occurred while adding the company document
                        Toast.makeText(RegistrationActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveCompanyIDToSharedPreferences(String companyId) {
        // Save the company ID to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("companyId", companyId);
        editor.apply();
    }
}
