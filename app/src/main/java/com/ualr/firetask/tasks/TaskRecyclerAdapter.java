package com.ualr.firetask.tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.ualr.firetask.R;
import com.ualr.firetask.models.TaskCategory;

import java.util.List;

public class TaskRecyclerAdapter  extends RecyclerView.Adapter {
    private static final String TAG = TaskRecyclerAdapter.class.getSimpleName();
    private List<TaskCategory> mItems;
    private Context mContext;


    public TaskRecyclerAdapter(Context context, List<TaskCategory> taskCategoryList) {
        this.mItems = taskCategoryList;
        this.mContext = context;
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TaskCategoryViewHolder viewHolder = (TaskCategoryViewHolder) holder;
        TaskCategory taskCategory = mItems.get(position);

        viewHolder.categoryTitle.setText(taskCategory.getCategoryName());

    }

    @Override
    public int getItemCount() {
        return this.mItems.size();
    }

    private class TaskCategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryTitle;
        public View layoutParent;
        public MaterialButton addTaskBtn;
        public ImageButton optionsMenu;

        public TaskCategoryViewHolder(View v) {
            super(v);
            categoryTitle = v.findViewById(R.id.task_category_title);
            optionsMenu = v.findViewById(R.id.category_options_menu);
            layoutParent = v.findViewById(R.id.task_category_layout);
            addTaskBtn = v.findViewById(R.id.add_task_btn);

        }
    }

    public interface TaskActionListener {
        void onNewTaskButtonClicked(View v, TaskCategory category, int position);
        void onTaskEditClicked();
        void onTaskCategoryEditClicked();
        void onTaskDeleteClicked();
    }
}
