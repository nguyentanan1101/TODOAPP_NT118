package com.example.todoapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewAccountActivity extends AppCompatActivity {

    private TextView tvName, tvDOB, tvEmail, tvMobile, tvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ImageButton btnEdit = findViewById(R.id.btnEdit);
        ImageButton btnBack = findViewById(R.id.btnBack);

        tvName = findViewById(R.id.name);
        tvDOB = findViewById(R.id.tvDOB);
        tvEmail = findViewById(R.id.tvEmail);
        tvMobile = findViewById(R.id.tvMobile);
        tvAddress = findViewById(R.id.tvAddress);

        // Load dữ liệu lần đầu
        loadUserData();

        btnBack.setOnClickListener(v -> finish());
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ViewAccountActivity.this, EditAccountActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Khi quay lại màn hình sau khi chỉnh sửa, load lại dữ liệu mới
        loadUserData();
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        tvName.setText(prefs.getString("username", "No name"));
        tvEmail.setText(prefs.getString("email", ""));
        tvMobile.setText(prefs.getString("phone_number", ""));
        tvAddress.setText(prefs.getString("address", ""));

        // Chỉnh hiển thị ngày sinh dd/MM/yyyy
        String rawDate = prefs.getString("birthday", "");
        tvDOB.setText(formatDateForDisplay(rawDate));
    }

    private String formatDateForDisplay(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "";
        try {
            // Nếu dữ liệu từ server kiểu "2025-05-11T00:00:00.000Z"
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = isoFormat.parse(isoDate);
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return displayFormat.format(date);
        } catch (Exception e) {
            // Nếu không parse được, chỉ lấy phần trước "T"
            return isoDate.split("T")[0];
        }
    }
}
