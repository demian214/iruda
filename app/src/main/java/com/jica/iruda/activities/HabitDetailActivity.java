package com.jica.iruda.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.jica.iruda.adapters.DiaryAdapter;
import com.jica.iruda.databinding.ActivityHabitDetailBinding;
import com.jica.iruda.listeners.OnDiaryItemClickListener;
import com.jica.iruda.model.Diary;
import com.jica.iruda.model.Habit;
import com.jica.iruda.model.User;
import com.jica.iruda.utilities.Constants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HabitDetailActivity extends AppCompatActivity implements OnDiaryItemClickListener{
    private static final String TAG = "HabitDetailActivity";

    private ActivityHabitDetailBinding binding;
    private User currentUser;
    private Habit currentHabit;
    private DiaryAdapter adapter;
    private ArrayList<Diary> dates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHabitDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();
    }

    private void init(){
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra(Constants.USER);
        currentHabit = (Habit) intent.getSerializableExtra(Constants.HABIT);

        binding.textTodayDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("MM월 dd일")));
        binding.textDDay.setText(getDday(LocalDateTime.parse(currentHabit.getCreateTime()).toLocalDate()));
        binding.textCreateDate.setText(LocalDateTime.parse(currentHabit.getCreateTime()).format(DateTimeFormatter.ofPattern("yyyy.MM.dd ~")));
        binding.textTitle.setText(currentHabit.getTitle());
        binding.textContent.setText(currentHabit.getContent());
        if(currentHabit.getAlramTime() != null){
            binding.textAlramTime.setText(currentHabit.getAlramTime());
        } else {
            binding.container.setVisibility(View.GONE);
        }

        dates = new ArrayList<>();
        for (int i=0; i<21; i++){
            dates.add(i, new Diary());
        }
        adapter = new DiaryAdapter(dates, this);
        binding.recyclerViewDate.setAdapter(adapter);
    }

    private String getDday(LocalDate creatTime){
        int period = Integer.parseInt(Period.between(creatTime, LocalDate.now()).getDays() + "");
        String dDay = "";
        if (period - 21 > 0){
            dDay = "D + " + (period - 21);
        } else if(period - 21 == 0){
            dDay = "D - DAY";
        } else {
            dDay = "D - " + Math.abs(period - 21);
        }
        return dDay;
    }

    private void getDiaries(){
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        database.collection(Constants.USERS)
//                .document(currentUser.getUid())
//                .collection(Constants.HABITS)
//                .document()
    }

    @Override
    public void onDiaryClick(DiaryAdapter.ViewHolder viewHolder, View view, int position) {
        if (adapter.getItem(position) != null){
            Diary item = adapter.getItem(position);
        }
    }

    private void setListeners(){
        binding.buttonBack.setOnClickListener(view -> onBackPressed());
        binding.buttonDeleteHabit.setOnClickListener(view -> deleteHabit());
        binding.buttonShareSns.setOnClickListener(view -> shareSns());
        binding.buttonConvertView.setOnClickListener(view -> convertView());
        binding.buttonDiaryWrite.setOnClickListener(view -> goToDiaryWriteActivity());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HabitListActivity.class);
        intent.putExtra(Constants.USER, currentUser);
        startActivity(intent);
        finish();
    }

    private void deleteHabit(){

    }

    private void shareSns(){

    }

    private void convertView(){

    }

    private void goToDiaryWriteActivity(){
        Intent intent = new Intent(this, DiaryWriteActivity.class);
        intent.putExtra(Constants.USER, currentUser);
        intent.putExtra(Constants.HABIT, currentHabit);
        startActivity(intent);
        finish();
    }

}

