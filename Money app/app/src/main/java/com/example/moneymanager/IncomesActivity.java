package com.example.moneymanager;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Category;
import com.example.moneymanager.DAO.ExpensesAndIncomes;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IncomesActivity extends AppCompatActivity {

    private RecyclerViewIncomes recyclerViewAdapter;
    private RecyclerView recyclerView;
    private AppDatabase db;
    private static final String DATE_FORMAT = "dd/MM/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    List<ExpensesAndIncomes> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incomes_activity);

        db = AppDatabase.getInstance(this);

        getList();

        recyclerView = (RecyclerView) findViewById(R.id.incomes_recyclerview);
        recyclerViewAdapter = new RecyclerViewIncomes(getApplicationContext(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void getList() {
        Date date2 = new Date();
        String seventhDay = dateFormat.format(date2);
        String[] str = seventhDay.split("/");
        final int sevenDays = Integer.valueOf(str[1]);
        LiveData<List<ExpensesAndIncomes>> tasks = db.expensesAndIncomeDAO().getAllIncome();
        tasks.observe(this, new Observer<List<ExpensesAndIncomes>>() {
            @Override
            public void onChanged(@Nullable List<ExpensesAndIncomes> lista) {
                if (lista.size() != 0) {
                    List<ExpensesAndIncomes> l = new ArrayList<>();
                    for (int i = 0; i < lista.size(); i++) {
                        String dateFromLista = dateFormat.format(lista.get(i).getDate());
                        String[] parse = dateFromLista.split("/");
                        int parseInt = Integer.valueOf(parse[1]);
                        if (sevenDays <= parseInt) {
                            l.add(lista.get(i));
                        }
                    }
                    recyclerViewAdapter.setList(l);
                }
            }
        });
    }
}
