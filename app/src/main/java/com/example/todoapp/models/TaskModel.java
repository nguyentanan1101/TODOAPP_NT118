package com.example.todoapp.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskModel implements Serializable {

    public enum TaskType { PERSONAL, WORK_PRIVATE, WORK_GROUP }

    private int id;
    private String title;
    private TaskType type;
    private List<SubTaskModel> subTasks;
    private boolean done = false;
    private String completedDate;
    private String status;
    private String priority;
    private String description;

    // --- MỚI THÊM: Biến lưu ngày hết hạn ---
    private String dueDate;

    // Constructor đơn giản
    public TaskModel(int id, String title, TaskType type, List<SubTaskModel> subTasks, String priority) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.subTasks = subTasks;
        this.priority = priority;

        this.done = false;
        this.status = "ToDo";
        updateDoneStatus();
    }

    // Constructor đầy đủ
    public TaskModel(int id, String title, TaskType type, List<SubTaskModel> subTasks,
                     boolean done, String completedDate, String status, String priority) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.subTasks = subTasks;
        this.done = done;
        this.completedDate = completedDate;
        this.status = status;
        this.priority = priority;
    }

    // --- Getter & Setter ---
    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriority() {
        return (priority == null || priority.isEmpty()) ? "Low" : priority;
    }
    public void setPriority(String priority) { this.priority = priority; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public TaskType getType() { return type; }
    public void setType(TaskType type) { this.type = type; }

    public List<SubTaskModel> getSubTasks() { return subTasks; }

    public void setSubTasks(List<SubTaskModel> subTasks) {
        this.subTasks = subTasks;
        updateDoneStatus();
    }

    public boolean isDone() { return done; }

    public void setDone(boolean done) {
        this.done = done;
        if (done) {
            if (this.completedDate == null || this.completedDate.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                this.completedDate = sdf.format(new Date());
            }
            this.status = "Completed";
        } else {
            this.completedDate = null;
            this.status = "Working";
        }
    }

    public String getCompletedDate() { return completedDate; }
    public void setCompletedDate(String completedDate) { this.completedDate = completedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public void updateDoneStatus() {
        if (subTasks == null || subTasks.isEmpty()) {
            return;
        }

        boolean allDone = true;
        for (SubTaskModel sub : subTasks) {
            if (!sub.isDone()) {
                allDone = false;
                break;
            }
        }

        if (this.done != allDone) {
            setDone(allDone);
        }
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}