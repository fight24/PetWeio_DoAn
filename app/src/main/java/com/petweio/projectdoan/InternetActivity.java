package com.petweio.projectdoan;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;

public class InternetActivity extends MyAppCompatActivity{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_not_internet);
    }

}
