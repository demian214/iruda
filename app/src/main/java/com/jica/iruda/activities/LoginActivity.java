package com.jica.iruda.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jica.iruda.R;
import com.jica.iruda.databinding.ActivityLoginBinding;
import com.jica.iruda.utilities.Constants;
import com.jica.iruda.utilities.PreferenceManager;
import com.kakao.sdk.common.KakaoSdk;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private FirebaseFirestore database;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        initGoogleSignInClient();
        database = FirebaseFirestore.getInstance();
        mCallbackManager = CallbackManager.Factory.create();  // 페이스북 CallbackManager 초기화
        KakaoSdk.init(this, getResources().getString(R.string.kakao_app_key)); // 카카오 초기화

        setListeners();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(Constants.TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d(Constants.TAG, "TOKEN:" + token);
                });
    }

    private void setListeners(){
//        binding.buttonNaverLogin.setOnClickListener(view -> signInNaver());
//        binding.buttonFacebookLogin.setOnClickListener(view -> singInFacebook());
        binding.buttonGoogleLogin.setOnClickListener(view -> signInGoogle());
//        binding.buttonKakaoLogin.setOnClickListener(view -> signInKakao());
    }

    private void initGoogleSignInClient(){
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void signInGoogle() {
        Log.d(Constants.TAG, "signInGoogle()");
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Constants.GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(Constants.TAG, "onActivityResult()::requestCode:" + requestCode);
        if (requestCode == Constants.GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(Constants.TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
            }
        } else if(requestCode == Constants.FACEBOOK_SIGN_IN){
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        Log.d(Constants.TAG, "firebaseAuthWithGoogle()");
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(Constants.TAG, "firebaseAuthWithGoogle()::Sign in::sueccess");

                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Log.d(Constants.TAG, "firebaseAuthWithGoogle()::User ID:" + user.getUid());
                        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
                            goToHabitListActivity();
                        } else {
                            database.collection(Constants.KEY_COLLECTION_USERS)
                                    .whereEqualTo(Constants.KEY_USER_ID, user.getUid())
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        Log.d(Constants.TAG, "firebaseAuthWithGoogle()::query::sueccess");
                                        if (queryDocumentSnapshots.getDocuments().size() > 0){
                                            queryDocumentSnapshots.getDocuments().get(0);
                                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                            preferenceManager.putString(Constants.KEY_USER_ID, user.getUid());
                                            preferenceManager.putString(Constants.KEY_USER_NAME, user.getDisplayName());
                                            preferenceManager.putString(Constants.KEY_USER_IMAGE_URL, user.getPhotoUrl().toString());
                                            goToHabitListActivity();
                                        } else {
                                            createNewUserWithFirebase(user);
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.d(Constants.TAG, "firebaseAuthWithGoogle()::query::failure, Error message:" + e.getMessage()));
                        }
                    } else {
                        Log.d(Constants.TAG, "firebaseAuthWithGoogle()::Sign in::sueccess");
                    }
                });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void createNewUserWithFirebase(FirebaseUser firebaseUser){
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_USER_ID, firebaseUser.getUid());
        user.put(Constants.KEY_USER_NAME, firebaseUser.getDisplayName());
        user.put(Constants.KEY_USER_EMAIL, firebaseUser.getEmail());
        user.put(Constants.KEY_USER_IMAGE_URL, firebaseUser.getPhotoUrl());

        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(firebaseUser.getUid())
                .set(user)
                .addOnSuccessListener(unused -> {
                    Log.d(Constants.TAG, "createNewUser()::success");
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, firebaseUser.getUid());
                    preferenceManager.putString(Constants.KEY_USER_NAME, firebaseUser.getDisplayName());
                    preferenceManager.putString(Constants.KEY_USER_IMAGE_URL, firebaseUser.getPhotoUrl().toString());
                    goToHabitListActivity();
                })
                .addOnFailureListener(e -> Log.d(Constants.TAG, "createNewUser()::failure, Error message:" + e.getMessage()));
    }

    public void goToHabitListActivity(){
        Intent intent = new Intent(getApplicationContext(), HabitListActivity.class);
        startActivity(intent);
        finish();
    }

//    private void loading(Boolean isLoading){
//        if(isLoading){
//            binding.buttonGoogleLogin.setVisibility(View.INVISIBLE);
//            binding.progressBar.setVisibility(View.VISIBLE);
//        } else {
//            binding.progressBar.setVisibility(View.INVISIBLE);
//            binding.buttonGoogleLogin.setVisibility(View.VISIBLE);
//        }
//    }

