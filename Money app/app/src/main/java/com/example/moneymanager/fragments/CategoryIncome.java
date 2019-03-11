package com.example.moneymanager.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
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
import android.widget.Toast;

import com.example.moneymanager.CategoriesActivity;
import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Category;
import com.example.moneymanager.DAO.Type;
import com.example.moneymanager.R;
import com.example.moneymanager.RecyclerViewAdapter;
import com.example.moneymanager.AddIncomeActivity;

import java.util.ArrayList;
import java.util.List;

public class CategoryIncome extends Fragment {

    View view;
    private RecyclerView recyclerView;
    private AppDatabase db;
    private RecyclerViewAdapter recyclerViewAdapter;
    List<Category> listCategory;
    Button add;

    public CategoryIncome(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getInstance(getContext());
        retrieveIncomes();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.category_income, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.income_recyclerview);
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), listCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);

        add = (Button) view.findViewById(R.id.add_category_income);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddIncomeActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void retrieveIncomes() {
        LiveData<List<Category>> tasks = db.categoryDAO().loadAllIncomes();
        // COMPLETED (5) Observe tasks and move the logic from runOnUiThread to onChanged
        tasks.observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> categories) {
                recyclerViewAdapter.setIncomes(categories);
            }
        });
    }
}
