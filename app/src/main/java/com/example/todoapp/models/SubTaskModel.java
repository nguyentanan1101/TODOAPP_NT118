package com.example.todoapp.models;

import java.io.Serializable;

public class SubTaskModel implements Serializable {

    private int id;
    private String title;
    private String description;
    private String dueDate;
    private boolean done; // trạng thái hoàn thành

    public SubTaskModel(int id, String title, String description, String dueDate, boolean done) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.done = done;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
}
