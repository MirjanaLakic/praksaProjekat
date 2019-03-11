package com.example.moneymanager.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Database;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.moneymanager.AddExpenseActivity;
import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Category;
import com.example.moneymanager.DAO.Type;
import com.example.moneymanager.R;
import com.example.moneymanager.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoryExpenses extends Fragment {


    private AppDatabase db;
    private RecyclerViewAdapter recyclerViewAdapter;
    View view;
    private RecyclerView recyclerView;
    List<Category> listCategory;
    Button add;
    ImageButton delete;

    public CategoryExpenses(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getInstance(getContext());
        retrieveExpenses();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.category_expenses, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.expenses_recyclerview);
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), listCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);

        add = (Button) view.findViewById(R.id.add_category_expenses);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
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
