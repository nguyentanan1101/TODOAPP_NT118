package com.example.todoapp.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.adapter.NotificationAdapter;
import com.example.todoapp.models.NotificationModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationModel> allList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        btnBack = findViewById(R.id.btnBack);
        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.recyclerNotification);

        btnBack.setOnClickListener(v -> finish());

        // 1. Tạo dữ liệu giả (Mock Data)
        createMockData();

        // 2. Setup RecyclerView
        adapter = new NotificationAdapter(this, allList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 3. Setup Tabs
        setupTabs();
    }

    private void createMockData() {
        allList = new ArrayList<>();
        allList.add(new NotificationModel("App", "10:30", "30/10/2025", "Sự cố hệ thống đã được xử lý", "Sự cố kết nối tới máy chủ lúc 09:30 sáng đã được khắc phục hoàn toàn.", true));
        allList.add(new NotificationModel("App", "6:30", "29/10/2025", "Buổi sáng tốt lành, Finn!", "Hôm nay bạn có 3 công việc cần hoàn thành.", true));
        allList.add(new NotificationModel("Work", "9:30", "28/10/2025", "Nhiệm vụ mới trong nhóm Marketing", "Finn, bạn được giao nhiệm vụ chuẩn bị nội dung bài đăng Facebook.", true));
        allList.add(new NotificationModel("App", "7:30", "27/10/2025", "Ứng dụng Todo đã có phiên bản mới!", "Phiên bản 2.3 mang đến giao diện tối (Dark Mode).", true));
        allList.add(new NotificationModel("Work", "7:30", "27/10/2025", "Trưởng nhóm dự án đã xuất hiện", "Chào Finn, quản lý xin thông báo bổ nhiệm Thịnh làm trưởng nhóm.", true));
    }

    private void setupTabs() {
        // Tính toán số lượng
        int countAll = allList.size();
        int countWork = 0;
        int countApp = 0;

        for (NotificationModel item : allList) {
            if (item.getType().equals("Work")) countWork++;
            else if (item.getType().equals("App")) countApp++;
        }

        // Thêm Tabs với số lượng
        tabLayout.addTab(tabLayout.newTab().setText("All (" + countAll + ")"));
        tabLayout.addTab(tabLayout.newTab().setText("Work (" + countWork + ")"));
        tabLayout.addTab(tabLayout.newTab().setText("App (" + countApp + ")"));

        // Lắng nghe sự kiện chọn Tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterList(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void filterList(int position) {
        List<NotificationModel> filteredList = new ArrayList<>();

        if (position == 0) {
            // Tab All
            filteredList.addAll(allList);
        } else if (position == 1) {
            // Tab Work
            for (NotificationModel item : allList) {
                if (item.getType().equals("Work")) filteredList.add(item);
            }
        } else if (position == 2) {
            // Tab App
            for (NotificationModel item : allList) {
                if (item.getType().equals("App")) filteredList.add(item);
            }
        }

        // Cập nhật Adapter
        adapter.updateList(filteredList);
    }
}