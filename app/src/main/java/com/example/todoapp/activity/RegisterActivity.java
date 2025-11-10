package com.example.todoapp.activity;

import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText etAccount, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private ImageView googleBtn, facebookBtn;
    private TextView tvSignIn;

    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private CallbackManager callbackManager;

    private OkHttpClient client = new OkHttpClient();
    private static final String SERVER_URL = "http://163.61.110.132:4000/api/auth/sign-up";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ view
        etAccount = findViewById(R.id.etAccount);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        googleBtn = findViewById(R.id.google_logo);
        facebookBtn = findViewById(R.id.facebook_logo);
        tvSignIn = findViewById(R.id.tvSignIn);

        // Quay lại đăng nhập
        tvSignIn.setOnClickListener(v -> finish());

        // Đăng ký bằng email/sdt + password
        btnSignUp.setOnClickListener(v -> handleSignUp());

        // Setup Google & Facebook login
        setupGoogleSignIn();
        setupFacebookSignIn();
    }

    // ------------------------- SIGN UP -------------------------
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
                    String res = response.body().string();
                    if (!response.isSuccessful()) {
                        runOnUiThread(() ->
                                Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show()
                        );
                        return;
                    }

                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        finish(); // quay lại Login
                    });
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
                        Toast.makeText(RegisterActivity.this, "FB login failed", Toast.LENGTH_SHORT).show();
                    }
                });

        facebookBtn.setOnClickListener(v ->
                LoginManager.getInstance().logInWithReadPermissions(RegisterActivity.this,
                        Arrays.asList("public_profile"))
        );
    }

    // ------------------------- ACTIVITY RESULT -------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) handleGoogleSignInResult(data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // ------------------------- NAVIGATION -------------------------
    private void navigateToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
