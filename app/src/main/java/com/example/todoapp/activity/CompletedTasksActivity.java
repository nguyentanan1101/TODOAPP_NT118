package com.example.todoapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.adapter.TaskAdapter;
import com.example.todoapp.models.SubTaskModel;
import com.example.todoapp.models.TaskModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CompletedTasksActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private TaskAdapter adapter;
    private List<TaskModel> completedTaskList = new ArrayList<>();

    private OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "http://34.124.178.44:4000/api/tasks/user-tasks";
    private static final String SUBTASK_URL = "http://34.124.178.44:4000/api/subtask/task/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_tasks);

        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerCompletedTasks);
        tvEmpty = findViewById(R.id.tvEmpty);

        btnBack.setOnClickListener(v -> finish());

        adapter = new TaskAdapter(this, completedTaskList);

        adapter.setOnTaskClickListener(task -> {
            Intent intent = new Intent(CompletedTasksActivity.this, TaskDetailActivity.class);
            intent.putExtra("TASK", task);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        fetchCompletedTasks();
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "";
        try {
            if (isoDate.contains("T")) {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                Date date = isoFormat.parse(isoDate);
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                return displayFormat.format(date);
            } else {
                SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = simpleFormat.parse(isoDate);
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                return displayFormat.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return isoDate;
        }
    }

    private void fetchCompletedTasks() {
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");

        // 1. LẤY USER ID HIỆN TẠI ĐỂ SO SÁNH
        String currentUserIdStr = sp.getString("user_id", "0");
        int currentUserId = Integer.parseInt(currentUserIdStr);

        Request request = new Request.Builder()
                .url(BASE_URL)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(CompletedTasksActivity.this, "Connection Error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("API_ERROR", "Code: " + response.code());
                    return;
                }

                try {
                    String res = response.body().string();
                    JSONObject obj = new JSONObject(res);
                    JSONArray arr = obj.getJSONArray("tasks");

                    completedTaskList.clear();
                    String token = sp.getString("accessToken", "");

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject t = arr.getJSONObject(i);
                        int taskId = t.optInt("task_id", 0);
                        String title = t.optString("task_name", "");
                        String statusStr = t.optString("task_status", "").trim();

                        boolean isDone = statusStr.equalsIgnoreCase("Completed") || statusStr.equalsIgnoreCase("Done");

                        if (isDone) {
                            // --- 2. SỬA LOGIC PHÂN LOẠI TẠI ĐÂY ---
                            int createdBy = t.optInt("created_by", 0);
                            TaskModel.TaskType type;

                            if (createdBy == currentUserId) {
                                // Do mình tạo -> PERSONAL (Màu xanh lá)
                                type = TaskModel.TaskType.PERSONAL;
                            } else {
                                // Do người khác tạo -> WORK (Màu cam/vàng)
                                type = TaskModel.TaskType.WORK_GROUP; // hoặc WORK_PRIVATE
                            }

                            // Lấy thêm priority từ JSON
                            String priority = t.optString("priority", "Low");

                            // Truyền priority vào Constructor (tham số cuối cùng)
                            TaskModel task = new TaskModel(taskId, title, type, new ArrayList<>(), priority);
                            task.setDone(true);

                            String rawDate = t.optString("updated_at", "");
                            if (rawDate.isEmpty() || rawDate.equals("null")) {
                                rawDate = t.optString("due_date", "");
                            }
                            task.setCompletedDate(formatDate(rawDate));

                            completedTaskList.add(task);
                            loadSubTasks(task, taskId, token);
                        }
                    }

                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        if (completedTaskList.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            tvEmpty.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadSubTasks(TaskModel task, int taskId, String accessToken) {
        Request request = new Request.Builder()
                .url(SUBTASK_URL + taskId)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) return;
                try {
                    String res = response.body().string();
                    JSONObject obj = new JSONObject(res);
                    JSONArray arr = obj.getJSONArray("subtasks");
                    List<SubTaskModel> subTasks = new ArrayList<>();

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject s = arr.getJSONObject(i);
                        int subId = s.optInt("subtask_id", 0);
                        String subTitle = s.optString("subtask_name", "");

                        String subStatus = s.optString("subtask_status", "").trim();
                        boolean done = subStatus.equalsIgnoreCase("Completed") || subStatus.equalsIgnoreCase("Done");

                        subTasks.add(new SubTaskModel(subId, subTitle, "", "", done));
                    }
                    task.setSubTasks(subTasks);
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                } catch (Exception e) { e.printStackTrace(); }
            }
        });
    }
}