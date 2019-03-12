package com.example.moneymanager.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moneymanager.AddNewExpense;
import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.ExpensesAndIncomes;
import com.example.moneymanager.R;
import com.example.moneymanager.RecyclerViewAdapter;
import com.example.moneymanager.RecyclerViewAdapterExpense;

import java.util.Date;
import java.util.List;

public class ExpensesDaily extends Fragment {

    private View view;
    private FloatingActionButton add;
    private RecyclerViewAdapterExpense recyclerViewAdapter;
    private RecyclerView recyclerView;
    List<ExpensesAndIncomes> list;
    private AppDatabase db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getInstance(getContext());
        getList();
    }

    public ExpensesDaily() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.daily_expenses, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.day_recyclerview);
        recyclerViewAdapter = new RecyclerViewAdapterExpense(getContext(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);

        add = (FloatingActionButton) view.findViewById(R.id.add_expense);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddNewExpense.class);
                startActivity(intent);
            }
        });

        return view;
    }
    private void getList() {
        LiveData<List<ExpensesAndIncomes>> tasks = db.expensesAndIncomeDAO().getAll();
        tasks.observe(this, new Observer<List<ExpensesAndIncomes>>() {
            @Override
            public void onChanged(@Nullable List<ExpensesAndIncomes> lista) {
                recyclerViewAdapter.setList(lista);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
