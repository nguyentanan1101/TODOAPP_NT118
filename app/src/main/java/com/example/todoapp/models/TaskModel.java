package com.example.todoapp.models;

import java.util.List;
import java.io.Serializable;
import java.util.List;

public class TaskModel implements Serializable {
    public enum TaskType { PERSONAL, WORK_PRIVATE, WORK_GROUP }

    private String title;
    private TaskType type;
    private List<SubTaskModel> subTasks;

    public TaskModel(String title, TaskType type, List<SubTaskModel> subTasks) {
        this.title = title;
        this.type = type;
        this.subTasks = subTasks;
    }

    public String getTitle() { return title; }
    public TaskType getType() { return type; }
    public List<SubTaskModel> getSubTasks() { return subTasks; }
}
