package com.jica.iruda.listeners;

import android.view.View;

import com.jica.iruda.adapters.DiaryAdapter;
import com.jica.iruda.databinding.DateItemBinding;

public interface OnDiaryItemClickListener {
    public void onDiaryClick(DiaryAdapter.ViewHolder viewHolder, View view, int position, DateItemBinding binding);
}
