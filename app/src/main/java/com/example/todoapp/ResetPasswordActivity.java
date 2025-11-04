package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnContinue;
    private TextView tvPasswordError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // 1. Ánh xạ các View
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnContinue = findViewById(R.id.btnContinue);
        tvPasswordError = findViewById(R.id.tvPasswordError);

        // 2. Xử lý sự kiện cho Button Continue
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleContinue();
            }
        });
    }

    private void handleContinue() {
        String newPass = etNewPassword.getText().toString();
        String confirmPass = etConfirmPassword.getText().toString();

        tvPasswordError.setVisibility(View.GONE); // Ẩn lỗi cũ trước

        // 1. Kiểm tra rỗng và độ dài tối thiểu (tùy chọn)
        if (newPass.isEmpty() || confirmPass.isEmpty() || newPass.length() < 6) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu hợp lệ (ít nhất 6 ký tự)", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Kiểm tra Mật khẩu xác nhận
        if (!newPass.equals(confirmPass)) {
            // Mật khẩu không khớp -> Hiển thị lỗi đỏ
            tvPasswordError.setVisibility(View.VISIBLE);
            return;
        }

        // 3. Logic Đặt lại Mật khẩu thành công

        // TODO: Gọi API để lưu mật khẩu mới vào cơ sở dữ liệu

        Toast.makeText(this, "Updated new password. Please sign in", Toast.LENGTH_LONG).show();


        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);

        // Cờ CLEAR_TOP để xóa tất cả các màn hình Quên Mật Khẩu/OTP khỏi stack
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish(); // Đóng Activity hiện tại
    }
}
