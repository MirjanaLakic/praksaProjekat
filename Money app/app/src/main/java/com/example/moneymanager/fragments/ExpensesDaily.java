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
import android.widget.TextView;

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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpensesDaily extends Fragment {

    private View view;
    private RecyclerViewAdapterExpense recyclerViewAdapter;
    private RecyclerView recyclerView;
    List<ExpensesAndIncomes> list;
    private AppDatabase db;
    private PieChart pieDaily;
    final ArrayList<PieEntry> yEntry = new ArrayList<>();
    final ArrayList<String> xEntry = new ArrayList<>();
    private static final String DATE_FORMAT = "dd/MM/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private float finalSum;
    private TextView budget;
    private TextView balance;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getInstance(getContext());
        getList();
    }

    public ExpensesDaily() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.daily_expenses, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.day_recyclerview);
        recyclerViewAdapter = new RecyclerViewAdapterExpense(getContext(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);

        return view;
    }
    private void getList() {
        Date date1 = new Date();
        final String date = dateFormat.format(date1);
        LiveData<List<ExpensesAndIncomes>> tasks = db.expensesAndIncomeDAO().getAllExpenses();
        tasks.observe(this, new Observer<List<ExpensesAndIncomes>>() {
            @Override
            public void onChanged(@Nullable List<ExpensesAndIncomes> lista) {
                List<ExpensesAndIncomes> l = new ArrayList<>();
                for (int i = 0; i < lista.size(); i++) {
                        String dateFromLista = dateFormat.format(lista.get(i).getDate());
                        if (dateFromLista.equals(date)){
                            l.add(lista.get(i));
                        }
                }
                recyclerViewAdapter.setList(l);
                setBudget();
                setBalance();
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
        Date date1 = new Date();
        final String date = dateFormat.format(date1);
        LiveData<List<Category>> tasks = db.categoryDAO().loadAllExpences();
        tasks.observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> lista) {
                for (int i = 0; i < lista.size(); i++) {
                    float sum = 0;
                    List<ExpensesAndIncomes> categoryExpesess = db.expensesAndIncomeDAO().getExpensesForOneCategory(lista.get(i).getId());
                    for (int j = 0; j < categoryExpesess.size(); j++) {
                        String dateFromLista = dateFormat.format(categoryExpesess.get(j).getDate());
                        if (dateFromLista.equals(date)) {
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
                pieDaily.setData(pieData);
                pieDaily.setCenterText("Daily \n Expenses \n"+ finalSum);
                pieDaily.setTouchEnabled(false);
                pieDaily.invalidate();

            }

        });
    }

    public void setPieChart(){
        pieDaily = (PieChart) view.findViewById(R.id.pie_daily);
        pieDaily.setRotationEnabled(true);
        pieDaily.setHoleRadius(70f);
        pieDaily.setTransparentCircleAlpha(0);
        pieDaily.setCenterTextSize(15);
        pieDaily.getDescription().setEnabled(false);
        pieDaily.getLegend().setEnabled(false);
        addDataToChart();
    }

    public void setBudget(){
        Date date2 = new Date();
        String seventhDay = dateFormat.format(date2);
        String[] str = seventhDay.split("/");
        final int sevenDays = Integer.valueOf(str[1]);
        final LiveData<List<ExpensesAndIncomes>> list = db.expensesAndIncomeDAO().getAllIncome();
        list.observe(this, new Observer<List<ExpensesAndIncomes>>() {
            @Override
            public void onChanged(@Nullable List<ExpensesAndIncomes> lista) {
                if (lista.size() != 0) {
                    int sum = 0;
                    List<ExpensesAndIncomes> l = new ArrayList<>();
                    for (int i = 0; i < lista.size(); i++) {
                        String dateFromLista = dateFormat.format(lista.get(i).getDate());
                        String[] parse = dateFromLista.split("/");
                        int parseInt = Integer.valueOf(parse[1]);
                        if (sevenDays <= parseInt) {
                            l.add(lista.get(i));
                        }
                    }
                    for (int i = 0; i < l.size(); i++) {
                        sum += l.get(i).getPrice();
                    }
                    budget = (TextView) view.findViewById(R.id.budget);
                    String s = Float.toString(sum);
                    budget.setText("Budget: " + s);
                }else {
                    budget = (TextView) view.findViewById(R.id.budget);
                    budget.setText("Budget: 0.0");
                }
            }
        });
    }

    public void setBalance(){
        final Date date2 = new Date();
        String seventhDay = dateFormat.format(date2);
        String[] str = seventhDay.split("/");
        final int sevenDays = Integer.valueOf(str[1]);
        final LiveData<List<Category>> tasks = db.categoryDAO().loadAllExpences();
        tasks.observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> lista) {
                if (lista.size() != 0) {
                    float sum = 0;
                    xEntry.clear();
                    yEntry.clear();
                    for (int i = 0; i < lista.size(); i++) {

                        List<ExpensesAndIncomes> categoryExpesess = db.expensesAndIncomeDAO().getExpensesForOneCategory(lista.get(i).getId());
                        for (int j = 0; j < categoryExpesess.size(); j++) {
                            String dateFromLista = dateFormat.format(categoryExpesess.get(j).getDate());
                            String[] parse = dateFromLista.split("/");
                            int parseInt = Integer.valueOf(parse[1]);
                            if (sevenDays == parseInt) {
                                sum += categoryExpesess.get(j).getPrice();
                            }

                        }

                    }

                    List<Category> item = db.categoryDAO().loadIncomes();


                    float budget = 0;
                    for (int i = 0; i < item.size(); i++) {
                        List<ExpensesAndIncomes> categoryIncome = db.expensesAndIncomeDAO().getExpensesForOneCategory(item.get(i).getId());
                        for (int j = 0; j < categoryIncome.size(); j++) {
                            String dateFromLista = dateFormat.format(categoryIncome.get(j).getDate());
                            String[] parse = dateFromLista.split("/");
                            int parseInt1 = Integer.valueOf(parse[1]);
                            if (sevenDays == parseInt1) {
                                budget += categoryIncome.get(j).getPrice();
                            }
                        }
                    }

                    float finalBalance = budget - sum;
                    balance = (TextView) view.findViewById(R.id.balance);
                    if (finalBalance >= 0) {
                        balance.setTextColor(Color.GREEN);
                    } else {
                        balance.setTextColor(Color.RED);
                    }
                    String s = Float.toString(finalBalance);
                    balance.setText("Balance: " + s);
                }
            }
        });
    }
}
