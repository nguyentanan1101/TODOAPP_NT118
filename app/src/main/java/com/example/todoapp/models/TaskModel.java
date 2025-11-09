package com.example.todoapp.models;

public class TaskModel {
    private String title;
    private String details;
    private int color; // mã màu nền

    public TaskModel(String title, String details, int color) {
        this.title = title;
        this.details = details;
        this.color = color;
    }

    public String getTitle() { return title; }
    public String getDetails() { return details; }
    public int getColor() { return color; }
}
