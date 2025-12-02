package com.example.todoapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvTitleCompleteTasks;
    private LinearLayout labelDots;
    private Button btnToday, btnPersonal, btnWork;
    private ImageView btnSearch, btnNotification, btnCloseSearch, btnAdd;
    private LinearLayout layoutSearchInput;
    private EditText etSearch;

    private List<TaskModel> taskList = new ArrayList<>();
    private List<TaskModel> originalTaskList = new ArrayList<>();

    private TaskAdapter adapter;
    private OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "http://163.61.110.132:4000/api/tasks/user-tasks";
    private static final String SUBTASK_URL = "http://163.61.110.132:4000/api/subtask/task/";

    private String currentFilter = "TODAY";
    private int totalTasksToLoad = 0;
    private AtomicInteger tasksLoadedCount = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavHelper.setupBottomNav(this);

        // Ánh xạ View
        recyclerView = findViewById(R.id.recyclerTasks);
        labelDots = findViewById(R.id.labelDots);
        btnToday = findViewById(R.id.btnToday);
        btnPersonal = findViewById(R.id.btnPersonal);
        btnWork = findViewById(R.id.btnWork);
        btnAdd = findViewById(R.id.btnAdd);
        tvTitleCompleteTasks = findViewById(R.id.tvTitleCompleteTasks);
        btnSearch = findViewById(R.id.btnSearch);
        btnNotification = findViewById(R.id.btnNotification);
        layoutSearchInput = findViewById(R.id.layoutSearchInput);
        etSearch = findViewById(R.id.etSearch);
        btnCloseSearch = findViewById(R.id.btnCloseSearch);

        initButtons();
        setupSearchFunctionality();

        // Setup RecyclerView
        adapter = new TaskAdapter(this, taskList);
        adapter.setOnTaskClickListener(task -> {
            Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
            intent.putExtra("TASK", task);
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        // Các sự kiện chuyển màn hình
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });

        btnNotification.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        tvTitleCompleteTasks.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CompletedTasksActivity.class);
            startActivity(intent);
        });

        selectButton(btnToday);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchTasksFromAPI();
    }

    private void initButtons() {
        btnToday.setOnClickListener(v -> {
            currentFilter = "TODAY";
            selectButton(btnToday);
            filterTasks(currentFilter);
        });
        btnPersonal.setOnClickListener(v -> {
            currentFilter = "PERSONAL";
            selectButton(btnPersonal);
            filterTasks(currentFilter);
        });
        btnWork.setOnClickListener(v -> {
            currentFilter = "WORK";
            selectButton(btnWork);
            filterTasks(currentFilter);
        });
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

        // --- 1. LẤY ID CỦA NGƯỜI DÙNG HIỆN TẠI ---
        String currentUserIdStr = sp.getString("user_id", "0"); // Mặc định 0 nếu lỗi
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

                    originalTaskList.clear();
                    totalTasksToLoad = 0;
                    tasksLoadedCount.set(0);

                    runOnUiThread(() -> {
                        taskList.clear();
                        adapter.notifyDataSetChanged();
                    });

                    String token = sp.getString("accessToken", "");

                    // Lọc sơ bộ: Chỉ lấy Task chưa hoàn thành
                    List<JSONObject> validTasks = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject t = arr.getJSONObject(i);
                        String statusStr = t.optString("task_status", "");

                        if (statusStr.equalsIgnoreCase("Working") ||
                                statusStr.equalsIgnoreCase("ToDo")) {
                            validTasks.add(t);
                        }
                    }

                    totalTasksToLoad = validTasks.size();
                    if (totalTasksToLoad == 0) return;

                    for (JSONObject t : validTasks) {
                        int taskId = t.optInt("task_id", 0);
                        String title = t.optString("task_name", "");
                        int createdBy = t.optInt("created_by", 0); // Lấy người tạo task

                        // --- 2. LOGIC MỚI: PHÂN LOẠI THEO CREATED_BY ---
                        TaskModel.TaskType type;
                        if (createdBy == currentUserId) {
                            // Do mình tạo -> PERSONAL
                            type = TaskModel.TaskType.PERSONAL;
                        } else {
                            // Do người khác tạo -> WORK (Công việc được giao)
                            // Ở đây tôi dùng WORK_PRIVATE làm đại diện cho Work
                            type = TaskModel.TaskType.WORK_PRIVATE;
                        }

                        TaskModel task = new TaskModel(taskId, title, type, new ArrayList<>());

                        String statusStr = t.optString("task_status", "ToDo");
                        boolean isDone = statusStr.equalsIgnoreCase("Completed") || statusStr.equalsIgnoreCase("Done");
                        task.setDone(isDone);

                        originalTaskList.add(task);
                        loadSubTasks(task, taskId, token);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // ... (Các phần loadSubTasks, checkAndRefreshUI giữ nguyên) ...
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
                checkAndRefreshUI();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
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
                            String subStatus = s.optString("subtask_status", "");
                            boolean done = subStatus.equalsIgnoreCase("Completed") || subStatus.equalsIgnoreCase("Done");
                            subTasks.add(new SubTaskModel(subId, subTitle, subDesc, dueDate, done));
                        }
                        task.setSubTasks(subTasks);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    checkAndRefreshUI();
                }
            }
        });
    }

    private void checkAndRefreshUI() {
        int currentCount = tasksLoadedCount.incrementAndGet();
        if (currentCount == totalTasksToLoad) {
            runOnUiThread(() -> filterTasks(currentFilter));
        }
    }

    private void filterTasks(String filterType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayStr = sdf.format(new Date());

        if (filterType.equals("TODAY")) labelDots.setVisibility(View.VISIBLE);
        else labelDots.setVisibility(View.GONE);

        List<TaskModel> filtered = new ArrayList<>();

        for (TaskModel t : originalTaskList) {
            if (t.isDone()) continue;

            switch (filterType) {
                case "TODAY":
                    // Logic lọc Today giữ nguyên (dựa vào subtask)
                    if(t.getSubTasks() != null) {
                        for (SubTaskModel sub : t.getSubTasks()) {
                            if (sub.getDueDate() != null && sub.getDueDate().equals(todayStr)) {
                                filtered.add(t);
                                break;
                            }
                        }
                    }
                    break;

                case "PERSONAL":
                    // Chỉ lấy loại PERSONAL
                    if (t.getType() == TaskModel.TaskType.PERSONAL) {
                        filtered.add(t);
                    }
                    break;

                case "WORK":
                    // Lấy tất cả các loại WORK (không phải Personal)
                    if (t.getType() != TaskModel.TaskType.PERSONAL) {
                        filtered.add(t);
                    }
                    break;
            }
        }
        adapter.setTasks(filtered);
    }

    // ... (Phần Search và Keyboard giữ nguyên) ...
    private void setupSearchFunctionality() {
        btnSearch.setOnClickListener(v -> showSearchLayout(true));
        btnCloseSearch.setOnClickListener(v -> {
            etSearch.setText("");
            showSearchLayout(false);
            filterTasks(currentFilter);
            hideKeyboard();
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { filterBySearch(s.toString()); }
            public void afterTextChanged(Editable s) {}
        });
    }
    private void showSearchLayout(boolean isSearching) {
        if (isSearching) {
            btnSearch.setVisibility(View.GONE);
            btnNotification.setVisibility(View.GONE);
            layoutSearchInput.setVisibility(View.VISIBLE);
            etSearch.requestFocus();
            showKeyboard();
        } else {
            btnSearch.setVisibility(View.VISIBLE);
            btnNotification.setVisibility(View.VISIBLE);
            layoutSearchInput.setVisibility(View.GONE);
        }
    }
    private void filterBySearch(String query) {
        List<TaskModel> filteredList = new ArrayList<>();
        for (TaskModel task : originalTaskList) {
            if (task.getTitle().toLowerCase().contains(query.toLowerCase())) filteredList.add(task);
        }
        adapter.setTasks(filteredList);
    }
    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
    }
}