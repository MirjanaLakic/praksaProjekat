package com.example.moneymanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.DateConverter;
import com.example.moneymanager.DAO.ExpensesAndIncomeDAO;
import com.example.moneymanager.DAO.ExpensesAndIncomes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddNewExpense extends AppCompatActivity {

    private EditText memo;
    private EditText price;
    private Button add;
    AppDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expense);

        db = AppDatabase.getInstance(getApplicationContext());

        initViews();
    }

    private void initViews() {
        memo = (EditText) findViewById(R.id.memo);
        price = (EditText) findViewById(R.id.price);
        add = (Button) findViewById(R.id.add_expense);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClicked();
            }
        });
    }

    public void onSaveButtonClicked(){
        String memoStr = memo.getText().toString();
        float priceint = Integer.parseInt(price.getText().toString());
        Date date = new Date();
        final ExpensesAndIncomes obj = new ExpensesAndIncomes(memoStr, priceint, date, 8);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.expensesAndIncomeDAO().addNew(obj);
                finish();
            }
        });
    }
}
