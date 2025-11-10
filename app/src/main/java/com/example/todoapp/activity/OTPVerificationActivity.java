package com.example.todoapp.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class OTPVerificationActivity extends AppCompatActivity {

    private EditText etOtp1, etOtp2, etOtp3, etOtp4;
    private TextView tvSendAgain;
    private String recoveredEmail; // email để gửi lại OTP

    private OkHttpClient client = new OkHttpClient();
    private static final String CHECK_TOKEN_URL = "http://163.61.110.132:4000/api/auth/check-reset-token";
    private static final String FORGOT_PASSWORD_URL = "http://163.61.110.132:4000/api/auth/forgot-password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        // Lấy email từ Intent
        recoveredEmail = getIntent().getStringExtra("email");

        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);
        tvSendAgain = findViewById(R.id.tvSendAgain);

        tvSendAgain.setPaintFlags(tvSendAgain.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        setupOtpInputListeners();

        // Gán click cho "Send again"
        tvSendAgain.setOnClickListener(v -> sendOtpAgain());
    }

    private void setupOtpInputListeners() {
        final EditText[] otps = {etOtp1, etOtp2, etOtp3, etOtp4};

        for (int i = 0; i < otps.length; i++) {
            final int index = i;
            otps[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1) {
                        if (index < otps.length - 1) otps[index + 1].requestFocus();
                        else {
                            otps[index].clearFocus();
                            checkResetTokenWithBackend();
                        }
                    } else if (s.length() == 0 && before == 1) {
                        if (index > 0) otps[index - 1].requestFocus();
                    }
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void checkResetTokenWithBackend() {
        String token = etOtp1.getText().toString() + etOtp2.getText().toString() +
                etOtp3.getText().toString() + etOtp4.getText().toString();

        if (token.length() < 4) return;

        try {
            JSONObject json = new JSONObject();
            json.put("token", token);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(CHECK_TOKEN_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(OTPVerificationActivity.this, "Không thể kết nối server.", Toast.LENGTH_SHORT).show()
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
                                Toast.makeText(OTPVerificationActivity.this, message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(OTPVerificationActivity.this, ResetPasswordActivity.class);
                                intent.putExtra("reset_token", token);
                                startActivity(intent);
                            } else {
                                Toast.makeText(OTPVerificationActivity.this, message, Toast.LENGTH_SHORT).show();
                                clearOtpFields();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            Toast.makeText(OTPVerificationActivity.this, "Mã đặt lại không chính xác.", Toast.LENGTH_SHORT).show();
                            clearOtpFields();
                        });
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendOtpAgain() {
        if (recoveredEmail == null || recoveredEmail.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy email.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject json = new JSONObject();
            json.put("email", recoveredEmail);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(FORGOT_PASSWORD_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(OTPVerificationActivity.this, "Không thể kết nối server.", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String res = response.body().string();
                    try {
                        JSONObject obj = new JSONObject(res);
                        final String message = obj.optString("message", "Lỗi server");

                        runOnUiThread(() ->
                                Toast.makeText(OTPVerificationActivity.this, message, Toast.LENGTH_SHORT).show()
                        );

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(OTPVerificationActivity.this, "Phản hồi server không hợp lệ.", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Có lỗi xảy ra.", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearOtpFields() {
        etOtp1.setText(""); etOtp2.setText(""); etOtp3.setText(""); etOtp4.setText("");
        etOtp1.requestFocus();
    }
}
