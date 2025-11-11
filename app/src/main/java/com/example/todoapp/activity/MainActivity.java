package com.example.todoapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.adapter.TaskAdapter;
import com.example.todoapp.models.SubTaskModel;
import com.example.todoapp.models.TaskModel;
import com.example.todoapp.utils.BottomNavHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout labelDots;
    private Button btnToday, btnPersonal, btnWork;
    private List<TaskModel> taskList = new ArrayList<>();
    private TaskAdapter adapter;
    private OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "http://163.61.110.132:4000/api/tasks/user-tasks";
    private static final String SUBTASK_URL = "http://163.61.110.132:4000/api/subtask/task/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavHelper.setupBottomNav(this);

        recyclerView = findViewById(R.id.recyclerTasks);
        labelDots = findViewById(R.id.labelDots);
        btnToday = findViewById(R.id.btnToday);
        btnPersonal = findViewById(R.id.btnPersonal);
        btnWork = findViewById(R.id.btnWork);

        initButtons();

        adapter = new TaskAdapter(this, taskList);
        adapter.setOnTaskClickListener(task -> {
            Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
            intent.putExtra("TASK", task);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        fetchTasksFromAPI();
    }

    private void initButtons() {
        btnToday.setOnClickListener(v -> { selectButton(btnToday); filterTasks("TODAY"); });
        btnPersonal.setOnClickListener(v -> { selectButton(btnPersonal); filterTasks("PERSONAL"); });
        btnWork.setOnClickListener(v -> { selectButton(btnWork); filterTasks("WORK"); });
    }

    private void selectButton(Button selectedButton) {
        btnToday.setSelected(false);
        btnPersonal.setSelected(false);
        btnWork.setSelected(false);
        selectedButton.setSelected(true);
    }

    private void fetchTasksFromAPI() {
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
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Cannot connect to server", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to load tasks", Toast.LENGTH_SHORT).show());
                    return;
                }

                try {
                    String res = response.body().string();
                    JSONObject obj = new JSONObject(res);
                    JSONArray arr = obj.getJSONArray("tasks");
                    taskList.clear();

                    String token = sp.getString("accessToken", "");

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject t = arr.getJSONObject(i);
                        int taskId = t.optInt("task_id", 0);
                        String title = t.optString("task_name", "");
                        String statusStr = t.optString("task_status", "ToDo");

                        TaskModel.TaskType type;
                        if (statusStr.equalsIgnoreCase("ToDo")) type = TaskModel.TaskType.PERSONAL;
                        else if (statusStr.equalsIgnoreCase("Working")) type = TaskModel.TaskType.WORK_PRIVATE;
                        else type = TaskModel.TaskType.WORK_GROUP;

                        TaskModel task = new TaskModel(title, type, new ArrayList<>());
                        if (statusStr.equalsIgnoreCase("Completed")) task.setDone(true);

                        taskList.add(task);
                        loadSubTasks(task, taskId, token);
                    }

                    runOnUiThread(() -> adapter.notifyDataSetChanged());

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error parsing tasks", Toast.LENGTH_SHORT).show());
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
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

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
                        String subDesc = s.optString("subtask_description", "");
                        String dueDate = s.optString("due_date", "");
                        boolean done = s.optString("subtask_status", "").equalsIgnoreCase("Completed");

                        subTasks.add(new SubTaskModel(subId, subTitle, subDesc, dueDate, done));
                    }

                    task.setSubTasks(subTasks);

                    runOnUiThread(() -> adapter.notifyDataSetChanged());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void filterTasks(String filterType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = sdf.format(new Date());

        if (filterType.equals("TODAY")) labelDots.setVisibility(View.VISIBLE);
        else labelDots.setVisibility(View.GONE);

        List<TaskModel> filtered = new ArrayList<>();

        for (TaskModel t : taskList) {
            switch (filterType) {
                case "TODAY":
                    for (SubTaskModel sub : t.getSubTasks()) {
                        if (sub.getDueDate() != null && sub.getDueDate().equals(todayStr)) {
                            filtered.add(t);
                            break;
                        }
                    }
                    break;
                case "PERSONAL":
                    if (t.getType() == TaskModel.TaskType.PERSONAL) filtered.add(t);
                    break;
                case "WORK":
                    if (t.getType() != TaskModel.TaskType.PERSONAL) filtered.add(t);
                    break;
            }
        }

        adapter.setTasks(filtered);
        adapter.notifyDataSetChanged();
    }
}
