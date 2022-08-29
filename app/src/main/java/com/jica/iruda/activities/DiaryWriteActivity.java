package com.jica.iruda.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jica.iruda.R;

public class DiaryWriteActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnBack;
    TextView tvHabitTitle;
    ImageView ivSelectecEmoji;
    RadioGroup rgEmoji;
    ImageButton btnReloadEmoji;
    EditText etDiaryContent;
    Button btnAddImage;
    ImageView ivDiary;
    RadioGroup rgGoalState;
    Button btnSubmitDiary;

    Intent userIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write);

        btnBack = findViewById(R.id.button_back);
        tvHabitTitle = findViewById(R.id.text_habit_title);
        ivSelectecEmoji = findViewById(R.id.image_selected_emoji);
        rgEmoji = findViewById(R.id.rg_emoji);
        btnReloadEmoji = findViewById(R.id.button_reload_emoji);
        etDiaryContent = findViewById(R.id.edit_diary_content);
        btnAddImage = findViewById(R.id.button_add_image);
        ivDiary = findViewById(R.id.image_diary);
        rgGoalState = findViewById(R.id.rg_goal_state);
        btnSubmitDiary = findViewById(R.id.button_submit_diary);

        btnBack.setOnClickListener(this);
        btnReloadEmoji.setOnClickListener(this);
        btnAddImage.setOnClickListener(this);
        btnSubmitDiary.setOnClickListener(this);

        userIntent = getIntent();


    }

    @Override
    public void onClick(View view) {
        int curId = view.getId();

        switch (curId){
            case R.id.button_back:
                onBackPressed();
                break;
            case R.id.button_reload_emoji:
                break;
            case R.id.button_add_image:
                break;
            case R.id.button_submit_diary:
                startActivity(userIntent.setClass(this, HabitDetailActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(userIntent.setClass(this, HabitDetailActivity.class));
        finish();
    }
}