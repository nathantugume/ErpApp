package com.example.salestrackingapp.Classes;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.salestrackingapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class LoadExpensesTask extends AsyncTask<Void, Void, List<String>> {
   private WeakReference<Context> contextWeakReference;
   private Spinner accountSpinner;
   private String currentAccount;

   public LoadExpensesTask(Context context,Spinner accountSpinner){
       this.contextWeakReference = new WeakReference<>(context);
       this.accountSpinner =accountSpinner;
   }


    @Override
    protected List<String> doInBackground(Void... voids) {
       // Load expense accounts from Firestore
       List<String> accountNames = new ArrayList<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        try{
            Thread.sleep(0);
            firestore.collection("accounts")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots.getDocuments()){
                            String accountName = documentSnapshot.getString("account_name");
                            accountNames.add(accountName);

//                            call the onPostExecute method with the loaded expense names
                            onPostExecute(accountNames);

                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(contextWeakReference.get(), "Sorry we couldn't load expense accounts "+e, Toast.LENGTH_SHORT).show();
                    })

            ;
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return accountNames;
    }

    @Override
    protected void onPostExecute(List<String> accountNames) {
        Context context = contextWeakReference.get();
        if (context != null){
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                    accountNames);
            adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
            accountSpinner.setAdapter(adapter);

//            set the selected account
            int accountPosition = accountNames.indexOf(currentAccount);
            accountSpinner.setSelection(accountPosition);
        }
    }
}
