package com.example.moneymanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.moneymanager.DAO.Icon;

import java.util.List;

public class RecyclerViewIcons extends RecyclerView.Adapter<RecyclerViewIcons.MyViewHolder> {
    private Context context;
    private List<Integer> icons;
    private Icon icon;
    private View v;

    public RecyclerViewIcons(Context context, List<Integer> icons, Icon icon) {
        this.context = context;
        this.icons = icons;
        this.icon = icon;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        v = LayoutInflater.from(context).inflate(R.layout.icon_category, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {

        myViewHolder.img.setImageResource(icons.get(i));

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

        public MyViewHolder(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.icon_image);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               icon.selectedIcon(icons.get(position));
            }
        });
    }
}
