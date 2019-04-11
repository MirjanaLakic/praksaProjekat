package com.example.moneymanager.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
        final List<String> check = getWeekList();
        LiveData<List<ExpensesAndIncomes>> tasks = db.expensesAndIncomeDAO().getAllExpenses();
        tasks.observe(this, new Observer<List<ExpensesAndIncomes>>() {
            @Override
            public void onChanged(@Nullable List<ExpensesAndIncomes> lista) {
                List<ExpensesAndIncomes> l = new ArrayList<>();
                for (int i = 0; i < lista.size(); i++) {
                    String dateFromLista = dateFormat.format(lista.get(i).getDate());
                    if (check.contains(dateFromLista)){
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
        final List<String> check = getWeekList();
        finalSum = 0;
        LiveData<List<Category>> tasks = db.categoryDAO().loadAllExpences();
        tasks.observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> lista) {
                for (int i = 0; i < lista.size(); i++) {
                    float sum = 0;
                    List<ExpensesAndIncomes> categoryExpesess = db.expensesAndIncomeDAO().getExpensesForOneCategory(lista.get(i).getId());
                    for (int j = 0; j < categoryExpesess.size(); j++) {
                        String dateFromLista = dateFormat.format(categoryExpesess.get(j).getDate());
                        if (check.contains(dateFromLista)){
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

    public void setBudget(){
        Date date2 = new Date();
        String seventhDay = dateFormat.format(date2);
        String[] str = seventhDay.split("/");
        final int sevenDays = Integer.valueOf(str[1]);
        LiveData<List<ExpensesAndIncomes>> list = db.expensesAndIncomeDAO().getAllIncome();
        list.observe(this, new Observer<List<ExpensesAndIncomes>>() {
            @Override
            public void onChanged(@Nullable List<ExpensesAndIncomes> lista) {
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
                budget.setTextColor(Color.rgb(35, 76, 226));
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
                float sum = 0;
                xEntry.clear();
                yEntry.clear();
                for (int i = 0; i < lista.size(); i++) {

                    List<ExpensesAndIncomes> categoryExpesess = db.expensesAndIncomeDAO().getExpensesForOneCategory(lista.get(i).getId());
                    for (int j = 0; j < categoryExpesess.size(); j++) {
                        String dateFromLista = dateFormat.format(categoryExpesess.get(j).getDate());
                        String[] parse = dateFromLista.split("/");
                        int parseInt = Integer.valueOf(parse[1]);
                        if (sevenDays == parseInt){
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
                        if (sevenDays == parseInt1){
                            budget += categoryIncome.get(j).getPrice();
                        }
                    }

                }
                float finalBalance = budget - sum;
                balance = (TextView) view.findViewById(R.id.balance);
                if (finalBalance >= 0){
                    balance.setTextColor(Color.rgb(47, 163, 57));
                }else {
                    balance.setTextColor(Color.rgb(198, 24,21));
                }
                String s = Float.toString(finalBalance);
                balance.setText("Balance: "+s);
            }
        });
    }

    public ArrayList<String> getWeekList(){
        ArrayList<String> listofMonths = new ArrayList<>();
        Date current = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(current);
        cal.set(Calendar.DATE, (cal.get(Calendar.DATE)-7));
        String s;
        for (int i = 6; i >= 0; i--) {
            cal.set(Calendar.DATE, (cal.get(Calendar.DATE)+1));
            current = cal.getTime();
            s = dateFormat.format(current);
            listofMonths.add(s);
        }
        return listofMonths;
    }
}
