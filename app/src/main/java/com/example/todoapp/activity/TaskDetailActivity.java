package com.example.todoapp.activity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todoapp.R;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView tvTitle;
    private LinearLayout tvSubtasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        tvTitle = findViewById(R.id.tvTitle);
        tvSubtasks = findViewById(R.id.tvSubtasks);

        String title = getIntent().getStringExtra("TASK_TITLE");
        tvTitle.setText(title);

        // nếu truyền subtask thì bạn có thể hiển thị
    }
}
