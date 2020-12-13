package com.ualr.firetask.models;

import java.util.List;

public class TaskCategory {
    private String categoryName;
    private List<Task> taskList;

    public TaskCategory() {} // Empty constructor for Firestore

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public Task getTask(int i) {
        return taskList.get(i);
    }

    public void updateTask(Task task, int i) {
        taskList.set(i, task);
    }

    public int getTaskCount() {
        return taskList.size();
    }
}
