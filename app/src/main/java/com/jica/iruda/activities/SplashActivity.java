package com.jica.iruda.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.jica.iruda.R;
import com.jica.iruda.databinding.ActivitySplashBinding;
import com.jica.iruda.utilities.PreferenceManager;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    private FirebaseFirestore database;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();
        showAnimation();
        goToLoginActivity();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        if (mAuth.getCurrentUser() != null){
//            if (preferenceManager.getString(Constants.KEY_USER_ID) != null){
//                goToHabitListActivity();
//            }
//        } else {
//            goToLoginActivity();
//        }
//    }

    private void showAnimation(){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_image_logo_fade_in);
        binding.imageLogo.startAnimation(animation);
        new Handler().postDelayed(() -> {
        }, 2000);
    }

    private void goToLoginActivity(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToHabitListActivity(){
        Intent intent = new Intent(getApplicationContext(), HabitListActivity.class);
        startActivity(intent);
        finish();
    }

}