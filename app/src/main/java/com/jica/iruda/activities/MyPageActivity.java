package com.jica.iruda.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.jica.iruda.R;
import com.jica.iruda.databinding.ActivityMyPageBinding;
import com.jica.iruda.model.User;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MyPageActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMyPageBinding binding;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonBack.setOnClickListener(this);
        binding.buttonLevelInfo.setOnClickListener(this);

        initView();
    }

    private void initView(){
        intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");
        if (user != null){
            binding.textName.setText(user.getName());

            new DownloadFilesTask().execute(user.getProfileImg());
            Log.d("TAG", user.getProfileImg());
        }
        binding.imageProfile.setClipToOutline(true);
    }

    @Override
    public void onClick(View view) {
        int curId = view.getId();

        switch (curId){
            case R.id.button_back:
                onBackPressed();
                break;
            case R.id.buttonLevelInfo:
                break;
        }
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
        startActivity(intent.setClass(this, HabitListActivity.class));
        finish();
    }
}