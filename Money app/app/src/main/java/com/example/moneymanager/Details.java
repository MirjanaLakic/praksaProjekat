package com.example.moneymanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Category;
import com.example.moneymanager.DAO.ExpensesAndIncomes;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Details extends AppCompatActivity {

    private ImageView img;
    private TextView note;
    private TextView date;
    private TextView type;
    private TextView category;
    private TextView price;
    private AppDatabase db;
    private static final String DATE_FORMAT = "dd/MM/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_item);

        int id = getIntent().getIntExtra("id", 0);
        db = AppDatabase.getInstance(this);
        ExpensesAndIncomes item = db.expensesAndIncomeDAO().findById(id);

        img = (ImageView) findViewById(R.id.img_row);
        note = (TextView) findViewById(R.id.note_row);
        date = (TextView) findViewById(R.id.date_row);
        type = (TextView) findViewById(R.id.type_row);
        category = (TextView) findViewById(R.id.category_row);
        price = (TextView) findViewById(R.id.price_row);
        String dateformat = dateFormat.format(item.getDate());

        Category cat = db.categoryDAO().findById(item.getCategory());

        note.setText(item.getNote());
        date.setText(dateformat);
        type.setText(cat.getType());



    }




}
