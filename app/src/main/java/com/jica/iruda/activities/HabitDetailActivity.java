package com.jica.iruda.activities;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jica.iruda.R;
import com.jica.iruda.adapters.DiaryAdapter;
import com.jica.iruda.databinding.ActivityHabitDetailBinding;
import com.jica.iruda.databinding.DateItemBinding;
import com.jica.iruda.listeners.OnDiaryItemClickListener;
import com.jica.iruda.model.Diary;
import com.jica.iruda.model.Habit;
import com.jica.iruda.utilities.Constants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HabitDetailActivity extends AppCompatActivity implements OnDiaryItemClickListener {

    private ActivityHabitDetailBinding binding;
    private DiaryAdapter adapter;
    private Habit habit;
    private ArrayList<Diary> diaries;

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
        habit = (Habit) intent.getSerializableExtra(Constants.KEY_HABIT);

        binding.textTodayDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("MM월 dd일")));
        binding.textDDay.setText(getDday(habit.getTimestamp().toLocalDate()));
        binding.textCreateDate.setText(habit.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy.MM.dd ~")));
        binding.textTitle.setText(habit.getTitle());
        binding.textContent.setText(habit.getContent());
        if(habit.getAlarmTime() != null){
            binding.textAlarmTime.setText(habit.getAlarmTime().toString());
        } else {
            binding.container.setVisibility(View.GONE);
        }
        diaries = new ArrayList<>();
        for (int i = 0; i < 21; i++){
            diaries.add(null);
        }
        adapter = new DiaryAdapter(diaries, this);
        binding.recyclerViewDate.setAdapter(adapter);
        getDiaryFromDatabase();
    }

    private String getDday(LocalDate creatTime){
        int period = Period.between(creatTime, LocalDate.now()).getDays();
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

    @Override
    public void onDiaryClick(DiaryAdapter.ViewHolder viewHolder, View view, int position, DateItemBinding itemBinding) {
        if (adapter.getItem(position) != null){
            Diary item = adapter.getItem(position);
            binding.diaryContainer.setVisibility(View.VISIBLE);
            if (!adapter.isViewFlag()){
                if (item.getAchievement()){
                    itemBinding.textDate.setBackground(getDrawable(R.drawable.bg_date_tile_pass_clicked));
                    itemBinding.textDate.setTextColor(getColor(R.color.red_peach));
                } else {
                    itemBinding.textDate.setBackground(getDrawable(R.drawable.bg_date_tile_fail_clicked));
                    itemBinding.textDate.setTextColor(getColor(R.color.background_tile_fail));
                }
            }
            binding.textDiaryDay.setText(item.getDay() + 1 + "일차");
            binding.textDiaryCreateDate.setText(item.getTimestamp().format(DateTimeFormatter.ofPattern("y.M.d")));
            TypedArray emojies = binding.getRoot().getResources().obtainTypedArray(R.array.emojies);
            binding.imageEmoji.setImageDrawable(emojies.getDrawable(item.getEmogiIndex()));
            binding.textDiaryContent.setText(item.getContent());
            if (item.getImageUrl() != null){
                binding.imageDiary.setVisibility(View.VISIBLE);
                Glide.with(this).load(item.getImageUrl()).into(binding.imageDiary);
                binding.imageDiary.setClipToOutline(true);
            }
        }
    }

    private void setListeners(){
        binding.buttonBack.setOnClickListener(view -> onBackPressed());
        binding.buttonDeleteHabit.setOnClickListener(view -> deleteHabit());
        binding.buttonShareSns.setOnClickListener(view -> shareSns());
        binding.imageConvertView.setOnClickListener(view -> convertView());
        binding.buttonDiaryWrite.setOnClickListener(view -> goToDiaryWriteActivity());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HabitListActivity.class);
        startActivity(intent);
        finish();
    }

    private void deleteHabit(){

    }

    private void shareSns(){

    }

    private void convertView(){
        if (!adapter.isViewFlag()){
            binding.imageConvertView.setImageResource(R.drawable.ic_convert);
            adapter.setViewFlag(true);
        } else {
            binding.imageConvertView.setImageResource(R.drawable.ic_emoji);
            adapter.setViewFlag(false);
        }
        adapter.notifyDataSetChanged();
    }

    private void goToDiaryWriteActivity(){
        Intent intent = new Intent(this, DiaryWriteActivity.class);
        intent.putExtra(Constants.KEY_HABIT, habit);
        startActivity(intent);
        finish();
    }

    private void getDiaryFromDatabase(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_DIARIES)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null){
                        Log.d(Constants.TAG, "getDiaryFromDatabase():success");
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            int day = queryDocumentSnapshot.getLong(Constants.KEY_DAY).intValue();
                            int emogiIndex = queryDocumentSnapshot.getLong(Constants.KEY_DIARY_EMOJI_INDEX).intValue();
                            String content = queryDocumentSnapshot.getString(Constants.KEY_DIARY_CONTENT);
                            String imageUrl;
                            if (queryDocumentSnapshot.getString(Constants.KEY_DIARY_IMAGE_URL) != null){
                                imageUrl = queryDocumentSnapshot.getString(Constants.KEY_DIARY_IMAGE_URL);
                            } else {
                                imageUrl = null;
                            }
                            boolean achievementFlag = (Boolean) queryDocumentSnapshot.get(Constants.KEY_DIARY_ACHIEVEMENT_FLAG);
                            LocalDateTime timestamp = LocalDateTime.parse(queryDocumentSnapshot.getString(Constants.KEY_DIARY_TIMESTAMP));
                            if (timestamp.toLocalDate().isEqual(LocalDate.now())){
                                binding.buttonDiaryWrite.setClickable(false);
                                binding.buttonDiaryWriteText.setTextColor(getColor(R.color.done_text));
                                binding.buttonDiaryWrite.setBackground(getDrawable(R.drawable.bg_btn_submit_done));
                                binding.buttonDiaryWriteIcon.setColorFilter(getColor(R.color.done_text));
                            }
                            Diary diary = new Diary(day, emogiIndex, content, imageUrl, achievementFlag, timestamp);
                            diaries.set(diary.getDay(), diary);
                        }
                        adapter.setItems(diaries);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d(Constants.TAG, "No such document");
                    }
                });
    }

}

