package com.example.todoapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.adapter.SubTaskGroupAdapter;
import com.example.todoapp.models.SubTaskGroup;
import com.example.todoapp.models.SubTaskModel;
import com.example.todoapp.models.TaskModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TaskDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerSubTasks;
    private TextView tvTaskTitle;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        tvTaskTitle = findViewById(R.id.tvTaskTitle);
        btnBack = findViewById(R.id.btnBack);
        recyclerSubTasks = findViewById(R.id.recyclerSubTasks);

        btnBack.setOnClickListener(v -> finish());

        TaskModel task = (TaskModel) getIntent().getSerializableExtra("TASK");
        if (task != null) {
            tvTaskTitle.setText(task.getTitle());
            showGroupedSubTasks(task.getSubTasks());
        }
    }

    private void showGroupedSubTasks(List<SubTaskModel> subTasks) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        String todayStr = sdf.format(c.getTime());
        c.add(Calendar.DAY_OF_MONTH, 1);
        String tomorrowStr = sdf.format(c.getTime());

        // Gom nhóm subtasks theo due date
        Comparator<String> dateComparator = (a, b) -> {
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
        };

        Map<String, List<SubTaskModel>> map = new TreeMap<>(dateComparator);

        for (SubTaskModel sub : subTasks) {
            String due = sub.getDueDate();
            if (due == null || due.isEmpty()) due = "No due date";
            else if (due.equals(todayStr)) due = "Today";
            else if (due.equals(tomorrowStr)) due = "Tomorrow";

            if (!map.containsKey(due)) map.put(due, new ArrayList<>());
            map.get(due).add(sub);
        }

        List<SubTaskGroup> groups = new ArrayList<>();
        for (Map.Entry<String, List<SubTaskModel>> e : map.entrySet()) {
            groups.add(new SubTaskGroup(e.getKey(), e.getValue()));
        }

        recyclerSubTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerSubTasks.setAdapter(new SubTaskGroupAdapter(this, groups));
    }
}
