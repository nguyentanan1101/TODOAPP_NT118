package com.example.todoapp.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditAccountActivity extends AppCompatActivity {

    private static final String SERVER_URL = "http://34.124.178.44:4000/api/auth/update-profile";

    private EditText edtName, edtDate, edtEmail, edtMobile, edtAddress;
    private Button btnSave, btnExit;
    private ImageView btnPickDate, btnChangePhoto, imgAvatar;

    // --- BIẾN CHO GENDER ---
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;

    private Uri selectedImageUri = null;
    private String pendingAvatarBase64 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        initViews();
        loadUserData();

        // Events
        btnPickDate.setOnClickListener(v -> showDatePicker());
        btnChangePhoto.setOnClickListener(v -> openImagePicker());
        btnSave.setOnClickListener(v -> updateUserProfile());
        btnExit.setOnClickListener(v -> finish());
    }

    private void initViews() {
        edtName = findViewById(R.id.edtName);
        edtDate = findViewById(R.id.edtDate);
        edtEmail = findViewById(R.id.edtEmail);
        edtMobile = findViewById(R.id.edtMobile);
        edtAddress = findViewById(R.id.edtAddress);
        btnSave = findViewById(R.id.btnSave);
        btnExit = findViewById(R.id.btnExit);
        btnPickDate = findViewById(R.id.btnPickDate);

        imgAvatar = findViewById(R.id.imgAvatar);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);

        // Ánh xạ Gender
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
    }

    private void loadUserData() {
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        // Load Text
        String rawDate = sp.getString("birthday", "");
        edtDate.setText(formatDateForDisplay(rawDate));
        edtName.setText(sp.getString("username", ""));
        edtEmail.setText(sp.getString("email", ""));
        edtMobile.setText(sp.getString("phone_number", ""));
        edtAddress.setText(sp.getString("address", ""));

        // --- LOAD GENDER ---
        String gender = sp.getString("gender", "");
        if (gender.equalsIgnoreCase("Male") || gender.equalsIgnoreCase("Nam")) {
            rbMale.setChecked(true);
        } else if (gender.equalsIgnoreCase("Female") || gender.equalsIgnoreCase("Nữ")) {
            rbFemale.setChecked(true);
        }

        // --- LOAD AVATAR ---
        String savedAvatar = sp.getString("avatar", "");
        if (!savedAvatar.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(savedAvatar, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imgAvatar.setImageBitmap(decodedByte);
            } catch (Exception e) {
                e.printStackTrace();
                imgAvatar.setImageResource(R.drawable.ic_user);
            }
        }
    }

    // --- GỬI API ---
    private void updateUserProfile() {
        String name = edtName.getText().toString().trim();
        String birthday = edtDate.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtMobile.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        // Lấy giới tính
        String gender = "";
        if (rbMale.isChecked()) gender = "Male";
        else if (rbFemale.isChecked()) gender = "Female";

        final String finalGender = gender; // Biến final để dùng trong thread

        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String token = sp.getString("accessToken", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("username", name);
                body.put("birthday", birthday);
                body.put("email", email);
                body.put("phone_number", phone);
                body.put("address", address);

                // Gửi gender lên server
                body.put("gender", finalGender);

                if (pendingAvatarBase64 != null) {
                    body.put("avatar", pendingAvatarBase64);
                }

                URL url = new URL(SERVER_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(body.toString().getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = conn.getResponseCode();
                InputStream is = (responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);

                if (responseCode == 200) {
                    JSONObject rootObj = new JSONObject(response.toString());

                    if (rootObj.has("user")) {
                        JSONObject userObj = rootObj.getJSONObject("user");
                        SharedPreferences.Editor editor = sp.edit();

                        editor.putString("username", userObj.optString("username", name));
                        editor.putString("email", userObj.optString("email", email));
                        editor.putString("phone_number", userObj.optString("phone_number", phone));
                        editor.putString("address", userObj.optString("address", address));
                        editor.putString("birthday", userObj.optString("birthday", birthday));

                        // Cập nhật gender vào SharedPreferences từ response (hoặc dùng giá trị gửi đi nếu server ko trả về)
                        editor.putString("gender", userObj.optString("gender", finalGender));

                        if (pendingAvatarBase64 != null) {
                            editor.putString("avatar", pendingAvatarBase64);
                        }

                        editor.apply();
                    }

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    String errorRes = response.toString();
                    Log.e("UPLOAD_ERROR", errorRes);
                    runOnUiThread(() -> {
                        if (errorRes.contains("<!DOCTYPE") || errorRes.contains("<html")) {
                            Toast.makeText(this, "Ảnh quá lớn, Server từ chối!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Lỗi cập nhật (" + responseCode + ")", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // --- CÁC HÀM XỬ LÝ ẢNH VÀ NGÀY THÁNG (GIỮ NGUYÊN) ---

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        imgAvatar.setImageURI(selectedImageUri);
                        pendingAvatarBase64 = convertImageToBase64(selectedImageUri);
                    }
                }
            }
    );

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private String convertImageToBase64(Uri imageUri) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap originalBitmap = BitmapFactory.decodeStream(imageStream);
            Bitmap resizedBitmap = getResizedBitmap(originalBitmap, 600);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.NO_WRAP);
        } catch (Exception e) { return null; }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) { width = maxSize; height = (int) (width / bitmapRatio); }
        else { height = maxSize; width = (int) (height * bitmapRatio); }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private String formatDateForDisplay(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "";
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = isoFormat.parse(isoDate);
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return displayFormat.format(date);
        } catch (Exception e) { return isoDate.split("T")[0]; }
    }

    private void showDatePicker() {
        String currentDate = edtDate.getText().toString();
        int year, month, day;
        if (!currentDate.isEmpty()) {
            try {
                String[] parts = currentDate.split("/");
                day = Integer.parseInt(parts[0]); month = Integer.parseInt(parts[1]) - 1; year = Integer.parseInt(parts[2]);
            } catch (Exception e) { Calendar c = Calendar.getInstance(); year = c.get(Calendar.YEAR); month = c.get(Calendar.MONTH); day = c.get(Calendar.DAY_OF_MONTH); }
        } else { Calendar c = Calendar.getInstance(); year = c.get(Calendar.YEAR); month = c.get(Calendar.MONTH); day = c.get(Calendar.DAY_OF_MONTH); }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, y, m, d) -> {
            String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y);
            edtDate.setText(formattedDate);
        }, year, month, day);
        datePickerDialog.show();
    }
}