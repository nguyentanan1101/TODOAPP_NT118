package com.example.todoapp.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TaskModel implements Serializable {

    public enum TaskType { PERSONAL, WORK_PRIVATE, WORK_GROUP }

    private String title;
    private TaskType type;
    private List<SubTaskModel> subTasks;
    private boolean done = false;
    private String completedDate;
    private String status; // thêm status dạng String

    public TaskModel(String title, TaskType type, List<SubTaskModel> subTasks) {
        this.title = title;
        this.type = type;
        this.subTasks = subTasks;
        updateDoneStatus();
    }

    public TaskModel(String title, TaskType type, List<SubTaskModel> subTasks,
                     boolean done, String completedDate, String status) {
        this.title = title;
        this.type = type;
        this.subTasks = subTasks;
        this.done = done;
        this.completedDate = completedDate;
        this.status = status;
        updateDoneStatus();
    }

    public String getTitle() { return title; }

    public TaskType getType() { return type; }

    public List<SubTaskModel> getSubTasks() { return subTasks; }

    public void setSubTasks(List<SubTaskModel> subTasks) {
        this.subTasks = subTasks;
        updateDoneStatus();
    }

    public boolean isDone() { return done; }

    public void setDone(boolean done) {
        this.done = done;
        if (done && (this.completedDate == null || this.completedDate.isEmpty())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            this.completedDate = sdf.format(new Date());
        } else if (!done) {
            this.completedDate = null;
        }
    }

    public String getCompletedDate() { return completedDate; }

    public void setCompletedDate(String completedDate) { this.completedDate = completedDate; }

    // getter/setter cho status
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    // Kiểm tra tất cả subtask để cập nhật trạng thái task lớn
    public void updateDoneStatus() {
        if (subTasks == null || subTasks.isEmpty()) {
            setDone(false);
            setStatus("ToDo");
            return;
        }

        boolean allDone = true;
        for (SubTaskModel sub : subTasks) {
            if (!sub.isDone()) {
                allDone = false;
                break;
            }
        }

        setDone(allDone);
        setStatus(allDone ? "Working" : "Done");
    }
}
