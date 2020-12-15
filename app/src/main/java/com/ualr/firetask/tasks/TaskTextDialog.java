package com.ualr.firetask.tasks;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.ualr.firetask.R;

public class TaskTextDialog extends DialogFragment {
    private static final String TAG = TaskTextDialog.class.getSimpleName();
    private static final String typeKey = "dialogType";
    private static final String typeTask = "TASK";
    private static final String typeCategory = "CATEGORY";
    private String mDialogType;
    private TaskTextSubmitListener mListener;


    public static TaskTextDialog newInstance(String type) {
        TaskTextDialog dialog = new TaskTextDialog();
        Bundle args = new Bundle();

        if (type.equals(typeTask)) {
            Log.d(TAG, "Creating task edit dialog");
            args.putString(typeKey, typeTask);
        }
        else if (type.equals(typeCategory)) {
            Log.d(TAG, "Creating task category edit dialog");
            args.putString(typeKey, typeCategory);
        }

        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_MaterialComponents_DayNight_Dialog_MinWidth);
        mDialogType = getArguments().getString(typeKey);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof TaskTextSubmitListener) {
            mListener = (TaskTextSubmitListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_task_text, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = view.findViewById(R.id.edit_dialog_title);
        final TextInputLayout editText = view.findViewById(R.id.task_text_input);
        MaterialButton confirmBtn = view.findViewById(R.id.task_edit_confirm_btn);

        if (mDialogType.equals(typeTask)) {
            title.setText(getResources().getString(R.string.task_edit_title));
            editText.setHint(getResources().getString(R.string.task_edit_hint));
        }
        else if (mDialogType.equals(typeCategory)) {
            title.setText(getResources().getString(R.string.task_category_edit_title));
            editText.setHint(getResources().getString(R.string.task_category_edit_hint));
        }

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fieldsEmpty(editText)){
                    mListener.onTextSubmitted(editText.getEditText().getText().toString(), mDialogType);
                    dismiss();
                }
                else {
                    editText.setErrorEnabled(true);
                    editText.getEditText().setError("Text field is empty!");
                }
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                editText.setErrorEnabled(false);
                return false;
            }
        });
    }

    private boolean fieldsEmpty(TextInputLayout input) {
        return input.getEditText().getText().toString().equals("");
    }

    public interface TaskTextSubmitListener {
        void onTextSubmitted(String newText, String dialogType);
    }
}
