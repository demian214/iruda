package com.jica.iruda.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jica.iruda.R;
import com.jica.iruda.databinding.ActivitySplashBinding;
import com.jica.iruda.model.User;
import com.jica.iruda.utilities.Constants;

import java.io.Serializable;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();
        showAnimation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            FirebaseUser currentUser = mAuth.getCurrentUser();
            database.collection(Constants.USERS)
                    .document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        User user = documentSnapshot.toObject(User.class);
                        goToHabitListActivity(user);
                    });
        } else {
            goToLoginActivity();
        }
    }

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

    public void goToHabitListActivity(User user){
        Intent intent = new Intent(getApplicationContext(), HabitListActivity.class);
        intent.putExtra(Constants.USER, (Serializable) user);
        startActivity(intent);
        finish();
    }

}