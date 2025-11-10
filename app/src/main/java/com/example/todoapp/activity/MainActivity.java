package com.example.todoapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;

import com.example.todoapp.R;
import com.example.todoapp.adapter.TaskAdapter;
import com.example.todoapp.adapter.TaskGroupAdapter;
import com.example.todoapp.models.SubTaskModel;
import com.example.todoapp.models.TaskGroup;
import com.example.todoapp.models.TaskModel;

import com.example.todoapp.utils.BottomNavHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<TaskModel> taskList;

    private Button btnToday, btnPersonal, btnWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavHelper.setupBottomNav(this);

        initViews();
        initButtons();
        createDemoTasks();
        filterTasks("TODAY"); // mặc định
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerTasks);

        btnToday = findViewById(R.id.btnToday);
        btnPersonal = findViewById(R.id.btnPersonal);
        btnWork = findViewById(R.id.btnWork);
    }

    private void initButtons() {
        selectButton(btnToday);
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

    private void createDemoTasks() {
        taskList = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        String today = String.format("%04d-%02d-%02d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
        c.add(Calendar.DAY_OF_MONTH, 1);
        String tomorrow = String.format("%04d-%02d-%02d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));

        TaskModel task1 = new TaskModel("Đi siêu thị", TaskModel.TaskType.PERSONAL, new ArrayList<>());
        task1.getSubTasks().add(new SubTaskModel("Mua sữa", today));
        task1.getSubTasks().add(new SubTaskModel("Mua bánh", ""));
        task1.getSubTasks().add(new SubTaskModel("Mua rau", ""));
        taskList.add(task1);

        TaskModel task2 = new TaskModel("Báo cáo tuần", TaskModel.TaskType.WORK_PRIVATE, new ArrayList<>());
        task2.getSubTasks().add(new SubTaskModel("Hoàn thành biểu mẫu", tomorrow));
        task2.getSubTasks().add(new SubTaskModel("Gửi email", ""));
        taskList.add(task2);

        TaskModel task3 = new TaskModel("Dự án App nhóm", TaskModel.TaskType.WORK_GROUP, new ArrayList<>());
        task3.getSubTasks().add(new SubTaskModel("Thiết kế UI", today));
        task3.getSubTasks().add(new SubTaskModel("Code backend", ""));
        task3.getSubTasks().add(new SubTaskModel("Test chức năng", ""));
        taskList.add(task3);

        TaskModel task4 = new TaskModel("Đọc sách", TaskModel.TaskType.PERSONAL, new ArrayList<>());
        task4.getSubTasks().add(new SubTaskModel("Chương 1", today));
        task4.getSubTasks().add(new SubTaskModel("Chương 2", ""));
        taskList.add(task4);
    }

    private void filterTasks(String filterType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        String todayStr = sdf.format(c.getTime());
        c.add(Calendar.DAY_OF_MONTH, 1);
        String tomorrowStr = sdf.format(c.getTime());

        switch (filterType) {

            case "TODAY": // chỉ những task có subtask today
                List<TaskModel> todayTasks = new ArrayList<>();
                for(TaskModel t : taskList) {
                    if(hasSubtaskDueToday(t)) todayTasks.add(t);
                }

                // Today: Grid 2 cột, dùng TaskAdapter
                GridLayoutManager layoutToday = new GridLayoutManager(this, 2);
                recyclerView.setLayoutManager(layoutToday);
                recyclerView.setAdapter(new TaskAdapter(this, todayTasks));
                break;

            case "PERSONAL":
            case "WORK":
                List<TaskGroup> groups = new ArrayList<>();
                Map<String, List<TaskModel>> map = new TreeMap<>();
                for(TaskModel t : taskList) {
                    if(filterType.equals("PERSONAL") && t.getType() != TaskModel.TaskType.PERSONAL) continue;
                    if(filterType.equals("WORK") && t.getType() == TaskModel.TaskType.PERSONAL) continue;

                    String due = getEarliestDue(t);
                    if(due.equals(todayStr)) due = "Today";
                    else if(due.equals(tomorrowStr)) due = "Tomorrow";
                    else if(due.isEmpty()) due = "No due date";

                    if(!map.containsKey(due)) map.put(due, new ArrayList<>());
                    map.get(due).add(t);
                }

                for(Map.Entry<String, List<TaskModel>> e : map.entrySet()) {
                    groups.add(new TaskGroup(e.getKey(), e.getValue()));
                }

                GridLayoutManager layout = new GridLayoutManager(this, 2);
                TaskGroupAdapter groupAdapter = new TaskGroupAdapter(this, groups);
                groupAdapter.attachToRecyclerView(recyclerView, layout);
                break;
        }
    }

    private boolean hasSubtaskDueToday(TaskModel task) {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);

        for(SubTaskModel sub : task.getSubTasks()) {
            String due = sub.getDueDate();
            if(due != null && !due.isEmpty()) {
                String[] parts = due.split("-");
                int yy = Integer.parseInt(parts[0]);
                int mm = Integer.parseInt(parts[1]);
                int dd = Integer.parseInt(parts[2]);
                if(yy == y && mm == m && dd == d) return true;
            }
        }
        return false;
    }

    private String getEarliestDue(TaskModel t) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date minDate = null;

        for(SubTaskModel sub : t.getSubTasks()) {
            String due = sub.getDueDate();
            if(due == null || due.isEmpty()) continue;
            try {
                Date d = sdf.parse(due);
                if(minDate == null || d.before(minDate)) minDate = d;
            } catch(ParseException e) { e.printStackTrace(); }
        }

        if(minDate == null) return "";
        return sdf.format(minDate);
    }
}
