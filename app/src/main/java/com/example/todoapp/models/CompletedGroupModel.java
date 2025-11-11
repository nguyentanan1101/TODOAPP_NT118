package com.example.todoapp.models;

import java.util.List;

public class CompletedGroupModel {
    private String date;
    private List<TaskModel> tasks;

    public CompletedGroupModel(String date, List<TaskModel> tasks) {
        this.date = date;
        this.tasks = tasks;
    }

    public String getDate() {
        return date;
    }

    public List<TaskModel> getTasks() {
        return tasks;
    }
}
