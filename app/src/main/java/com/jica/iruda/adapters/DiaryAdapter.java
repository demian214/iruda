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
    private static Boolean isEmojiView = false;

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
        holder.setItem(item, position);
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

        public DateItemBinding binding;

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

        public void setItem(Diary item, int position){
            binding.textDate.setVisibility(View.VISIBLE);
            binding.textDate.setText(++position + "");

            if (item != null){

                if (isEmojiView){
                    if (item.getContent() != null){
                        binding.textDate.setVisibility(View.INVISIBLE);
                        binding.imageEmoji.setVisibility(View.VISIBLE);
                        TypedArray emojies = binding.getRoot().getResources().obtainTypedArray(R.array.emojies);
                        binding.imageEmoji.setImageDrawable(emojies.getDrawable(item.getEmogiIndex()));
                    } else {
                        // 목표 달성 못 했을 때(아무것도 작성 안한 날)
                        binding.textDate.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.white));
                        binding.textDate.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.bg_date_tile_fail));
                    }
                } else {
                    binding.textDate.setVisibility(View.VISIBLE);
                    binding.imageEmoji.setVisibility(View.INVISIBLE);
                    if (item.getAchievement()){
                        // 목표 달성했을 때
                        binding.textDate.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.white));
                        binding.textDate.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.bg_date_tile_pass));
                    } else {
                        // 목표 달성 못 했을 때
                        binding.textDate.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.white));
                        binding.textDate.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.bg_date_tile_fail));
                    }
                }

            }
        }

        public void setItemClicked(Boolean isAchieved){
            if (isAchieved){
                binding.textDate.setBackground(binding.getRoot().getResources().getDrawable(R.drawable.bg_date_tile_pass_clicked));
                binding.textDate.setTextColor(binding.getRoot().getResources().getColor(R.color.red_peach));
            } else {
                binding.textDate.setBackground(binding.getRoot().getResources().getDrawable(R.drawable.bg_date_tile_fail_clicked));
                binding.textDate.setTextColor(binding.getRoot().getResources().getColor(R.color.background_tile_fail));
            }
        }

        public void setItemInit(Diary item){
            if (item.getAchievement()){
                // 목표 달성했을 때
                binding.textDate.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.white));
                binding.textDate.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.bg_date_tile_pass));
            } else {
                // 목표 달성 못 했을 때
                binding.textDate.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.white));
                binding.textDate.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.bg_date_tile_fail));
            }
        }

    }

    public void setEmojiView(Boolean emojiView) {
        isEmojiView = emojiView;
    }
}
