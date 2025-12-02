package com.example.todoapp.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddTaskActivity extends AppCompatActivity {

    private ImageView btnBack, btnAddSubtask;
    private ImageView btnPickStartDate, btnPickEndDate;
    private TextView btnSave;

    private EditText etStartDay, etStartMonth, etStartYear;
    private EditText etEndDay, etEndMonth, etEndYear;
    private EditText etTitle, etNotes;
    private LinearLayout containerSubtasks;

    private Spinner spinnerReminder, spinnerPriority;
    private TextView tvSelectedTime;

    private Calendar customReminderCal = Calendar.getInstance();
    private String selectedReminderOption = "None";
    private String selectedPriority = "Low";

    private Calendar startCal = Calendar.getInstance();
    private Calendar endCal = Calendar.getInstance();

    private OkHttpClient client = new OkHttpClient();
    private static final String CREATE_TASK_URL = "http://34.124.178.44:4000/api/tasks/create";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();
        setupListeners();
        setDefaultDate();
        setupSpinners();

        // Cài đặt validate nhập liệu ngày tháng
        setupDateInputValidation(etStartDay, etStartMonth, etStartYear);
        setupDateInputValidation(etEndDay, etEndMonth, etEndYear);

        addSubtaskRow();
    }

    // --- LOGIC VALIDATE NGÀY THÁNG AN TOÀN ---
    private void setupDateInputValidation(EditText etDay, EditText etMonth, EditText etYear) {
        View.OnFocusChangeListener listener = (v, hasFocus) -> {
            if (!hasFocus) {
                validateDateFields(etDay, etMonth, etYear);
            }
        };

        etDay.setOnFocusChangeListener(listener);
        etMonth.setOnFocusChangeListener(listener);
        etYear.setOnFocusChangeListener(listener);
    }

    private void validateDateFields(EditText etDay, EditText etMonth, EditText etYear) {
        String dStr = etDay.getText().toString().trim();
        String mStr = etMonth.getText().toString().trim();
        String yStr = etYear.getText().toString().trim();

        if (dStr.isEmpty() && mStr.isEmpty()) return; // Chưa nhập gì thì bỏ qua

        try {
            int d = dStr.isEmpty() ? 1 : Integer.parseInt(dStr);
            int m = mStr.isEmpty() ? 1 : Integer.parseInt(mStr);
            // Nếu năm chưa nhập đủ 4 số, lấy năm hiện tại để check ngày hợp lệ
            int y = (yStr.length() < 4) ? Calendar.getInstance().get(Calendar.YEAR) : Integer.parseInt(yStr);

            // 1. Validate Tháng
            if (m < 1) m = 1;
            if (m > 12) m = 12;

            // 2. Validate Ngày theo Tháng/Năm
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, y);
            cal.set(Calendar.MONTH, m - 1);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            if (d < 1) d = 1;
            if (d > maxDay) d = maxDay;

            // 3. Update lại UI (thêm số 0 nếu cần)
            if (!dStr.isEmpty()) etDay.setText(String.format(Locale.getDefault(), "%02d", d));
            if (!mStr.isEmpty()) etMonth.setText(String.format(Locale.getDefault(), "%02d", m));

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    // --- API CALL ---
    private void createNewTask(String title, String startDate, String endDate, String priority, String reminder, String note, List<String> subtasks) {
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");

        if (accessToken.isEmpty()) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("title", title);
            jsonBody.put("description", note);
            jsonBody.put("start_date", startDate);
            jsonBody.put("due_date", endDate);
            jsonBody.put("priority", priority);
            jsonBody.put("reminder", reminder);
            jsonBody.put("status", "ToDo");

            JSONArray subtaskArray = new JSONArray();
            for (String subName : subtasks) {
                JSONObject subObj = new JSONObject();
                subObj.put("subtask_name", subName);
                subObj.put("subtask_status", "ToDo");
                subtaskArray.put(subObj);
            }
            jsonBody.put("subtasks", subtaskArray);

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(CREATE_TASK_URL)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(AddTaskActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resStr = response.body().string();
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            Toast.makeText(AddTaskActivity.this, "Tạo task thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } else {
                        Log.e("API_ERROR", "Code: " + response.code() + " - " + resStr);
                        runOnUiThread(() -> Toast.makeText(AddTaskActivity.this, "Lỗi tạo task: " + response.code(), Toast.LENGTH_SHORT).show());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- SETUP LISTENERS ---
    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPickStartDate.setOnClickListener(v -> showDatePicker(startCal, etStartDay, etStartMonth, etStartYear));
        btnPickEndDate.setOnClickListener(v -> showDatePicker(endCal, etEndDay, etEndMonth, etEndYear));

        tvSelectedTime.setOnClickListener(v -> showCustomReminderPicker());
        btnAddSubtask.setOnClickListener(v -> addSubtaskRow());

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate lần cuối
            validateDateFields(etStartDay, etStartMonth, etStartYear);
            validateDateFields(etEndDay, etEndMonth, etEndYear);

            String startDate = getSelectedDate(etStartDay, etStartMonth, etStartYear);
            String endDate = getSelectedDate(etEndDay, etEndMonth, etEndYear);
            List<String> subtasks = getSubtaskListFromUI();
            String note = etNotes.getText().toString().trim();

            String finalReminder = selectedReminderOption;
            if (selectedReminderOption.equals("Custom")) {
                SimpleDateFormat sdfSave = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                finalReminder = sdfSave.format(customReminderCal.getTime());
            }

            createNewTask(title, startDate, endDate, selectedPriority, finalReminder, note, subtasks);
        });
    }

    // --- HELPER FUNCTIONS ---
    private void setDefaultDate() {
        Calendar today = Calendar.getInstance();
        String d = String.format(Locale.getDefault(), "%02d", today.get(Calendar.DAY_OF_MONTH));
        String m = String.format(Locale.getDefault(), "%02d", today.get(Calendar.MONTH) + 1);
        String y = String.valueOf(today.get(Calendar.YEAR));
        etStartDay.setText(d); etStartMonth.setText(m); etStartYear.setText(y);
        etEndDay.setText(d); etEndMonth.setText(m); etEndYear.setText(y);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack); btnSave = findViewById(R.id.btnSave);
        etTitle = findViewById(R.id.etTitle); etNotes = findViewById(R.id.etNotes);
        btnPickStartDate = findViewById(R.id.btnPickStartDate); btnPickEndDate = findViewById(R.id.btnPickEndDate);

        etStartDay = findViewById(R.id.etStartDay); etStartMonth = findViewById(R.id.etStartMonth); etStartYear = findViewById(R.id.etStartYear);
        etEndDay = findViewById(R.id.etEndDay); etEndMonth = findViewById(R.id.etEndMonth); etEndYear = findViewById(R.id.etEndYear);

        containerSubtasks = findViewById(R.id.containerSubtasks); btnAddSubtask = findViewById(R.id.btnAddSubtask);
        spinnerReminder = findViewById(R.id.spinnerReminder); spinnerPriority = findViewById(R.id.spinnerPriority);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
    }

    private void setupSpinners() {
        String[] reminderOptions = {"None", "5 minutes", "10 minutes", "30 minutes", "1 hour", "1 day", "Custom"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reminderOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReminder.setAdapter(adapter);
        spinnerReminder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                selectedReminderOption = reminderOptions[pos];
                if (selectedReminderOption.equals("Custom")) showCustomReminderPicker();
                else tvSelectedTime.setVisibility(View.GONE);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        String[] priorityOptions = {"Low", "Medium", "High", "Critical"};
        ArrayAdapter<String> pAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorityOptions);
        pAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(pAdapter);
        spinnerPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) { selectedPriority = priorityOptions[pos]; }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void showCustomReminderPicker() {
        DatePickerDialog dateDialog = new DatePickerDialog(this, (view, year, month, day) -> {
            customReminderCal.set(Calendar.YEAR, year); customReminderCal.set(Calendar.MONTH, month); customReminderCal.set(Calendar.DAY_OF_MONTH, day);
            showTimePickerForCustom();
        }, customReminderCal.get(Calendar.YEAR), customReminderCal.get(Calendar.MONTH), customReminderCal.get(Calendar.DAY_OF_MONTH));
        dateDialog.setOnCancelListener(d -> spinnerReminder.setSelection(0));
        dateDialog.show();
    }

    private void showTimePickerForCustom() {
        TimePickerDialog timeDialog = new TimePickerDialog(this, (view, hour, minute) -> {
            customReminderCal.set(Calendar.HOUR_OF_DAY, hour); customReminderCal.set(Calendar.MINUTE, minute);
            updateCustomTimeText();
        }, customReminderCal.get(Calendar.HOUR_OF_DAY), customReminderCal.get(Calendar.MINUTE), true);
        timeDialog.setOnCancelListener(d -> spinnerReminder.setSelection(0));
        timeDialog.show();
    }

    private void updateCustomTimeText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        tvSelectedTime.setText("Alarm: " + sdf.format(customReminderCal.getTime()) + " (Tap to edit)");
        tvSelectedTime.setVisibility(View.VISIBLE);
    }

    private void showDatePicker(Calendar calendar, EditText etDay, EditText etMonth, EditText etYear) {
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year); calendar.set(Calendar.MONTH, month); calendar.set(Calendar.DAY_OF_MONTH, day);
            etDay.setText(String.format(Locale.getDefault(), "%02d", day));
            etMonth.setText(String.format(Locale.getDefault(), "%02d", month + 1));
            etYear.setText(String.valueOf(year));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private String getSelectedDate(EditText etDay, EditText etMonth, EditText etYear) {
        String d = etDay.getText().toString().trim();
        String m = etMonth.getText().toString().trim();
        String y = etYear.getText().toString().trim();
        if (d.length() == 1) d = "0" + d;
        if (m.length() == 1) m = "0" + m;
        return y + "-" + m + "-" + d;
    }

    private void addSubtaskRow() {
        View view = LayoutInflater.from(this).inflate(R.layout.item_add_subtask, containerSubtasks, false);
        ImageView btnRemove = view.findViewById(R.id.btnRemoveSubtask);
        btnRemove.setOnClickListener(v -> containerSubtasks.removeView(view));
        containerSubtasks.addView(view);
    }

    private List<String> getSubtaskListFromUI() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < containerSubtasks.getChildCount(); i++) {
            View view = containerSubtasks.getChildAt(i);
            EditText etContent = view.findViewById(R.id.etSubtaskContent);
            if (etContent != null) {
                String content = etContent.getText().toString().trim();
                if (!content.isEmpty()) list.add(content);
            }
        }
        return list;
    }
}