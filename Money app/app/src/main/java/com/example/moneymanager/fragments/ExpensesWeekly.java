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
import com.example.moneymanager.DAO.Category;
import com.example.moneymanager.DAO.ExpensesAndIncomes;
import com.example.moneymanager.R;
import com.example.moneymanager.RecyclerViewAdapterExpense;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpensesWeekly extends Fragment {

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
    private static final String DATE_FORMAT = "dd/MM/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private float finalSum;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getInstance(getContext());
        getList();
    }

    public ExpensesWeekly() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.weekly_expenses, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.week_recyclerview);
        recyclerViewAdapter = new RecyclerViewAdapterExpense(getContext(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);
        return view;
    }
    private void getList() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        Date date2 = calendar.getTime();
        String seventhDay = dateFormat.format(date2);
        String[] str = seventhDay.split("/");
        final int sevenDays = Integer.valueOf(str[0]);
        LiveData<List<ExpensesAndIncomes>> tasks = db.expensesAndIncomeDAO().getAllExpenses();
        tasks.observe(this, new Observer<List<ExpensesAndIncomes>>() {
            @Override
            public void onChanged(@Nullable List<ExpensesAndIncomes> lista) {
                List<ExpensesAndIncomes> l = new ArrayList<>();
                for (int i = 0; i < lista.size(); i++) {
                    String dateFromLista = dateFormat.format(lista.get(i).getDate());
                    String[] parse = dateFromLista.split("/");
                    int parseInt = Integer.valueOf(parse[0]);
                    if (sevenDays <= parseInt){
                        l.add(lista.get(i));
                    }
                }

                recyclerViewAdapter.setList(l);
                yEntry.clear();
                xEntry.clear();
                setPieChart();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void addDataToChart(){
        finalSum = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        Date date2 = calendar.getTime();
        String seventhDay = dateFormat.format(date2);
        String[] str = seventhDay.split("/");
        final int sevenDays = Integer.valueOf(str[0]);
        LiveData<List<Category>> tasks = db.categoryDAO().loadAllExpences();
        tasks.observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> lista) {
                for (int i = 0; i < lista.size(); i++) {
                    float sum = 0;
                    List<ExpensesAndIncomes> categoryExpesess = db.expensesAndIncomeDAO().getExpensesForOneCategory(lista.get(i).getId());
                    for (int j = 0; j < categoryExpesess.size(); j++) {
                        String dateFromLista = dateFormat.format(categoryExpesess.get(j).getDate());
                        String[] parse = dateFromLista.split("/");
                        int parseInt = Integer.valueOf(parse[0]);
                        if (sevenDays <= parseInt){
                            sum += categoryExpesess.get(j).getPrice();
                            xEntry.add(lista.get(i).getName());
                        }

                    }
                    if (sum != 0) {
                        yEntry.add(new PieEntry(sum, i));
                        finalSum += sum;
                    }
                }

                //create the data set
                PieDataSet pieDataSet = new PieDataSet(yEntry, "");
                pieDataSet.setSliceSpace(0);
                //pieDataSet.setDrawValues(false);
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                //create pie data object
                PieData pieData = new PieData(pieDataSet);
                pieDaily.setCenterText("Weekly \n Expenses \n"+ finalSum);
                pieDaily.setTouchEnabled(false);
                pieDaily.setData(pieData);
                pieDaily.invalidate();

            }

        });
    }

    public void setPieChart(){
        pieDaily = (PieChart) view.findViewById(R.id.pie_weekly);
        pieDaily.setRotationEnabled(true);
        pieDaily.setHoleRadius(70f);
        pieDaily.setTransparentCircleAlpha(0);
        pieDaily.setCenterTextSize(15);
        pieDaily.getDescription().setEnabled(false);
        pieDaily.getLegend().setEnabled(false);
        addDataToChart();
    }
}
