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
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ExpensesYearly extends Fragment {

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

    public ExpensesYearly() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.yearly_expenses, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.year_recyclerview);
        recyclerViewAdapter = new RecyclerViewAdapterExpense(getContext(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);

        return view;
    }
    private void getList() {
        Date date2 = new Date();
        String seventhDay = dateFormat.format(date2);
        String[] str = seventhDay.split("/");
        final int sevenDays = Integer.valueOf(str[2]);
        LiveData<List<ExpensesAndIncomes>> tasks = db.expensesAndIncomeDAO().getAllExpenses();
        tasks.observe(this, new Observer<List<ExpensesAndIncomes>>() {
            @Override
            public void onChanged(@Nullable List<ExpensesAndIncomes> lista) {
                if (lista.size() != 0) {
                    List<ExpensesAndIncomes> l = new ArrayList<>();
                    for (int i = 0; i < lista.size(); i++) {
                        String dateFromLista = dateFormat.format(lista.get(i).getDate());
                        String[] parse = dateFromLista.split("/");
                        int parseInt = Integer.valueOf(parse[2]);
                        if (sevenDays <= parseInt) {
                            l.add(lista.get(i));
                        }
                    }

                    recyclerViewAdapter.setList(l);

                }
                addDataToChart();
            }
        });
    }

    private void addDataToChart(){
        final LiveData<List<ExpensesAndIncomes>> tasks = db.expensesAndIncomeDAO().getAll();
        final List<Integer> years = Arrays.asList(2016,2017,2018,2019);
        if (tasks != null) {
            tasks.observe(this, new Observer<List<ExpensesAndIncomes>>() {
                @Override
                public void onChanged(@Nullable List<ExpensesAndIncomes> lista) {
                    float sum = 0;
                    if (lista.size() != 0) {
                        xEntry.clear();
                        yEntry.clear();
                        List<ExpensesAndIncomes> expenses = db.expensesAndIncomeDAO().getExpenses();
                        List<ExpensesAndIncomes> incomes = db.expensesAndIncomeDAO().getIncome();
                        for (int i = 0; i < years.size(); i++) {

                            for (int j = 0; j <expenses.size(); j++) {
                                String dateFromLista = dateFormat.format(expenses.get(j).getDate());
                                String[] parse = dateFromLista.split("/");
                                int monthInt = Integer.valueOf(parse[2]);
                                if (years.get(i).equals(monthInt)){
                                    sum += expenses.get(j).getPrice();
                                }
                            }
                            yEntry.add(new Entry(years.get(i), sum));
                            sum = 0;

                            for (int k = 0; k <incomes.size(); k++) {
                                String dateFromLista = dateFormat.format(incomes.get(k).getDate());
                                String[] parse = dateFromLista.split("/");
                                int monthInt = Integer.valueOf(parse[2]);
                                if (years.get(i).equals(monthInt)){
                                    sum += incomes.get(k).getPrice();
                                }
                            }
                            xEntry.add(new Entry(years.get(i), sum));
                            sum = 0;

                        }
                        setChart(xEntry, yEntry);
                    }
                }
            });
        }
    }

    private void setChart(ArrayList<Entry> xEntry, ArrayList<Entry> yEntry) {
        lineChart = (LineChart) view.findViewById(R.id.line_yearly);
        //create the data set
        LineDataSet lineDataSet = new LineDataSet(yEntry, "expense");
        LineDataSet lineDataSet1 = new LineDataSet(xEntry, "income");
        lineDataSet1.setColor(Color.rgb(47, 163, 57));
        lineDataSet.setColor(Color.rgb(198, 24,21));
        lineDataSet1.setCircleColor(Color.rgb(47, 163, 57));
        lineDataSet.setCircleColor(Color.rgb(198, 24,21));
        lineDataSet1.setCircleColorHole(Color.rgb(47, 163, 57));
        lineDataSet.setCircleColorHole(Color.rgb(198, 24,21));
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        dataSets.add(lineDataSet1);

        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);

        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(false);

        XAxis xAxis = lineChart.getXAxis();

        xAxis.setGranularity(1.0f);

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int)value);
            }
        });

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();
    }
}
