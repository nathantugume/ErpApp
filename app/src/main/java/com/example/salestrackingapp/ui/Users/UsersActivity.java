package com.example.salestrackingapp.ui.Users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.salestrackingapp.Admin.AdminDashboardActivity;
import com.example.salestrackingapp.Classes.User;
import com.example.salestrackingapp.R;
import com.example.salestrackingapp.adapters.UserAdapter;
import com.example.salestrackingapp.ui.categories.CategoryActivity;
import com.example.salestrackingapp.ui.products.ProductsActivity;
import com.example.salestrackingapp.ui.reports.ReportsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private List<User> userList; // Replace with your data source
    private UserAdapter userAdapter;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference usersRef = firestore.collection("users");
    private String selectedRole;
    private Spinner roleSpinner;
    private  String[] roles = {"Admin", "Sales", "Store"};
    private MaterialToolbar toolbar;
    private  String companyId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewUsers);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        companyId = sharedPreferences.getString("companyId", null);

        // Initialize user list and adapter
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);

        // Set RecyclerView adapter
        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.home){
                    Intent intent = new Intent(UsersActivity.this, AdminDashboardActivity.class);
                    startActivity(intent);
                }else if(item.getItemId() == R.id.category){
                    Intent intent = new Intent(UsersActivity.this, CategoryActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.analytics){
                    Intent intent = new Intent(UsersActivity.this, ReportsActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.product){
                    Intent intent = new Intent(UsersActivity.this, ProductsActivity.class);
                    startActivity(intent);
                }

                return false;
            }
        });

        // Handle FAB click to show user creation dialog
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserCreationDialog();
            }
        });
        // Fetch user data from Firestore and update userList
        fetchUserDataFromFirestore();
    }

    private void fetchUserDataFromFirestore() {
        Source source = Source.CACHE;
        usersRef
                .whereEqualTo("companyId",companyId)
                .get(source)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Clear existing data if needed
                        userList.clear();

                        // Iterate through the documents and add user data to the userList
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String userId = document.getString("userId");
                            String email = document.getString("email");
                            String companyName = document.getString("companyName");
                            String fullName = document.getString("fullName");
                            String role = document.getString("role");
                            String password = document.getString("password");

                            User user = new User();
                            user.setUserId(userId);
                            user.setEmail(email);
                            user.setCompanyName(companyName);
                            user.setRole(role);
                            user.setFullName(fullName);
                            user.setPassword(password);
                            userList.add(user);
                        }

                        // Update the RecyclerView adapter
                //        userAdapter.setData(userList);
                        userAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle errors while fetching data
                      //  Log.e("Firestore", "Error fetching user data: " + e.getMessage());
                    }
                });

    }

    // Method to show the user creation dialog
    private void showUserCreationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_user, null);
        builder.setView(dialogView);

        EditText emailEditText = dialogView.findViewById(R.id.emailEditText);
        EditText fullNameEditText = dialogView.findViewById(R.id.fullNameEditText);
        EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);
        Button saveButton = dialogView.findViewById(R.id.registerButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        roleSpinner = dialogView.findViewById(R.id.roleSpinner); // Corrected this line

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        roleSpinner.setAdapter(adapter); // Set the adapter for the correct spinner


        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected role here
                selectedRole = roles[position];
                // You can store the selected role in a variable or perform any other actions.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing if nothing is selected.
            }
        });


        AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String fullName = fullNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Add logic to add the new user to the data source (Firestore) here
                // You can use the fetched data to update the adapter or Firestore
                // Once the user is added, dismiss the dialog
                // For example, you can call a method to add the user to Firestore here

                // Add user to Firestore or your data source

                // Initialize Firebase Authentication
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                // Create a user with email and password
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(UsersActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // User creation success
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    String userId = firebaseUser.getUid();

                                    // Now, add the user to Firestore with the retrieved userId

                                    addUserToFirestore(userId,email, fullName, password, selectedRole);
                                } else {
                                    // User creation failed, handle the error
                                    Exception exception = task.getException();
                                    // Handle the exception as needed
                                }
                            }
                        });

                // Dismiss the dialog
                dialog.dismiss();
            }

            private void addUserToFirestore(String uid, String email, String fullName, String password, String selectedRole) {
                // Initialize Firestore
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();


                // Create a User object with the provided details
                User user = new User();
                user.setUserId(uid);
                user.setRole(selectedRole);
                user.setEmail(email);
                user.setFullName(fullName);
                user.setPassword(password);
                user.setCompanyId(companyId);

                // Add the user to the "users" collection in Firestore
                firestore.collection("users")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // User added successfully
                                // You can handle this as needed, e.g., update the adapter
                                Toast.makeText(UsersActivity.this, "User Added Successfully", Toast.LENGTH_SHORT).show();
                              userAdapter.addUser(user);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error occurred while adding the user
                                // Handle the error as needed
                            }
                        });
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancel button action: Dismiss the dialog
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    // Add other methods as needed
}
