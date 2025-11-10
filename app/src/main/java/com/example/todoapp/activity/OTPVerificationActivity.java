package com.example.todoapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Thư viện cần thiết cho việc gạch chân (nếu dùng phương án Java)
import android.graphics.Paint;

import com.example.todoapp.R;

public class OTPVerificationActivity extends AppCompatActivity {

    private EditText etOtp1, etOtp2, etOtp3, etOtp4;
    private TextView tvSendAgain;
    private String recoveredEmail; // Lưu trữ email nhận OTP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        // Lấy email được gửi từ RecoverPasswordActivity
        recoveredEmail = getIntent().getStringExtra("account_email");

        // 1. Ánh xạ các View
        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);
        tvSendAgain = findViewById(R.id.tvSendAgain);

        // 2. Thiết lập Tự động chuyển focus và Tự động xác nhận
        setupOtpInputListeners();

        // 3. Thiết lập gạch chân cho "Send again" (Nếu không dùng HTML/String resource)
        tvSendAgain.setPaintFlags(tvSendAgain.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // 4. Xử lý sự kiện Gửi lại mã OTP
        tvSendAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSendAgain();
            }
        });
    }

    private void setupOtpInputListeners() {
        // Mảng các EditText để dễ quản lý
        final EditText[] otps = {etOtp1, etOtp2, etOtp3, etOtp4};

        for (int i = 0; i < otps.length; i++) {
            final int index = i;

            otps[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1) {
                        // Tự động chuyển focus sang ô tiếp theo
                        if (index < otps.length - 1) {
                            otps[index + 1].requestFocus();
                        } else {
                            // Nếu là ô cuối cùng, tự động ẩn bàn phím và xác nhận
                            otps[index].clearFocus();
                            verifyCode(); // Tự động gọi hàm xác nhận
                        }
                    } else if (s.length() == 0 && before == 1) {
                        // Xử lý khi người dùng nhấn backspace để xóa
                        if (index > 0) {
                            otps[index - 1].requestFocus();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void verifyCode() {
        // Lấy mã OTP đã nhập
        String code = etOtp1.getText().toString() + etOtp2.getText().toString() +
                etOtp3.getText().toString() + etOtp4.getText().toString();

        if (code.length() < 4) {
            // Toast.makeText(this, "Mã chưa đủ 4 số.", Toast.LENGTH_SHORT).show();
            return; // Chưa đủ 4 số, không làm gì
        }



        if (code.equals("1234")) { // Giả định mã đúng là 1234 (THỰC TẾ LÀ GỌI API)
            Toast.makeText(this, "Xác nhận thành công! Chuyển đến trang Đổi Mật khẩu.", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(OTPVerificationActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
            // finish();
        } else {
            Toast.makeText(this, "Mã OTP không hợp lệ.", Toast.LENGTH_SHORT).show();
            // Xóa nội dung để người dùng nhập lại
            etOtp1.setText(""); etOtp2.setText("");
            etOtp3.setText(""); etOtp4.setText("");
            etOtp1.requestFocus(); // Focus lại ô đầu tiên
        }
    }

    private void handleSendAgain() {
        if (recoveredEmail != null) {
            Toast.makeText(this, "Đang gửi lại mã OTP đến " + recoveredEmail, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Không tìm thấy địa chỉ email đã nhập.", Toast.LENGTH_LONG).show();
        }
        // TODO: Gửi yêu cầu API để gửi lại mã OTP
    }
}