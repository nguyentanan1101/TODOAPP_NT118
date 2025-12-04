package com.example.todoapp.activity;

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

    // URL API
    private static final String UPDATE_SUBTASK_URL = "http://34.124.178.44:4000/api/subtask/";
    private static final String UPDATE_TASK_URL_BASE = "http://34.124.178.44:4000/api/tasks/";
    private static final String GET_SUBTASK_URL = "http://34.124.178.44:4000/api/subtask/task/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        initViews();

        // Nhận dữ liệu
        task = (TaskModel) getIntent().getSerializableExtra("TASK");
        if (task != null) {
            displayTaskData();
            fetchLatestSubTasks(task.getId());
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
        // --- SỬA LỖI HIỂN THỊ DESCRIPTION ---
        // Kiểm tra kỹ null, rỗng hoặc chuỗi "null"
        if (desc == null || desc.trim().isEmpty() || desc.equalsIgnoreCase("null")) {
            tvDescription.setText("No description provided.");
        } else {
            tvDescription.setText(desc);
        }

        if (task.getSubTasks() == null || task.getSubTasks().isEmpty()) {
            recyclerSubTasks.setVisibility(View.GONE);
            layoutManualDone.setVisibility(View.VISIBLE);
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

    // ... (Giữ nguyên handleSaveTask, fetchLatestSubTasks, updateTaskStatusToServer, updateSubTasksToServer) ...
    // Copy lại các hàm này từ code cũ, không thay đổi gì ở đây

    private void fetchLatestSubTasks(int taskId) {
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Request request = new Request.Builder().url(GET_SUBTASK_URL + taskId).addHeader("Authorization", "Bearer " + accessToken).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) { e.printStackTrace(); }
            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) return;
                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    JSONArray arr = obj.getJSONArray("subtasks");
                    List<SubTaskModel> subTasks = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject s = arr.getJSONObject(i);
                        int subId = s.optInt("subtask_id", 0);
                        String subTitle = s.optString("subtask_name", "");
                        String subStatus = s.optString("subtask_status", "");
                        if (subStatus.equals("null") || subStatus.isEmpty()) subStatus = "ToDo";
                        boolean done = subStatus.equalsIgnoreCase("Completed") || subStatus.equalsIgnoreCase("Done");
                        String dueDate = s.optString("due_date", "");
                        if(dueDate.contains("T")) dueDate = dueDate.split("T")[0];
                        subTasks.add(new SubTaskModel(subId, subTitle, "", dueDate, done));
                    }
                    task.setSubTasks(subTasks);
                    runOnUiThread(() -> {
                        // Cập nhật lại UI để subtask mới load cũng được gom nhóm đúng
                        displayTaskData();
                    });
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
                    if (!sub.isDone()) { allDone = false; break; }
                }
                String newTaskStatus = allDone ? "Done" : "Working";
                if (!allDone && isAllNew(updatedSubTasks)) newTaskStatus = "Working";
                task.setStatus(newTaskStatus);
                task.setDone(allDone);
                updateSubTasksToServer(updatedSubTasks);
                updateTaskStatusToServer(task.getId(), newTaskStatus);
            }
        } else {
            String statusToSend = task.isDone() ? "Done" : "Working";
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

    private void updateTaskStatusToServer(int taskId, String newStatus) {
        String accessToken = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("accessToken", "");
        Map<String, Object> map = new HashMap<>(); map.put("newStatus", newStatus); map.put("task_status", newStatus); map.put("status", newStatus);
        RequestBody body = RequestBody.create(gson.toJson(map), MediaType.get("application/json; charset=utf-8"));
        client.newCall(new Request.Builder().url(UPDATE_TASK_URL_BASE + taskId + "/status").addHeader("Authorization", "Bearer " + accessToken).patch(body).build()).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {}
            @Override public void onResponse(Call call, Response response) throws IOException {}
        });
    }

    private void updateSubTasksToServer(List<SubTaskModel> subTasks) {
        String accessToken = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("accessToken", "");
        for (SubTaskModel sub : subTasks) {
            try {
                JSONObject jsonBody = new JSONObject();
                String status = sub.isDone() ? "Done" : "Working";
                jsonBody.put("subtask_status", status); jsonBody.put("status", status); jsonBody.put("newStatus", status);
                RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));
                client.newCall(new Request.Builder().url(UPDATE_SUBTASK_URL + sub.getId() + "/status").addHeader("Authorization", "Bearer " + accessToken).patch(body).build()).enqueue(new Callback() {
                    @Override public void onFailure(Call call, IOException e) {}
                    @Override public void onResponse(Call call, Response response) throws IOException {}
                });
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    // --- LOGIC GOM NHÓM VÀ HIỂN THỊ TIÊU ĐỀ NHÓM (ĐÃ SỬA LỖI NULL) ---
    private void showGroupedSubTasks(List<SubTaskModel> subTasks) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        String todayStr = sdf.format(c.getTime());
        c.add(Calendar.DAY_OF_MONTH, 1);
        String tomorrowStr = sdf.format(c.getTime());

        Map<String, List<SubTaskModel>> map = new TreeMap<>((a, b) -> {
            // Logic sắp xếp nhóm: Today -> Tomorrow -> No due date -> Ngày khác
            if (a.equals(b)) return 0;
            if (a.equals("Today")) return -1; if (b.equals("Today")) return 1;
            if (a.equals("Tomorrow")) return -1; if (b.equals("Tomorrow")) return 1;
            if (a.equals("No Due Date")) return 1; if (b.equals("No Due Date")) return -1; // Đẩy No Due Date xuống cuối
            try { return sdf.parse(a).compareTo(sdf.parse(b)); } catch (ParseException e) { return a.compareTo(b); }
        });

        for (SubTaskModel sub : subTasks) {
            String due = sub.getDueDate();

            // --- SỬA LỖI HIỂN THỊ DUE DATE ---
            if (due == null || due.isEmpty() || due.equalsIgnoreCase("null")) {
                due = "No Due Date"; // Gán nhãn rõ ràng
            } else if (due.equals(todayStr)) {
                due = "Today";
            } else if (due.equals(tomorrowStr)) {
                due = "Tomorrow";
            }

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