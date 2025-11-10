package com.example.todoapp.models;

public class SubTaskModel {
    private String title;
    private String dueDate; // Ngầm dùng, không hiển thị

    public SubTaskModel(String title, String dueDate) {
        this.title = title;
        this.dueDate = dueDate;
    }

    public String getTitle() { return title; }
    public String getDueDate() { return dueDate; }
}
