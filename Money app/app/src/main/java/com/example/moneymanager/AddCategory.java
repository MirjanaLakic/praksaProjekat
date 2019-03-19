package com.example.moneymanager;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class AddCategory extends AppCompatActivity implements Icon {

    private RecyclerView recyclerView;
    private RecyclerViewIcons recyclerViewAdapter;
    private List<Integer> icons = new ArrayList<>();
    private EditText name;
    private ImageView img;
    private ImageView add;
    private int pos = 0;
    private AppDatabase db;

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
            category = new Category(name.getText().toString(), pos, getIntent().getStringExtra("type"));
            db = AppDatabase.getInstance(getApplicationContext());
            db.categoryDAO().addCategory(category);
            finish();
        }
    }

    @Override
    public void selectedIcon(int value) {
        img.setImageResource(value);
        pos = value;
    }
}
