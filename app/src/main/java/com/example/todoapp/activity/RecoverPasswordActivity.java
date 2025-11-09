package com.example.todoapp.activity;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;

public class RecoverPasswordActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etRecoverAccount;
    private Button btnRecoverPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        // 1. Ánh xạ các View
        ivBack = findViewById(R.id.ivBack);
        etRecoverAccount = findViewById(R.id.etRecoverAccount);
        btnRecoverPassword = findViewById(R.id.btnRecoverPassword);

        // 2. Xử lý sự kiện: Quay về trang Đăng nhập
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay về Activity trước đó (thường là LoginActivity)
                finish();
            }
        });

        // 3. Xử lý sự kiện: Nhận mã OTP (Recover Password)
        btnRecoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRecoverPassword();
            }
        });
    }

    private void handleRecoverPassword() {
        String account = etRecoverAccount.getText().toString().trim();

        if (account.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email hoặc số điện thoại.", Toast.LENGTH_SHORT).show();
            return;
        }

        // *** Mô phỏng (Mock) logic gửi OTP thành công ***
        Toast.makeText(this, "Đang gửi mã OTP đến: " + account + "...", Toast.LENGTH_SHORT).show();


       Intent intent = new Intent(RecoverPasswordActivity.this, OTPVerificationActivity.class);

       startActivity(intent);
       // finish();
    }
}