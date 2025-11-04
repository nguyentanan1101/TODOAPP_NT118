package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etAccount;
    private EditText etPassword;
    private Button btnSignIn;
    private TextView tvPasswordError;
    private TextView tvForgotPassword;
    private TextView tvSignUp;

    // Mật khẩu giả định để kiểm tra lỗi
    private static final String CORRECT_PASSWORD = "123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Ánh xạ các View
        etAccount = findViewById(R.id.etAccount);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);

        // 2. Xử lý sự kiện cho Button Đăng nhập
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignIn();
            }
        });

        // 3. Xử lý sự kiện Quên mật khẩu
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RecoverPasswordActivity.class);
                startActivity(intent);
            }
        });

        // 4. Xử lý sự kiện Đăng ký tài khoản
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handleSignIn() {
        String account = etAccount.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Kiểm tra rỗng
        if (account.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ Tài khoản và Mật khẩu", Toast.LENGTH_SHORT).show();
            tvPasswordError.setVisibility(View.GONE);
            return;
        }

        // Logic kiểm tra Mật khẩu (Chức năng: Hiển thị lỗi)
        if (password.equals(CORRECT_PASSWORD)) {
            // Mật khẩu đúng
            tvPasswordError.setVisibility(View.GONE);
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

            // TODO: Chuyển sang màn hình chính
        } else {
            // Mật khẩu sai (Hiển thị lỗi theo yêu cầu)
            tvPasswordError.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Đăng nhập thất bại. Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
        }
    }
}