package com.ualr.firetask.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ualr.firetask.models.TaskCategory;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class FireUtil {
    private static final String TAG = FireUtil.class.getSimpleName();

    public interface FireCallbacks {
        void getUserTasks(ArrayList<Map<String, Object>> rawData);
    }

    public static FirebaseFirestore initializeFirestore() {
        Log.d(TAG, "Initializing Firestore");
        return FirebaseFirestore.getInstance();
    }

    public static FirebaseAuth initializeAuth() {
        Log.d(TAG, "Initializing FirebaseAuth");
        return FirebaseAuth.getInstance();
    }

    public static String getUuid(FirebaseAuth auth) {
        return auth.getCurrentUser().getUid();
    }

    public static CollectionReference getAllUsers(FirebaseFirestore db) {
        return db.collection("users");
    }

    public static DocumentReference getUserDocument(CollectionReference allUsers, String uuid) {
        return allUsers.document(uuid);
    }

    // Firebase Data methods

    public static void getUserTasks(DocumentReference user, final FireCallbacks fireCallbacks) {
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        fireCallbacks.getUserTasks((ArrayList<Map<String,Object>>) doc.get("categories"));
                    }
                    else {
                        ArrayList<Map<String, Object>> nullArray = new ArrayList<>();
                        fireCallbacks.getUserTasks(nullArray);
                    }

                }
            }
        });
    }

    public static void addCategory(CollectionReference allUsers, final DocumentReference userDoc, final TaskCategory newCategory) {
        getUserTasks(userDoc, new FireCallbacks() {
            @Override
            public void getUserTasks(ArrayList<Map<String, Object>> rawData) {
                ArrayList<Map<String, Object>> rawTaskCategories = rawData;
                ArrayList<TaskCategory> taskCategories = DataUtil.getTaskCategories(rawData);
                taskCategories.add(newCategory);

                userDoc.update("categories", taskCategories);
            }
        });
    }

    public static void editCategoryName(CollectionReference allUsers, final DocumentReference userDoc, final String newCategoryName, final int categoryPosition) {
                getUserTasks(userDoc, new FireCallbacks() {
                    @Override
                    public void getUserTasks(ArrayList<Map<String, Object>> rawData) {
                        ArrayList<Map<String, Object>> rawTaskCategories = rawData;
                        ArrayList<TaskCategory> taskCategories = DataUtil.getTaskCategories(rawTaskCategories);
                        taskCategories.get(categoryPosition).setCategoryName(newCategoryName);
                        userDoc.update("categories", taskCategories);
                    }
                });
    }

    public static void deleteCategory(CollectionReference allUsers, final DocumentReference userDoc, final int categoryPosition) {
        getUserTasks(userDoc, new FireCallbacks() {
            @Override
            public void getUserTasks(ArrayList<Map<String, Object>> rawData) {
                ArrayList<Map<String, Object>> rawTaskCategories = rawData;
                ArrayList<TaskCategory> taskCategories = DataUtil.getTaskCategories(rawTaskCategories);
                taskCategories.remove(categoryPosition);
                userDoc.update("categories", taskCategories);
            }
        });

    }

    public static void updateTaskStatus(CollectionReference allUsers, final DocumentReference userDoc, final int categoryPosition, final int taskPosition, final boolean isComplete) {
        getUserTasks(userDoc, new FireCallbacks() {
            @Override
            public void getUserTasks(ArrayList<Map<String, Object>> rawData) {
                ArrayList<Map<String, Object>> rawTaskCategories = rawData;
                ArrayList<TaskCategory> taskCategories = DataUtil.getTaskCategories(rawTaskCategories);
                com.ualr.firetask.models.Task currentTask = taskCategories.get(categoryPosition).getTask(taskPosition);
                currentTask.setComplete(isComplete);

                if (currentTask.isComplete()) {
                    currentTask.setDateComplete(new Timestamp(new Date()));
                }
                else {
                    currentTask.setDateComplete(null);
                }

                taskCategories.get(categoryPosition).getTasks().set(taskPosition, currentTask);

                userDoc.update("categories", taskCategories);
            }
        });
    }

    public static void deleteTask(CollectionReference allUsers, final DocumentReference userDoc, final int categoryPosition, final int taskPosition) {
        getUserTasks(userDoc, new FireCallbacks() {
            @Override
            public void getUserTasks(ArrayList<Map<String, Object>> rawData) {
                ArrayList<Map<String, Object>> rawTaskCategories = rawData;
                ArrayList<TaskCategory> taskCategories = DataUtil.getTaskCategories(rawTaskCategories);
                taskCategories.get(categoryPosition).getTasks().remove(taskPosition);
                userDoc.update("categories", taskCategories);
            }
        });
    }

    public static void editTaskName(CollectionReference allUsers, final DocumentReference userDoc, final int categoryPosition, final int taskPosition, final String newTaskName) {
        getUserTasks(userDoc, new FireCallbacks() {
            @Override
            public void getUserTasks(ArrayList<Map<String, Object>> rawData) {
                ArrayList<Map<String, Object>> rawTaskCategories = rawData;
                ArrayList<TaskCategory> taskCategories = DataUtil.getTaskCategories(rawTaskCategories);
                taskCategories.get(categoryPosition).getTask(taskPosition).setName(newTaskName);
                userDoc.update("categories", taskCategories);
            }
        });
    }

    public static void addTask(CollectionReference allUsers, final DocumentReference userDoc, final int categoryPosition, final com.ualr.firetask.models.Task newTask) {
        getUserTasks(userDoc, new FireCallbacks() {
            @Override
            public void getUserTasks(ArrayList<Map<String, Object>> rawData) {
                ArrayList<Map<String, Object>> rawTaskCategories = rawData;
                ArrayList<TaskCategory> taskCategories = DataUtil.getTaskCategories(rawTaskCategories);
                taskCategories.get(categoryPosition).addTask(newTask);
                userDoc.update("categories", taskCategories);
            }
        });
    }
}
