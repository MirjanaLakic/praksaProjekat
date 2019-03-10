package com.example.moneymanager.fragments;

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

import com.example.moneymanager.AddExpenseActivity;
import com.example.moneymanager.DAO.Category;
import com.example.moneymanager.DAO.Type;
import com.example.moneymanager.R;
import com.example.moneymanager.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoryExpenses extends Fragment {

    View view;
    private RecyclerView recyclerView;
    List<Category> listCategory;
    Button add;

    public CategoryExpenses(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listCategory = categoryListOfExspenses();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.category_expenses, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.expenses_recyclerview);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(), listCategory);
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

    public List<Category> categoryListOfExspenses(){
        List<Category> list = new ArrayList<>();
        list.add(new Category("Food", R.drawable.food, Type.EXPENSES));
        list.add(new Category("Bills", R.drawable.bills, Type.EXPENSES));
        list.add(new Category("Shopping", R.drawable.shopping_bag, Type.EXPENSES));
        list.add(new Category("Entertainment", R.drawable.entertainment, Type.EXPENSES));
        list.add(new Category("Home", R.drawable.home, Type.EXPENSES));
        return list;
    }
}
