package com.ualr.firetask.models;

import java.util.ArrayList;


public class TaskCategory {
    private String categoryName;
    private ArrayList<Task> tasks;

    public TaskCategory() {} // Empty constructor for Firestore

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public int getIndexFromName(String taskName) {
        int result = -1;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getName().equals(taskName)) {
                result = i;
            }
        }
        return result;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public Task getTask(int i) {
        return tasks.get(i);
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public double getCompletionPercentage() {
        double numComplete = 0;
        int numElements = tasks.size();

        for (Task task: tasks) {
            if (task.isComplete()) {
                numComplete++;
            }
        }

        return numComplete / numElements;
    }
}
