package com.jica.iruda.adapters;

import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jica.iruda.R;
import com.jica.iruda.databinding.DateItemBinding;
import com.jica.iruda.listeners.OnDiaryItemClickListener;
import com.jica.iruda.model.Diary;

import java.util.ArrayList;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> implements OnDiaryItemClickListener {
    private ArrayList<Diary> items;
    private OnDiaryItemClickListener listener;
    private boolean viewFlag = false;

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
        holder.setItem(item, position, isViewFlag());
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
    public void onDiaryClick(ViewHolder viewHolder, View view, int position, DateItemBinding binding) {
        if(listener != null){
            listener.onDiaryClick(viewHolder, view, position, binding);
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
                    listener.onDiaryClick(ViewHolder.this, view, position, binding);
                }
            });
        }

        public void setItem(Diary item, int position, boolean isEmojiView){
            binding.textDate.setText(++position + "");

            if (item != null){
                if (!isEmojiView){
                    // 이모지뷰 껐을 때
                    binding.imageEmoji.setVisibility(View.INVISIBLE);
                    binding.textDate.setVisibility(View.VISIBLE);
                    if (item.getAchievement()){
                        // 목표 달성했을 때
                        binding.textDate.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.white));
                        binding.textDate.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.bg_date_tile_pass));
                    } else {
                        // 목표 달성 못 했을 때
                        binding.textDate.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.white));
                        binding.textDate.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.bg_date_tile_fail));
                    }
                } else {
                    // 이모지뷰 켰을 때
                    TypedArray emojies = binding.getRoot().getResources().obtainTypedArray(R.array.emojies);
                    binding.imageEmoji.setImageDrawable(emojies.getDrawable(item.getEmogiIndex()));
                    binding.textDate.setVisibility(View.INVISIBLE);
                    binding.imageEmoji.setVisibility(View.VISIBLE);
                }
            }
        }


    }

    public boolean isViewFlag() {
        return viewFlag;
    }

    public void setViewFlag(boolean viewFlag) {
        this.viewFlag = viewFlag;
    }

}
