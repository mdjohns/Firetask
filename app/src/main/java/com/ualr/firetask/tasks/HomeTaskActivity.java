package com.ualr.firetask.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ualr.firetask.R;
import com.ualr.firetask.auth.AuthActivity;
import com.ualr.firetask.help.HelpDialog;
import com.ualr.firetask.models.TaskCategory;
import com.ualr.firetask.settings.FormSettingsDialogFragment;
import com.ualr.firetask.settings.UserSettingsFragment;
import com.ualr.firetask.utils.QuoteUtil;

import java.util.HashMap;
import java.util.Map;

public class HomeTaskActivity extends AppCompatActivity
        implements UserSettingsFragment.UserSettingsListener,
        FormSettingsDialogFragment.FormEventListener,
        NewCategoryDialogFragment.NewCategoryAddedListener
{
    private static final String TAG = HomeTaskActivity.class.getSimpleName();
    private static final String HELP_TAG = HelpDialog.class.getSimpleName();
    private static final String FORM_TAG = FormSettingsDialogFragment.class.getSimpleName();
    private static final String NEW_CATEGORY_TAG = NewCategoryDialogFragment.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseFirestore mDB;
    private BottomNavigationView bottomNav;

    private ImageView noTasksIcon;
    private TextView noTasksText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();
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

        // Views to display when there are no tasks
        noTasksIcon = findViewById(R.id.no_tasks_icon);
        noTasksText = findViewById(R.id.no_tasks);

        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setVisibility(View.VISIBLE);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.task_history:
                        navigateToTaskHistory();
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

        // Firestore Init
        initializeFirestore();
        final CollectionReference allUsers = getAllUsers();
        final String uuid = getUuid();
        DocumentReference currentUserTasks = getUserTasks(allUsers, uuid);
        getUserTasks(currentUserTasks);
    }

    // Firestore Methods
    public void initializeFirestore() {
        mDB = FirebaseFirestore.getInstance();
    }

    public CollectionReference getAllUsers() {
        return mDB.collection("users");
    }

    public String getUuid() {
        return mAuth.getCurrentUser().getUid();
    }

    public DocumentReference getUserTasks(CollectionReference allUsers, String uuid) {
        return allUsers.document(uuid);
    }

    public void getUserTasks(DocumentReference user) {
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot tasks = task.getResult();
                    if (tasks.exists()) {
                        Log.d(TAG, "Tasks found for user, populating recyclerView");
                        hideView(noTasksIcon);
                        hideView(noTasksText);
                        //TODO: Populate recyclerView
                    }
                    else {
                        Log.d(TAG, "No tasks found for user");
                        //TODO: Display empty recyclerView
                    }

                }
                else {
                    Log.w(TAG, "Failed to get document: ", task.getException());
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Failed to fetch: ", e);
                        Toast.makeText(HomeTaskActivity.this, "Error fetching task data", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
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
            case R.id.menu_search:
                openSearch();
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

    public void toggleBottomNav() {
        if (bottomNav.isShown()) {
            bottomNav.setVisibility(View.GONE);
        }
        bottomNav.setVisibility(View.VISIBLE);
    }

    // Open dialogs
    public void openSearch() {
        // Search button selected from menu, open search
    }

    public void openShare() {
        // Share button selected from menu, open share intent
    }

    public void openHelpDialog() {
        HelpDialog helpDialog = new HelpDialog();
        helpDialog.show(getSupportFragmentManager(), HELP_TAG);
    }

    public void openNewCategoryDialog() {
        NewCategoryDialogFragment newCategoryDialogFragment = new NewCategoryDialogFragment();
        newCategoryDialogFragment.show(getSupportFragmentManager(), NEW_CATEGORY_TAG);
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

    // Navigation to other fragments or activities
    public void navigateHome() {
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

    public void navigateToTaskHistory() {
        // Remove bottom nav
    }

    public void navigateToSettings() {
        UserSettingsFragment userSettingsFragment = new UserSettingsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.active_fragment_container, userSettingsFragment);
        ft.commit();

        addBackNavigation(getResources().getString(R.string.settings_title));
        bottomNav.setVisibility(View.GONE);
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

    }

    // Form Dialog Interface methods
    @Override
    public void onSubmitEmailChange(String newEmail) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updateEmail(newEmail);
        }
    }

    @Override
    public void onSubmitPasswordChange(String newPass) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPass);
        }

    }

    // New Category Interface Method
    @Override
    public void onNewCategoryAdded(String category, String firstTask) {

    }
}
