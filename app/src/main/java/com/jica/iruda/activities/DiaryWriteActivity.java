package com.jica.iruda.activities;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jica.iruda.R;
import com.jica.iruda.adapters.EmojiAdapter;
import com.jica.iruda.databinding.ActivityDiaryWriteBinding;
import com.jica.iruda.listeners.OnEmojiItemClickListener;
import com.jica.iruda.model.Habit;
import com.jica.iruda.utilities.Constants;
import com.jica.iruda.utilities.PreferenceManager;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;

public class DiaryWriteActivity extends AppCompatActivity implements OnEmojiItemClickListener {
    private ActivityDiaryWriteBinding binding;
    private PreferenceManager preferenceManager;
    private Habit habit;
    private EmojiAdapter adapter;
    private int emogiIndex;
    private Boolean state;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiaryWriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        init();
        setListeners();
    }

    private void init(){
        emogiIndex = -1;
        Intent intent = getIntent();
        habit = (Habit) intent.getSerializableExtra(Constants.KEY_HABIT);

        binding.textHabitTitle.setText(habit.getTitle());

        TypedArray emojies = getResources().obtainTypedArray(R.array.emojies);
        ArrayList<Drawable> drawables = new ArrayList<>();
        for(int i=0; i<emojies.length(); i++){
            drawables.add(emojies.getDrawable(i));
        }

        adapter = new EmojiAdapter(drawables, this);
        binding.recyclerviewEmoji.setAdapter(adapter);
    }

    private void setListeners(){
        binding.buttonBack.setOnClickListener(view -> onBackPressed());
        binding.buttonReload.setOnClickListener(view -> {binding.imageSelectedEmoji.setImageDrawable(null);});
        binding.pickImage.setOnClickListener(view -> inputImage());
        binding.buttonSubmitDiary.setOnClickListener(view -> {
            if (isVaildDiaryDetails()){
                submitDiary();
            }
        });
        binding.rgGoalCheck.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == binding.rbSuccess.getId()){
                state = true;
            } else if (i == binding.rbFail.getId()){
                state = false;
            }
        });

    }

    private void inputImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImage.launch(intent);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.pickImage.setImageBitmap(bitmap);
                            binding.pickImage.setClipToOutline(true);
                            binding.linearLayoutAddImage.setVisibility(View.GONE);
                        } catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void submitDiary(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        if (imageUri != null){
            // 이미지가 있는 습관 일지 생성
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imagesRef = storageRef.child("images/" + imageUri.getLastPathSegment());
            UploadTask uploadTask = imagesRef.putFile(imageUri);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return imagesRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    // 습관 일지 객체 생성
                    HashMap<String, Object> diary = new HashMap<>();
                    diary.put(Constants.KEY_DAY, Period.between(habit.getTimestamp().toLocalDate(), LocalDate.now()).getDays());
                    diary.put(Constants.KEY_DIARY_EMOJI_INDEX, emogiIndex);
                    diary.put(Constants.KEY_DIARY_CONTENT, binding.inputContent.getText().toString());
                    diary.put(Constants.KEY_DIARY_IMAGE_URL, downloadUri.toString());
                    diary.put(Constants.KEY_DIARY_ACHIEVEMENT_FLAG, state);
                    diary.put(Constants.KEY_DIARY_TIMESTAMP, LocalDateTime.now().toString());

                    // DB에 저장
                    database.collection(Constants.KEY_COLLECTION_USERS)
                            .document(preferenceManager.getString(Constants.KEY_USER_ID))
                            .collection(Constants.KEY_COLLECTION_HABITS)
                            .document(habit.getId())
                            .collection(Constants.KEY_COLLECTION_DIARIES)
                            .add(diary)
                            .addOnSuccessListener(documentReference -> {
                                Log.d(Constants.TAG, "DiaryWriteActivity>>submitDiary()>>::success(image)");
                                loading(false);
                                goToHabitDetailActivity();
                                showToast("습관 일지 생성 완료");
                            })
                            .addOnFailureListener(e -> Log.d(Constants.TAG, "DiaryWriteActivity>>submitDiary()::failure, Error message:" + e.getMessage()));
                } else {
                    Log.d(Constants.TAG, "DiaryWriteActivity>>submitDiary()>>task.isSuccessful()>>false");
                }
            });
        } else {
            // 이미지가 없는 습관 일지 생성
            // 습관 일지 객체 생성
            HashMap<String, Object> diary = new HashMap<>();
            diary.put(Constants.KEY_DAY, Period.between(habit.getTimestamp().toLocalDate(), LocalDate.now()).getDays());
            diary.put(Constants.KEY_DIARY_EMOJI_INDEX, emogiIndex);
            diary.put(Constants.KEY_DIARY_CONTENT, binding.inputContent.getText().toString());
            diary.put(Constants.KEY_DIARY_IMAGE_URL, null);
            diary.put(Constants.KEY_DIARY_ACHIEVEMENT_FLAG, state);
            diary.put(Constants.KEY_DIARY_TIMESTAMP, LocalDateTime.now().toString());

            // DB에 저장
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .document(preferenceManager.getString(Constants.KEY_USER_ID))
                    .collection(Constants.KEY_COLLECTION_HABITS)
                    .document(habit.getId())
                    .collection(Constants.KEY_COLLECTION_DIARIES)
                    .add(diary)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(Constants.TAG, "DiaryWriteActivity>>submitDiary()>>::success(No image)");
                        loading(false);
                        goToHabitDetailActivity();
                        showToast("습관 일지 생성 완료");
                    })
                    .addOnFailureListener(e -> Log.d(Constants.TAG, "DiaryWriteActivity>>submitDiary()::failure, Error message:" + e.getMessage()));
        }
    }

    private Boolean isVaildDiaryDetails(){
        if (emogiIndex < 0){
            showToast("이모지를 선택해주세요");
            return false;
        } else if (binding.inputContent.getText().toString().trim().isEmpty()){
            showToast("습관 일지를 작성해주세요");
            return false;
        } else if (state == null){
            showToast("달성 여부를 체크해주세요");
            return false;
        } else {
            return true;
        }
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToHabitDetailActivity();
    }

    private void goToHabitDetailActivity(){
        Intent intent = new Intent(this, HabitDetailActivity.class);
        intent.putExtra(Constants.KEY_HABIT, habit);
        startActivity(intent);
        finish();
    }

    @Override
    public void onEmojiClick(EmojiAdapter.ViewHolder viewHolder, View view, int position) {
        binding.imageSelectedEmoji.setImageDrawable(adapter.getItem(position));
        emogiIndex = position;
    }

    private void loading(boolean isLoading){
        if (isLoading){
            binding.buttonSubmitDiary.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSubmitDiary.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}