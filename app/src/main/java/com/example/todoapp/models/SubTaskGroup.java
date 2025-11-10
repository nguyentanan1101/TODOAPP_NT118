package com.example.todoapp.models;

import java.util.List;

public class SubTaskGroup {
    private String dueLabel;
    private List<SubTaskModel> subTasks;

    public SubTaskGroup(String dueLabel, List<SubTaskModel> subTasks) {
        this.dueLabel = dueLabel;
        this.subTasks = subTasks;
    }

    public String getDueLabel() {
        return dueLabel;
    }

    public List<SubTaskModel> getSubTasks() {
        return subTasks;
    }
}
