package com.jica.iruda.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.jica.iruda.databinding.ActivityMyPageBinding;
import com.jica.iruda.utilities.Constants;
import com.jica.iruda.utilities.PreferenceManager;

public class MyPageActivity extends AppCompatActivity{

    private PreferenceManager preferenceManager;
    private ActivityMyPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenceManager = new PreferenceManager(getApplicationContext());
        super.onCreate(savedInstanceState);
        binding = ActivityMyPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
    }

    private void init(){
        binding.textName.setText(preferenceManager.getString(Constants.KEY_USER_NAME));
        Glide.with(this).load(preferenceManager.getString(Constants.KEY_USER_IMAGE_URL)).into(binding.imageProfile);
        binding.imageProfile.setClipToOutline(true);
    }

    private void setListeners(){
        binding.buttonBack.setOnClickListener(view -> onBackPressed());
        binding.buttonLevelInfo.setOnClickListener(view -> {});
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToHabitListActivity();
    }

    private void goToHabitListActivity(){
        Intent intent = new Intent(this, HabitListActivity.class);
        startActivity(intent);
        finish();
    }
}