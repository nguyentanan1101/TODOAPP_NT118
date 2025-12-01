package com.example.todoapp.models;

public class NotificationModel {
    private String type; // "App" hoặc "Work"
    private String time;
    private String date;
    private String title;
    private String content;
    private boolean isUnread;

    public NotificationModel(String type, String time, String date, String title, String content, boolean isUnread) {
        this.type = type;
        this.time = time;
        this.date = date;
        this.title = title;
        this.content = content;
        this.isUnread = isUnread;
    }

    // Getters
    public String getType() { return type; }
    public String getTime() { return time; }
    public String getDate() { return date; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public boolean isUnread() { return isUnread; }
}