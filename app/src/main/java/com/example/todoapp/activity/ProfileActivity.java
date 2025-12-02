package com.example.todoapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64; // Import Base64
import android.widget.ImageView; // Import ImageView
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide; // Import Glide
import com.example.todoapp.R;
import com.example.todoapp.utils.BottomNavHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class ProfileActivity extends AppCompatActivity {

    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private LinearLayout yourAccount;
    private LinearLayout signOut;
    private TextView tvName;
    private ImageView imgAvatar; // 1. Khai báo ImageView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ các view
        yourAccount = findViewById(R.id.your_account);
        signOut = findViewById(R.id.sign_out);
        tvName = findViewById(R.id.tvName);

        // 2. Ánh xạ Avatar (Đảm bảo trong activity_profile.xml có ImageView với id là imgAvatar)
        imgAvatar = findViewById(R.id.imgAvatar);

        // Load dữ liệu người dùng
        loadUserData();

        // Google Sign-In setup
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        // Chuyển sang màn ViewAccount
        yourAccount.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ViewAccountActivity.class);
            startActivity(intent);
        });

        // Xử lý đăng xuất
        signOut.setOnClickListener(v -> {
            SharedPreferences sp = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("accessToken");
            editor.remove("refreshToken");
            editor.remove("user_id");
            editor.remove("username");
            editor.remove("email");
            editor.remove("phone_number");
            editor.remove("address");
            editor.remove("birthday");
            editor.remove("avatar");
            editor.remove("gender");

            editor.apply();

            gsc.signOut().addOnCompleteListener(task -> {
                Toast.makeText(ProfileActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        });

        // Cấu hình Bottom Navigation
        BottomNavHelper.setupBottomNav(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Khi quay lại màn hình này, cập nhật lại tên và avatar
        loadUserData();
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "Your Full Name");
        tvName.setText(username);

        // 3. Xử lý hiển thị Avatar (URL hoặc Base64)
        String avatarData = prefs.getString("avatar", "");

        if (!avatarData.isEmpty()) {
            if (avatarData.startsWith("http")) {
                // Dùng Glide load URL
                if (imgAvatar != null) {
                    Glide.with(this)
                            .load(avatarData)
                            .placeholder(R.drawable.ic_user)
                            .error(R.drawable.ic_user)
                            .circleCrop()
                            .into(imgAvatar);
                }
            } else {
                // Dùng Base64 (Fallback)
                try {
                    byte[] decodedString = Base64.decode(avatarData, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    if (imgAvatar != null) {
                        imgAvatar.setImageBitmap(decodedByte);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (imgAvatar != null) imgAvatar.setImageResource(R.drawable.ic_user);
                }
            }
        } else {
            if (imgAvatar != null) imgAvatar.setImageResource(R.drawable.ic_user);
        }
    }
}