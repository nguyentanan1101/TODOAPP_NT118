package com.example.todoapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;
// Xóa import Facebook
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etAccount, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private ImageView googleBtn, githubBtn; // Đổi tên biến facebookBtn thành githubBtn
    private TextView tvSignIn;

    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    private OkHttpClient client = new OkHttpClient();
    private static final String SERVER_URL = "http://34.124.178.44:4000/api/auth/sign-up";

    // --- CẤU HÌNH GITHUB (Copy Client ID thật của bạn vào đây) ---
    private static final String GITHUB_CLIENT_ID = "YOUR_GITHUB_CLIENT_ID_HERE";
    private static final String GITHUB_REDIRECT_URI = "todoapp://callback";
    private static final String GITHUB_AUTH_URL = "https://github.com/login/oauth/authorize";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Kiểm tra xem có phải GitHub gọi lại không (trường hợp App cấu hình mở RegisterActivity khi callback)
        processGithubCallback(getIntent());

        // Ánh xạ view
        etAccount = findViewById(R.id.etAccount);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        googleBtn = findViewById(R.id.google_logo);
        githubBtn = findViewById(R.id.github_logo); // Ánh xạ nút Github
        tvSignIn = findViewById(R.id.tvSignIn);

        // Quay lại đăng nhập
        tvSignIn.setOnClickListener(v -> finish());

        // Đăng ký bằng email/sdt + password
        btnSignUp.setOnClickListener(v -> handleSignUp());

        // Setup Google & GitHub login
        setupGoogleSignIn();
        setupGithubSignIn(); // Thay thế Facebook bằng Github
    }

    // --- XỬ LÝ KHI MỞ LẠI ACTIVITY (Deep Link) ---
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processGithubCallback(intent);
    }

    private void processGithubCallback(Intent intent) {
        if (intent != null && intent.getData() != null) {
            Uri data = intent.getData();
            if (data.getScheme() != null && data.getScheme().equals("todoapp") && data.getHost().equals("callback")) {
                String code = data.getQueryParameter("code");
                if (code != null) {
                    handleGithubAuthCode(code);
                }
            }
        }
    }

    // ------------------------- SIGN UP (Server App) -------------------------
    private void handleSignUp() {
        String account = etAccount.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (account.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please enter all required information.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Password and confirmation password do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject json = new JSONObject();
            if (account.contains("@")) json.put("email", account);
            else json.put("phone_number", account);
            json.put("password", password);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(SERVER_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(RegisterActivity.this, "Unable to connect to the server.", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        runOnUiThread(() ->
                                Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show()
                        );
                        return;
                    }

                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        finish(); // quay lại Login để người dùng đăng nhập
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------------- GITHUB LOGIN -------------------------
    private void setupGithubSignIn() {
        githubBtn.setOnClickListener(v -> {
            // Mở trình duyệt đăng nhập GitHub
            // Lưu ý: Social Login (Github/Google) bản chất là Đăng nhập,
            // nếu tài khoản chưa có thì thường server sẽ tự tạo.
            String url = GITHUB_AUTH_URL + "?client_id=" + GITHUB_CLIENT_ID + "&redirect_uri=" + GITHUB_REDIRECT_URI + "&scope=user:email";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
    }

    private void handleGithubAuthCode(String code) {
        Toast.makeText(this, "Github Code received in Register: " + code, Toast.LENGTH_SHORT).show();

        // Về logic, khi đăng nhập Github thành công, ta nên chuyển thẳng vào MainActivity
        // Gọi API login github ở đây (giống LoginActivity)

        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // ------------------------- GOOGLE LOGIN -------------------------
    private void setupGoogleSignIn() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        // Ở màn hình đăng ký, ta không tự động chuyển trang nếu đã login Google trước đó
        // để người dùng có cơ hội đăng ký tài khoản mới nếu muốn.

        googleBtn.setOnClickListener(v -> {
            Intent signInIntent = gsc.getSignInIntent();
            startActivityForResult(signInIntent, 1000);
        });
    }

    private void handleGoogleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            task.getResult(ApiException.class);
            // Google Login thành công -> Vào Main
            navigateToMainActivity();
        } catch (ApiException e) {
            Toast.makeText(this, "Google login failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) handleGoogleSignInResult(data);
    }

    // ------------------------- NAVIGATION -------------------------
    private void navigateToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}