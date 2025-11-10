package com.example.todoapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnContinue;
    private TextView tvPasswordError;

    private String resetToken; // Nhận từ OTPVerificationActivity

    private OkHttpClient client = new OkHttpClient();
    private static final String RESET_PASSWORD_URL = "http://163.61.110.132:4000/api/auth/reset-password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Nhận token từ Intent
        resetToken = getIntent().getStringExtra("reset_token");

        // Ánh xạ các View
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnContinue = findViewById(R.id.btnContinue);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        tvPasswordError.setVisibility(View.GONE);

        // Xử lý sự kiện cho nút Continue
        btnContinue.setOnClickListener(v -> handleContinue());
    }

    private void handleContinue() {
        String newPass = etNewPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        tvPasswordError.setVisibility(View.GONE);

        // 1. Kiểm tra rỗng và độ dài tối thiểu
        if (newPass.isEmpty() || confirmPass.isEmpty() || newPass.length() < 6) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu hợp lệ (ít nhất 6 ký tự)", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Kiểm tra mật khẩu xác nhận
        if (!newPass.equals(confirmPass)) {
            tvPasswordError.setText("Mật khẩu xác nhận không khớp");
            tvPasswordError.setVisibility(View.VISIBLE);
            return;
        }

        // 3. Gọi API reset password
        if (resetToken == null || resetToken.isEmpty()) {
            Toast.makeText(this, "Token không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject json = new JSONObject();
            json.put("token", resetToken);
            json.put("newPassword", newPass);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(RESET_PASSWORD_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(ResetPasswordActivity.this, "Không thể kết nối server.", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String res = response.body().string();
                    try {
                        JSONObject obj = new JSONObject(res);
                        final String message = obj.optString("message", "Lỗi server");

                        runOnUiThread(() -> {
                            if (response.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_LONG).show();

                                // Chuyển về LoginActivity và xóa stack
                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(ResetPasswordActivity.this, "Phản hồi server không hợp lệ.", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Có lỗi xảy ra.", Toast.LENGTH_SHORT).show();
        }
    }
}
