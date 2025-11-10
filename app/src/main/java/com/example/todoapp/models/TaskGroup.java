package com.example.todoapp.models;

import java.util.List;

public class TaskGroup {
    private String dateTitle; // "Today", "Tomorrow", "2025-11-10"...
    private List<TaskModel> tasks;

    public TaskGroup(String dateTitle, List<TaskModel> tasks) {
        this.dateTitle = dateTitle;
        this.tasks = tasks;
    }

    public String getDateTitle() { return dateTitle; }
    public List<TaskModel> getTasks() { return tasks; }
}
