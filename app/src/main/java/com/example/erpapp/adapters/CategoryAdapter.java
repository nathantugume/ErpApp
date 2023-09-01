package com.example.erpapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.erpapp.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<DocumentSnapshot> categoryList;

    public CategoryAdapter(List<DocumentSnapshot> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        DocumentSnapshot categorySnapshot = categoryList.get(position);
        String categoryName = (String) categorySnapshot.get("name");
        String desc = (String) categorySnapshot.get("description");

        holder.categoryNameTextView.setText(categoryName);
        holder.categoryDesc.setText(desc);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView,categoryDesc;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
            categoryDesc = itemView.findViewById(R.id.categoryDescList);

        }

    }




}
