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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager; // Đổi sang Staggered

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
    // Cập nhật IP server cho đồng bộ
    private static final String BASE_URL = "http://34.124.178.44:4000/api/tasks/created-by-me";
    private static final String SUBTASK_URL = "http://34.124.178.44:4000/api/subtask/task/";
    // API xóa task (nếu cần)
    private static final String DELETE_TASK_URL = "http://34.124.178.44:4000/api/tasks/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_tasks);

        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerCompletedTasks);
        tvEmpty = findViewById(R.id.tvEmpty);

        btnBack.setOnClickListener(v -> finish());

        adapter = new TaskAdapter(this, completedTaskList);
        adapter.setCompletedScreen(true);

        // 1. Sự kiện xem chi tiết
        adapter.setOnTaskClickListener(task -> {
            Intent intent = new Intent(CompletedTasksActivity.this, TaskDetailActivity.class);
            intent.putExtra("TASK", task);
            startActivity(intent);
        });

        // 2. Sự kiện xóa task
        adapter.setOnDeleteClickListener((task, position) -> {
            deleteTaskFromServer(task.getId(), position);
        });

        // Dùng StaggeredGridLayoutManager để hiển thị đẹp hơn (giống Main)
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        fetchCompletedTasks();
    }

    // Hàm xóa task
    private void deleteTaskFromServer(int taskId, int position) {
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");

        Request request = new Request.Builder()
                .url(DELETE_TASK_URL + taskId)
                .addHeader("Authorization", "Bearer " + accessToken)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(CompletedTasksActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(CompletedTasksActivity.this, "Đã xóa task", Toast.LENGTH_SHORT).show();
                        adapter.removeItem(position);
                        if (adapter.getItemCount() == 0) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty() || isoDate.equals("null")) return "Unknown";
        try {
            SimpleDateFormat isoFormat;
            if (isoDate.contains("T")) {
                isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            } else {
                isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            }
            Date date = isoFormat.parse(isoDate);
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return displayFormat.format(date);
        } catch (Exception e) {
            return isoDate;
        }
    }

    private void fetchCompletedTasks() {
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");

        String currentUserIdStr = sp.getString("user_id", "0");
        int currentUserId = 0;
        try { currentUserId = Integer.parseInt(currentUserIdStr); } catch (Exception e) {}
        final int myId = currentUserId;

        Request request = new Request.Builder()
                .url(BASE_URL)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(CompletedTasksActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) return;

                try {
                    String res = response.body().string();
                    JSONObject obj = new JSONObject(res);
                    JSONArray arr = obj.getJSONArray("tasks");

                    completedTaskList.clear();
                    String token = sp.getString("accessToken", "");

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject t = arr.getJSONObject(i);

                        // Xử lý Status (Check null)
                        String statusStr = t.optString("task_status");
                        if (statusStr == null || statusStr.isEmpty() || statusStr.equals("null")) statusStr = t.optString("status");
                        if (statusStr == null || statusStr.isEmpty() || statusStr.equals("null")) statusStr = "ToDo";

                        boolean isDone = statusStr.equalsIgnoreCase("Completed") || statusStr.equalsIgnoreCase("Done");

                        if (isDone) {
                            int taskId = t.optInt("task_id", 0);

                            // Xử lý Title
                            String title = t.optString("task_name");
                            if (title.isEmpty()) title = t.optString("title", "No Title");

                            // Phân loại Task
                            int createdBy = t.optInt("created_by", 0);
                            int projectId = t.optInt("project_id", 0);
                            TaskModel.TaskType type;
                            if (projectId > 0) type = TaskModel.TaskType.WORK_GROUP;
                            else {
                                if (createdBy == myId) type = TaskModel.TaskType.PERSONAL;
                                else type = TaskModel.TaskType.WORK_PRIVATE;
                            }

                            String priority = t.optString("priority", "Low");

                            // Tạo Model
                            TaskModel task = new TaskModel(taskId, title, type, new ArrayList<>(), priority);
                            task.setDone(true);

                            // Lấy ngày hoàn thành
                            String rawDate = t.optString("updated_at", "");
                            if (rawDate.isEmpty() || rawDate.equals("null")) rawDate = t.optString("due_date", "");
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

                } catch (Exception e) { e.printStackTrace(); }
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
                        if (subTitle.isEmpty()) subTitle = s.optString("title", "");

                        String subStatus = s.optString("subtask_status", "");
                        if (subStatus.equals("null") || subStatus.isEmpty()) subStatus = "ToDo";
                        boolean done = subStatus.equalsIgnoreCase("Completed") || subStatus.equalsIgnoreCase("Done");

                        // Lấy ngày subtask
                        String dueDate = s.optString("due_date", "");
                        if(dueDate.contains("T")) dueDate = dueDate.split("T")[0];

                        subTasks.add(new SubTaskModel(subId, subTitle, "", dueDate, done));
                    }
                    task.setSubTasks(subTasks);
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                } catch (Exception e) { e.printStackTrace(); }
            }
        });
    }
}