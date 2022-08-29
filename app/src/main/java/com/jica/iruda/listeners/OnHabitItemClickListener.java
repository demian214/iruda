package com.jica.iruda.listeners;

import android.view.View;

import com.jica.iruda.adapters.HabitAdapter;

public interface OnHabitItemClickListener {
    public void onHabitClick(HabitAdapter.ViewHolder viewHolder, View view, int position);
}
