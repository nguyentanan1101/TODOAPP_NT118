package com.example.todoapp.models;

import java.util.List;
import java.io.Serializable;
import java.util.List;

public class TaskModel implements Serializable {
    public enum TaskType { PERSONAL, WORK_PRIVATE, WORK_GROUP }

    private String title;
    private TaskType type;
    private List<SubTaskModel> subTasks;
    private boolean done = false; // thêm thuộc tính này

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
    public void setDone(boolean done) { this.done = done; }
}
