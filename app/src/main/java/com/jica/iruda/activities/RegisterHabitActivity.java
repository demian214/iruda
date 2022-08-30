package com.jica.iruda.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jica.iruda.R;
import com.jica.iruda.databinding.ActivityRegisterHabitBinding;
import com.jica.iruda.fragments.TimePickerFragment;
import com.jica.iruda.model.Habit;
import com.jica.iruda.model.User;
import com.jica.iruda.utilities.Constants;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class RegisterHabitActivity extends AppCompatActivity implements TimePickerFragment.setAlramTime {

    private ActivityRegisterHabitBinding binding;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private User currentUser;

    private String alramTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterHabitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra(Constants.USER);
        setListeners();
    }

    private void setListeners(){
        binding.buttonBack.setOnClickListener(view -> onBackPressed());
        binding.buttonTimePicker.setOnClickListener(view -> {
            if (binding.switchAlarm.isChecked()){
                FragmentManager fm = getSupportFragmentManager();
                DialogFragment fragment = TimePickerFragment.newInstance();
                fragment.show(fm, "dialog");
            }});
        binding.buttonCreate.setOnClickListener(view -> {
            createHabit();
            goToHabitListActivity();
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
    public void setAlramTime(LocalTime localTime) {
        binding.buttonTimePicker.setText("매일 "+ localTime.toString());
        alramTime = localTime.toString();
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

    private void createHabit(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Habit habit = new Habit(
            binding.editHabitTitle.getText().toString().trim(),
            binding.editHabitContent.getText().toString().trim(),
            LocalDateTime.now().toString(),
            alramTime);


        database.collection(Constants.USERS)
                .document(uid)
                .collection(Constants.HABITS)
                .add(habit)
                .addOnSuccessListener(documentReference -> showToast("습관 생성 성공"))
                .addOnFailureListener(e -> showToast("습관 생성 실패"));

    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}