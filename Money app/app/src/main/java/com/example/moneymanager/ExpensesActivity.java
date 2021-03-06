package com.example.moneymanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.moneymanager.fragments.ExpensesDaily;
import com.example.moneymanager.fragments.ExpensesMonthly;
import com.example.moneymanager.fragments.ExpensesWeekly;
import com.example.moneymanager.fragments.ExpensesYearly;

public class ExpensesActivity extends AppCompatActivity{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        tabLayout = (TabLayout) findViewById(R.id.tablayout_id_expenses);
        viewPager = (ViewPager) findViewById(R.id.viewpaget_id_expenses);
        viewPager.setOffscreenPageLimit(3);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new ExpensesDaily(), "Daily");
        viewPagerAdapter.addFragment(new ExpensesWeekly(), "Weekly");
        viewPagerAdapter.addFragment(new ExpensesMonthly(), "Monthly");
        viewPagerAdapter.addFragment(new ExpensesYearly(), "Yearly");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        try {
            if (getIntent().getIntExtra("income", 0) == 2) {
                TabLayout.Tab tab = tabLayout.getTabAt(2);
                tab.select();
            }
        }catch (NullPointerException e){

        }

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setElevation(0);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // When the home button is pressed, take the user back to the MoneyManager
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }else if (id == R.id.add_income){
            Intent intent = new Intent(getApplicationContext(), AddNewExpense.class);
            intent.putExtra("item", "INCOME");
            intent.putExtra("edit", "add");
            startActivity(intent);
        }else if (id == R.id.add_expense){
            Intent intent = new Intent(getApplicationContext(), AddNewExpense.class);
            intent.putExtra("item", "EXPENSES");
            intent.putExtra("edit", "add");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public ExpensesActivity(){ }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.expense_menu, menu);
        return true;
    }

}
