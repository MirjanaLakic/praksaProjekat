package com.example.moneymanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Category;
import com.example.moneymanager.DAO.Icon;
import com.example.moneymanager.DAO.TimeStamp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddCategory extends AppCompatActivity implements Icon {

    private RecyclerView recyclerView;
    private RecyclerViewIcons recyclerViewAdapter;
    private List<Integer> icons = new ArrayList<>();
    private EditText name;
    private ImageView img;
    private ImageView add;
    private int pos = 0;
    private AppDatabase db;
    private FirebaseAuth auth;
    private FirebaseFirestore fireDB;
    private static final String TIME_FORMAT = "dd/MM/yyy HH:mm:ss";
    private SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_category);

        addToList();
        img = (ImageView) findViewById(R.id.icon_img_selected);
        img.setImageResource(icons.get(0));
        recyclerView = (RecyclerView) findViewById(R.id.icon_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 7);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerViewAdapter = new RecyclerViewIcons(getApplicationContext(), icons, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        add = (ImageView) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

    }

    public void addToList(){
        icons.add(R.drawable.bills);
        icons.add(R.drawable.dividends);
        icons.add(R.drawable.entertainment);
        icons.add(R.drawable.food);
        icons.add(R.drawable.home);
        icons.add(R.drawable.investments);
        icons.add(R.drawable.salary);
        icons.add(R.drawable.shopping_bag);
    }

    public void save(){
        name = (EditText) findViewById(R.id.name);
        Category category;
        if (name.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Please Enter Name", Toast.LENGTH_SHORT).show();
        }else {
            if (pos == 0){
                pos = icons.get(0);
            }
            category = new Category(name.getText().toString(), pos, getIntent().getStringExtra("type"));

            auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            db = AppDatabase.getInstance(getApplicationContext());
            db.categoryDAO().addCategory(category);
            Category category1 = db.categoryDAO().find(name.getText().toString(), pos, getIntent().getStringExtra("type"));
            Map<String, Object> map = new HashMap<>();
            map.put("id", category1.getId());
            map.put("name", category1.getName());
            map.put("photo", category1.getPhoto());
            map.put("type", category1.getType());

            Date date = new Date();
            String time = timeFormat.format(date);
            Map<String, String> timeMap = new HashMap<>();
            timeMap.put("time", time);

            TimeStamp timeStamp = db.timeStampDAO().getCategoryTime();
            fireDB = FirebaseFirestore.getInstance();
            if (category1.getType().equals("EXPENSES")) {
                fireDB.collection("Categories").document(currentUser.getEmail()).collection("UserCategoriesExpenses").document(category1.getName())
                        .set(map);
                fireDB.collection("Categories").document(currentUser.getEmail()).collection("UserCategoriesExpenses").document("time")
                        .set(timeMap);
                timeStamp.setTimeCategory(time);
                db.timeStampDAO().edit(timeStamp);
            }else {
                fireDB.collection("Categories").document(currentUser.getEmail()).collection("UserCategoryIncomes").document(category1.getName())
                        .set(map);
                fireDB.collection("Categories").document(currentUser.getEmail()).collection("UserCategoryIncomes").document("time")
                        .set(timeMap);
                timeStamp.setTimeCategoryIncome(time);
                db.timeStampDAO().edit(timeStamp);
            }
            finish();
        }
    }

    @Override
    public void selectedIcon(int value) {
        img.setImageResource(value);
        pos = value;
    }
}
