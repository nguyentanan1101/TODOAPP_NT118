package com.example.todoapp.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;

// --- ĐÃ XÓA CÁC IMPORT CỦA FACEBOOK ---
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

public class LoginActivity extends AppCompatActivity {

    private EditText etAccount, etPassword;
    private Button btnSignIn;
    private TextView tvForgotPassword, tvSignUp;
    private ImageView googleBtn, githubBtn; // Đổi tên từ facebookBtn thành githubBtn
    private CheckBox chkRememberMe;

    private boolean isPasswordVisible = false;

    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    private OkHttpClient client = new OkHttpClient();
    private static final String SERVER_URL = "http://163.61.110.132:4000/api/auth/sign-in";

    // --- CẤU HÌNH GITHUB ---
    // Bạn hãy lấy Client ID từ trang Github Developer Settings dán vào đây
    private static final String GITHUB_CLIENT_ID = "Ov23linN6K2PUJKdvuVQ";
    private static final String GITHUB_REDIRECT_URI = "todoapp://callback";
    private static final String GITHUB_AUTH_URL = "https://github.com/login/oauth/authorize";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. XỬ LÝ KHI GITHUB REDIRECT VỀ APP
        processGithubCallback(getIntent());

        // --- Kiểm tra token trước khi hiển thị login ---
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String token = sp.getString("accessToken", "");
        if (!token.isEmpty()) {
            navigateToMainActivity();
            return;
        }

        // Ánh xạ View
        etAccount = findViewById(R.id.etAccount);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);
        googleBtn = findViewById(R.id.google_logo);
        githubBtn = findViewById(R.id.github_logo); // Ánh xạ nút Github (lưu ý ID trong XML phải là github_logo)
        chkRememberMe = findViewById(R.id.chkRememberMe);

        // --- XỬ LÝ ẨN/HIỆN MẬT KHẨU ---
        etPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int iconWidth = etPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                if (event.getX() >= (etPassword.getWidth() - etPassword.getPaddingEnd() - iconWidth)) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });

        loadRememberedAccount();

        btnSignIn.setOnClickListener(v -> handleAppSignIn());
        tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(this, RecoverPasswordActivity.class)));
        tvSignUp.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        setupGoogleSignIn();
        setupGithubSignIn(); // Thiết lập Github
    }

    // Hàm hỗ trợ để bắt Intent khi Activity chạy ngầm (SingleTask/SingleTop)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processGithubCallback(intent);
    }

    // --- LOGIC XỬ LÝ GITHUB CALLBACK ---
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

    // ------------------------- GITHUB LOGIN -------------------------
    private void setupGithubSignIn() {
        githubBtn.setOnClickListener(v -> {
            // Tạo URL để mở trình duyệt
            String url = GITHUB_AUTH_URL + "?client_id=" + GITHUB_CLIENT_ID + "&redirect_uri=" + GITHUB_REDIRECT_URI + "&scope=user:email";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
    }

    private void handleGithubAuthCode(String code) {
        // Có CODE từ Github.
        // Bước tiếp theo: Gửi CODE này lên Backend Node.js của bạn.
        // Backend sẽ dùng Code + ClientSecret để đổi lấy AccessToken thật từ Github.

        Toast.makeText(this, "Github Authorized! Code: " + code, Toast.LENGTH_SHORT).show();

        // TẠM THỜI: Giả lập đăng nhập thành công để bạn test luồng App
        // (Sau này bạn cần viết API gọi lên server ở đây giống hàm handleAppSignIn)

        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        // Lưu dữ liệu giả để vào được Main
        editor.putString("accessToken", "github_mock_token_" + code);
        editor.putString("username", "Github User");
        editor.putString("email", "github@example.com");
        editor.apply();

        navigateToMainActivity();
    }

    // ------------------------- APP LOGIN -------------------------
    private void handleAppSignIn() {
        String account = etAccount.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (account.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your Email/Phone number and Password.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isEmail = account.contains("@");

        try {
            JSONObject json = new JSONObject();
            if (isEmail) json.put("email", account);
            else json.put("phone_number", account);
            json.put("password", password);

            RequestBody body = RequestBody.create(
                    json.toString(), MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(SERVER_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this, "Unable to connect to the server.", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    assert response.body() != null;
                    String res = response.body().string();
                    if (!response.isSuccessful()) {
                        runOnUiThread(() ->
                                Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_SHORT).show()
                        );
                        return;
                    }

                    try {
                        JSONObject obj = new JSONObject(res);
                        JSONObject user = obj.getJSONObject("user");
                        String accessToken = obj.getString("accessToken");
                        String refreshToken = obj.getString("refreshToken");

                        saveUserSession(user, accessToken, refreshToken, account, password);
                        runOnUiThread(LoginActivity.this::navigateToMainActivity);

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(LoginActivity.this, "Invalid server response.", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveUserSession(JSONObject user, String accessToken, String refreshToken, String rawAccount, String rawPassword) {
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("accessToken", accessToken != null ? accessToken : "");
        editor.putString("refreshToken", refreshToken != null ? refreshToken : "");

        editor.putString("user_id", user.optString("user_id", ""));
        editor.putString("username", user.optString("username", ""));
        // ... (Các trường user khác)

        // XỬ LÝ REMEMBER ME
        if (chkRememberMe.isChecked()) {
            editor.putBoolean("isRemembered", true);
            editor.putString("savedAccount", rawAccount);
            editor.putString("savedPassword", rawPassword);
        } else {
            editor.remove("isRemembered");
            editor.remove("savedAccount");
            editor.remove("savedPassword");
        }

        editor.apply();
    }

    private void loadRememberedAccount() {
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        boolean isRemembered = sp.getBoolean("isRemembered", false);

        if (isRemembered) {
            String savedAccount = sp.getString("savedAccount", "");
            String savedPassword = sp.getString("savedPassword", "");
            etAccount.setText(savedAccount);
            etPassword.setText(savedPassword);
            chkRememberMe.setChecked(true);
        }
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
            isPasswordVisible = false;
        } else {
            etPassword.setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0);
            isPasswordVisible = true;
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    // ------------------------- GOOGLE LOGIN -------------------------
    private void setupGoogleSignIn() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) navigateToMainActivity();

        googleBtn.setOnClickListener(v -> {
            Intent signInIntent = gsc.getSignInIntent();
            startActivityForResult(signInIntent, 1000);
        });
    }

    private void handleGoogleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("username", account.getDisplayName());
                editor.putString("email", account.getEmail());
                editor.putString("accessToken", "google");
                editor.apply();

                navigateToMainActivity();
            }
        } catch (ApiException e) {
            Toast.makeText(this, "Google login failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) handleGoogleSignInResult(data);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}