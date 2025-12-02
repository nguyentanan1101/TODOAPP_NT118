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
import java.text.SimpleDateFormat; // Import này cần thiết
import java.util.ArrayList;
import java.util.Date; // Import này cần thiết
import java.util.List;
import java.util.Locale; // Import này cần thiết

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
    private static final String BASE_URL = "http://163.61.110.132:4000/api/tasks/user-tasks";
    private static final String SUBTASK_URL = "http://163.61.110.132:4000/api/subtask/task/";

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

    // --- HÀM MỚI: FORMAT NGÀY GIỜ ---
    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "";
        try {
            // Trường hợp 1: Server trả về ISO 8601 (có chữ T) -> 2025-11-30T10:00:00.000Z
            if (isoDate.contains("T")) {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                Date date = isoFormat.parse(isoDate);
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                return displayFormat.format(date);
            }
            // Trường hợp 2: Server trả về yyyy-MM-dd -> 2025-11-30
            else {
                SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = simpleFormat.parse(isoDate);
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                return displayFormat.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return isoDate; // Nếu lỗi format thì trả về nguyên gốc
        }
    }

    private void fetchCompletedTasks() {
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");

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
                            TaskModel.TaskType type = TaskModel.TaskType.PERSONAL;

                            TaskModel task = new TaskModel(taskId, title, type, new ArrayList<>());
                            task.setDone(true);

                            // --- CẬP NHẬT: Lấy ngày update và format lại ---
                            // Ưu tiên lấy updated_at (ngày hoàn thành thực tế), nếu không có thì lấy due_date
                            String rawDate = t.optString("updated_at", "");
                            if (rawDate.isEmpty() || rawDate.equals("null")) {
                                rawDate = t.optString("due_date", "");
                            }

                            // Gọi hàm format trước khi set vào model
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