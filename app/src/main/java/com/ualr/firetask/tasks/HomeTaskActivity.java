package com.ualr.firetask.tasks;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ualr.firetask.R;
import com.ualr.firetask.auth.AuthActivity;
import com.ualr.firetask.help.HelpDialog;
import com.ualr.firetask.motivation.MotivationFragment;
import com.ualr.firetask.models.Task;
import com.ualr.firetask.models.TaskCategory;
import com.ualr.firetask.settings.FormSettingsDialogFragment;
import com.ualr.firetask.settings.UserSettingsFragment;
import com.ualr.firetask.utils.DataUtil;
import com.ualr.firetask.utils.FireUtil;
import com.ualr.firetask.utils.QuoteUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeTaskActivity extends AppCompatActivity
        implements UserSettingsFragment.UserSettingsListener,
        FormSettingsDialogFragment.FormEventListener,
        NewTaskDialogFragment.NewTaskAddedListener,
        NewCategoryDialogFragment.NewCategoryAddedListener,
        ConfirmDeleteDialog.ConfirmDeleteDialogListener,
        TaskTextDialog.TaskTextSubmitListener,
        TaskRecyclerFragment.TaskActionListener {

    private static final String TAG = HomeTaskActivity.class.getSimpleName();
    private static final String HELP_TAG = HelpDialog.class.getSimpleName();
    private static final String EDIT_TAG = TaskTextDialog.class.getSimpleName();
    private static final String FORM_TAG = FormSettingsDialogFragment.class.getSimpleName();
    private static final String NEW_CATEGORY_TAG = NewCategoryDialogFragment.class.getSimpleName();
    private static final String NEW_TASK_TAG = NewTaskDialogFragment.class.getSimpleName();
    private static final String CONFIRM_DELETE_TAG = ConfirmDeleteDialog.class.getSimpleName();

    // Task/Category Edit Dialog Types
    private static final String categoryType = "CATEGORY";
    private static final String taskType = "TASK";
    private static final String deleteAccountType = "DELETE";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseFirestore mDB;
    private BottomNavigationView bottomNav;

    // Values for task/category modifications
    private String mEditType;
    private int mCategoryPosition;
    private int mTaskPosition;
    private String mNewCategoryName;
    private String mNewTaskName;

    private void resetCategoryInfo() {
        mCategoryPosition = -1;
        mNewCategoryName = null;
        mEditType = null;
    }

    private void resetTaskInfo() {
        mTaskPosition = -1;
        mNewTaskName = null;
        mEditType = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuthStateListener != null) {
            mAuth.addAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                navigateHome();
                return true;
            case R.id.menu_share:
                openShare();
                return true;
            case R.id.menu_help:
                openHelpDialog();
                return true;
            case R.id.menu_settings:
                navigateToSettings();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Firebase Auth
        mAuth = FireUtil.initializeAuth();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();

                if (user == null) {
                    sendToSignIn();
                }
            }
        };
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initGreeting();

        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setVisibility(View.VISIBLE);
        bottomNav.getMenu().setGroupCheckable(0, false, true);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.motivation:
                        navigateToMotivation();
                        return true;
                    case R.id.new_category:
                        openNewCategoryDialog();
                        return true;
                    case R.id.sign_out:
                        mAuth.signOut();
                        return true;
                    default:
                        return false;
                }
            }
        });

        // Firestore and RecyclerView Init
        mDB = FireUtil.initializeFirestore();
        final CollectionReference allUsers = FireUtil.getAllUsers(mDB);
        final String uuid = FireUtil.getUuid(mAuth);
        DocumentReference userDoc = FireUtil.getUserDocument(allUsers, uuid);
        //populateRecycler(userDoc);
        addTaskRecyclerView();
    }

    public void toggleBottomNav() {
        if (bottomNav.isShown()) {
            bottomNav.setVisibility(View.GONE);
        }
        bottomNav.setVisibility(View.VISIBLE);
    }

    // Open dialogs
    public void openShare() {
        String emailUriText = String.format("mailto:%s", mAuth.getCurrentUser().getEmail());
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setData(Uri.parse(emailUriText));

        startActivity(i);
    }

    public void openHelpDialog() {
        HelpDialog helpDialog = new HelpDialog();
        helpDialog.show(getSupportFragmentManager(), HELP_TAG);
    }

    public void openConfirmDeleteDialog() {
        ConfirmDeleteDialog confirmDeleteDialog = new ConfirmDeleteDialog();
        confirmDeleteDialog.show(getSupportFragmentManager(), CONFIRM_DELETE_TAG);
    }

    public void openNewCategoryDialog() {
        NewCategoryDialogFragment newCategoryDialogFragment = new NewCategoryDialogFragment();
        newCategoryDialogFragment.show(getSupportFragmentManager(), NEW_CATEGORY_TAG);
    }

    public void openNewTaskDialog() {
        NewTaskDialogFragment newTaskDialogFragment = new NewTaskDialogFragment();
        newTaskDialogFragment.show(getSupportFragmentManager(), NEW_TASK_TAG);
    }

    public void hideView(View view) {
        view.setVisibility(View.GONE);
    }

    public void initGreeting() {
        String userEmail = mAuth.getCurrentUser().getEmail();
        String randomQuote = QuoteUtil.getRandomQuote(this);
        UserGreetingFragment fragment = UserGreetingFragment.newInstance(userEmail, randomQuote);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.greeting_fragment_container, fragment);
        ft.commit();
    }

    public void addTaskRecyclerView() {
        TaskRecyclerFragment taskRecyclerFragment = new TaskRecyclerFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.active_fragment_container, taskRecyclerFragment);
        ft.commit();
    }

    // Navigation to other fragments or activities
    public void navigateHome() {
        initGreeting();
        TaskRecyclerFragment taskRecyclerFragment = new TaskRecyclerFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.active_fragment_container, taskRecyclerFragment);
        ft.commit();
        removeBackNavigation();
    }

    public void sendToSignIn() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }

    public void navigateToMotivation() {
        MotivationFragment motivationFragment = new MotivationFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.active_fragment_container, motivationFragment);
        ft.commit();

        addBackNavigation("Motivation");
        toggleBottomNav();
        hideView(getSupportFragmentManager().findFragmentById(R.id.greeting_fragment_container).getView());

    }

    public void navigateToSettings() {
        UserSettingsFragment userSettingsFragment = new UserSettingsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.active_fragment_container, userSettingsFragment);
        ft.commit();

        addBackNavigation(getResources().getString(R.string.settings_title));
        toggleBottomNav();
    }


    // Navigation -> Backwards navigate
    @Override
    public boolean onSupportNavigateUp() {
        navigateHome();
        toggleBottomNav();
        return true;
    }

    public void addBackNavigation(String newTitle) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(newTitle);
        }
    }

    public void removeBackNavigation() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }
    }

    // User Settings Interface methods
    @Override
    public void onChangePasswordClicked() {
        // Open form dialog with new password field
        FormSettingsDialogFragment form = FormSettingsDialogFragment.newInstance("PASSWORD");
        form.show(getSupportFragmentManager(), FORM_TAG);
    }

    @Override
    public void onChangeEmailClicked() {
        // Open form dialog with new email field
        FormSettingsDialogFragment form = FormSettingsDialogFragment.newInstance("EMAIL");
        form.show(getSupportFragmentManager(), FORM_TAG);
    }

    @Override
    public void onDeleteAccountClicked() {
        // Open dialog with confirm button
        mEditType = deleteAccountType;
        openConfirmDeleteDialog();
    }

    // Form Dialog Interface methods
    @Override
    public void onSubmitEmailChange(String newEmail) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(HomeTaskActivity.this, "Email changed successfully", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(HomeTaskActivity.this, "There was an error updating your email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onSubmitPasswordChange(String newPass) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(HomeTaskActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(HomeTaskActivity.this, "There was an error updating your password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    // New Category Interface Method
    @Override
    public void onNewCategoryAdded(String category, String firstTask) {
        final TaskCategory newCategory = DataUtil.createInitialTaskCategory(category, firstTask);
        final DocumentReference userDoc = FireUtil.getUserDocument(FireUtil.getAllUsers(mDB), FireUtil.getUuid(mAuth));

        FireUtil.getUserTasks(userDoc, new FireUtil.FireCallbacks() {
            @Override
            public void getUserTasks(ArrayList<Map<String, Object>> rawData) {
                if (rawData.size() == 0) {
                    HashMap<String, ArrayList<TaskCategory>> newBundle = DataUtil.createInitialTaskBundle(newCategory);
                    userDoc.set(newBundle).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "First task added");
                            }
                            
                            else {
                                Toast.makeText(HomeTaskActivity.this, "Error adding first task category", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    
                }
                else {
                    FireUtil.addCategory(FireUtil.getAllUsers(mDB), userDoc, newCategory);
                }
            }
        });

        //FireUtil.addCategory(FireUtil.getAllUsers(mDB), userDoc, newCategory);
    }

    // New Task Interface Method
    @Override
    public void onNewTaskAdded(String taskName) {
        CollectionReference allUsers = FireUtil.getAllUsers(mDB);
        DocumentReference user = FireUtil.getUserDocument(allUsers, FireUtil.getUuid(mAuth));
        Task newTask = new Task(taskName);
        FireUtil.addTask(allUsers, user, mCategoryPosition, newTask);

        resetTaskInfo();
        resetCategoryInfo();
    }


    // Task and Task Category Interface Methods from RecyclerView
    @Override
    public void onNewTaskClicked(int categoryPosition) {
        mCategoryPosition = categoryPosition;
        mEditType = taskType;
        openNewTaskDialog();
    }

    @Override
    public void onTaskEditClicked(int categoryPosition, int taskPosition) {
        mCategoryPosition = categoryPosition;
        mTaskPosition = taskPosition;

        TaskTextDialog taskTextDialog = TaskTextDialog.newInstance(taskType);
        taskTextDialog.show(getSupportFragmentManager(), EDIT_TAG);
    }

    public void completeTaskEdit(int categoryPosition, int taskPosition, String newTaskName) {
        CollectionReference allUsers = FireUtil.getAllUsers(mDB);
        DocumentReference currentUser = FireUtil.getUserDocument(allUsers, FireUtil.getUuid(mAuth));

        Log.d(TAG, String.format("Confirming task edit for task %d in category %d", taskPosition, categoryPosition));

        FireUtil.editTaskName(allUsers, currentUser, categoryPosition, taskPosition, newTaskName);

        resetCategoryInfo();
        resetTaskInfo();
    }

    @Override
    public void onTaskDeleteClicked(int categoryPosition, int taskPosition) {
        mCategoryPosition = categoryPosition;
        mEditType = taskType;
        mTaskPosition = taskPosition;
        openConfirmDeleteDialog();
    }

    @Override
    public void onTaskChecked(boolean isComplete, int categoryPosition, int taskPosition) {
        CollectionReference allUsers = FireUtil.getAllUsers(mDB);
        DocumentReference currentUser = FireUtil.getUserDocument(allUsers, FireUtil.getUuid(mAuth));
        Log.d(TAG, String.format("Task %d at category position %d completion status: %b", taskPosition, categoryPosition, isComplete));
        FireUtil.updateTaskStatus(allUsers, currentUser, categoryPosition, taskPosition, isComplete);
    }

    @Override
    public void onCategoryEditClicked(int categoryPosition) {
        mCategoryPosition = categoryPosition;
        mEditType = categoryType;

        TaskTextDialog taskTextDialog = TaskTextDialog.newInstance(categoryType);
        taskTextDialog.show(getSupportFragmentManager(), EDIT_TAG);
    }

    public void completeCategoryEdit(int categoryPosition, String newCategoryName) {
        CollectionReference allUsers = FireUtil.getAllUsers(mDB);
        DocumentReference currentUser = FireUtil.getUserDocument(allUsers, FireUtil.getUuid(mAuth));

        Log.d(TAG, String.format("Confirming category edit for category at position %d", categoryPosition));

        FireUtil.editCategoryName(allUsers, currentUser, newCategoryName, categoryPosition);
        resetCategoryInfo();
    }

    @Override
    public void onCategoryDeleteClicked(int categoryPosition) {
        mCategoryPosition = categoryPosition;
        mEditType = categoryType;
        openConfirmDeleteDialog();
    }

    public void completeCategoryDelete(int categoryPosition) {
        CollectionReference allUsers = FireUtil.getAllUsers(mDB);
        DocumentReference currentUser = FireUtil.getUserDocument(allUsers, FireUtil.getUuid(mAuth));

        Log.d(TAG, String.format("Confirming category delete for category at position %d", categoryPosition));

        FireUtil.deleteCategory(allUsers, currentUser, categoryPosition);
        resetCategoryInfo();
    }

    public void completeTaskDelete(int categoryPosition, int taskPosition) {
        CollectionReference allUsers = FireUtil.getAllUsers(mDB);
        DocumentReference currentUser = FireUtil.getUserDocument(allUsers, FireUtil.getUuid(mAuth));

        Log.d(TAG, String.format("Confirming task delete for task %d in category %d", taskPosition, categoryPosition));

        FireUtil.deleteTask(allUsers, currentUser, categoryPosition, taskPosition);
        resetTaskInfo();
        resetCategoryInfo();
    }

    public void completeAccountDelete() {
        FirebaseUser user = mAuth.getCurrentUser();
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(HomeTaskActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.w(TAG, "Error: ", task.getException());
                    Toast.makeText(HomeTaskActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Confirm Delete Dialog Interface method
    @Override
    public void onDeleteConfirm() {
        if (mEditType.equals(categoryType)) {
            completeCategoryDelete(mCategoryPosition);
        } else if (mEditType.equals(taskType)) {
            completeTaskDelete(mCategoryPosition, mTaskPosition);
        } else if (mEditType.equals(deleteAccountType)) {
            completeAccountDelete();
        }
    }

    // Task / Task Category Edit Dialog Interface method
    @Override
    public void onTextSubmitted(String newText, String dialogType) {
        if (dialogType.equals(categoryType)) {
            completeCategoryEdit(mCategoryPosition, newText);
        } else if (dialogType.equals(taskType)) {
            completeTaskEdit(mCategoryPosition, mTaskPosition, newText);
        }
    }

}