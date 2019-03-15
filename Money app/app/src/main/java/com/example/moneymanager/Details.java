package com.example.moneymanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.view.Menu;
import android.view.MenuItem;
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
    private TextView category;
    private TextView price;
    private AppDatabase db;
    private ExpensesAndIncomes item;
    private static final String DATE_FORMAT = "dd/MM/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    FloatingActionButton edit;
    ActionMenuItemView delete;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_item);

        int id = getIntent().getIntExtra("id", 0);
        db = AppDatabase.getInstance(this);
        item = db.expensesAndIncomeDAO().findById(id);

        img = (ImageView) findViewById(R.id.img_row);
        note = (TextView) findViewById(R.id.note_row);
        date = (TextView) findViewById(R.id.date_row);
        category = (TextView) findViewById(R.id.category_row);
        price = (TextView) findViewById(R.id.price_row);
        String dateformat = dateFormat.format(item.getDate());

        Category cat = db.categoryDAO().findById(item.getCategory());

        note.setText(item.getNote());
        date.setText(dateformat);
        img.setImageResource(cat.getPhoto());
        category.setText(cat.getName());
        price.setText(String.valueOf(item.getPrice()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete_item) {
            deleteItem(id);
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteItem(int id){
        delete = (ActionMenuItemView) findViewById(id);
        AlertDialog.Builder builder = new AlertDialog.Builder(Details.this);
        builder.setCancelable(true);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete this item?");
        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int idItem = getIntent().getIntExtra("id", 0);
                        ExpensesAndIncomes item = db.expensesAndIncomeDAO().findById(idItem);
                        db.expensesAndIncomeDAO().delete(item);
                        finish();
                    }
                });
            }
        });
        builder.show();
    }
}
