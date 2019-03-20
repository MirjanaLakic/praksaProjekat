package com.example.moneymanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneymanager.DAO.Category;
import com.example.moneymanager.DAO.Icon;

import java.util.List;

public class RecyclerViewIconAndName extends RecyclerView.Adapter<RecyclerViewIconAndName.MyViewHolder>  {
    private Context context;
    private List<Category> icons;
    private Icon icon;
    private View v;

    public RecyclerViewIconAndName(Context context, List<Category> icons, Icon icon) {
        this.context = context;
        this.icons = icons;
        this.icon = icon;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        v = LayoutInflater.from(context).inflate(R.layout.icon_and_name, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.img.setImageResource(icons.get(i).getPhoto());
        myViewHolder.name.setText(icons.get(i).getName());
    }

    @Override
    public int getItemCount() {
        if (icons == null){
            return 0;
        }
        return icons.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView img;
        private TextView name;

        public MyViewHolder(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.img_icon);
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icon.selectedIcon(icons.get(position).getId());
            }
        });
    }
}
