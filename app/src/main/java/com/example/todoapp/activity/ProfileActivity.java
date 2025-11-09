package com.example.todoapp.activity;

import android.content.Intent;
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

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView name, email;
    LinearLayout yourAccount;
    LinearLayout signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ các view
        yourAccount = findViewById(R.id.your_account);
        signOut = findViewById(R.id.sign_out);
        name = findViewById(R.id.tvName);
        email = findViewById(R.id.tvPhone); // nếu muốn hiện email, thay id tương ứng

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

        // Xử lý đăng xuất (Google + Facebook)
        signOut.setOnClickListener(v -> {
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
}
