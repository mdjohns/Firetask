package com.ualr.firetask.tasks;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.ualr.firetask.R;

public class NewTaskDialogFragment extends DialogFragment {
    private static final String TAG = NewTaskDialogFragment.class.getSimpleName();
    private NewTaskAddedListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof NewTaskAddedListener) {
            mListener = (NewTaskAddedListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_MaterialComponents_DayNight_Dialog_MinWidth);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_new_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextInputLayout newTask = view.findViewById(R.id.new_task_input);
        MaterialButton submitBtn = view.findViewById(R.id.new_task_submit_btn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fieldEmpty(newTask)) {
                    String newTaskName = newTask.getEditText().getText().toString();
                    mListener.onNewTaskAdded(newTaskName);
                    dismiss();
                }
                else {
                    newTask.setError("Input a task name");
                }
            }
        });

    }

    private boolean fieldEmpty(TextInputLayout input) {
        return input.getEditText().getText().toString().equals("");
    }

    public interface NewTaskAddedListener {
        void onNewTaskAdded(String taskName);
    }
}
