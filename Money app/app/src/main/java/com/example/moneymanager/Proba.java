package com.example.moneymanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Category;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Proba extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private AppDatabase appdb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proba);
        db = FirebaseFirestore.getInstance();
        appdb = AppDatabase.getInstance(this);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        List<Category> categories = appdb.categoryDAO().loadIncomes();
        List<Category> categorie = appdb.categoryDAO().loadExpenses();
        Map<String, List<Category>> map = new HashMap<>();
        map.put("categoryIncome", categories);
        map.put("categoryExpenses", categorie);



        db.collection("MoneyApp").document(currentUser.getEmail())
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Proba.this, "Dodao", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Proba.this, "NIJE", Toast.LENGTH_SHORT).show();
                    }
                });

        DocumentReference reference = db.collection("MoneyApp").document(currentUser.getEmail());
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<Category> c = (List<Category>) documentSnapshot.get("categoryExpenses");
                System.out.println();
            }
        });

    }
}
