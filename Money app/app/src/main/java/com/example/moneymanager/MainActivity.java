package com.example.moneymanager;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Category;
import com.example.moneymanager.DAO.ExpensesAndIncomes;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final ArrayList<PieEntry> yEntry = new ArrayList<>();
    final ArrayList<String> xEntry = new ArrayList<>();
    final ArrayList<BarEntry> yBarEx = new ArrayList<>();
    final ArrayList<BarEntry> yBarIn = new ArrayList<>();
    private static final String DATE_FORMAT = "dd/MM/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private float finalSum;
    private AppDatabase db;
    private PieChart pieChart;
    private BarChart barChart;
    private BarChart barChartIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        pieChart = (PieChart) findViewById(R.id.pie_main);
        setPieChart();

        pieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExpensesActivity.class);
                startActivity(intent);
            }
        });
        barChart = (BarChart) findViewById(R.id.bar_expenses_main);
        setBarEx();
        barChartIncome = (BarChart) findViewById(R.id.bar_incomes_main);
        setBarIn();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(this, CategoriesActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, ExpensesActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setPieChart(){
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(70f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterTextSize(15);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        addDataToChart();
    }

    private void addDataToChart(){
        finalSum = 0;
        Date date2 = new Date();
        String seventhDay = dateFormat.format(date2);
        String[] str = seventhDay.split("/");
        final int sevenDays = Integer.valueOf(str[1]);
        db = AppDatabase.getInstance(MainActivity.this);
        LiveData<List<Category>> tasks = db.categoryDAO().loadAllExpences();
        tasks.observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> lista) {
                for (int i = 0; i < lista.size(); i++) {
                    float sum = 0;
                    List<ExpensesAndIncomes> categoryExpesess = db.expensesAndIncomeDAO().getExpensesForOneCategory(lista.get(i).getId());
                    for (int j = 0; j < categoryExpesess.size(); j++) {
                        String dateFromLista = dateFormat.format(categoryExpesess.get(j).getDate());
                        String[] parse = dateFromLista.split("/");
                        int parseInt = Integer.valueOf(parse[1]);
                        if (sevenDays <= parseInt){
                            sum += categoryExpesess.get(j).getPrice();
                            xEntry.add(lista.get(i).getName());
                        }

                    }
                    if (sum != 0) {
                        yEntry.add(new PieEntry(sum, i));
                        finalSum += sum;
                    }
                }
                //create the data set
                PieDataSet pieDataSet = new PieDataSet(yEntry, "");
                pieDataSet.setSliceSpace(0);
                //pieDataSet.setDrawValues(false);
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                //create pie data object
                PieData pieData = new PieData(pieDataSet);
                String month = getMonth();
                pieChart.setCenterText(month +"\n Expenses \n"+ finalSum);
                pieChart.setTouchEnabled(false);
                pieChart.setData(pieData);
                pieChart.invalidate();

            }

        });
    }

    public String getMonth(){
        String[] monthName = {"January", "February",
                "March", "April", "May", "June", "July",
                "August", "September", "October", "November",
                "December"};

        Calendar cal = Calendar.getInstance();
        String month = monthName[cal.get(Calendar.MONTH)];

        return month;
    }

    public void addDataToBarEx(){
        Date date2 = new Date();
        String seventhDay = dateFormat.format(date2);
        final String[] str = seventhDay.split("/");
        final int sevenDays = Integer.valueOf(str[1]);
        db = AppDatabase.getInstance(MainActivity.this);
        LiveData<List<Category>> tasks = db.categoryDAO().loadAllExpences();
        tasks.observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> lista) {
                ArrayList<Integer> xMonth = new ArrayList<>();
                final ArrayList<Integer> listMonths = getMonthList();
                int increment = 0;
                for (int j = 0; j < listMonths.size(); j++) {
                    float sum = 0;
                    for (int i = 0; i < lista.size(); i++) {
                        List<ExpensesAndIncomes> categoryExpesess = db.expensesAndIncomeDAO().getExpensesForOneCategory(lista.get(i).getId());
                        for (int k = 0; k < categoryExpesess.size(); k++) {
                            String dateFromLista = dateFormat.format(categoryExpesess.get(k).getDate());
                            String[] parse = dateFromLista.split("/");
                            int parseInt = Integer.valueOf(parse[1]);
                            if (listMonths.get(j) == parseInt) {
                                sum += categoryExpesess.get(k).getPrice();
                            }

                        }
                    }
                    yBarEx.add(new BarEntry(increment, sum));
                    increment += 1;
                }


                BarDataSet barDataSet = new BarDataSet(yBarEx, "Expenses");
                BarData data = new BarData(barDataSet);
                barDataSet.setDrawValues(false);
                barChart.setData(data);
                barDataSet.setColor(Color.RED);
            }

        });
    }

    public ArrayList<Integer> getMonthList(){
        ArrayList<Integer> listofMonths = new ArrayList<>();
        Date current = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(current);
        cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH)-6));
        current = cal.getTime();
        String s = dateFormat.format(current);
        String[] parse = s.split("/");
        int month = Integer.valueOf(parse[1]);
        listofMonths.add(month);
        for (int i = 5; i >= 0; i--) {
            cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH)+1));
            current = cal.getTime();
            s = dateFormat.format(current);
            parse = s.split("/");
            month = Integer.valueOf(parse[1]);
            listofMonths.add(month);
        }
        return listofMonths;
    }

    public ArrayList<String> getMonthFirstLetter(){
        String[] firstLetter = {"J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D"};
        ArrayList<Integer> lastSix = getMonthList();
        ArrayList<String> first = new ArrayList<>();
        for (int i = 0; i < lastSix.size(); i++) {
            first.add(firstLetter[lastSix.get(i)-1]);
        }

        return first;
    }

    public void setBarEx(){
        final ArrayList<String> strLetter = getMonthFirstLetter();
        final XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return strLetter.get(Math.round(value));
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getLegend().setPosition(Legend.LegendPosition.LEFT_OF_CHART_CENTER);
        barChart.setDrawValueAboveBar(false);
        barChart.setTouchEnabled(false);
        addDataToBarEx();
    }

    public void addDataToBarIn(){
        Date date2 = new Date();
        String seventhDay = dateFormat.format(date2);
        final String[] str = seventhDay.split("/");
        final int sevenDays = Integer.valueOf(str[1]);
        db = AppDatabase.getInstance(MainActivity.this);
        LiveData<List<Category>> tasks = db.categoryDAO().loadAllIncomes();
        tasks.observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> lista) {
                ArrayList<Integer> xMonth = new ArrayList<>();
                final ArrayList<Integer> listMonths = getMonthList();
                int increment = 0;
                for (int j = 0; j < listMonths.size(); j++) {
                    float sum = 0;
                    for (int i = 0; i < lista.size(); i++) {
                        List<ExpensesAndIncomes> categoryExpesess = db.expensesAndIncomeDAO().getExpensesForOneCategory(lista.get(i).getId());
                        for (int k = 0; k < categoryExpesess.size(); k++) {
                            String dateFromLista = dateFormat.format(categoryExpesess.get(k).getDate());
                            String[] parse = dateFromLista.split("/");
                            int parseInt = Integer.valueOf(parse[1]);
                            if (listMonths.get(j) == parseInt) {
                                sum += categoryExpesess.get(k).getPrice();
                            }

                        }
                    }
                    yBarIn.add(new BarEntry(increment, sum));
                    increment += 1;
                }


                BarDataSet barDataSet = new BarDataSet(yBarIn, "Incomes");
                BarData data = new BarData(barDataSet);
                barDataSet.setDrawValues(false);
                barChartIncome.setData(data);
                barDataSet.setColor(Color.GREEN);
            }

        });
    }

    public void setBarIn(){
        final ArrayList<String> strLetter = getMonthFirstLetter();
        final XAxis xAxis = barChartIncome.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return strLetter.get(Math.round(value));
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barChartIncome.getDescription().setEnabled(false);
        barChartIncome.getAxisRight().setEnabled(false);
        barChartIncome.getAxisLeft().setEnabled(false);
        barChartIncome.getXAxis().setDrawGridLines(false);
        barChartIncome.getAxisRight().setDrawGridLines(false);
        barChartIncome.getAxisLeft().setDrawGridLines(false);
        barChartIncome.getLegend().setPosition(Legend.LegendPosition.LEFT_OF_CHART_CENTER);
        barChartIncome.setDrawValueAboveBar(false);
        barChartIncome.setTouchEnabled(false);
        addDataToBarIn();
    }

}
