package com.example.moneymanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Category;
import com.example.moneymanager.DAO.ExpensesAndIncomes;
import com.example.moneymanager.DAO.Icon;

import java.util.Date;
import java.util.List;

public class AddNewExpense extends AppCompatActivity implements Icon {

    private EditText memo;
    private EditText price;
    private Button add;
    private ImageView img;
    private List<Category> icons;
    private RecyclerView recyclerView;
    private RecyclerViewIconAndName recyclerViewAdapter;
    private int categoryID;
    private ActionBar actionBar;
    AppDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expense);
        db = AppDatabase.getInstance(getApplicationContext());
        actionBar = this.getSupportActionBar();
        getList();
        img = (ImageView) findViewById(R.id.icon_img_selected2);
        if (getIntent().getStringExtra("edit").equals("edit")) {
            actionBar.setTitle("Edit");
            int id = getIntent().getIntExtra("id", -1);
            ExpensesAndIncomes item = db.expensesAndIncomeDAO().findById(id);
            Category category = db.categoryDAO().findById(item.getCategory());
            img.setImageResource(category.getPhoto());
            categoryID = category.getId();
        }else {
            img.setImageResource(icons.get(0).getPhoto());
            categoryID = icons.get(0).getId();
        }

        recyclerView = (RecyclerView) findViewById(R.id.rec_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 7);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerViewAdapter = new RecyclerViewIconAndName(getApplicationContext(), icons, this);
        recyclerView.setAdapter(recyclerViewAdapter);
        initViews();
    }

    private void getList() {
        if (getIntent().getStringExtra("item").equals("EXPENSES")) {
            icons = db.categoryDAO().loadIcons();
        }else if (getIntent().getStringExtra("item").equals("INCOME")){
            actionBar.setTitle("Add Income");
            icons = db.categoryDAO().loadIconsIncome();
        }else {
        }
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

                    if (memo.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Please Enter Name", Toast.LENGTH_SHORT).show();
                    }else if (price.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Please Enter Price", Toast.LENGTH_SHORT).show();
                    }else {
                        String memoStr = memo.getText().toString();
                        float priceint = Float.valueOf(price.getText().toString());
                        final ExpensesAndIncomes editItem = new ExpensesAndIncomes(item.getId(), memoStr, priceint, item.getDate(), item.getType(), categoryID);
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                db.expensesAndIncomeDAO().edit(editItem);
                                finish();
                            }
                        });
                    }
                }
            });
        }else {
            add.setText("ADD");
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (memo.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Please Enter Name", Toast.LENGTH_SHORT).show();
                    }else if (price.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Please Enter Price", Toast.LENGTH_SHORT).show();
                    }else {
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                onSaveButtonClicked();
                            }
                        });
                    }
                }
            });
        }
    }

    public void onSaveButtonClicked(){
        String memoStr = memo.getText().toString();
        float priceint = Float.valueOf(price.getText().toString());

        final ExpensesAndIncomes obj;
        Date date = new Date();
        obj = new ExpensesAndIncomes(memoStr, priceint, date, getIntent().getStringExtra("item"), categoryID);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.expensesAndIncomeDAO().addNew(obj);
                finish();
            }
        });
    }

    @Override
    public void selectedIcon(int value) {
        Category category = db.categoryDAO().findById(value);
        img.setImageResource(category.getPhoto());
        categoryID = value;
    }
}
