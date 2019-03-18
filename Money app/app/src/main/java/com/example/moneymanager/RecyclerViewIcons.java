package com.example.moneymanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Category;

import java.util.List;

public class RecyclerViewIcons extends RecyclerView.Adapter<RecyclerViewIcons.MyViewHolder> {

    private Context context;
    private List<Category> data;
    private AppDatabase db;

    public RecyclerViewIcons(Context context, List<Category> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v;
        db = AppDatabase.getInstance(context);
        v = LayoutInflater.from(context).inflate(R.layout.icon_category, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {

        int id = data.get(i).getId();
        Category category = db.categoryDAO().findById(id);
        myViewHolder.img.setImageResource(category.getPhoto());

    }

    @Override
    public int getItemCount() {
        if (data == null){
            return 0;
        }
        return data.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageButton img;

        public MyViewHolder(View itemView) {
            super(itemView);
            img = (ImageButton) itemView.findViewById(R.id.icon_img);
        }
    }
}
