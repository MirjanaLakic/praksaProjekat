package com.example.moneymanager.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.moneymanager.AddNewExpense;
import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Category;
import com.example.moneymanager.DAO.ExpensesAndIncomes;
import com.example.moneymanager.R;
import com.example.moneymanager.RecyclerViewAdapter;
import com.example.moneymanager.RecyclerViewAdapterExpense;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpensesDaily extends Fragment {

    private View view;
    private FloatingActionButton add;
    private RecyclerViewAdapterExpense recyclerViewAdapter;
    private RecyclerView recyclerView;
    List<ExpensesAndIncomes> list;
    private AppDatabase db;
    private PieChart pieDaily;
    private static String TAG = "MainActivity";
    final ArrayList<PieEntry> yEntry = new ArrayList<>();
    final ArrayList<String> xEntry = new ArrayList<>();

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
                pieDaily = (PieChart) view.findViewById(R.id.pie_daily);

                pieDaily.setRotationEnabled(true);
                pieDaily.setHoleRadius(70f);
                pieDaily.setTransparentCircleAlpha(0);
                pieDaily.setCenterText("Today\n Expenses" );
                pieDaily.setCenterTextSize(10);
                pieDaily.getDescription().setEnabled(false);
                pieDaily.getLegend().setEnabled(false);

                addDataToChart();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void addDataToChart(){
        LiveData<List<Category>> tasks = db.categoryDAO().loadAllExpences();
        tasks.observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> lista) {
                for (int i = 0; i < lista.size(); i++) {
                    float sum = 0;
                    List<ExpensesAndIncomes> categoryExpesess = db.expensesAndIncomeDAO().getExpensesForOneCategory(lista.get(i).getId());
                    if (categoryExpesess.size() != 0){
                        xEntry.add(lista.get(i).getName());
                        for (int j = 0; j < categoryExpesess.size(); j++) {
                            sum += categoryExpesess.get(j).getPrice();

                        }
                        yEntry.add(new PieEntry(sum, i));
                    }
                }
                //create the data set
                PieDataSet pieDataSet = new PieDataSet(yEntry, "");
                pieDataSet.setSliceSpace(0);
                //pieDataSet.setDrawValues(false);

                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

                //create pie data object
                PieData pieData = new PieData(pieDataSet);
                pieDaily.setData(pieData);
                pieDaily.invalidate();

            }

        });


    }
}
