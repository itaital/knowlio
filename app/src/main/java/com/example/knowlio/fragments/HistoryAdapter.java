package com.example.knowlio.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knowlio.R;
import com.example.knowlio.data.db.DailyFactEntity;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Holder> {
    private final List<DailyFactEntity> data = new ArrayList<>();
    private String lang = "en";

    public void setData(List<DailyFactEntity> items, String lang) {
        data.clear();
        if (items != null) {
            data.addAll(items);
        }
        this.lang = lang == null ? "en" : lang;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        DailyFactEntity item = data.get(position);
        holder.tv1.setText(item.date);
        holder.tv2.setText("he".equals(lang) ? item.he : item.en);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView tv1;
        TextView tv2;
        Holder(View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(android.R.id.text1);
            tv2 = itemView.findViewById(android.R.id.text2);
        }
    }
}
