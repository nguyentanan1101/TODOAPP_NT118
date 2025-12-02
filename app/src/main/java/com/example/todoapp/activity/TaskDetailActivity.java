package com.example.todoapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.adapter.SubTaskGroupAdapter;
import com.example.todoapp.models.SubTaskGroup;
import com.example.todoapp.models.SubTaskModel;
import com.example.todoapp.models.TaskModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger; // Thêm import này

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TaskDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerSubTasks;
    private TextView tvTaskTitle;
    private ImageView btnBack, btnTick;
    private TaskModel task;

    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();

    private static final String UPDATE_SUBTASK_URL = "http://34.124.178.44:4000/api/subtask/";
    private static final String UPDATE_TASK_URL_BASE = "http://34.124.178.44:4000/api/tasks/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        tvTaskTitle = findViewById(R.id.tvTaskTitle);
        btnBack = findViewById(R.id.btnBack);
        btnTick = findViewById(R.id.btnTick);
        recyclerSubTasks = findViewById(R.id.recyclerSubTasks);

        btnBack.setOnClickListener(v -> finish());

        task = (TaskModel) getIntent().getSerializableExtra("TASK");
        if (task != null) {
            tvTaskTitle.setText(task.getTitle());
            showGroupedSubTasks(task.getSubTasks());
        }

        btnTick.setOnClickListener(v -> {
            if (task != null) {
                SubTaskGroupAdapter adapter = (SubTaskGroupAdapter) recyclerSubTasks.getAdapter();
                if (adapter != null) {
                    List<SubTaskModel> updatedSubTasks = adapter.getAllSubTasks();
                    task.setSubTasks(updatedSubTasks);

                    // Logic kiểm tra hoàn thành
                    boolean allDone = true;
                    if (updatedSubTasks.isEmpty()) allDone = false;
                    for (SubTaskModel sub : updatedSubTasks) {
                        if (!sub.isDone()) {
                            allDone = false;
                            break;
                        }
                    }

                    // Cập nhật trạng thái Task cha Local
                    String newTaskStatus = allDone ? "Done" : "Working";
                    task.setStatus(newTaskStatus);
                    task.setDone(allDone);

                    // --- GỬI API CẬP NHẬT ---
                    // 1. Cập nhật Subtasks trước
                    updateSubTasksToServer(updatedSubTasks);

                    // 2. Cập nhật Task cha sau
                    updateTaskStatusToServer(task.getId(), newTaskStatus);
                }
            }

            Intent intent = new Intent();
            intent.putExtra("UPDATED_TASK", task);
            setResult(RESULT_OK, intent);
            Toast.makeText(this, "Saved changes", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void updateTaskStatusToServer(int taskId, String newStatus) {
        String accessToken = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("accessToken", "");
        Map<String, Object> map = new HashMap<>();
        map.put("newStatus", newStatus); // Key backend yêu cầu

        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        String url = UPDATE_TASK_URL_BASE + taskId + "/status";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .patch(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace(); }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("API_ERROR", "Task Update Failed: " + response.code());
                }
            }
        });
    }

    private void updateSubTasksToServer(List<SubTaskModel> subTasks) {
        String accessToken = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("accessToken", "");

        // Gửi song song các request (đơn giản nhưng hiệu quả với số lượng subtask ít)
        for (SubTaskModel sub : subTasks) {
            Map<String, Object> map = new HashMap<>();
            map.put("newStatus", sub.isDone() ? "Done" : "Working");

            String json = gson.toJson(map);
            RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(UPDATE_SUBTASK_URL + sub.getId() + "/status")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .patch(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) { e.printStackTrace(); }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(!response.isSuccessful()) Log.e("API_SUB_ERROR", "Subtask " + sub.getId() + " failed");
                }
            });
        }
    }

    // ... (Phần showGroupedSubTasks giữ nguyên không đổi) ...
    private void showGroupedSubTasks(List<SubTaskModel> subTasks) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        String todayStr = sdf.format(c.getTime());
        c.add(Calendar.DAY_OF_MONTH, 1);
        String tomorrowStr = sdf.format(c.getTime());

        Map<String, List<SubTaskModel>> map = new TreeMap<>((a, b) -> {
            if (a.equals(b)) return 0;
            if (a.equals("Today")) return -1;
            if (b.equals("Today")) return 1;
            if (a.equals("Tomorrow")) return -1;
            if (b.equals("Tomorrow")) return 1;
            if (a.equals("No due date")) return 1;
            if (b.equals("No due date")) return -1;
            try {
                Date da = sdf.parse(a);
                Date db = sdf.parse(b);
                return da.compareTo(db);
            } catch (ParseException e) {
                return a.compareTo(b);
            }
        });

        for (SubTaskModel sub : subTasks) {
            String due = sub.getDueDate();
            if (due == null || due.isEmpty()) due = "No due date";
            else if (due.equals(todayStr)) due = "Today";
            else if (due.equals(tomorrowStr)) due = "Tomorrow";

            map.computeIfAbsent(due, k -> new ArrayList<>()).add(sub);
        }

        List<SubTaskGroup> groups = new ArrayList<>();
        for (Map.Entry<String, List<SubTaskModel>> e : map.entrySet()) {
            groups.add(new SubTaskGroup(e.getKey(), e.getValue()));
        }

        recyclerSubTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerSubTasks.setAdapter(new SubTaskGroupAdapter(this, groups));
    }
}