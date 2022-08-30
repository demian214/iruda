package com.jica.iruda.listeners;

import android.view.View;

import com.jica.iruda.adapters.EmojiAdapter;

public interface OnEmojiItemClickListener {
    public void onEmojiClick(EmojiAdapter.ViewHolder viewHolder, View view, int position);
}
