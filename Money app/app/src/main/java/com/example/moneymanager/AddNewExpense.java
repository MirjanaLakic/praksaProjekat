package com.example.moneymanager;

import android.content.Intent;
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
import com.example.moneymanager.DAO.TimeStamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private AppDatabase db;
    private FirebaseAuth auth;
    private FirebaseFirestore fireDB;
    private static final String TIME_FORMAT = "dd/MM/yyy HH:mm:ss";
    private SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expense);
        db = AppDatabase.getInstance(getApplicationContext());
        actionBar = this.getSupportActionBar();
        getList();
        img = (ImageView) findViewById(R.id.icon_img_selected2);
        if (icons.size() == 0){
            Toast.makeText(this, "Please Enter Categories First", Toast.LENGTH_SHORT).show();
            finish();
        }else {
            if (getIntent().getStringExtra("edit").equals("edit")) {
                actionBar.setTitle("Edit");
                int id = getIntent().getIntExtra("id", -1);
                ExpensesAndIncomes item = db.expensesAndIncomeDAO().findById(id);
                Category category = db.categoryDAO().findById(item.getCategory());
                img.setImageResource(category.getPhoto());
                categoryID = category.getId();
            } else {
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
    }

    private void getList() {
        if (getIntent().getStringExtra("item").equals("EXPENSES")) {
            icons = db.categoryDAO().loadIcons();
        }else if (getIntent().getStringExtra("item").equals("INCOME")){
            actionBar.setTitle("Add Income");
            icons = db.categoryDAO().loadIconsIncome();
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

                                auth = FirebaseAuth.getInstance();
                                FirebaseUser currentUser = auth.getCurrentUser();
                                db = AppDatabase.getInstance(getApplicationContext());

                                Map<String, Object> map = new HashMap<>();
                                map.put("id", editItem.getId());
                                map.put("note", editItem.getNote());
                                map.put("price", editItem.getPrice());
                                map.put("date", editItem.getDate());
                                map.put("type", editItem.getType());
                                map.put("category", editItem.getCategory());

                                Date date = new Date();
                                String time = timeFormat.format(date);
                                Map<String, String> timeMap = new HashMap<>();
                                timeMap.put("time", time);

                                TimeStamp timeStamp = db.timeStampDAO().getCategoryTime();
                                fireDB = FirebaseFirestore.getInstance();
                                if (item.getType().equals("EXPENSES")) {
                                    fireDB.collection("Expenses").document(currentUser.getEmail()).collection("Expenses").document(editItem.getNote())
                                            .set(map);
                                    fireDB.collection("Expenses").document(currentUser.getEmail()).collection("Expenses").document("time")
                                            .set(timeMap);
                                    timeStamp.setTimeExpenses(time);
                                    db.timeStampDAO().edit(timeStamp);
                                }else {
                                    fireDB.collection("Expenses").document(currentUser.getEmail()).collection("Incomes").document(editItem.getNote())
                                            .set(map);
                                    fireDB.collection("Expenses").document(currentUser.getEmail()).collection("Incomes").document("time")
                                            .set(timeMap);
                                    timeStamp.setTimeIncomes(time);
                                    db.timeStampDAO().edit(timeStamp);
                                }

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
        final String memoStr = memo.getText().toString();
        final float priceint = Float.valueOf(price.getText().toString());

        final ExpensesAndIncomes obj;
        final Date date = new Date();
        obj = new ExpensesAndIncomes(memoStr, priceint, date, getIntent().getStringExtra("item"), categoryID);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.expensesAndIncomeDAO().addNew(obj);


                ExpensesAndIncomes item = db.expensesAndIncomeDAO().find(memoStr, priceint, date, getIntent().getStringExtra("item"), categoryID);
                auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();
                db = AppDatabase.getInstance(getApplicationContext());
                Map<String, Object> map = new HashMap<>();
                map.put("id", item.getId());
                map.put("note", item.getNote());
                map.put("price", item.getPrice());
                map.put("date", item.getDate());
                map.put("type", item.getType());
                map.put("category", item.getCategory());

                Date date = new Date();
                String time = timeFormat.format(date);
                Map<String, String> timeMap = new HashMap<>();
                timeMap.put("time", time);

                TimeStamp timeStamp = db.timeStampDAO().getCategoryTime();
                fireDB = FirebaseFirestore.getInstance();
                if (item.getType().equals("EXPENSES")) {
                    fireDB.collection("Expenses").document(currentUser.getEmail()).collection("Expenses").document(item.getNote())
                            .set(map);
                    fireDB.collection("Expenses").document(currentUser.getEmail()).collection("Expenses").document("time")
                            .set(timeMap);
                    timeStamp.setTimeExpenses(time);
                    db.timeStampDAO().edit(timeStamp);
                }else {
                    fireDB.collection("Expenses").document(currentUser.getEmail()).collection("Incomes").document(item.getNote())
                            .set(map);
                    fireDB.collection("Expenses").document(currentUser.getEmail()).collection("Incomes").document("time")
                            .set(timeMap);
                    timeStamp.setTimeIncomes(time);
                    db.timeStampDAO().edit(timeStamp);
                }
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
