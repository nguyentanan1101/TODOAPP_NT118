package com.example.todoapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;

import com.example.todoapp.R;
import com.example.todoapp.adapter.TaskAdapter;
import com.example.todoapp.models.SubTaskModel;
import com.example.todoapp.models.TaskModel;
import com.example.todoapp.utils.BottomNavHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<TaskModel> taskList;
    private TaskAdapter adapter;

    private Button btnToday, btnPersonal, btnWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavHelper.setupBottomNav(this);

        initViews();
        initButtons();
        createDemoTasks();
        showAllTasks();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerTasks);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        btnToday = findViewById(R.id.btnToday);
        btnPersonal = findViewById(R.id.btnPersonal);
        btnWork = findViewById(R.id.btnWork);
    }

    private void initButtons() {
        selectButton(btnToday);

        btnToday.setOnClickListener(v -> {
            selectButton(btnToday);
            filterTasks("TODAY");
        });

        btnPersonal.setOnClickListener(v -> {
            selectButton(btnPersonal);
            filterTasks("PERSONAL");
        });

        btnWork.setOnClickListener(v -> {
            selectButton(btnWork);
            filterTasks("WORK");
        });
    }

    private void selectButton(Button selectedButton) {
        btnToday.setSelected(false);
        btnPersonal.setSelected(false);
        btnWork.setSelected(false);
        selectedButton.setSelected(true);
    }

    private void createDemoTasks() {
        taskList = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        String today = String.format("%04d-%02d-%02d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));

        // Task 1: có subtask due hôm nay
        TaskModel task1 = new TaskModel("Đi siêu thị", TaskModel.TaskType.PERSONAL, new ArrayList<>());
        task1.getSubTasks().add(new SubTaskModel("Mua sữa", today));
        task1.getSubTasks().add(new SubTaskModel("Mua bánh", ""));
        task1.getSubTasks().add(new SubTaskModel("Mua rau", ""));
        taskList.add(task1);

        // Task 2: không có subtask hôm nay
        TaskModel task2 = new TaskModel("Báo cáo tuần", TaskModel.TaskType.WORK_PRIVATE, new ArrayList<>());
        task2.getSubTasks().add(new SubTaskModel("Hoàn thành biểu mẫu", ""));
        task2.getSubTasks().add(new SubTaskModel("Gửi email cho sếp", ""));
        taskList.add(task2);

        // Task 3: có subtask hôm nay
        TaskModel task3 = new TaskModel("Dự án App nhóm", TaskModel.TaskType.WORK_GROUP, new ArrayList<>());
        task3.getSubTasks().add(new SubTaskModel("Thiết kế UI", today));
        task3.getSubTasks().add(new SubTaskModel("Code backend", ""));
        task3.getSubTasks().add(new SubTaskModel("Test chức năng", ""));
        taskList.add(task3);

        // Task 4: không có subtask hôm nay
        TaskModel task4 = new TaskModel("Đọc sách", TaskModel.TaskType.PERSONAL, new ArrayList<>());
        task4.getSubTasks().add(new SubTaskModel("Chương 1", ""));
        task4.getSubTasks().add(new SubTaskModel("Chương 2", ""));
        taskList.add(task4);
    }

    private void showAllTasks() {
        adapter = new TaskAdapter(this, taskList, position -> {
            taskList.remove(position);
            adapter.notifyItemRemoved(position);
        });
        recyclerView.setAdapter(adapter);
    }

    private void filterTasks(String filterType) {
        List<TaskModel> filtered = new ArrayList<>();

        for (TaskModel t : taskList) {
            switch (filterType) {
                case "TODAY":
                    if (hasSubtaskDueToday(t)) filtered.add(t);
                    break;
                case "PERSONAL":
                    if (t.getType() == TaskModel.TaskType.PERSONAL) filtered.add(t);
                    break;
                case "WORK":
                    if (t.getType() == TaskModel.TaskType.WORK_PRIVATE || t.getType() == TaskModel.TaskType.WORK_GROUP)
                        filtered.add(t);
                    break;
            }
        }

        adapter = new TaskAdapter(this, filtered, position -> {
            TaskModel t = filtered.get(position);
            if (t.getType() == TaskModel.TaskType.PERSONAL) {
                taskList.remove(t);
                filtered.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private boolean hasSubtaskDueToday(TaskModel task) {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);

        for (SubTaskModel sub : task.getSubTasks()) {
            String due = sub.getDueDate();
            if (due != null && !due.isEmpty()) {
                String[] parts = due.split("-");
                int yy = Integer.parseInt(parts[0]);
                int mm = Integer.parseInt(parts[1]);
                int dd = Integer.parseInt(parts[2]);
                if (yy == y && mm == m && dd == d) return true;
            }
        }
        return false;
    }
}
