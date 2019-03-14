package com.example.moneymanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Category;
import com.example.moneymanager.DAO.ExpensesAndIncomes;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RecyclerViewAdapterExpense extends RecyclerView.Adapter<RecyclerViewAdapterExpense.MyViewHolder> {

    private Context context;
    private List<ExpensesAndIncomes> data;
    private AppDatabase db;
    private static final String DATE_FORMAT = "dd/MM/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public RecyclerViewAdapterExpense(Context context, List<ExpensesAndIncomes> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v;
        db = AppDatabase.getInstance(context);
        v = LayoutInflater.from(context).inflate(R.layout.item_expense, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {

        myViewHolder.memo.setText(data.get(i).getNote());
        int id = data.get(i).getCategory();
        Category category = db.categoryDAO().findById(id);
        myViewHolder.category.setText(category.getName());
        String f = Float.toString(data.get(i).getPrice());
        myViewHolder.price.setText(f);
        String date = dateFormat.format(data.get(i).getDate());
        myViewHolder.date.setText(date);
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

        private TextView memo;
        private TextView category;
        private TextView price;
        private TextView date;
        private ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);

            memo = (TextView) itemView.findViewById(R.id.memo);
            category = (TextView) itemView.findViewById(R.id.cateogory);
            price = (TextView) itemView.findViewById(R.id.price);
            date = (TextView) itemView.findViewById(R.id.date);
            img = (ImageView) itemView.findViewById(R.id.img_list);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,final int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Details.class);
                int id = data.get(position).getId();
                intent.putExtra("id", id);
                v.getContext().startActivity(intent);
            }
        });
    }

    public void setList(List<ExpensesAndIncomes> list){
        data = list;
        notifyDataSetChanged();
    }
}
