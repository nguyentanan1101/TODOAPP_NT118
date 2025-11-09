package com.example.todoapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;

import com.example.todoapp.R;
import com.example.todoapp.utils.BottomNavHelper;
import com.example.todoapp.models.TaskModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    List<TaskModel> taskList;

    Button btnToday, btnPersonal, btnWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavHelper.setupBottomNav(this);

        recyclerView = findViewById(R.id.recyclerTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Topbar
        btnToday = findViewById(R.id.btnToday);
        btnPersonal = findViewById(R.id.btnPersonal);
        btnWork = findViewById(R.id.btnWork);

        // Chọn mặc định là Today
        selectButton(btnToday);

        btnToday.setOnClickListener(v -> selectButton(btnToday));
        btnPersonal.setOnClickListener(v -> selectButton(btnPersonal));
        btnWork.setOnClickListener(v -> selectButton(btnWork));

        // Danh sách ví dụ

    }

    private void selectButton(Button selectedButton) {
        // Bỏ chọn tất cả
        btnToday.setSelected(false);
        btnPersonal.setSelected(false);
        btnWork.setSelected(false);

        // Chọn lại nút được bấm
        selectedButton.setSelected(true);
    }
}
