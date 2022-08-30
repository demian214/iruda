package com.jica.iruda.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.jica.iruda.adapters.HabitAdapter;
import com.jica.iruda.databinding.ActivityHabitListBinding;
import com.jica.iruda.listeners.OnHabitItemClickListener;
import com.jica.iruda.model.Habit;
import com.jica.iruda.model.User;
import com.jica.iruda.utilities.Constants;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class HabitListActivity extends AppCompatActivity implements OnHabitItemClickListener {

    private static final String TAG = "HabitListActivity";
    private ActivityHabitListBinding binding;
    private User currentUser;
    private HabitAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHabitListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        init();
    }

    private void setListeners(){
        binding.buttonMypage.setOnClickListener(view -> goToMypageActivity());
        binding.fabAddHabit.setOnClickListener(view -> goToRegisterHabitActivity());
    }

    private void init(){
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra(Constants.USER);

        binding.textUserName.setText(currentUser.getName());

        new DownloadFilesTask().execute(currentUser.getProfileImg());
        binding.imageProfile.setClipToOutline(true);

        // 오늘 날짜 출력
        setUiDate();
        getHabits(FirebaseAuth.getInstance().getUid());
    }

    @Override
    public void onHabitClick(HabitAdapter.ViewHolder viewHolder, View view, int position) {
        Intent intent = new Intent(getApplicationContext(), HabitDetailActivity.class);
        intent.putExtra(Constants.USER, currentUser);
        intent.putExtra(Constants.HABIT, adapter.getItem(position));
        startActivity(intent);
        finish();
    }

    // 이미지 파일 다운로드 테스크
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

    // 오늘 날짜 및 요일 출력
    private void setUiDate(){
        String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM월-dd일 | E요일").withLocale(Locale.KOREA));
        binding.textTodayDate.setText(todayDate);
    }

    private void goToMypageActivity(){
        Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToRegisterHabitActivity(){
        Intent intent = new Intent(getApplicationContext(), RegisterHabitActivity.class);
        intent.putExtra(Constants.USER, currentUser);
        startActivity(intent);
        finish();
    }

    // https://www.geeksforgeeks.org/how-to-read-data-from-firebase-firestore-in-android/ 참고 사이트
    private void getHabits(String uid){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.USERS)
                    .document(uid)
                    .collection(Constants.HABITS)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()){
                                binding.textEmptyMessage.setVisibility(View.GONE);
                                ArrayList<Habit> habits = new ArrayList<>();
                                for (DocumentSnapshot document : querySnapshot.getDocuments()){
                                    habits.add(document.toObject(Habit.class));
                                }
                                // https://www.tutorialspoint.com/how-to-use-comparator-interface-for-listview-in-android
                                Collections.sort(habits, (habit1, habit2) -> habit1.compareTo(habit2));
                                adapter = new HabitAdapter(habits, this);
                                binding.recyclerview.setAdapter(adapter);

//                                adapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    });
    }



}