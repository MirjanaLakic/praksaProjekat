package com.example.moneymanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private List<Category> data;
    private AppDatabase db;
    private FirebaseUser currentUser;
    private FirebaseFirestore fireDB;
    private FirebaseAuth auth;

    private static final String TIME_FORMAT = "dd/MM/yyy HH:mm:ss";
    private SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());

    public RecyclerViewAdapter(Context context, List<Category> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_category, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {

        myViewHolder.tv_name.setText(data.get(i).getName());
        myViewHolder.img.setImageResource(data.get(i).getPhoto());

    }

    @Override
    public int getItemCount() {
        if (data == null){
            return 0;
        }
        return data.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_name;
        private ImageView img;
        private ImageButton delete;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.name_category);
            img = (ImageView) itemView.findViewById(R.id.img_category);
            delete = (ImageButton) itemView.findViewById(R.id.delete_category);
        }
    }

    public void setExpenses(List<Category> categoryList){
        data = categoryList;
        notifyDataSetChanged();
    }

    public void setIncomes(List<Category> categoryList){
        data = categoryList;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete this category all your data will be lost");
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
                                db = AppDatabase.getInstance(context);
                                auth = FirebaseAuth.getInstance();
                                fireDB = FirebaseFirestore.getInstance();
                                Category category = db.categoryDAO().findById(data.get(position).getId());
                                List<ExpensesAndIncomes> toDelete = db.expensesAndIncomeDAO().getExpensesForOneCategory(category.getId());
                                FirebaseUser currentUser = auth.getCurrentUser();
                                Date date = new Date();
                                String time = timeFormat.format(date);
                                Map<String, String> timeMap = new HashMap<>();
                                timeMap.put("time", time);

                                TimeStamp timeStamp = db.timeStampDAO().getCategoryTime();
                                if (toDelete.size() != 0) {
                                    for (int i = 0; i < toDelete.size(); i++) {
                                        ExpensesAndIncomes item = db.expensesAndIncomeDAO().findById(toDelete.get(i).getId());
                                        if (category.getType().equals("EXPENSES")) {
                                            fireDB.collection("Expenses").document(currentUser.getEmail()).collection("Expenses").document(item.getNote())
                                                    .delete();
                                            timeStamp.setTimeCategory(time);
                                            db.timeStampDAO().edit(timeStamp);
                                            fireDB.collection("Expenses").document(currentUser.getEmail()).collection("Expenses").document("time")
                                                    .set(timeMap);
                                        } else {
                                            fireDB.collection("Expenses").document(currentUser.getEmail()).collection("Incomes").document(category.getName())
                                                    .delete();
                                            fireDB.collection("Expenses").document(currentUser.getEmail()).collection("Incomes").document("time")
                                                    .set(timeMap);
                                            timeStamp.setTimeCategoryIncome(time);
                                            db.timeStampDAO().edit(timeStamp);
                                        }
                                        db.expensesAndIncomeDAO().delete(item);

                                    }
                                }
                                db.categoryDAO().deleteCateogry(category);


                                if (category.getType().equals("EXPENSES")) {
                                    fireDB.collection("Categories").document(currentUser.getEmail()).collection("UserCategoriesExpenses").document(category.getName())
                                            .delete();
                                    timeStamp.setTimeCategory(time);
                                    db.timeStampDAO().edit(timeStamp);
                                    fireDB.collection("Categories").document(currentUser.getEmail()).collection("UserCategoriesExpenses").document("time")
                                            .set(timeMap);
                                } else {
                                    fireDB.collection("Categories").document(currentUser.getEmail()).collection("UserCategoryIncomes").document(category.getName())
                                            .delete();
                                    fireDB.collection("Categories").document(currentUser.getEmail()).collection("UserCategoryIncomes").document("time")
                                            .set(timeMap);
                                    timeStamp.setTimeCategoryIncome(time);
                                    db.timeStampDAO().edit(timeStamp);
                                }


                            }
                        });
                    }
                });
                builder.show();

            }
        });
    }
}
