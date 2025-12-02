package com.example.todoapp.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskModel implements Serializable {

    public enum TaskType { PERSONAL, WORK_PRIVATE, WORK_GROUP }

    private int id; // Thêm ID để call API
    private String title;
    private TaskType type;
    private List<SubTaskModel> subTasks;
    private boolean done = false;
    private String completedDate;
    private String status;

    // Constructor đơn giản (thêm id)
    public TaskModel(int id, String title, TaskType type, List<SubTaskModel> subTasks) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.subTasks = subTasks;
        updateDoneStatus();
    }

    // Constructor đầy đủ (thêm id)
    public TaskModel(int id, String title, TaskType type, List<SubTaskModel> subTasks,
                     boolean done, String completedDate, String status) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.subTasks = subTasks;
        this.done = done;
        this.completedDate = completedDate;
        this.status = status;
        // Không gọi updateDoneStatus ở đây để tôn trọng dữ liệu từ server trả về
    }

    // --- Getter & Setter ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public TaskType getType() { return type; }
    public void setType(TaskType type) { this.type = type; }

    public List<SubTaskModel> getSubTasks() { return subTasks; }

    public void setSubTasks(List<SubTaskModel> subTasks) {
        this.subTasks = subTasks;
        updateDoneStatus(); // Tự động cập nhật trạng thái task cha khi set list con mới
    }

    public boolean isDone() { return done; }

    public void setDone(boolean done) {
        this.done = done;
        if (done) {
            // Nếu hoàn thành và chưa có ngày, set ngày hiện tại
            if (this.completedDate == null || this.completedDate.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                this.completedDate = sdf.format(new Date());
            }
            this.status = "Completed";
        } else {
            this.completedDate = null;
            // Nếu bỏ done, quay về Working hoặc ToDo
            this.status = "Working";
        }
    }

    public String getCompletedDate() { return completedDate; }
    public void setCompletedDate(String completedDate) { this.completedDate = completedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    /**
     * Logic tự động kiểm tra:
     * - Nếu danh sách subtask rỗng -> Không tự động set done.
     * - Nếu TẤT CẢ subtask đã done -> Task cha Done (Completed).
     * - Nếu có ít nhất 1 subtask chưa done -> Task cha chưa Done (Working).
     */
    public void updateDoneStatus() {
        if (subTasks == null || subTasks.isEmpty()) {
            // Nếu không có subtask, giữ nguyên trạng thái hiện tại hoặc mặc định
            return;
        }

        boolean allDone = true;
        for (SubTaskModel sub : subTasks) {
            if (!sub.isDone()) {
                allDone = false;
                break;
            }
        }

        // Cập nhật biến cờ boolean
        this.done = allDone;

        // Cập nhật status string cho khớp với Backend
        if (allDone) {
            setStatus("Completed");
            if (this.completedDate == null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                this.completedDate = sdf.format(new Date());
            }
        } else {
            setStatus("Working"); // Hoặc "ToDo" tùy logic ban đầu
            this.completedDate = null;
        }
    }
}