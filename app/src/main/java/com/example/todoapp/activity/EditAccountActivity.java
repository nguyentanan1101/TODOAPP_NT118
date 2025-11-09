package com.example.todoapp.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;

import java.util.Calendar;

public class EditAccountActivity extends AppCompatActivity {

    private EditText edtDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        ImageView btnChangePhoto = findViewById(R.id.btnChangePhoto);
        btnChangePhoto.setOnClickListener(v -> {
            // TODO: Mở gallery hoặc camera
            Toast.makeText(this, "Change photo clicked!", Toast.LENGTH_SHORT).show();
        });


        // Ánh xạ
        edtDate = findViewById(R.id.edtDate);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnExit = findViewById(R.id.btnExit);

        // Khi nhấn vào ô ngày -> mở DatePickerDialog
        edtDate.setOnClickListener(v -> {
            // Lấy ngày hiện tại
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Mở dialog chọn ngày
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EditAccountActivity.this,
                    (DatePicker view, int year1, int month1, int dayOfMonth) -> {
                        String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                        edtDate.setText(selectedDate);
                    },
                    year, month, day
            );

            datePickerDialog.show();
        });

        // Sự kiện nút Save
        btnSave.setOnClickListener(v -> {
            // Xử lý lưu thông tin ở đây
            // Ví dụ: Toast.makeText(this, "Đã lưu!", Toast.LENGTH_SHORT).show();
        });

        // Sự kiện nút Exit
        btnExit.setOnClickListener(v -> finish());
    }
}
