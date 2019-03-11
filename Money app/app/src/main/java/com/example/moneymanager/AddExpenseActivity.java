package com.example.moneymanager;

import android.arch.persistence.room.Database;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Category;

public class AddExpenseActivity extends AppCompatActivity {

    public static final String INSTANCE_EXPENSE_ID = "instanceExpenseId";
    private static final int DEFAULT_CATEGORY_ID = -1;
    private AppDatabase db;
    EditText text;
    Button button;

    private int expenseId = DEFAULT_CATEGORY_ID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expence_activity);

        initViews();

        db = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_EXPENSE_ID)){
            expenseId = savedInstanceState.getInt(INSTANCE_EXPENSE_ID, DEFAULT_CATEGORY_ID);
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_EXPENSE_ID, expenseId);
        super.onSaveInstanceState(outState);

    }

    private void initViews() {
        text = (EditText) findViewById(R.id.name_of_expense);
        button = (Button) findViewById(R.id.add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClicked();
            }
        });
    }

    public void onSaveButtonClicked(){
        String name = text.getText().toString();
        final Category category = new Category(name, R.drawable.home, "EXPENSES");
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.categoryDAO().addCategory(category);
                finish();
            }
        });
    }

    public AddExpenseActivity(){}
}
