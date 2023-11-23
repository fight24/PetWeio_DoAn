package com.petweio.projectdoan.Sign;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.petweio.projectdoan.Menu.BottomNavActivity;
import com.petweio.projectdoan.Model.ApiResponse;
import com.petweio.projectdoan.Model.Token;
import com.petweio.projectdoan.MyAppCompatActivity;
import com.petweio.projectdoan.Notification.MyApplication;
import com.petweio.projectdoan.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends MyAppCompatActivity {

    private static final String TAG = "[LoginActivity]";
    private EditText edtUser,edtPass;
    private ImageButton btnClose;
    private ImageView frameStackRectangle;
    AppCompatButton btnLogin,btnMoveSignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

    }


    private void init(){
        btnClose = findViewById(R.id.btnClose);
        btnMoveSignup = findViewById(R.id.btnMoveSignup);
        runOnUiThread(()-> btnClose.setOnClickListener(view -> {
            finish();
            overridePendingTransition(0,R.anim.slide_left_out);

        }));

        btnLogin = findViewById(R.id.btnLoginUser);
        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPassword);

        btnMoveSignup.setOnClickListener(v->{
            Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        });
            btnLogin.setOnClickListener(v-> {
                String username = edtUser.getText().toString().trim();
                String password = edtPass.getText().toString().trim();
                if (username.isEmpty() || password.isEmpty()) {

                    // Hiển thị thông báo lỗi nếu username hoặc password trống
                    Toast.makeText(this, "Please enter your complete information", Toast.LENGTH_SHORT).show();
                } else {
                    // Thực hiện đăng nhập hoặc xử lý dữ liệu
                    login(username, password,((MyApplication) getApplication()).getFcmToken());
                }

            });
        frameStackRectangle = findViewById(R.id.frameStackRectangle);
        LinearLayout linearColumn = findViewById(R.id.linearColumn);
        // set focus edit text, password
        setFocus();
        linearColumn.setOnClickListener(view ->{
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtUser.getWindowToken(), 0);
            edtUser.clearFocus(); // Bỏ focus khỏi EditText khi người dùng chạm vào vùng khác
            edtPass.clearFocus(); // Bỏ focus khỏi EditText khi người dùng chạm vào vùng khác
        });


    }
    public void login(String username, String password,String tokenValue) {
        // Gọi yêu cầu đăng nhập bằng Retrofit
        Call<ApiResponse> call = super.apiService.login(new Token(username, password,tokenValue));
        Log.d(TAG, "Login : " + call);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse loginResponse = response.body();
                    assert loginResponse != null;
                    String message = loginResponse.getMessage();
                    // Xử lý đăng nhập thành công
                    Log.d(TAG, message);

                    Intent intent = new Intent(LoginActivity.this, BottomNavActivity.class);
                    Log.e(TAG, username);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", username);
                    intent.putExtras(bundle);
                    Log.e(TAG, "startActivity");
                    startActivity(intent);
                    Log.e(TAG, "startActivity");
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                } else {
                    // Xử lý lỗi đăng nhập (sai tên người dùng/mật khẩu, lỗi server, v.v.)
                    Log.e(TAG, "Login failed");
                    Toast.makeText(LoginActivity.this,"The account or password is wrong. Please check again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                // Xử lý lỗi kết nối hoặc lỗi khác
                Log.e(TAG, "onFailure");
            }
        });

    }
    private void setFocus(){
        edtUser.setOnFocusChangeListener((v, hasFocus) -> toggleImageVisibility(hasFocus));

        edtPass.setOnFocusChangeListener((v, hasFocus) -> toggleImageVisibility(hasFocus));
    }
    private void toggleImageVisibility(boolean hasFocus) {
        runOnUiThread(() -> {
            if (hasFocus) {

                frameStackRectangle.setVisibility(View.GONE); // Ẩn ảnh khi người dùng chạm vào EditText

            } else {
                frameStackRectangle.setVisibility(View.VISIBLE); // Hiển thị ảnh khi EditText không còn focus
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
