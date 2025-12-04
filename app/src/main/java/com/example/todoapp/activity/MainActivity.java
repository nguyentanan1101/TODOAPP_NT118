package com.example.todoapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
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
    private ImageView btnSearch, btnNotification, btnCloseSearch, btnAdd, btnSort;
    private LinearLayout layoutSearchInput;
    private EditText etSearch;

    private List<TaskModel> taskList = new ArrayList<>();
    private List<TaskModel> originalTaskList = new ArrayList<>();

    private TaskAdapter adapter;
    private OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "http://34.124.178.44:4000/api/tasks/created-by-me";
    private static final String SUBTASK_URL = "http://34.124.178.44:4000/api/subtask/task/";

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
        btnSort = findViewById(R.id.btnSort);

        initButtons();
        setupSearchFunctionality();

        // --- SETUP SORT BUTTON ---
        btnSort.setOnClickListener(v -> showSortMenu(v));

        // Setup RecyclerView
        adapter = new TaskAdapter(this, taskList);
        adapter.setOnTaskClickListener(task -> {
            Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
            intent.putExtra("TASK", task);
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.StaggeredGridLayoutManager(2, androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL));
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

        // Mặc định chọn nút Today
        selectButton(btnToday);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchTasksFromAPI();
    }

    // --- CÁC HÀM SORT ---
    private void showSortMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenu().add(0, 1, 0, "Sort by Due Date (A-Z)");
        popup.getMenu().add(0, 2, 0, "Sort by Priority (High-Low)");
        popup.getMenu().add(0, 3, 0, "Sort by Name (A-Z)");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1: sortTasksByDate(); return true;
                case 2: sortTasksByPriority(); return true;
                case 3: sortTasksByName(); return true;
            }
            return false;
        });
        popup.show();
    }

    private void sortTasksByName() {
        Collections.sort(taskList, (t1, t2) -> {
            String n1 = t1.getTitle() != null ? t1.getTitle() : "";
            String n2 = t2.getTitle() != null ? t2.getTitle() : "";
            return n1.compareToIgnoreCase(n2);
        });
        adapter.notifyDataSetChanged();
    }

    private void sortTasksByDate() {
        Collections.sort(taskList, (t1, t2) -> {
            String d1 = getSubTaskDueDate(t1);
            String d2 = getSubTaskDueDate(t2);
            if (d1.isEmpty()) return 1;
            if (d2.isEmpty()) return -1;
            return d1.compareTo(d2);
        });
        adapter.notifyDataSetChanged();
    }

    private String getSubTaskDueDate(TaskModel t) {
        if (t.getSubTasks() != null && !t.getSubTasks().isEmpty()) {
            return t.getSubTasks().get(0).getDueDate();
        }
        return "";
    }

    private void sortTasksByPriority() {
        Collections.sort(taskList, (t1, t2) -> {
            int p1 = getPriorityValue(t1.getPriority());
            int p2 = getPriorityValue(t2.getPriority());
            return p2 - p1;
        });
        adapter.notifyDataSetChanged();
    }

    private int getPriorityValue(String priority) {
        if (priority == null) return 0;
        if (priority.equalsIgnoreCase("High") || priority.equalsIgnoreCase("Critical")) return 3;
        if (priority.equalsIgnoreCase("Medium")) return 2;
        if (priority.equalsIgnoreCase("Low")) return 1;
        return 0;
    }

    // --- CÁC HÀM UI BUTTON ---
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

    // --- API FETCH TASKS ---
    private void fetchTasksFromAPI() {
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        String userIdStr = sp.getString("user_id", "0");
        int currentUserId = 0;
        try { currentUserId = Integer.parseInt(userIdStr); } catch (Exception e) {}
        final int myId = currentUserId;

        Request request = new Request.Builder()
                .url(BASE_URL)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Connection Error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) return;

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
                    List<JSONObject> validTasks = new ArrayList<>();

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject t = arr.getJSONObject(i);
                        String statusStr = t.optString("task_status");
                        if (statusStr == null || statusStr.isEmpty() || statusStr.equals("null")) {
                            statusStr = t.optString("status");
                        }
                        if (statusStr == null || statusStr.isEmpty() || statusStr.equals("null")) {
                            statusStr = "ToDo";
                        }
                        t.put("safe_status", statusStr);

                        if (statusStr.equalsIgnoreCase("Working") || statusStr.equalsIgnoreCase("ToDo")) {
                            validTasks.add(t);
                        }
                    }

                    totalTasksToLoad = validTasks.size();
                    if (totalTasksToLoad == 0) {
                        runOnUiThread(() -> {
                            taskList.clear();
                            adapter.notifyDataSetChanged();
                        });
                        return;
                    }

                    for (JSONObject t : validTasks) {
                        int taskId = t.optInt("task_id", 0);
                        String title = t.optString("task_name");
                        if (title.isEmpty()) title = t.optString("title", "No Title");

                        int createdBy = t.optInt("created_by", 0);
                        int projectId = t.optInt("project_id", 0);

                        TaskModel.TaskType type;
                        if (projectId > 0) type = TaskModel.TaskType.WORK_GROUP;
                        else {
                            if (createdBy == myId) type = TaskModel.TaskType.PERSONAL;
                            else type = TaskModel.TaskType.WORK_PRIVATE;
                        }

                        String priority = t.optString("priority", "Low");
                        TaskModel task = new TaskModel(taskId, title, type, new ArrayList<>(), priority);

                        // --- LẤY DUE DATE (Đoạn code bạn vừa gửi) ---
                        String dueDate = t.optString("due_date", "");
                        if (dueDate.contains("T")) {
                            dueDate = dueDate.split("T")[0];
                        }
                        task.setDueDate(dueDate);
                        // ---------------------------------------------

                        // --- LẤY DESCRIPTION (Để hiển thị ở màn hình chi tiết) ---
                        String desc = t.optString("description");
                        if(desc.isEmpty()) desc = t.optString("task_description", "");
                        task.setDescription(desc);
                        // ---------------------------------------------------------

                        String safeStatus = t.optString("safe_status", "ToDo");
                        boolean isDone = safeStatus.equalsIgnoreCase("Completed") || safeStatus.equalsIgnoreCase("Done");
                        task.setDone(isDone);
                        task.setStatus(safeStatus);

                        originalTaskList.add(task);
                        loadSubTasks(task, taskId, token);
                    }

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
            public void onFailure(Call call, IOException e) { checkAndRefreshUI(); }

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

                            // Lấy Title: JSON trả về "title"
                            String subTitle = s.optString("title");
                            if (subTitle.isEmpty()) subTitle = s.optString("subtask_name", "");

                            // --- SỬA ĐOẠN NÀY: LẤY ĐÚNG STATUS ---
                            String subStatus = s.optString("status"); // JSON có key "status"
                            if (subStatus == null || subStatus.isEmpty() || subStatus.equals("null")) {
                                subStatus = s.optString("subtask_status", "ToDo");
                            }

                            // Kiểm tra cả "Done" và "Completed"
                            boolean done = subStatus.equalsIgnoreCase("Done") || subStatus.equalsIgnoreCase("Completed");
                            // ---------------------------------------

                            // Lấy ngày nếu có
                            String dueDate = s.optString("due_date", "");
                            if(dueDate.contains("T")) dueDate = dueDate.split("T")[0];

                            subTasks.add(new SubTaskModel(subId, subTitle, "", dueDate, done));
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
            // Bỏ qua task đã hoàn thành
            if (t.isDone()) continue;

            switch (filterType) {
                case "TODAY":
                    boolean isShow = false;

                    // 1. Kiểm tra ngày của Task cha
                    // (Lưu ý: Cần đảm bảo TaskModel đã có getter getDueDate() và dữ liệu đã được set trong fetchTasksFromAPI)
                    if (t.getDueDate() != null && t.getDueDate().equals(todayStr)) {
                        isShow = true;
                    }

                    // 2. Nếu Task cha không phải hôm nay, kiểm tra tiếp Subtask
                    if (!isShow && t.getSubTasks() != null) {
                        for (SubTaskModel sub : t.getSubTasks()) {
                            if (sub.getDueDate() != null && sub.getDueDate().equals(todayStr)) {
                                isShow = true;
                                break;
                            }
                        }
                    }

                    if (isShow) {
                        filtered.add(t);
                    }
                    break;

                case "PERSONAL":
                    if (t.getType() == TaskModel.TaskType.PERSONAL) {
                        filtered.add(t);
                    }
                    break;

                case "WORK":
                    if (t.getType() != TaskModel.TaskType.PERSONAL) {
                        filtered.add(t);
                    }
                    break;
            }
        }

        // Cập nhật list hiển thị
        taskList.clear();
        taskList.addAll(filtered);
        adapter.notifyDataSetChanged();
    }

    // --- SEARCH ---
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
            btnSearch.setVisibility(View.GONE); btnNotification.setVisibility(View.GONE);
            layoutSearchInput.setVisibility(View.VISIBLE); etSearch.requestFocus(); showKeyboard();
        } else {
            btnSearch.setVisibility(View.VISIBLE); btnNotification.setVisibility(View.VISIBLE);
            layoutSearchInput.setVisibility(View.GONE);
        }
    }
    private void filterBySearch(String query) {
        List<TaskModel> filteredList = new ArrayList<>();
        for (TaskModel task : originalTaskList) {
            if (task.getTitle().toLowerCase().contains(query.toLowerCase())) filteredList.add(task);
        }
        taskList.clear();
        taskList.addAll(filteredList);
        adapter.notifyDataSetChanged();
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