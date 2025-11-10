package com.example.todoapp.models;

public class TaskItem {

    public enum ItemType { HEADER, TASK }

    public ItemType type;
    public String headerTitle; // nếu là HEADER
    public TaskModel task;     // nếu là TASK

    // Constructor header
    public TaskItem(String headerTitle) {
        this.type = ItemType.HEADER;
        this.headerTitle = headerTitle;
    }

    // Constructor task
    public TaskItem(TaskModel task) {
        this.type = ItemType.TASK;
        this.task = task;
    }

    public boolean isHeader() { return type == ItemType.HEADER; }
    public String getHeaderTitle() { return headerTitle; }
    public TaskModel getTask() { return task; }
}
