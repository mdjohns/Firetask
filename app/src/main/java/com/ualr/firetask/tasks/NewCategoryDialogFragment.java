package com.ualr.firetask.tasks;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.ualr.firetask.R;

public class NewCategoryDialogFragment extends DialogFragment {
    private static final String TAG = NewCategoryDialogFragment.class.getSimpleName();
    private NewCategoryAddedListener mListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_MaterialComponents_DayNight_Dialog_MinWidth);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_new_category, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof NewCategoryAddedListener) {
            mListener = (NewCategoryAddedListener) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextInputLayout newCategory = view.findViewById(R.id.new_category_input);
        final TextInputLayout firstTask = view.findViewById(R.id.new_category_firt_task_input);
        MaterialButton submitBtn = view.findViewById(R.id.new_category_submit_btn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fieldsEmpty(newCategory, firstTask)) {
                    String newCategoryText = newCategory.getEditText().getText().toString();
                    String firstTaskText = firstTask.getEditText().getText().toString();
                    mListener.onNewCategoryAdded(newCategoryText, firstTaskText);
                    dismiss();
                }

            }
        });
    }

    private boolean fieldsEmpty(TextInputLayout input1, TextInputLayout input2) {
        return input1.getEditText().getText().toString().equals("")
                && input2.getEditText().getText().toString().equals("");
    }

    public interface NewCategoryAddedListener {
        void onNewCategoryAdded(String category, String firstTask);
    }
}
