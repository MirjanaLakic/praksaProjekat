package com.example.moneymanager;

import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Category;
import com.example.moneymanager.DAO.ExpensesAndIncomes;
import com.example.moneymanager.DAO.TimeStamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
    private Category cat;
    private FloatingActionButton edit;
    private ActionMenuItemView delete;
    private FirebaseAuth auth;
    private FirebaseFirestore fireDB;
    private static final String TIME_FORMAT = "dd/MM/yyy HH:mm:ss";
    private SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_item);
        db = AppDatabase.getInstance(this);
        int id = getIntent().getIntExtra("id", 0);
        item = db.expensesAndIncomeDAO().findById(id);

        img = (ImageView) findViewById(R.id.img_row);
        note = (TextView) findViewById(R.id.note_row);
        date = (TextView) findViewById(R.id.date_row);
        category = (TextView) findViewById(R.id.category_row);
        price = (TextView) findViewById(R.id.price_row);
        edit = (FloatingActionButton) findViewById(R.id.edit_item);
        final String dateFinal = dateFormat.format(item.getDate());
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddNewExpense.class);
                intent.putExtra("edit", "edit");
                intent.putExtra("id", item.getId());
                Category category1 = db.categoryDAO().findById(item.getCategory());
                intent.putExtra("item", category1.getType());
                startActivity(intent);
            }
        });
        LiveData<ExpensesAndIncomes> oneItem = db.expensesAndIncomeDAO().findItem(id);
        oneItem.observe(this, new Observer<ExpensesAndIncomes>() {
            @Override
            public void onChanged(@Nullable ExpensesAndIncomes edit) {
                if (edit != null) {
                    note.setText(edit.getNote());
                    date.setText(dateFinal);
                    cat = db.categoryDAO().findById(edit.getCategory());
                    img.setImageResource(cat.getPhoto());
                    category.setText(cat.getName());
                    price.setText(String.valueOf(edit.getPrice()));
                }
            }
        });
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
                        auth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = auth.getCurrentUser();
                        db = AppDatabase.getInstance(getApplicationContext());

                        Date date = new Date();
                        String time = timeFormat.format(date);
                        Map<String, String> timeMap = new HashMap<>();
                        timeMap.put("time", time);

                        TimeStamp timeStamp = db.timeStampDAO().getCategoryTime();
                        fireDB = FirebaseFirestore.getInstance();
                        if (item.getType().equals("EXPENSES")) {
                            fireDB.collection("Expenses").document(currentUser.getEmail()).collection("Expenses").document(item.getNote())
                                    .delete();
                            fireDB.collection("Expenses").document(currentUser.getEmail()).collection("Expenses").document("time")
                                    .set(timeMap);
                            timeStamp.setTimeExpenses(time);
                            db.timeStampDAO().edit(timeStamp);
                        }else {
                            fireDB.collection("Expenses").document(currentUser.getEmail()).collection("Incomes").document(item.getNote())
                                    .delete();
                            fireDB.collection("Expenses").document(currentUser.getEmail()).collection("Incomes").document("time")
                                    .set(timeMap);
                            timeStamp.setTimeIncomes(time);
                            db.timeStampDAO().edit(timeStamp);
                        }
                        db.expensesAndIncomeDAO().delete(item);
                        finish();
                    }
                });
            }
        });
        builder.show();
    }
}
