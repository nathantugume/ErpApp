package com.example.erpapp.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.erpapp.Classes.User;
import com.example.erpapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final List<User> userList;
    private Context context;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);


        // Bind user data to the ViewHolder
        holder.emailTextView.setText("Email: "+user.getEmail());
        holder.companyNameTextView.setText("FullName: "+user.getFullName());
        holder.roleTextView.setText("Role: "+user.getRole());
        holder.menuIcon.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.menuIcon);
            popupMenu.inflate(R.menu.card_menu);

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.editOption) {
                    showEditUserDialog(user);
                    return true;
                } else if (itemId == R.id.deleteOption) {
                    // Handle delete option
                    AlertDialog.Builder confirmDialogBuilder = new AlertDialog.Builder(context);
                    confirmDialogBuilder.setMessage("Are you sure you want to delete this user ?");
                    confirmDialogBuilder.setPositiveButton("Yes", (dialogInterface, i) -> {
                        // User confirmed the delete action

                        // Delete the category document from Firestore
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();


                        firestore.collection("users").document(user.getUserId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Data deleted successfully from Firestore
                                    userList.remove(position);// Remove the category from the list
                                    notifyDataSetChanged(); // Refresh the RecyclerView
                                    Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle the failure to delete data in Firestore
                                    Toast.makeText(context, "Error deleting user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    });
                    confirmDialogBuilder.setNegativeButton("No", (dialogInterface, i) -> {
                        // User canceled the delete action
                    });
                    AlertDialog confirmDialog = confirmDialogBuilder.create();
                    confirmDialog.show();
                    return true;
                } else {
                    return false;
                }
            });

            popupMenu.show();
        });

    }

    private void showEditUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.edit_user_dialog, null);
        builder.setView(dialogView);

        // Find views in the dialog layout
        EditText userIdEditText = dialogView.findViewById(R.id.userIdEditText);
        EditText fullNameEditText = dialogView.findViewById(R.id.fullNameEditText);
        EditText emailEditText = dialogView.findViewById(R.id.emailEditText);
        EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);
        Spinner roleSpinner = dialogView.findViewById(R.id.roleSpinner);
          String[] roles = {"Admin", "Sales", "Store"};

        // Set the user's data in the dialog's input fields
        userIdEditText.setText(user.getUserId());
        fullNameEditText.setText(user.getFullName());
        emailEditText.setText(user.getEmail());
        passwordEditText.setText(user.getPassword());

        // Create an ArrayAdapter for the roleSpinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);
        roleSpinner.setSelection(Arrays.asList(roles).indexOf(user.getRole()));

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Handle save button click to update user data
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve updated data from input fields
                String updatedUserId = userIdEditText.getText().toString();
                String updatedFullName = fullNameEditText.getText().toString();
                String updatedEmail = emailEditText.getText().toString();
                String updatedPassword = passwordEditText.getText().toString();
                String updatedRole = roleSpinner.getSelectedItem().toString();

                // Update the user data in your data source (e.g., Firestore)
                // Call a method to update the user data
                updateUser(updatedUserId, updatedFullName, updatedEmail, updatedPassword, updatedRole);

                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Handle cancel button click to dismiss the dialog
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog without making any changes
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }

    // Add a method to update user data in your data source
    private void updateUser(String userId, String fullName, String email, String password, String role) {
        // Step 1: Obtain a reference to the Firestore collection
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference usersRef = firestore.collection("users");

        // Step 2: Create a query to find the document with the matching userId
        Query query = usersRef.whereEqualTo("userId", userId);

        // Step 3: Execute the query
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Check if the query returned any documents
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Get the first document (there should be only one matching document)
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                            // Create a map to hold the updated user data
                            Map<String, Object> updatedUserData = new HashMap<>();
                            updatedUserData.put("fullName", fullName);
                            updatedUserData.put("email", email);
                            updatedUserData.put("password", password);
                            updatedUserData.put("role", role);

                            // Step 4: Update the user document with the new data
                            documentSnapshot.getReference().update(updatedUserData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @SuppressLint("NotifyDataSetChanged")
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // User data updated successfully
                                            // You can handle this as needed (e.g., show a toast)
                                            Toast.makeText(context, "User data updated successfully", Toast.LENGTH_SHORT).show();
                                            notifyDataSetChanged();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error occurred while updating user data
                                            // Handle the error as needed (e.g., show an error message)
                                            Toast.makeText(context, "Failed to update user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // No document found with the matching userId
                            // Handle this case as needed (e.g., show an error message)
                            Toast.makeText(context, "User not found with userId: " + userId, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error occurred while executing the query
                        // Handle the error as needed (e.g., show an error message)
                        Toast.makeText(context, "Query failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    public int getItemCount() {
        return userList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addUser(User user) {
        userList.add(user);
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView emailTextView;
        TextView companyNameTextView;
        TextView roleTextView;
        ImageView menuIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            companyNameTextView = itemView.findViewById(R.id.companyNameTextView);
            roleTextView = itemView.findViewById(R.id.roleTextView);
            menuIcon = itemView.findViewById(R.id.menuIcon);
        }
    }
}
