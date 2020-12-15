package com.ualr.firetask.tasks;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ualr.firetask.R;
import com.ualr.firetask.models.Task;
import com.ualr.firetask.models.TaskCategory;
import com.ualr.firetask.utils.DataUtil;

import java.util.ArrayList;
import java.util.Map;

public class TaskRecyclerFragment extends Fragment {
    private static final String TAG = TaskRecyclerFragment.class.getSimpleName();
    private FirebaseFirestore mDB;
    private FirebaseAuth mAuth;
    private Context mContext;
    private TaskRecyclerAdapter mAdapter;
    private RecyclerView recyclerView;
    private TaskActionListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof TaskActionListener) {
            mListener = (TaskActionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_taskview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDB = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final CollectionReference users = mDB.collection("users");
        final DocumentReference tasksRef = users.document(mAuth.getUid());
        tasksRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed:", error);
                    Toast.makeText(mContext, "There was an error getting tasks", Toast.LENGTH_LONG).show();
                }

                if (value != null && value.exists()) {
                    // User has task data, get list of TaskCategory
                    Map<String, Object> results = value.getData();
                    ArrayList<Map<String, Object>> categories = (ArrayList<Map<String, Object>>) results.get("categories");

                    initRecycler(DataUtil.getTaskCategories(categories), view);
                }
            }
        });
    }

    private void initRecycler(ArrayList<TaskCategory> taskCategories, View view) {
        view.findViewById(R.id.no_tasks_layout).setVisibility(View.GONE);
        mAdapter = new TaskRecyclerAdapter(mContext, taskCategories, mListener);
        recyclerView = view.findViewById(R.id.task_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    public interface TaskActionListener {
        void onNewTaskClicked(int categoryPosition);

        void onTaskEditClicked(int categoryPosition, int taskPosition);

        void onTaskDeleteClicked(int categoryPosition, int taskPosition);

        void onTaskChecked(boolean isComplete, int categoryPosition, int taskPosition);

        void onCategoryEditClicked(int categoryPosition);

        void onCategoryDeleteClicked(int categoryPosition);
    }

}
