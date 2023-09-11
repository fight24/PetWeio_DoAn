package com.petweio.projectdoan.Sign;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.petweio.projectdoan.Menu.BottomNavActivity;
import com.petweio.projectdoan.MyAppCompatActivity;
import com.petweio.projectdoan.R;


public class LoginActivity extends MyAppCompatActivity {

    private EditText edtUser,edtPass;
    private ImageButton btnClose;
    private FrameLayout frameStackRectangle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }
    private void init(){


        Button btnLogin =  findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v ->{
            Intent intent = new Intent(LoginActivity.this, BottomNavActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            finish();
        });

        btnClose = findViewById(R.id.btnClose);

        runOnUiThread(()-> btnClose.setOnClickListener(view -> {
            finish();
            overridePendingTransition(0,R.anim.slide_left_out);

        }));


        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPassword);

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
}
