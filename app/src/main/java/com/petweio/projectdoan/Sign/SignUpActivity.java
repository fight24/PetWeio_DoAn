package com.petweio.projectdoan.Sign;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.petweio.projectdoan.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtUser,edtPass,edtEmail;
    private ImageButton btnClose;
    private FrameLayout frameStackRectangle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
    }
    private void init(){
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.green_50));

        btnClose = findViewById(R.id.btnCloseSignUp);

        runOnUiThread(()-> btnClose.setOnClickListener(view -> {
            finish();
            overridePendingTransition(0,R.anim.slide_right_out);

        }));


        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        frameStackRectangle = findViewById(R.id.frameStackbackground);
        LinearLayout linearColumn = findViewById(R.id.linearColumn);


        // set focus edit text, password
        setFocus();
        linearColumn.setOnClickListener(view ->{
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtUser.getWindowToken(), 0);
            edtUser.clearFocus(); // Bỏ focus khỏi EditText khi người dùng chạm vào vùng khác
            edtPass.clearFocus(); // Bỏ focus khỏi EditText khi người dùng chạm vào vùng khác
            edtEmail.clearFocus();
        });


    }
    private void setFocus(){
        edtUser.setOnFocusChangeListener((v, hasFocus) -> toggleImageVisibility(hasFocus));

        edtPass.setOnFocusChangeListener((v, hasFocus) -> toggleImageVisibility(hasFocus));
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
