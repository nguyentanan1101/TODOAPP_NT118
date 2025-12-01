package com.example.todoapp.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();
        setupListeners();
        setupSpinners();

        setupDateInputValidation(etStartDay, etStartMonth, etStartYear);
        setupDateInputValidation(etEndDay, etEndMonth, etEndYear);

    }

    private void setupDateInputValidation(EditText etDay, EditText etMonth, EditText etYear) {
        etMonth.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String val = etMonth.getText().toString().trim();
                if (!val.isEmpty()) {
                    int m = Integer.parseInt(val);
                    if (m < 1) m = 1;
                    if (m > 12) m = 12;
                    etMonth.setText(String.format(Locale.getDefault(), "%02d", m));

                    validateDay(etDay, etMonth, etYear);
                }
            }
        });

        etDay.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateDay(etDay, etMonth, etYear);
            }
        });

        etYear.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String val = etYear.getText().toString().trim();
                if (!val.isEmpty() && val.length() == 4) {
                    validateDay(etDay, etMonth, etYear);
                }
            }
        });
    }

    private void validateDay(EditText etDay, EditText etMonth, EditText etYear) {
        String dStr = etDay.getText().toString().trim();
        String mStr = etMonth.getText().toString().trim();
        String yStr = etYear.getText().toString().trim();

        if (dStr.isEmpty() || mStr.isEmpty() || yStr.isEmpty()) return;

        int d = Integer.parseInt(dStr);
        int m = Integer.parseInt(mStr);
        int y = Integer.parseInt(yStr);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, y);
        cal.set(Calendar.MONTH, m - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (d < 1) d = 1;
        if (d > maxDay) d = maxDay;

        etDay.setText(String.format(Locale.getDefault(), "%02d", d));
    }

    private void setDefaultDate() {
        Calendar today = Calendar.getInstance();
        String currentDay = String.format(Locale.getDefault(), "%02d", today.get(Calendar.DAY_OF_MONTH));
        String currentMonth = String.format(Locale.getDefault(), "%02d", today.get(Calendar.MONTH) + 1);
        String currentYear = String.valueOf(today.get(Calendar.YEAR));

        etStartDay.setText(currentDay); etStartMonth.setText(currentMonth); etStartYear.setText(currentYear);
        etEndDay.setText(currentDay); etEndMonth.setText(currentMonth); etEndYear.setText(currentYear);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        etTitle = findViewById(R.id.etTitle);
        etNotes = findViewById(R.id.etNotes);

        btnPickStartDate = findViewById(R.id.btnPickStartDate);
        btnPickEndDate = findViewById(R.id.btnPickEndDate);

        etStartDay = findViewById(R.id.etStartDay); etStartMonth = findViewById(R.id.etStartMonth); etStartYear = findViewById(R.id.etStartYear);
        etEndDay = findViewById(R.id.etEndDay); etEndMonth = findViewById(R.id.etEndMonth); etEndYear = findViewById(R.id.etEndYear);

        containerSubtasks = findViewById(R.id.containerSubtasks);
        btnAddSubtask = findViewById(R.id.btnAddSubtask);

        spinnerReminder = findViewById(R.id.spinnerReminder);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
    }

    private void setupSpinners() {
        String[] reminderOptions = {"None", "5 minutes before", "10 minutes before", "30 minutes before", "1 hour before", "1 day before", "Custom"};
        ArrayAdapter<String> reminderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reminderOptions);
        reminderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReminder.setAdapter(reminderAdapter);

        spinnerReminder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedReminderOption = reminderOptions[position];
                if (selectedReminderOption.equals("Custom")) {
                    showCustomReminderPicker();
                } else {
                    tvSelectedTime.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        String[] priorityOptions = {"Low", "Medium", "High", "Critical"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorityOptions);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        spinnerPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPriority = priorityOptions[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void showCustomReminderPicker() {
        DatePickerDialog dateDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            customReminderCal.set(Calendar.YEAR, year);
            customReminderCal.set(Calendar.MONTH, month);
            customReminderCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            showTimePickerForCustom();
        }, customReminderCal.get(Calendar.YEAR), customReminderCal.get(Calendar.MONTH), customReminderCal.get(Calendar.DAY_OF_MONTH));

        dateDialog.setOnCancelListener(dialog -> spinnerReminder.setSelection(0));
        dateDialog.show();
    }

    private void showTimePickerForCustom() {
        TimePickerDialog timeDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            customReminderCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            customReminderCal.set(Calendar.MINUTE, minute);
            customReminderCal.set(Calendar.SECOND, 0);
            updateCustomTimeText();
        }, customReminderCal.get(Calendar.HOUR_OF_DAY), customReminderCal.get(Calendar.MINUTE), true);

        timeDialog.setOnCancelListener(dialog -> spinnerReminder.setSelection(0));
        timeDialog.show();
    }

    private void updateCustomTimeText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedTime = sdf.format(customReminderCal.getTime());
        tvSelectedTime.setText("Alarm: " + formattedTime + " (Tap to edit)");
        tvSelectedTime.setVisibility(View.VISIBLE);
    }

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

            validateDay(etStartDay, etStartMonth, etStartYear);
            validateDay(etEndDay, etEndMonth, etEndYear);

            String startDate = getSelectedDate(etStartDay, etStartMonth, etStartYear);
            String endDate = getSelectedDate(etEndDay, etEndMonth, etEndYear);
            List<String> subtasks = getSubtaskListFromUI();

            String finalReminderValue = selectedReminderOption;
            if (selectedReminderOption.equals("Custom")) {
                SimpleDateFormat sdfSave = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                finalReminderValue = sdfSave.format(customReminderCal.getTime());
            }

            String msg = "Saved!\n" +
                    "Title: " + title + "\n" +
                    "Start: " + startDate + "\n" +
                    "End: " + endDate;

            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void showDatePicker(Calendar calendar, EditText etDay, EditText etMonth, EditText etYear) {
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            etDay.setText(String.format(Locale.getDefault(), "%02d", dayOfMonth));
            etMonth.setText(String.format(Locale.getDefault(), "%02d", month + 1));
            etYear.setText(String.valueOf(year));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private String getSelectedDate(EditText etDay, EditText etMonth, EditText etYear) {
        String d = etDay.getText().toString().trim();
        String m = etMonth.getText().toString().trim();
        String y = etYear.getText().toString().trim();
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