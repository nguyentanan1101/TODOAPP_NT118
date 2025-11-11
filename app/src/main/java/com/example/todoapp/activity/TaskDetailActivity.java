package com.example.todoapp.activity;

import android.content.Intent;
import android.os.Bundle;
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
    private static final String UPDATE_SUBTASK_URL = "http://163.61.110.132:4000/api/subtask/";
    private Gson gson = new Gson();

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
                    updateSubTasksToServer(updatedSubTasks); // cập nhật server

                    // Kiểm tra nếu tất cả subtasks Done
                    boolean allDone = true;
                    for (SubTaskModel sub : updatedSubTasks) {
                        if (!sub.isDone()) {
                            allDone = false;
                            break;
                        }
                    }
                    if (allDone) {
                        task.setStatus("Completed"); // đặt trạng thái Task là Completed
                    }
                }
            }

            // Trả task về MainActivity kèm trạng thái mới
            Intent intent = new Intent();
            intent.putExtra("UPDATED_TASK", task);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

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

    private void updateSubTasksToServer(List<SubTaskModel> subTasks) {
        String accessToken = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getString("accessToken", "");

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
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(TaskDetailActivity.this,
                            "Update subtask failed", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        runOnUiThread(() -> Toast.makeText(TaskDetailActivity.this,
                                "Update subtask failed", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        }
    }
}
