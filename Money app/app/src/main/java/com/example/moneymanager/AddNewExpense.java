package com.example.moneymanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Category;
import com.example.moneymanager.DAO.ExpensesAndIncomes;

import java.util.Date;
import java.util.List;

public class AddNewExpense extends AppCompatActivity {

    private EditText memo;
    private EditText price;
    private Button add;
    List<Category> list;
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
        if (getIntent().getStringExtra("edit").equals("edit")){
            final int id = getIntent().getIntExtra("id", -1);
            final ExpensesAndIncomes item = db.expensesAndIncomeDAO().findById(id);
            memo.setText(item.getNote());
            price.setText(String.valueOf(item.getPrice()));
            add.setText("EDIT");

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String memoStr = memo.getText().toString();
                    float priceint = Float.valueOf(price.getText().toString());
                    final ExpensesAndIncomes editItem = new ExpensesAndIncomes(item.getId(), memoStr, priceint, item.getDate(), item.getType(), item.getCategory());
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            db.expensesAndIncomeDAO().edit(editItem);
                            finish();
                        }
                    });
                }
            });
        }else {
            add.setText("ADD");
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            onSaveButtonClicked();
                        }
                    });
                }
            });
        }
    }

    public void onSaveButtonClicked(){
        String memoStr = memo.getText().toString();
        float priceint = Float.valueOf(price.getText().toString());
        final ExpensesAndIncomes obj;
        Date date = new Date();
        if (getIntent().getStringExtra("item").equals("EXPENSE")){
            obj = new ExpensesAndIncomes(memoStr, priceint, date, "EXPENSE", 2);
        }else
            obj = new ExpensesAndIncomes(memoStr, priceint, date, "INCOME", 4);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.expensesAndIncomeDAO().addNew(obj);
                finish();
            }
        });
    }
}
