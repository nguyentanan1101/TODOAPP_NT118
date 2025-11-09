package com.example.todoapp.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import com.example.todoapp.R;
import com.example.todoapp.utils.BottomNavHelper;

public class ProfileActivity extends AppCompatActivity {

    LinearLayout yourAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        yourAccount = findViewById(R.id.your_account);

        yourAccount.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ViewAccountActivity.class);
            startActivity(intent);
        });

        BottomNavHelper.setupBottomNav(this);
    }
}

