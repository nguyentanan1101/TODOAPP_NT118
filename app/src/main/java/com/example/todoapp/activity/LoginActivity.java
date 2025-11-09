package com.example.todoapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

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
    private TextView tvPasswordError, tvForgotPassword, tvSignUp;
    private ImageView googleBtn, facebookBtn;

    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private CallbackManager callbackManager;

    private OkHttpClient client = new OkHttpClient();
    private static final String SERVER_URL = "http://163.61.110.132:4000/api/auth/sign-in"; // backend login endpoint

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ View
        etAccount = findViewById(R.id.etAccount);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);
        googleBtn = findViewById(R.id.google_logo);
        facebookBtn = findViewById(R.id.facebook_logo);

        // Setup đăng nhập App
        btnSignIn.setOnClickListener(v -> handleAppSignIn());
        tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(this, RecoverPasswordActivity.class)));
        tvSignUp.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        // Setup đăng nhập Google
        setupGoogleSignIn();

        // Setup đăng nhập Facebook
        setupFacebookSignIn();
    }

    // ------------------------- APP LOGIN -------------------------
    private void handleAppSignIn() {
        String account = etAccount.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (account.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Email/SĐT và mật khẩu", Toast.LENGTH_SHORT).show();
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
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this, "Không thể kết nối server", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String res = response.body().string();
                    if (!response.isSuccessful()) {
                        runOnUiThread(() ->
                                Toast.makeText(LoginActivity.this, "Login thất bại", Toast.LENGTH_SHORT).show()
                        );
                        return;
                    }

                    try {
                        JSONObject obj = new JSONObject(res);
                        JSONObject user = obj.getJSONObject("user");
                        String accessToken = obj.getString("accessToken");
                        String refreshToken = obj.getString("refreshToken");

                        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("user_id", user.getString("user_id"));
                        editor.putString("user_name", user.getString("username"));
                        editor.putString("accessToken", accessToken);
                        editor.putString("refreshToken", refreshToken);
                        editor.apply();

                        runOnUiThread(LoginActivity.this::navigateToMainActivity);

                    } catch (Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(LoginActivity.this, "Phản hồi server không hợp lệ", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            task.getResult(ApiException.class);
            navigateToMainActivity();
        } catch (ApiException e) {
            Toast.makeText(this, "Google login failed", Toast.LENGTH_SHORT).show();
        }
    }

    // ------------------------- FACEBOOK LOGIN -------------------------
    private void setupFacebookSignIn() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        navigateToMainActivity();
                    }

                    @Override
                    public void onCancel() {}

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, "FB login failed", Toast.LENGTH_SHORT).show();
                    }
                });

        facebookBtn.setOnClickListener(v ->
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("public_profile"))
        );
    }

    // ------------------------- ACTIVITY RESULT -------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Google
        if (requestCode == 1000) handleGoogleSignInResult(data);
        // Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // ------------------------- NAVIGATION -------------------------
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
