package com.example.todoapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;
import com.example.todoapp.utils.BottomNavHelper;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class ProfileActivity extends AppCompatActivity {

    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private LinearLayout yourAccount;
    private LinearLayout signOut;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ các view
        yourAccount = findViewById(R.id.your_account);
        signOut = findViewById(R.id.sign_out);
        tvName = findViewById(R.id.tvName);

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

        // Xử lý đăng xuất (Google + Facebook + xóa session)
        signOut.setOnClickListener(v -> {
            // Xóa thông tin user trong SharedPreferences
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

            editor.apply();

            // Google Sign-Out
            gsc.signOut().addOnCompleteListener(task -> {
                // Facebook Sign-Out
                LoginManager.getInstance().logOut();

                Toast.makeText(ProfileActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

                // Quay về LoginActivity
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
        // Khi quay lại màn hình này (ví dụ sau khi chỉnh sửa thông tin), cập nhật lại tên
        loadUserData();
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "Your Full Name");
        tvName.setText(username);
    }
}
