package com.example.todoapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TaskDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerSubTasks;
    private TextView tvTaskTitle, tvDescription;
    private ImageView btnBack, btnTick;

    private LinearLayout layoutManualDone;
    private CheckBox chkManualDone;

    private TaskModel task;
    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();

    // --- CẬP NHẬT URL API (BỎ /status) ---
    // Backend mới: PATCH /api/subtask/{id}
    private static final String UPDATE_SUBTASK_URL = "http://34.124.178.44:4000/api/subtask/";
    // Backend mới: PATCH /api/tasks/{id}
    private static final String UPDATE_TASK_URL = "http://34.124.178.44:4000/api/tasks/";

    // API GET vẫn giữ nguyên
    private static final String GET_SUBTASK_URL = "http://34.124.178.44:4000/api/subtask/task/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        initViews();

        // Nhận dữ liệu
        task = (TaskModel) getIntent().getSerializableExtra("TASK");
        if (task != null) {
            displayTaskData(); // Hiển thị dữ liệu cũ (cache)
            fetchLatestSubTasks(task.getId()); // Gọi API lấy dữ liệu mới nhất
        }

        btnBack.setOnClickListener(v -> finish());

        // --- XỬ LÝ KHI BẤM SAVE (TICK) ---
        btnTick.setOnClickListener(v -> handleSaveTask());
    }

    private void initViews() {
        tvTaskTitle = findViewById(R.id.tvTaskTitle);
        tvDescription = findViewById(R.id.tvDescription);
        btnBack = findViewById(R.id.btnBack);
        btnTick = findViewById(R.id.btnTick);
        recyclerSubTasks = findViewById(R.id.recyclerSubTasks);
        layoutManualDone = findViewById(R.id.layoutManualDone);
        chkManualDone = findViewById(R.id.chkManualDone);
    }

    private void displayTaskData() {
        tvTaskTitle.setText(task.getTitle());

        String desc = task.getDescription();
        tvDescription.setText((desc != null && !desc.isEmpty()) ? desc : "No description provided.");

        setupUIWithTaskData();
    }

    private void setupUIWithTaskData() {
        if (task.getSubTasks() == null || task.getSubTasks().isEmpty()) {
            recyclerSubTasks.setVisibility(View.GONE);
            layoutManualDone.setVisibility(View.VISIBLE);

            chkManualDone.setOnCheckedChangeListener(null);
            chkManualDone.setChecked(task.isDone());

            chkManualDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setDone(isChecked);
                task.setStatus(isChecked ? "Done" : "Working");
            });

        } else {
            recyclerSubTasks.setVisibility(View.VISIBLE);
            layoutManualDone.setVisibility(View.GONE);
            showGroupedSubTasks(task.getSubTasks());
        }
    }

    // --- FETCH DỮ LIỆU MỚI NHẤT TỪ SERVER ---
    private void fetchLatestSubTasks(int taskId) {
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");

        Request request = new Request.Builder()
                .url(GET_SUBTASK_URL + taskId)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) { e.printStackTrace(); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) return;

                try {
                    String res = response.body().string();
                    JSONObject obj = new JSONObject(res);

                    // Parse Subtasks
                    JSONArray arr = obj.optJSONArray("subtasks");
                    if (arr == null && obj.has("task")) {
                        arr = obj.getJSONObject("task").optJSONArray("subtasks");
                    }

                    List<SubTaskModel> subTasks = new ArrayList<>();
                    if (arr != null) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject s = arr.getJSONObject(i);
                            int subId = s.optInt("subtask_id", 0);
                            String subTitle = s.optString("title");
                            if(subTitle.isEmpty()) subTitle = s.optString("subtask_name", "");

                            // Parse Status
                            String subStatus = s.optString("status");
                            if (subStatus.isEmpty() || subStatus.equals("null")) subStatus = s.optString("subtask_status", "ToDo");

                            boolean done = subStatus.equalsIgnoreCase("Completed") || subStatus.equalsIgnoreCase("Done");

                            String dueDate = s.optString("due_date", "");
                            if(dueDate.contains("T")) dueDate = dueDate.split("T")[0];

                            subTasks.add(new SubTaskModel(subId, subTitle, "", dueDate, done));
                        }
                    }

                    // Cập nhật Model và UI
                    task.setSubTasks(subTasks);

                    // Parse Task Status (để cập nhật checkbox task cha nếu cần)
                    if (obj.has("task")) {
                        JSONObject t = obj.getJSONObject("task");
                        String tStatus = t.optString("status", "ToDo");
                        task.setDone(tStatus.equalsIgnoreCase("Done") || tStatus.equalsIgnoreCase("Completed"));
                    }

                    runOnUiThread(() -> setupUIWithTaskData());

                } catch (Exception e) { e.printStackTrace(); }
            }
        });
    }

    private void handleSaveTask() {
        if (task == null) return;

        boolean hasSubtasks = task.getSubTasks() != null && !task.getSubTasks().isEmpty();

        if (hasSubtasks) {
            SubTaskGroupAdapter adapter = (SubTaskGroupAdapter) recyclerSubTasks.getAdapter();
            if (adapter != null) {
                List<SubTaskModel> updatedSubTasks = adapter.getAllSubTasks();
                task.setSubTasks(updatedSubTasks);

                boolean allDone = true;
                for (SubTaskModel sub : updatedSubTasks) {
                    if (!sub.isDone()) {
                        allDone = false;
                        break;
                    }
                }

                String newTaskStatus = allDone ? "Done" : "Working";
                if (!allDone && isAllNew(updatedSubTasks)) newTaskStatus = "ToDo";

                task.setStatus(newTaskStatus);
                task.setDone(allDone);

                // Gọi API cập nhật
                updateSubTasksToServer(updatedSubTasks);
                updateTaskStatusToServer(task.getId(), newTaskStatus);
            }
        } else {
            String statusToSend = task.isDone() ? "Done" : "ToDo";
            updateTaskStatusToServer(task.getId(), statusToSend);
        }

        Intent intent = new Intent();
        intent.putExtra("UPDATED_TASK", task);
        setResult(RESULT_OK, intent);
        Toast.makeText(this, "Saved changes", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean isAllNew(List<SubTaskModel> subs) {
        for(SubTaskModel s : subs) if(s.isDone()) return false;
        return true;
    }

    // --- API UPDATE TASK CHA (SỬA URL) ---
    private void updateTaskStatusToServer(int taskId, String newStatus) {
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Map<String, Object> map = new HashMap<>();
        map.put("status", newStatus); // Key chính xác
        map.put("task_status", newStatus); // Backup

        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        // URL: /api/tasks/{id} (Bỏ /status)
        String url = UPDATE_TASK_URL + taskId;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .patch(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {}
            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) Log.e("API_ERR", "Task update failed: " + response.code());
            }
        });
    }

    // --- API UPDATE SUBTASKS (SỬA URL) ---
    private void updateSubTasksToServer(List<SubTaskModel> subTasks) {
        String accessToken = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("accessToken", "");
        for (SubTaskModel sub : subTasks) {
            try {
                JSONObject jsonBody = new JSONObject();
                String status = sub.isDone() ? "Done" : "ToDo";

                // Gửi key chuẩn: "status"
                jsonBody.put("status", status);
                jsonBody.put("subtask_status", status); // Backup
                jsonBody.put("newStatus", status); // Backup code cũ

                RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));

                // URL: /api/subtask/{id} (Bỏ /status)
                String url = UPDATE_SUBTASK_URL + sub.getId();

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .patch(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override public void onFailure(Call call, IOException e) {}
                    @Override public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) Log.e("API_ERR", "Subtask update failed: " + response.code());
                    }
                });
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    // ... (showGroupedSubTasks giữ nguyên) ...
    private void showGroupedSubTasks(List<SubTaskModel> subTasks) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        String todayStr = sdf.format(c.getTime());
        c.add(Calendar.DAY_OF_MONTH, 1);
        String tomorrowStr = sdf.format(c.getTime());

        Map<String, List<SubTaskModel>> map = new TreeMap<>((a, b) -> {
            if (a.equals(b)) return 0;
            if (a.equals("Today")) return -1; if (b.equals("Today")) return 1;
            if (a.equals("Tomorrow")) return -1; if (b.equals("Tomorrow")) return 1;
            if (a.equals("No Due Date")) return 1; if (b.equals("No Due Date")) return -1;
            try { return sdf.parse(a).compareTo(sdf.parse(b)); } catch (ParseException e) { return a.compareTo(b); }
        });

        for (SubTaskModel sub : subTasks) {
            String due = sub.getDueDate();
            if (due == null || due.isEmpty() || due.equalsIgnoreCase("null")) due = "No Due Date";
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