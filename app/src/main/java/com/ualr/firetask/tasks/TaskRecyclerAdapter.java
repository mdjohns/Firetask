package com.ualr.firetask.tasks;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.ualr.firetask.R;
import com.ualr.firetask.models.Task;
import com.ualr.firetask.models.TaskCategory;

import java.util.ArrayList;

public class TaskRecyclerAdapter  extends RecyclerView.Adapter implements PopupMenu.OnMenuItemClickListener {
    private static final String TAG = TaskRecyclerAdapter.class.getSimpleName();
    private ArrayList<TaskCategory> mItems;
    private Context mContext;
    private TaskRecyclerFragment.TaskActionListener mListener;
    private int selectedCategory;




    public TaskRecyclerAdapter(Context context, ArrayList<TaskCategory> taskCategoryList, TaskRecyclerFragment.TaskActionListener taskActionListener) {
        this.mItems = taskCategoryList;
        this.mContext = context;
        this.mListener = taskActionListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View taskCategoryView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_task_category, parent, false);
        vh = new TaskCategoryViewHolder(taskCategoryView);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        TaskCategoryViewHolder viewHolder = (TaskCategoryViewHolder) holder;
        final TaskCategory taskCategory = mItems.get(position);
        ArrayList<Task> tasks = taskCategory.getTasks();
        Log.d(TAG, String.format("Adapter size: %d", mItems.size()));

        viewHolder.categoryTitle.setText(taskCategory.getCategoryName());

        viewHolder.addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onNewTaskClicked(position);
            }
        });
        viewHolder.optionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCategory = position;
                showCategoryOptions(view);
            }
        });
        for (final Task task: tasks) {
            final TaskView taskView = new TaskView(mContext);
            taskView.populateTask(task.getName(), task.isComplete());
            final int taskIndex = mItems.get(position).getIndexFromName(task.getName());


            taskView.editTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onTaskEditClicked(position, taskIndex);
                }
            });
            taskView.taskCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onTaskChecked(taskView.taskCheckBox.isChecked(), position, taskIndex);
                }
            });
            taskView.deleteTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onTaskDeleteClicked(position, taskIndex);
                }
            });

            viewHolder.tasksLayout.addView(taskView);
        }

    }

    public void showCategoryOptions(View v) {
        PopupMenu popupMenu = new PopupMenu(mContext, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.menu_category_options);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.category_edit_name:
                mListener.onCategoryEditClicked(selectedCategory);
                return true;
            case R.id.category_delete:
                mListener.onCategoryDeleteClicked(selectedCategory);
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getItemCount() {
        return this.mItems.size();
    }

    public void setNewContent(ArrayList<TaskCategory> newCategories) {
        this.mItems = newCategories;
    }

    private class TaskCategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryTitle;
        public View layoutParent;
        public MaterialButton addTaskBtn;
        public ImageButton optionsMenu;
        public LinearLayout tasksLayout;

        public TaskCategoryViewHolder(View v) {
            super(v);
            categoryTitle = v.findViewById(R.id.task_category_title);
            optionsMenu = v.findViewById(R.id.category_options_menu);
            layoutParent = v.findViewById(R.id.task_category_layout);
            addTaskBtn = v.findViewById(R.id.add_task_btn);
            tasksLayout = v.findViewById(R.id.tasks);
        }
    }
}
