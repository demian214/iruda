package com.jica.iruda.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jica.iruda.databinding.DateItemBinding;
import com.jica.iruda.listeners.OnDiaryItemClickListener;
import com.jica.iruda.model.Diary;

import java.util.ArrayList;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> implements OnDiaryItemClickListener {
    private ArrayList<Diary> items;
    private OnDiaryItemClickListener listener;

    public DiaryAdapter(ArrayList<Diary> items, OnDiaryItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DiaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DateItemBinding binding = DateItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Diary item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Diary item){
        items.add(item);
    }

    public void setItems(ArrayList<Diary> items){
        this.items = items;
    }

    public Diary getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, Diary item){
        items.set(position, item);
    }

    @Override
    public void onDiaryClick(ViewHolder viewHolder, View view, int position) {
        if(listener != null){
            listener.onDiaryClick(viewHolder, view, position);
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        DateItemBinding binding;

        public ViewHolder(@NonNull DateItemBinding binding, final OnDiaryItemClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(view -> {
                int position = getAdapterPosition();

                if(listener != null){
                    listener.onDiaryClick(ViewHolder.this, view, position);
                }
            });
        }

        public void setItem(Diary item){
            binding.textDiaryDate.setText(item.getCreateTime()+"");
        }
    }
}
