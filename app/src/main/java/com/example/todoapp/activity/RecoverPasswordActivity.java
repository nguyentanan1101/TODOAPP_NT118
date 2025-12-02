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

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecoverPasswordActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etRecoverAccount;
    private Button btnRecoverPassword;

    private OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "http://34.124.178.44:4000/api/auth/forgot-password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        // Ánh xạ View
        ivBack = findViewById(R.id.ivBack);
        etRecoverAccount = findViewById(R.id.etRecoverAccount);
        btnRecoverPassword = findViewById(R.id.btnRecoverPassword);

        // Quay về Login
        ivBack.setOnClickListener(v -> finish());

        // Gửi yêu cầu recover password
        btnRecoverPassword.setOnClickListener(v -> handleRecoverPassword());
    }

    private void handleRecoverPassword() {
        String email = etRecoverAccount.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject json = new JSONObject();
            json.put("email", email);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(RecoverPasswordActivity.this, "Unable to connect to the server.", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String res = response.body().string();

                    if (!response.isSuccessful()) {
                        runOnUiThread(() ->
                                Toast.makeText(RecoverPasswordActivity.this, "Failed to send the code.", Toast.LENGTH_SHORT).show()
                        );
                        return;
                    }

                    try {
                        JSONObject obj = new JSONObject(res);
                        String message = obj.getString("message");

                        runOnUiThread(() -> {
                            Toast.makeText(RecoverPasswordActivity.this, message, Toast.LENGTH_SHORT).show();

                            // Chuyển sang màn OTP verification
                            Intent intent = new Intent(RecoverPasswordActivity.this, OTPVerificationActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        });

                    } catch (Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(RecoverPasswordActivity.this, "Invalid server response.", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong.", Toast.LENGTH_SHORT).show();
        }
    }
}
