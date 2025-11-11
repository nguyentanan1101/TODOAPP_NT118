package com.example.todoapp.models;

import java.util.List;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskModel implements Serializable {
    public enum TaskType { PERSONAL, WORK_PRIVATE, WORK_GROUP }

    private String title;
    private TaskType type;
    private List<SubTaskModel> subTasks;
    private boolean done = false; // trạng thái hoàn thành
    private String completedDate; // ✅ thêm thuộc tính ngày hoàn thành

    public TaskModel(String title, TaskType type, List<SubTaskModel> subTasks) {
        this.title = title;
        this.type = type;
        this.subTasks = subTasks;
    }

    public String getTitle() { return title; }
    public TaskType getType() { return type; }
    public List<SubTaskModel> getSubTasks() { return subTasks; }

    public void setSubTasks(List<SubTaskModel> subTasks) {
        this.subTasks = subTasks;
    }

    public boolean isDone() { return done; }

    public void setDone(boolean done) {
        this.done = done;

        // ✅ Khi task hoàn thành, lưu lại ngày hoàn thành
        if (done) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            this.completedDate = sdf.format(new Date());
        } else {
            this.completedDate = null;
        }
    }

    // ✅ Getter và Setter cho ngày hoàn thành
    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }
}
