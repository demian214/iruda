package com.jica.iruda.activities;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jica.iruda.R;
import com.jica.iruda.adapters.EmojiAdapter;
import com.jica.iruda.databinding.ActivityDiaryWriteBinding;
import com.jica.iruda.listeners.OnEmojiItemClickListener;
import com.jica.iruda.model.Diary;
import com.jica.iruda.model.Habit;
import com.jica.iruda.model.User;
import com.jica.iruda.utilities.Constants;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DiaryWriteActivity extends AppCompatActivity implements OnEmojiItemClickListener {
    private ActivityDiaryWriteBinding binding;
    private User currentUser;
    private Habit currentHabit;
    private EmojiAdapter adapter;
    private String encodedImage;
    private int emogiIndex;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiaryWriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();
        init();
        setListeners();

    }

    private void init(){
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra(Constants.USER);
        currentHabit = (Habit) intent.getSerializableExtra(Constants.HABIT);
        binding.textHabitTitle.setText(currentHabit.getTitle());

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
        binding.inputImage.setOnClickListener(view -> inputImage());
        binding.buttonSubmitDiary.setOnClickListener(view -> submitDiary());
    }

    private void inputImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImage.launch(intent);
    }

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 296;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.inputImage.setImageBitmap(bitmap);
                            binding.inputImage.setClipToOutline(true);
                            binding.linearLayoutAddImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void submitDiary(){
//        int days = Integer.parseInt(Period.between(LocalDateTime.parse(currentHabit.getCreateTime()).toLocalDate(), LocalDate.now()).getDays()+"");
        Diary diary = new Diary(emogiIndex,
                                binding.inputDiaryContent.getText().toString().trim(),
                                encodedImage,
                                getState(),
                                LocalDateTime.now().toString());

        database.collection(Constants.USERS)
                .document(currentUser.getUid())
                .collection(Constants.HABITS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                            if (documentSnapshot.toObject(Habit.class).getCreateTime().equals(currentHabit.getCreateTime())){
                                database.collection(Constants.USERS)
                                        .document(currentUser.getUid())
                                        .collection(Constants.HABITS)
                                        .document(documentSnapshot.getId())
                                        .collection(Constants.DIARIES)
                                        .add(diary)
                                        .addOnSuccessListener(documentReference -> showToast("일기 생성 성공"))
                                        .addOnFailureListener(e -> showToast("일기 생성 실패"));
                                break;
                            }

                        }
                    }
                });

        goToHabitDetailActivity();
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean getState(){
        if (binding.rgGoalCheck.getCheckedRadioButtonId() == binding.rbSuccess.getId()){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToHabitDetailActivity();
    }

    private void goToHabitDetailActivity(){
        Intent intent = new Intent(this, HabitDetailActivity.class);
        intent.putExtra(Constants.USER, currentUser);
        intent.putExtra(Constants.HABIT, currentHabit);
        startActivity(intent);
        finish();
    }

    @Override
    public void onEmojiClick(EmojiAdapter.ViewHolder viewHolder, View view, int position) {
        binding.imageSelectedEmoji.setImageDrawable(adapter.getItem(position));
        emogiIndex = position;
    }
}