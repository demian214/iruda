package com.jica.iruda.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.jica.iruda.databinding.ActivityMyPageBinding;
import com.jica.iruda.utilities.Constants;
import com.jica.iruda.utilities.PreferenceManager;

public class MyPageActivity extends AppCompatActivity{

    private PreferenceManager preferenceManager;
    private ActivityMyPageBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenceManager = new PreferenceManager(getApplicationContext());
        super.onCreate(savedInstanceState);
        binding = ActivityMyPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
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
        binding.buttonLevelInfo.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(MyPageActivity.this);
            builder.setTitle(binding.textLevel.getText().toString());
            builder.setMessage(binding.textLevel.getText().toString() + "는 어쩌고 저쩌고 ... 좋다");
            builder.setPositiveButton("확인", (dialogInterface, i) -> {
                dialogInterface.cancel();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
        binding.buttonSignOut.setOnClickListener(view -> {
            auth.signOut();
            preferenceManager.clear();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
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