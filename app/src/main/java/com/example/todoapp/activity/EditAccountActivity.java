package com.example.todoapp.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;

import org.json.JSONObject;

import java.io.BufferedReader;
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

    private static final String SERVER_URL = "http://163.61.110.132:4000/api/auth/update-profile";

    private EditText edtName, edtDate, edtEmail, edtMobile, edtAddress;
    private Button btnSave, btnExit;
    private ImageButton btnPickDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        edtName = findViewById(R.id.edtName);
        edtDate = findViewById(R.id.edtDate);
        edtEmail = findViewById(R.id.edtEmail);
        edtMobile = findViewById(R.id.edtMobile);
        edtAddress = findViewById(R.id.edtAddress);
        btnSave = findViewById(R.id.btnSave);
        btnExit = findViewById(R.id.btnExit);
        btnPickDate = findViewById(R.id.btnPickDate);

        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        // Xử lý hiển thị ngày sinh chỉ lấy yyyy-MM-dd hoặc dd/MM/yyyy
        String rawDate = sp.getString("birthday", "");
        edtDate.setText(formatDateForDisplay(rawDate));

        edtName.setText(sp.getString("username", ""));
        edtEmail.setText(sp.getString("email", ""));
        edtMobile.setText(sp.getString("phone_number", ""));
        edtAddress.setText(sp.getString("address", ""));

        // Khi nhấn icon calendar, hiển thị DatePicker
        btnPickDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> updateUserProfile());
        btnExit.setOnClickListener(v -> finish());
    }

    private String formatDateForDisplay(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "";
        try {
            // Nếu dữ liệu từ server kiểu "2025-05-11T00:00:00.000Z"
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = isoFormat.parse(isoDate);
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return displayFormat.format(date);
        } catch (Exception e) {
            // Nếu không parse được, trả về chuỗi nguyên thủy
            return isoDate.split("T")[0];
        }
    }

    private void showDatePicker() {
        String currentDate = edtDate.getText().toString();
        int year, month, day;

        if (!currentDate.isEmpty()) {
            try {
                String[] parts = currentDate.split("/"); // dd/MM/yyyy
                day = Integer.parseInt(parts[0]);
                month = Integer.parseInt(parts[1]) - 1;
                year = Integer.parseInt(parts[2]);
            } catch (Exception e) {
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            }
        } else {
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    edtDate.setText(formattedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void updateUserProfile() {
        String name = edtName.getText().toString().trim();
        String birthday = edtDate.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtMobile.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String token = sp.getString("accessToken", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("username", name);
            body.put("birthday", birthday); // giữ định dạng dd/MM/yyyy
            body.put("email", email);
            body.put("phone_number", phone);
            body.put("address", address);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi tạo dữ liệu", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
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
                InputStream is = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);

                if (responseCode == 200) {
                    JSONObject obj = new JSONObject(response.toString());
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username", obj.getString("username"));
                    editor.putString("email", obj.getString("email"));
                    editor.putString("phone_number", obj.getString("phone_number"));
                    editor.putString("address", obj.getString("address"));
                    editor.putString("birthday", obj.getString("birthday"));
                    editor.apply();

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Lỗi cập nhật: " + response, Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Không thể kết nối server", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
