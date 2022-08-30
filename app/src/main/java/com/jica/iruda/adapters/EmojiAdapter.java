package com.jica.iruda.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jica.iruda.databinding.EmojiItemBinding;
import com.jica.iruda.listeners.OnEmojiItemClickListener;

import java.util.ArrayList;

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.ViewHolder> implements OnEmojiItemClickListener {

    private ArrayList<Drawable> emojies;
    private OnEmojiItemClickListener listener;

    public EmojiAdapter(ArrayList<Drawable> emojies, OnEmojiItemClickListener listener) {
        this.emojies = emojies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EmojiItemBinding binding = EmojiItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding, this);
    }

    @Override
    public void onBindViewHolder(@NonNull EmojiAdapter.ViewHolder holder, int position) {
        holder.setItem(emojies.get(position));
    }

    @Override
    public int getItemCount() {
        return emojies.size();
    }

    @Override
    public void onEmojiClick(ViewHolder viewHolder, View view, int position) {
        if(listener != null){
            listener.onEmojiClick(viewHolder, view, position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        EmojiItemBinding binding;

        public ViewHolder(@NonNull EmojiItemBinding binding, final OnEmojiItemClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(view -> {
                int position = getAdapterPosition();

                if(binding != null){
                    listener.onEmojiClick(ViewHolder.this, view, position);
                }
            });
        }

        public void setItem(Drawable drawable){
            binding.image.setImageDrawable(drawable);
        }


    }

    public Drawable getItem(int position){
        return emojies.get(position);
    }

}
