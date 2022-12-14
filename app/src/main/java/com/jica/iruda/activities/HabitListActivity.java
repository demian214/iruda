package com.jica.iruda.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jica.iruda.AlarmReceiver;
import com.jica.iruda.adapters.HabitAdapter;
import com.jica.iruda.databinding.ActivityHabitListBinding;
import com.jica.iruda.listeners.OnHabitItemClickListener;
import com.jica.iruda.model.Habit;
import com.jica.iruda.utilities.Constants;
import com.jica.iruda.utilities.PreferenceManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;
import java.util.TimeZone;

public class HabitListActivity extends AppCompatActivity implements OnHabitItemClickListener {

    private ActivityHabitListBinding binding;
    private PreferenceManager preferenceManager;
    private HabitAdapter adapter;
    private ArrayList<Habit> habits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHabitListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        init();


    }

    private void init(){
        binding.textTodayDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("MM월-dd일 | E요일").withLocale(Locale.KOREA)));
        binding.textUserName.setText(preferenceManager.getString(Constants.KEY_USER_NAME));
        Glide.with(this).load(preferenceManager.getString(Constants.KEY_USER_IMAGE_URL)).into(binding.imageProfile);
        binding.imageProfile.setClipToOutline(true);
        getHabitsFromDatabase();
    }

    private void setListeners(){
        binding.buttonMypage.setOnClickListener(view -> goToMypageActivity());
        binding.fabAddHabit.setOnClickListener(view -> goToRegisterHabitActivity());
    }

    @Override
    public void onHabitClick(HabitAdapter.ViewHolder viewHolder, View view, int position) {
        Log.d(Constants.TAG, "onHabitClick::habit ID:" + position +", "+ adapter.getItem(position).getContent());
        Intent intent = new Intent(getApplicationContext(), HabitDetailActivity.class);
        intent.putExtra(Constants.KEY_HABIT, adapter.getItem(position));
        startActivity(intent);
        finish();
    }

    private void goToMypageActivity(){
        Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToRegisterHabitActivity(){
        Intent intent = new Intent(getApplicationContext(), RegisterHabitActivity.class);
        startActivity(intent);
        finish();
    }

    // https://www.geeksforgeeks.org/how-to-read-data-from-firebase-firestore-in-android/ 참고 사이트
    private void getHabitsFromDatabase(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .document(preferenceManager.getString(Constants.KEY_USER_ID))
                    .collection(Constants.KEY_COLLECTION_HABITS)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null){
                            Log.d(Constants.TAG, "getHabitsFromDatabase():success");
                            habits = new ArrayList<>();
                                if (!task.getResult().isEmpty()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                                        String title = queryDocumentSnapshot.getString(Constants.KEY_HABIT_TITLE);
                                        String content = queryDocumentSnapshot.getString(Constants.KEY_HABIT_CONTENT);
                                        LocalDateTime timestamp = LocalDateTime.parse(queryDocumentSnapshot.getString(Constants.KEY_HABIT_TIMESTAMP));
                                        LocalTime alarmTime = null;
                                        if (queryDocumentSnapshot.getString(Constants.KEY_HABIT_ALARM_TIME) != null) {
                                            alarmTime = LocalTime.parse(queryDocumentSnapshot.getString(Constants.KEY_HABIT_ALARM_TIME));
                                        }
                                        Log.d(Constants.TAG, "Habit ID:" + queryDocumentSnapshot.getId());
                                        Habit habit = new Habit(queryDocumentSnapshot.getId(), title, content, timestamp, alarmTime);
                                        habits.add(habit);
                                        Log.d(Constants.TAG, "getHabitsFromDatabase():Habit Info:" + queryDocumentSnapshot.getId() + ", " + title + ", " + content + ", " + timestamp + ", " + alarmTime);
                                    }
                                    habits.sort(Comparator.comparing(Habit::getTimestamp));
                                    adapter = new HabitAdapter(habits, this);
                                    loading(false);
                                    binding.textEmptyMessage.setVisibility(View.GONE);
                                    binding.recyclerview.setVisibility(View.VISIBLE);
                                    binding.recyclerview.setAdapter(adapter);
                                }
                            } else {
                                Log.d(Constants.TAG, "No such document");
                            }
                        }
                    );
    }

    private void setAlarm(Habit habit){
        if (habit == null){
            return;
        }

        // Receiver 설정
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(Constants.KEY_HABIT, habit);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 알람 설정
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        calendar.set(Calendar.HOUR_OF_DAY, habit.getAlarmTime().getHour());
        calendar.set(Calendar.MINUTE, habit.getAlarmTime().getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar now = Calendar.getInstance();
        if(calendar.before(now) || now.getTime() == calendar.getTime()){
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent);
    }

    private void loading(Boolean isLoading){
        if(isLoading){
//            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
//            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }



}