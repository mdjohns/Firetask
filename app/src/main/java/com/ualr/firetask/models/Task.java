package com.ualr.firetask.models;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Task {
    private String name;
    private boolean isComplete;
    private Timestamp dateAdded;
    private Timestamp dateComplete;

    public Task() {} // Empty constructor for Firestore

    public Task(String name, boolean isComplete, Timestamp dateAdded) {
        this.name = name;
        this.isComplete = isComplete;
        this.dateAdded = dateAdded;
    }
    public Task(String name) {
        this.name = name;
        this.isComplete = false;
        this.dateAdded = new Timestamp(new Date());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Timestamp dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Timestamp getDateComplete() {
        return dateComplete;
    }

    public void setDateComplete(Timestamp dateComplete) {
        this.dateComplete = dateComplete;
    }
}
