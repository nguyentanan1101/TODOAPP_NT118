package com.example.todoapp.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.adapter.CalendarTaskAdapter;
import com.example.todoapp.models.SubTaskModel;
import com.example.todoapp.models.TaskModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private TextView tvSelectedDate, tvEmpty;
    private ImageView btnBack;

    private List<TaskModel> allTasks = new ArrayList<>(); // Chứa tất cả task tải về
    private List<TaskModel> filteredTasks = new ArrayList<>(); // Chứa task đã lọc theo ngày
    private CalendarTaskAdapter adapter;

    private String currentSelectedDate; // Ngày đang chọn (yyyy-MM-dd)

    private OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "http://163.61.110.132:4000/api/tasks/user-tasks";
    private static final String SUBTASK_URL = "http://163.61.110.132:4000/api/subtask/task/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        initViews();

        // Mặc định chọn ngày hôm nay
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentSelectedDate = sdf.format(new Date());
        tvSelectedDate.setText("Tasks for: " + currentSelectedDate);

        // Setup Adapter
        adapter = new CalendarTaskAdapter(this, filteredTasks, currentSelectedDate);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Sự kiện chọn ngày trên lịch
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // month bắt đầu từ 0 -> cần +1
            currentSelectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
            tvSelectedDate.setText("Tasks for: " + currentSelectedDate);

            // Cập nhật ngày cho adapter để nó biết lọc subtask nào
            adapter.updateDate(currentSelectedDate);

            // Lọc lại danh sách task cha
            filterTasksByDate();
        });

        // Tải dữ liệu
        fetchAllTasks();
    }

    private void initViews() {
        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recyclerCalendarTasks);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
    }

    // Logic lọc: Task cha được hiện nếu nó có DueDate trùng ngày chọn
    // HOẶC có ít nhất 1 Subtask có DueDate trùng ngày chọn
    private void filterTasksByDate() {
        filteredTasks.clear();

        for (TaskModel task : allTasks) {
            boolean showTask = false;

            // 1. Check ngày của Task cha (nếu có logic này)
            if (task.getCompletedDate() != null && task.getCompletedDate().equals(currentSelectedDate)) {
                // Logic này tùy bạn: hiện task due date hay task completed date?
                // Thường Calendar hiện DueDate. Giả sử TaskModel có getDueDate()
                // if (task.getDueDate().equals(currentSelectedDate)) showTask = true;
            }

            // 2. Check ngày của Subtask
            if (task.getSubTasks() != null) {
                for (SubTaskModel sub : task.getSubTasks()) {
                    if (sub.getDueDate() != null && sub.getDueDate().equals(currentSelectedDate)) {
                        showTask = true;
                        break;
                    }
                }
            }

            if (showTask) {
                filteredTasks.add(task);
            }
        }

        adapter.notifyDataSetChanged();

        if (filteredTasks.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void fetchAllTasks() {
        // ... (Code gọi API giống MainActivity, lấy token, user_id ...)
        // ... (Chỉ khác là sau khi parse xong JSON, lưu vào allTasks và gọi filterTasksByDate())

        // Ví dụ vắn tắt:
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");

        // ... Request ...

        // Trong onResponse:
        // ... Parse JSON -> tạo TaskModel -> add vào allTasks ...
        // ... Gọi loadSubTasks cho từng task ...

        // Sau khi load xong hết (hoặc trong quá trình load):
        // runOnUiThread(() -> filterTasksByDate());
    }

    // Bạn copy lại hàm fetchTasksFromAPI và loadSubTasks từ MainActivity qua đây,
    // thay đổi phần xử lý kết quả cuối cùng là gọi filterTasksByDate()
}