//    private void signInNaver(){
//        NaverIdLoginSDK.INSTANCE.initialize(this,
//                                                getString(R.string.naver_client_id),
//                                                getString(R.string.naver_client_secret),
//                                                getString(R.string.app_name));
//
//        OAuthLoginCallback mOAuthLoginCallback = new OAuthLoginCallback() {
//            @Override
//            public void onSuccess() {
//                NidOAuthLogin nidOAuthLogin = new NidOAuthLogin();
//                nidOAuthLogin.callProfileApi(new NidProfileCallback<NidProfileResponse>() {
//                    @Override
//                    public void onSuccess(NidProfileResponse nidProfileResponse) {
//                        String userName = nidProfileResponse.getProfile().getName();
//                        String userImageUrl = nidProfileResponse.getProfile().getProfileImage();
//                        Log.d(TAG, "name: " + userName + " url: " + userImageUrl);
//
//                        User user = new User();
//                        user.setName(userName);
//                        user.setProfileImg(userImageUrl);
//                        goToHabitListActivity(user);
//                        finish();
//                    }
//
//                    @Override
//                    public void onFailure(int httpStatus, @NonNull String message) {
//                        String errorCode = String.valueOf(httpStatus);
//                        Log.e(TAG, "errorCode: " + errorCode + "\n" + "errorDescription" + message);
//                    }
//
//                    @Override
//                    public void onError(int errorCode, @NonNull String message) {
//                        onFailure(errorCode, message);
//                    }
//                });
//
//            }
//
//            @Override
//            public void onFailure(int httpStatus, @NonNull String message) {
//                String errorCode = NaverIdLoginSDK.INSTANCE.getLastErrorCode().toString();
//                String errorDescription = NaverIdLoginSDK.INSTANCE.getLastErrorDescription();
//                Log.e(TAG, "errorCode: " + errorCode + "\n" + "errorDescription" + errorDescription);
//
//            }
//
//            @Override
//            public void onError(int errorCode, @NonNull String message) {
//                onFailure(errorCode, message);
//            }
//        };
//        NaverIdLoginSDK.INSTANCE.authenticate(this, mOAuthLoginCallback);
//    }
//
//    private void signInKakao(){
//        UserApiClient client = UserApiClient.getInstance();
//        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
//            @Override
//            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
//                if (throwable != null) {
//                    Log.w(TAG, "카카오계정으로 로그인 실패" + throwable);
//                } else if (oAuthToken != null) {
//                    Log.i(TAG, "카카오계정으로 로그인 성공" + oAuthToken.getAccessToken());
//                    client.me((kakaoUser, error) -> {
//                        if (error != null) {
//                            Log.e(TAG, "사용자 정보 요청 실패" + error);
//                        } else if (kakaoUser != null) {
//                            Log.i(TAG, "사용자 정보 요청 성공 " +
//                                    "\n회원정보: " + kakaoUser.getId() +
//                                    "\n이메일: " + kakaoUser.getKakaoAccount().getEmail() +
//                                    "\n이름: " + kakaoUser.getKakaoAccount().getName() +
//                                    "\n닉네임: " + kakaoUser.getKakaoAccount().getProfile().getNickname() +
//                                    "\n프로필사진: " + kakaoUser.getKakaoAccount().getProfile().getProfileImageUrl());
//
//                            User user = new User();
//                            user.setName(kakaoUser.getKakaoAccount().getProfile().getNickname());
//                            user.setProfileImg(kakaoUser.getKakaoAccount().getProfile().getProfileImageUrl());
//                            goToHabitListActivity(user);
//                        }
//                        return null;
//                    });
//
//                }
//                return null;
//            }
//        };
//
//        if(client.isKakaoTalkLoginAvailable(this)){
//            client.loginWithKakaoTalk(getApplicationContext(), callback);
//        }else {
//            client.loginWithKakaoAccount(getApplicationContext(), callback);
//        }
//    }
//
//    private void singInFacebook(){
//        // LoginManager instance 얻어오기
//        LoginManager mLoginManager = LoginManager.getInstance();
//        mLoginManager.logInWithReadPermissions(this, Arrays.asList("email", "public_profile")); // 권한 설정하기(사용자의 어떤 항목을 조회할 수 있는지)
//        mLoginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {   // callback 등록
//            @Override
//            // 파일로 따로 만들어도 되고, 내부 클래스로 구현해도 된다.
//            public void onSuccess(LoginResult loginResult) {                  // 로그인 성공 시
//                Log.d(TAG, "facebook:onSuccess:" + loginResult);
//                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//                Log.d(TAG, "facebook:onCancel");
//            }
//
//            @Override
//            public void onError(@NonNull FacebookException error) {
//                Log.d(TAG, "facebook:onError", error);
//            }
//        });
//    }
//
//    private void handleFacebookAccessToken(AccessToken token) {
//        Log.d(TAG, "handleFacebookAccessToken:" + token);
//
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        firebaseAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            Toast.makeText(LoginActivity.this, "페이스북 로그인 성공", Toast.LENGTH_SHORT).show();
//                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//                            if (firebaseUser != null) {
//                                User user = new User();
//                                user.setName(firebaseUser.getDisplayName());
//                                user.setProfileImg(firebaseUser.getPhotoUrl().toString());
//                                goToHabitListActivity(user);
//                            }
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Toast.makeText(LoginActivity.this, "페이스북 로그인 실패", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

}