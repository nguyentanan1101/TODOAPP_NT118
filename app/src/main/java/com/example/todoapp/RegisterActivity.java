package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etAccount;
    private EditText etPassword;
    private EditText etConfirmPassword; // KHAI BÁO MỚI
    private Button btnSignUp;           // ĐỔI TÊN BUTTON
    private TextView tvSignIn;          // ĐỔI TÊN TEXTVIEW

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // ĐỔI TÊN LAYOUT

        // 1. Ánh xạ các View
        etAccount = findViewById(R.id.etAccount);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword); // ÁNH XẠ MỚI
        btnSignUp = findViewById(R.id.btnSignUp);
        tvSignIn = findViewById(R.id.tvSignIn);

        // 2. Xử lý sự kiện cho Button Đăng ký
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp();
            }
        });

        // 3. Xử lý sự kiện Chuyển về Đăng nhập
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang màn hình Đăng nhập
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Đóng màn hình đăng ký
            }
        });
    }

    private void handleSignUp() {
        String account = etAccount.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // 1. Kiểm tra rỗng
        if (account.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Logic kiểm tra Mật khẩu xác nhận
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp.", Toast.LENGTH_LONG).show();
            return;
        }

        // 3. Logic đăng ký (Nếu hợp lệ)
        Toast.makeText(this, "Đăng ký thành công! (Tạm thời)", Toast.LENGTH_SHORT).show();

        // TODO: Chuyển sang màn hình chính hoặc màn hình xác nhận
    }
}
