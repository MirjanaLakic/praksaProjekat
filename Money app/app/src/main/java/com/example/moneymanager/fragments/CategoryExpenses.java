package com.example.moneymanager.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.moneymanager.AddCategory;
import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Category;
import com.example.moneymanager.R;
import com.example.moneymanager.RecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.firebase.ui.auth.AuthUI.TAG;

public class CategoryExpenses extends Fragment {


    private AppDatabase db;
    private FirebaseFirestore firedb;
    private FirebaseAuth auth;
    private RecyclerViewAdapter recyclerViewAdapter;
    View view;
    private RecyclerView recyclerView;
    List<Category> listCategory;
    Button add;

    public static final String TAG = "CategoryExpenses";

    public CategoryExpenses(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getInstance(getContext());
        firedb = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        retrieveExpenses();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.category_expenses, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.expenses_recyclerview);
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), listCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);

        add = (Button) view.findViewById(R.id.add_category_expenses);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddCategory.class);
                intent.putExtra("type", "EXPENSES");
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void retrieveExpenses() {
        FirebaseUser currentUser = auth.getCurrentUser();
        LiveData<List<Category>> tasks = db.categoryDAO().loadAllExpences();
        // COMPLETED (5) Observe tasks and move the logic from runOnUiThread to onChanged
        tasks.observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> categories) {
                recyclerViewAdapter.setExpenses(categories);
            }
        });
    }
}
