package com.example.todoapp.models;

import java.io.Serializable;

public class SubTaskModel implements Serializable {

    private String title;
    private String dueDate;
    private boolean done; // trạng thái hoàn thành

    public SubTaskModel(String title, String dueDate) {
        this.title = title;
        this.dueDate = dueDate;
        this.done = false; // mặc định chưa hoàn thành
    }

    // Getter & Setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
