package com.jica.iruda.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jica.iruda.databinding.ActivityMyPageBinding;
import com.jica.iruda.model.User;
import com.jica.iruda.utilities.Constants;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MyPageActivity extends AppCompatActivity{

    private ActivityMyPageBinding binding;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
    }

    private void init(){
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra(Constants.USER);
        if (currentUser != null){
            binding.textName.setText(currentUser.getName());
            new DownloadFilesTask().execute(currentUser.getProfileImg());
        }
        binding.imageProfile.setClipToOutline(true);
    }

    private void setListeners(){
        binding.buttonBack.setOnClickListener(view -> onBackPressed());
        binding.buttonLevelInfo.setOnClickListener(view -> {});
    }

    private class DownloadFilesTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bmp = null;
            try {
                String img_url = strings[0]; //url of the image
                URL url = new URL(img_url);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // doInBackground 에서 받아온 total 값 사용 장소
            binding.imageProfile.setImageBitmap(result);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToHabitListActivity();
    }

    private void goToHabitListActivity(){
        Intent intent = new Intent(this, HabitListActivity.class);
        intent.putExtra(Constants.USER, currentUser);
        startActivity(intent);
        finish();
    }
}