package com.example.moneymanager.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Category;
import com.example.moneymanager.DAO.ExpensesAndIncomes;
import com.example.moneymanager.R;
import com.example.moneymanager.RecyclerViewAdapterExpense;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpensesMonthly extends Fragment {

    private View view;
    private RecyclerViewAdapterExpense recyclerViewAdapter;
    private RecyclerView recyclerView;
    List<ExpensesAndIncomes> list;
    private AppDatabase db;
    final ArrayList<Entry> yEntry = new ArrayList<>();
    final ArrayList<Entry> xEntry = new ArrayList<>();
    private static final String DATE_FORMAT = "dd/MM/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    LineChart lineChart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getInstance(getContext());
        getList();
    }

    public ExpensesMonthly() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.monthly_expenses, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.month_recyclerview);
        recyclerViewAdapter = new RecyclerViewAdapterExpense(getContext(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);

        return view;
    }

    private void getList() {
        Date date2 = new Date();
        String seventhDay = dateFormat.format(date2);
        String[] str = seventhDay.split("/");
        final int sevenDays = Integer.valueOf(str[1]);
        LiveData<List<ExpensesAndIncomes>> tasks = db.expensesAndIncomeDAO().getAllExpenses();
        tasks.observe(this, new Observer<List<ExpensesAndIncomes>>() {
            @Override
            public void onChanged(@Nullable List<ExpensesAndIncomes> lista) {
            List<ExpensesAndIncomes> l = new ArrayList<>();
            for (int i = 0; i < lista.size(); i++) {
                String dateFromLista = dateFormat.format(lista.get(i).getDate());
                String[] parse = dateFromLista.split("/");
                int parseInt = Integer.valueOf(parse[1]);
                if (sevenDays <= parseInt){
                    l.add(lista.get(i));
                }
            }
            recyclerViewAdapter.setList(l);
            addDataToChart();
            }
        });
    }

    private void addDataToChart(){
        final Date date2 = new Date();
        String seventhDay = dateFormat.format(date2);
        String[] str = seventhDay.split("/");
        final int sevenDays = Integer.valueOf(str[1]);
        final LiveData<List<Category>> tasks = db.categoryDAO().loadAllExpences();
        tasks.observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> lista) {
                float sum = 0;
                int month = 0;
                xEntry.clear();
                yEntry.clear();
                for (int i = 0; i < lista.size(); i++) {

                    List<ExpensesAndIncomes> categoryExpesess = db.expensesAndIncomeDAO().getExpensesForOneCategory(lista.get(i).getId());
                    for (int j = 0; j < categoryExpesess.size(); j++) {
                        String dateFromLista = dateFormat.format(categoryExpesess.get(j).getDate());
                        String[] parse = dateFromLista.split("/");
                        int parseInt = Integer.valueOf(parse[1]);
                        month = parseInt;
                        if (sevenDays == parseInt){
                            sum += categoryExpesess.get(j).getPrice();
                        }

                    }

                }
                if (sum != 0) {
                    yEntry.add(new Entry(month, sum));
                }

                List<Category> item = db.categoryDAO().loadIncomes();

                float sum1 = 0;
                int month1 = 0;
                for (int i = 0; i < item.size(); i++) {
                    List<ExpensesAndIncomes> categoryIncome = db.expensesAndIncomeDAO().getExpensesForOneCategory(item.get(i).getId());
                    for (int j = 0; j < categoryIncome.size(); j++) {
                        String dateFromLista = dateFormat.format(categoryIncome.get(j).getDate());
                        String[] parse = dateFromLista.split("/");
                        int parseInt1 = Integer.valueOf(parse[1]);
                        month1 = parseInt1;
                        if (sevenDays == parseInt1){
                            sum1 += categoryIncome.get(j).getPrice();
                        }
                    }

                }
                if (sum1 != 0) {
                    xEntry.add(new Entry(month1, sum1));
                }
                setChart(xEntry, yEntry);
            }
        });
    }

    private void setChart(ArrayList<Entry> xEntry, ArrayList<Entry> yEntry) {
        lineChart = (LineChart) view.findViewById(R.id.line_monthly);
        //create the data set
        LineDataSet lineDataSet = new LineDataSet(yEntry, "expense");
        LineDataSet lineDataSet1 = new LineDataSet(xEntry, "income");
        lineDataSet.setColor(Color.RED);
        lineDataSet1.setColor(Color.GREEN);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet1.setCircleColor(Color.GREEN);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        dataSets.add(lineDataSet1);

        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);

        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(false);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();
    }
}
