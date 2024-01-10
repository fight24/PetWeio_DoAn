package com.petweio.projectdoan.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.messaging.FirebaseMessaging;
import com.petweio.projectdoan.Introduction.IntroductionActivity;
import com.petweio.projectdoan.Menu.BottomNavActivity;
import com.petweio.projectdoan.Model.TokenResponse;
import com.petweio.projectdoan.MyAppCompatActivity;
import com.petweio.projectdoan.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends MyAppCompatActivity {
    private static final int SPLASH_TIME=2000;
    private static final String TAG = "SplashActivity";
    private String fcmToken;
    Call<TokenResponse> call;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        init();
    }

//    @SuppressLint("SetTextI18n")
//    private void init(){
//        TextView tvAppVersion = findViewById(R.id.tvAppVersion);
//        tvAppVersion.setText(R.string.msg_app_version_1_0);
//
//        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
//
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        String token = task.getResult();
//                        // Do something with the FCM token
//                        Log.i(getString(R.string.DEBUG_TAG), "The result: "+token);
//                        fcmToken = task.getResult();
//                        Log.e(TAG, "Token : "+fcmToken);
//                        call = super.apiService.loginWithToken(fcmToken);
//                        call.enqueue(new Callback<TokenResponse>() {
//                            @Override
//                            public void onResponse(@NonNull Call<TokenResponse> call, @NonNull Response<TokenResponse> response) {
//                                if(response.isSuccessful()){
//                                    TokenResponse tokenResponse = response.body();
//                                    assert tokenResponse != null;
//                                    String username = tokenResponse.getUserName();
//                                    new Handler().postDelayed(() -> {
//                                        Intent intent = new Intent(SplashActivity.this, BottomNavActivity.class);
//                                        Bundle bundle = new Bundle();
//                                        bundle.putString("username", username);
//                                        intent.putExtras(bundle);
//                                        Log.e(TAG, "startActivity");
//                                        startActivity(intent);
//                                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
//                                        finish();
//                                    }, SPLASH_TIME);
//                                }else if (response.code() == 404) {
//                                    // Xử lý khi mã trạng thái là 404
//                                    // Hiển thị thông báo cho người dùng hoặc thực hiện hành động phù hợp.
//                                    new Handler().postDelayed(() -> {
//                                        Intent intent = new Intent(SplashActivity.this, IntroductionActivity.class);
//                                        startActivity(intent);
//                                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
//                                        finish();
//                                    }, SPLASH_TIME);
//                                }
//
//                            }
//
//                            @Override
//                            public void onFailure(@NonNull Call<TokenResponse> call, @NonNull Throwable t) {
//                                Log.e(TAG, "onFailure");
//                                new Handler().postDelayed(() -> {
//                                    Intent intent = new Intent(SplashActivity.this, IntroductionActivity.class);
//                                    startActivity(intent);
//                                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
//                                    finish();
//                                }, SPLASH_TIME);
//                            }
//                        });
//                    } else {
//                        // Handle the error
//                        Log.i(getString(R.string.DEBUG_TAG), "Task Failed");
//
//                    }
//                });
//
//
//
//
//
//
//    }
//private void init() {
//    TextView tvAppVersion = findViewById(R.id.tvAppVersion);
//    tvAppVersion.setText(R.string.msg_app_version_1_0);
//
//
//    ExecutorService executorService = Executors.newSingleThreadExecutor();
//    Handler handler =new Handler(Looper.getMainLooper());
//    executorService.execute(()-> FirebaseMessaging.getInstance().getToken()
//            .addOnCompleteListener(task -> {
//                if (task.isSuccessful() && task.getResult() != null) {
//                    fcmToken = task.getResult();
//                    handler.post(this::handleFCMToken);
//                } else {
//                    // Handle the error, e.g., log or show a message
//                    Log.e(TAG, "Failed to get FCM token: " + task.getException());
//                }
//            }));
//}
    @SuppressLint("SetTextI18n")
    private void init(){
        TextView tvAppVersion = findViewById(R.id.tvAppVersion);
        tvAppVersion.setText(R.string.msg_app_version_1_0);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
        if (task.isSuccessful() && task.getResult() != null) {
            fcmToken = task.getResult();
            handleFCMToken();
        }
                });
    }
    private void handleFCMToken() {
        Log.i(getString(R.string.DEBUG_TAG), "The result: " + fcmToken);
        Log.e(TAG, "Token : " + fcmToken);
        call = super.apiService.loginWithToken(fcmToken);
        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(@NonNull Call<TokenResponse> call, @NonNull Response<TokenResponse> response) {
                if (response.isSuccessful()) {
                    // Xử lý kết quả thành công
                    assert response.body() != null;
                    handleSuccessResponse(response.body());
                } else if (response.code() == 404) {
                    // Xử lý khi mã trạng thái là 404
                    handleNotFoundResponse();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TokenResponse> call, @NonNull Throwable t) {
                // Xử lý khi gặp lỗi
                handleFailure();
            }
        });
    }

    private void handleSuccessResponse(TokenResponse tokenResponse) {
        String username = tokenResponse.getUserName();
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, BottomNavActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            intent.putExtras(bundle);
            Log.e(TAG, "startActivity");
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, SPLASH_TIME);
    }

    private void handleNotFoundResponse() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, IntroductionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, SPLASH_TIME);
    }

    private void handleFailure() {
        Log.e(TAG, "onFailure");
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, IntroductionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, SPLASH_TIME);
    }
}
