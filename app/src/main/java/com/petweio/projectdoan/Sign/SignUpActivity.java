package com.petweio.projectdoan.Sign;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.petweio.projectdoan.Model.ApiResponse;
import com.petweio.projectdoan.Model.User;
import com.petweio.projectdoan.MyAppCompatActivity;
import com.petweio.projectdoan.R;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends MyAppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private EditText edtUser,edtPass,edtEmail,edtRePass;
    private ImageButton btnClose;
    private FrameLayout frameStackRectangle;
    AppCompatButton btnSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
    }
    private void init(){

        btnClose = findViewById(R.id.btnCloseSignUp);

        runOnUiThread(()-> btnClose.setOnClickListener(view -> {
            finish();
            overridePendingTransition(0,R.anim.slide_right_out);

        }));


        edtUser = findViewById(R.id.edtUserSignUp);
        edtPass = findViewById(R.id.edtPasswordSignUp);
        edtRePass = findViewById(R.id.edtRePasswordSignUp);
        edtEmail = findViewById(R.id.edtEmailSignUp);
        btnSignup = findViewById(R.id.btnSignup);
        frameStackRectangle = findViewById(R.id.frameIllustrationSignUp);
        LinearLayout linearColumn = findViewById(R.id.linearColumnSignUp);

        btnSignup.setOnClickListener(v->{
            Log.d(TAG, "Click ok");
            String email = edtEmail.getText().toString();
            String username = edtUser.getText().toString();
            String password = edtPass.getText().toString();
            String rePassword = edtRePass.getText().toString();
            if(email.isEmpty() || password.isEmpty() || rePassword.isEmpty() || username.isEmpty() ){
                Toast.makeText(this,"Please enter your complete information",Toast.LENGTH_SHORT).show();
            }else{
                if(password.equals(rePassword)){
                    signUp(email,username,password);
                }else{
                    Toast.makeText(this,"Passwords are not the same. Please check again !!!",Toast.LENGTH_SHORT).show();
                }

            }
        });
        // set focus edit text, password
        setFocus();
        linearColumn.setOnClickListener(view ->{
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtUser.getWindowToken(), 0);
            edtUser.clearFocus(); // Bỏ focus khỏi EditText khi người dùng chạm vào vùng khác
            edtPass.clearFocus(); // Bỏ focus khỏi EditText khi người dùng chạm vào vùng khác
            edtEmail.clearFocus();
            edtRePass.clearFocus();
        });


    }
    private void signUp(String email,String username,String password){
        Call<ApiResponse> call = super.apiService.signUp(new User(email,username,password));
        Log.d(TAG,"signUp: " + call.toString());
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse signUpResponse = response.body();
                    assert signUpResponse != null;
                    String message = signUpResponse.getMessage();
                    // Xử lý đăng nhập thành công
                    Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, message);
                    new Handler().postDelayed((() ->{
                        finish();
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    }),2000);
            }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, Objects.requireNonNull(t.getMessage()));
                Toast.makeText(SignUpActivity.this,"The user account may already exist. Please check again", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setFocus(){
        edtUser.setOnFocusChangeListener((v, hasFocus) -> toggleImageVisibility(hasFocus));

        edtPass.setOnFocusChangeListener((v, hasFocus) -> toggleImageVisibility(hasFocus));
        edtRePass.setOnFocusChangeListener((v, hasFocus) -> toggleImageVisibility(hasFocus));
        edtEmail.setOnFocusChangeListener((v, hasFocus) -> toggleImageVisibility(hasFocus));
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
}
