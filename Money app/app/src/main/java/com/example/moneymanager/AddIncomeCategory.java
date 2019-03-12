package com.example.moneymanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Category;

public class AddIncomeCategory extends AppCompatActivity {

    public static final String INSTANCE_INCOME_ID = "instanceIncomeId";
    private static final int DEFAULT_CATEGORY_ID = -1;
    private AppDatabase db;
    EditText text;
    Button button;

    private int incomeId = DEFAULT_CATEGORY_ID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_income_activity);

        initViews();

        db = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_INCOME_ID)){
            incomeId = savedInstanceState.getInt(INSTANCE_INCOME_ID, DEFAULT_CATEGORY_ID);
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_INCOME_ID, incomeId);
        super.onSaveInstanceState(outState);

    }


    public AddIncomeCategory(){}

    private void initViews() {
        text = (EditText) findViewById(R.id.name_of_income);
        button = (Button) findViewById(R.id.add_button_income);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClicked();
            }
        });
    }

    public void onSaveButtonClicked(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String name = text.getText().toString();
                Category category = new Category(name, R.drawable.investments, "INCOME");
                db.categoryDAO().addCategory(category);
                finish();
            }
        });
    }
}
