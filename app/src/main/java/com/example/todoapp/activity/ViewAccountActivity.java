package com.example.todoapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide; // Import thư viện Glide
import com.example.todoapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewAccountActivity extends AppCompatActivity {

    private TextView tvName, tvDOB, tvEmail, tvMobile, tvAddress, tvGender;
    private ImageView imgAvatar;

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
        tvGender = findViewById(R.id.tvGender);

        imgAvatar = findViewById(R.id.imgAvatar);

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

        // Load thông tin text
        tvName.setText(prefs.getString("username", "No name"));
        tvEmail.setText(prefs.getString("email", ""));
        tvMobile.setText(prefs.getString("phone_number", ""));
        tvAddress.setText(prefs.getString("address", ""));
        tvGender.setText(prefs.getString("gender", ""));

        String rawDate = prefs.getString("birthday", "");
        tvDOB.setText(formatDateForDisplay(rawDate));

        // --- XỬ LÝ HIỂN THỊ AVATAR ---
        String avatarData = prefs.getString("avatar", "");

        if (!avatarData.isEmpty()) {
            // Trường hợp 1: Dữ liệu là URL (bắt đầu bằng http) -> Dùng Glide
            if (avatarData.startsWith("http")) {
                Glide.with(this)
                        .load(avatarData)
                        .placeholder(R.drawable.ic_user) // Ảnh hiển thị khi đang tải
                        .error(R.drawable.ic_user)       // Ảnh hiển thị khi lỗi
                        .circleCrop()                    // Cắt hình tròn
                        .into(imgAvatar);
            }
            // Trường hợp 2: Dữ liệu là Base64 (Code cũ hoặc dữ liệu chưa sync) -> Decode thủ công
            else {
                try {
                    byte[] decodedString = Base64.decode(avatarData, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imgAvatar.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    e.printStackTrace();
                    imgAvatar.setImageResource(R.drawable.ic_user);
                }
            }
        } else {
            // Không có dữ liệu -> Hiển thị ảnh mặc định
            imgAvatar.setImageResource(R.drawable.ic_user);
        }
    }

    private String formatDateForDisplay(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "";
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = isoFormat.parse(isoDate);
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return displayFormat.format(date);
        } catch (Exception e) {
            return isoDate.split("T")[0];
        }
    }
}