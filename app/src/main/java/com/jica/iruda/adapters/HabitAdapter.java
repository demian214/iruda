package com.jica.iruda.adapters;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jica.iruda.databinding.HabitItemBinding;
import com.jica.iruda.listeners.OnHabitItemClickListener;
import com.jica.iruda.model.Habit;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.ViewHolder> implements OnHabitItemClickListener {

    private ArrayList<Habit> habits;
    private OnHabitItemClickListener listener;

    public HabitAdapter(ArrayList<Habit> habits, OnHabitItemClickListener listener) {
        this.habits = habits;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HabitAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HabitItemBinding itemBinding = HabitItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding, this);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitAdapter.ViewHolder holder, int position) {
        holder.setItem(habits.get(position));
    }

    @Override
    public void onHabitClick(ViewHolder viewHolder, View view, int position) {
        if(listener != null){
            listener.onHabitClick(viewHolder, view, position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        HabitItemBinding itemBinding;

        public ViewHolder(@NonNull HabitItemBinding itemBinding, final OnHabitItemClickListener listener) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;

            itemBinding.getRoot().setOnClickListener(view -> {
                int position = getAdapterPosition();

                if(itemBinding != null){
                    listener.onHabitClick(ViewHolder.this, view, position);
                }
            });

        }

        public void setItem(Habit item){
            itemBinding.textHabitTitle.setText(item.getTitle());
            itemBinding.textHabitContent.setText(item.getContent());

            if(item.getAlarmTime() != null){
                itemBinding.linearLayout.setVisibility(View.VISIBLE);
                itemBinding.textAlarmTime.setText(item.getAlarmTime().format(DateTimeFormatter.ofPattern("a HH:mm", Locale.US)));
            }

            int period = Period.between(item.getTimestamp().toLocalDate(), LocalDate.now()).getDays();
            itemBinding.progressBar.setProgress(period);

            ObjectAnimator animation = ObjectAnimator.ofInt (itemBinding.progressBar, "progress", 0, period * 1000); // see this max value coming back here, we animale towards that value
            animation.setDuration (2000); //in milliseconds
            animation.setInterpolator (new DecelerateInterpolator());
            animation.start();

            String dDay = "";
            if (period - 21 > 0){
                dDay = "D+" + (period - 21);
            } else if(period - 21 == 0){
                dDay = "D-DAY";
            } else {
                dDay = "D-" + Math.abs(period - 21);
            }
            itemBinding.textDDay.setText(dDay);
        }
    }


    @Override
    public int getItemCount() {
        return habits.size();
    }

    public void addItem(Habit item){
        habits.add(item);
    }

    public void setItems(ArrayList<Habit> items){
        this.habits = items;
    }

    public Habit getItem(int position){
        return habits.get(position);
    }

    public void setItem(int position, Habit item){
        habits.set(position, item);
    }

}
