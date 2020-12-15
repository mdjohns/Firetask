package com.ualr.firetask.tasks;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.ualr.firetask.R;

public class TaskView extends LinearLayout {
    private LinearLayout taskLayout;
    private TextView taskName;

    public MaterialCheckBox taskCheckBox;
    public ImageView editTaskButton;
    public ImageView deleteTaskButton;


    public TaskView(Context context) {
        super(context);
        initTask(context);
    }




    private void initTask(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.layout_task, this);

        taskLayout = this.findViewById(R.id.single_task_layout);
        taskCheckBox = this.findViewById(R.id.task_checkbox);
        taskName = this.findViewById(R.id.task_name);
        editTaskButton = this.findViewById(R.id.edit_task);
        deleteTaskButton = this.findViewById(R.id.delete_task);
    }

    public void populateTask(String name, boolean isComplete) {
        taskName.setText(name);
        taskCheckBox.setChecked(isComplete);
    }

}
