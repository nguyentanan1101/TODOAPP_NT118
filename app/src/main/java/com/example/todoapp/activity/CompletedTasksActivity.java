package com.example.todoapp.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.adapter.CompletedGroupAdapter;
import com.example.todoapp.models.CompletedGroupModel;
import com.example.todoapp.models.TaskModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompletedTasksActivity extends AppCompatActivity {

    private RecyclerView recyclerCompletedGroups;
    private CompletedGroupAdapter groupAdapter;
    private List<TaskModel> allTasks;
    private List<CompletedGroupModel> groupedTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_tasks);

        recyclerCompletedGroups = findViewById(R.id.recyclerCompletedGroups);
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        recyclerCompletedGroups.setLayoutManager(new LinearLayoutManager(this));

        loadTasks();
        groupTasksByDate();

        groupAdapter = new CompletedGroupAdapter(this, groupedTasks);
        recyclerCompletedGroups.setAdapter(groupAdapter);
    }

    private void loadTasks() {
        SharedPreferences prefs = getSharedPreferences("TASK_PREFS", Context.MODE_PRIVATE);
        String json = prefs.getString("tasks", "[]");
        Type type = new TypeToken<ArrayList<TaskModel>>(){}.getType();
        allTasks = new Gson().fromJson(json, type);
    }

    private void groupTasksByDate() {
        groupedTasks = new ArrayList<>();
        Map<String, List<TaskModel>> map = new HashMap<>();

        for (TaskModel t : allTasks) {
            if (t.isDone() && t.getCompletedDate() != null) {
                map.computeIfAbsent(t.getCompletedDate(), k -> new ArrayList<>()).add(t);
            }
        }

        for (Map.Entry<String, List<TaskModel>> entry : map.entrySet()) {
            groupedTasks.add(new CompletedGroupModel(entry.getKey(), entry.getValue()));
        }
    }
}
