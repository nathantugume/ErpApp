package com.example.salestrackingapp.ui.Users;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.salestrackingapp.Admin.AdminDashboardActivity;
import com.example.salestrackingapp.R;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText,
            passwordEditText;
    private Button loginButton;
    private TextView resetPasswordButton;
    private FirebaseAuth mAuth;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Login successful
                                    String uid = mAuth.getCurrentUser().getUid();

                                    // Reference to Firestore
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                                    // Get the user's role from Firestore
                                    db.collection("users").whereEqualTo("userId", uid)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        String companyId = "";
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            String userRole = document.getString("role");
                                                            companyId = document.getString("companyId");


                                                            // Save the user's role in SharedPreferences
                                                            SharedPreferences preferences = getSharedPreferences("user_role", MODE_PRIVATE);
                                                            SharedPreferences.Editor editor = preferences.edit();
                                                            editor.putString("role", userRole);
                                                            editor.apply();

                                                        }
                                                        saveCompanyIDToSharedPreferences(companyId);
                                                        Toast.makeText(LoginActivity.this, "Logged in Successfully!!", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                                                        intent.putExtra("userRole", userRole);
                                                        startActivity(intent);
                                                    } else {
                                                        // Handle the case where the query fails
                                                        Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    // Login failed
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();

                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    // Password reset email sent successfully
                                    Toast.makeText(LoginActivity.this,
                                            "Password reset email sent.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // Password reset failed
                                    Toast.makeText(LoginActivity.this,
                                            "Password reset failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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
