package com.jica.iruda.activities;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.jica.iruda.R;
import com.jica.iruda.databinding.ActivityRegisterHabitBinding;
import com.jica.iruda.fragments.TimePickerFragment;
import com.jica.iruda.utilities.Constants;
import com.jica.iruda.utilities.PreferenceManager;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

public class RegisterHabitActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private ActivityRegisterHabitBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private LocalTime alarmTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterHabitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        database = FirebaseFirestore.getInstance();
        alarmTime = null;
        setListeners();
    }

    private void setListeners(){
        binding.buttonBack.setOnClickListener(view -> onBackPressed());
        binding.buttonTimePicker.setOnClickListener(view -> {
            if (binding.switchAlarm.isChecked()){
                DialogFragment dialogFragment = new TimePickerFragment();
                dialogFragment.show(getSupportFragmentManager(), "timePicker");
            }});
        binding.buttonCreate.setOnClickListener(view -> {
            if (isValidCreateHabitDetails()){
                createHabit();
            }
        });

        binding.switchAlarm.setOnCheckedChangeListener((compoundButton, b) -> {
            if(binding.switchAlarm.isChecked()){
                binding.textAlarm.setTextColor(getColor(R.color.abled_text));
                binding.buttonTimePicker.setTextColor(getColor(R.color.abled_text));
                binding.buttonTimePicker.setCompoundDrawablesWithIntrinsicBounds(null,null, getDrawable(R.drawable.ic_arrow_right_on), null);
                binding.buttonTimePicker.setText(R.string.action_select);
            }else {
                binding.textAlarm.setTextColor(getColor(R.color.disabled_text));
                binding.buttonTimePicker.setTextColor(getColor(R.color.disabled_text));
                binding.buttonTimePicker.setCompoundDrawablesWithIntrinsicBounds(null,null, getDrawable(R.drawable.ic_arrow_right_off), null);
                binding.buttonTimePicker.setText(R.string.action_nothing);
            }
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

    private void createHabit(){
        HashMap<String, Object> habit = new HashMap<>();
        habit.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        habit.put(Constants.KEY_HABIT_TITLE, binding.editHabitTitle.getText().toString());
        habit.put(Constants.KEY_HABIT_CONTENT, binding.editHabitContent.getText().toString());
        String alarmTimeStr = null;
        if (alarmTime != null){
            alarmTimeStr = alarmTime.toString();
        }
        habit.put(Constants.KEY_HABIT_ALARM_TIME, alarmTimeStr);
        habit.put(Constants.KEY_HABIT_TIMESTAMP, LocalDateTime.now().toString());

        database.collection(Constants.KEY_COLLECTION_HABITS)
                .add(habit)
                .addOnSuccessListener(documentReference -> {
                    Log.d(Constants.TAG, "createHabit()::success");
                    showToast("습관 생성 완료");
                    goToHabitListActivity();
                })
                .addOnFailureListener(e -> Log.d(Constants.TAG, "createHabit()::failure, Error message:" + e.getMessage()));
    }

    private Boolean isValidCreateHabitDetails(){
        if (binding.editHabitTitle.getText().toString().trim().isEmpty()){
            showToast("습관 이름을 입력하세요");
            return false;
        } else if (binding.editHabitContent.getText().toString().isEmpty()){
            showToast("습관 메모를 입력하세요");
            return false;
        } else {
            return true;
        }
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.d(Constants.TAG, "RegisterHabitActivity::onTimeSet(): " + hourOfDay + ":" + minute + ":00");
        alarmTime = LocalTime.of(hourOfDay,minute);
        binding.buttonTimePicker.setText(getString(R.string.everyday) + " " + alarmTime.toString());
        Log.d(Constants.TAG, "LocalTime:" + alarmTime.toString());
    }
}