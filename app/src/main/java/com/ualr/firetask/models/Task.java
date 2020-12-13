package com.ualr.firetask.models;

public class Task {
    private String name;
    private boolean isComplete;
    private String timestampCreated;
    private String timestampCompleted;

    public Task() {} // Empty constructor for Firestore

    public Task(String name, boolean isComplete, String timestampCreated) {
        this.name = name;
        this.isComplete = isComplete;
        this.timestampCreated = timestampCreated;
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

    public String getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(String timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public String getTimestampCompleted() {
        return timestampCompleted;
    }

    public void setTimestampCompleted(String timestampCompleted) {
        this.timestampCompleted = timestampCompleted;
    }
}
