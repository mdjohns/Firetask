package com.ualr.firetask.utils;

import com.google.firebase.Timestamp;
import com.ualr.firetask.models.Task;
import com.ualr.firetask.models.TaskCategory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataUtil {
    private static final String TAG = DataUtil.class.getSimpleName();

    // Transform generic data returned from Firebase to Task
    public static Task getTaskFromMap(Map<String, Object> taskMap) {
        Task newTask = new Task();
        newTask.setName(taskMap.get("name").toString());
        newTask.setComplete((Boolean) taskMap.get("complete"));
        newTask.setDateAdded((Timestamp) taskMap.get("dateAdded"));

        if (newTask.isComplete()) {
            newTask.setDateComplete((Timestamp) taskMap.get("dateComplete"));
        }

        return newTask;
    }

    // Transform generic data returned from Firebase to TaskCategory
    public static TaskCategory getTaskCategoryFromMap(Map<String, Object> taskCategoryMap) {
        TaskCategory newCategory = new TaskCategory();
        newCategory.setCategoryName(taskCategoryMap.get("categoryName").toString());

        ArrayList<Map<String,Object>> taskObjs = (ArrayList<Map<String,Object>>) taskCategoryMap.get("tasks");
        ArrayList<Task> tasks = new ArrayList<>();
        for (Map<String, Object> taskObj : taskObjs) {
            tasks.add(getTaskFromMap(taskObj));
        }
        newCategory.setTasks(tasks);

        return newCategory;
    }

    // Given an ArrayList of generic maps from Firebase, return an ArrayList of TaskCategory
    public static ArrayList<TaskCategory> getTaskCategories(ArrayList<Map<String,Object>> categories) {
        ArrayList<TaskCategory> taskCategories = new ArrayList<>();
        for (Map<String, Object> obj: categories) {
            taskCategories.add(getTaskCategoryFromMap(obj));
        }
        return taskCategories;
    }

    public static TaskCategory createInitialTaskCategory(String category, String firstTask) {
        TaskCategory newCategory = new TaskCategory();
        Timestamp created = new Timestamp(new Date());
        Task newTask = new Task(firstTask, false, created);
        ArrayList<Task> newTaskList = new ArrayList<>();
        newTaskList.add(newTask);
        newCategory.setCategoryName(category);
        newCategory.setTasks(newTaskList);

        return newCategory;
    }

    // Create ArrayList with one TaskCategory for new user
    public static HashMap<String, ArrayList<TaskCategory>> createInitialTaskBundle(TaskCategory firstTask) {
        ArrayList<TaskCategory> initialCategories = new ArrayList<>();
        initialCategories.add(firstTask);

        HashMap<String, ArrayList<TaskCategory>> bundle = new HashMap<>();
        bundle.put("categories", initialCategories);
        return bundle;
    }
}
