package com.ualr.firetask.settings;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.ualr.firetask.R;

public class FormSettingsDialogFragment extends DialogFragment {
    private static final String TAG = FormSettingsDialogFragment.class.getSimpleName();
    private static final String emailForm = "EMAIL";
    private static final String passwordForm = "PASSWORD";
    private static final String typeKey = "dialogType";
    private String mDialogType;
    private TextInputLayout mFormInput;
    private TextInputLayout mFormInputConfirm;
    private FormEventListener mListener;

    public static FormSettingsDialogFragment newInstance(String type) {
        FormSettingsDialogFragment dialog = new FormSettingsDialogFragment();
        Bundle args = new Bundle();

        if (type.equals(emailForm)) {
            Log.d(TAG, "Creating email form dialog");
            args.putString(typeKey, emailForm);
        } else if (type.equals(passwordForm)) {
            Log.d(TAG, "Creating password form dialog");
            args.putString(typeKey, passwordForm);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_form, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FormEventListener) {
            mListener = (FormEventListener) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = view.findViewById(R.id.form_title);
        MaterialButton submitBtn = view.findViewById(R.id.form_submit_btn);
        mFormInput = view.findViewById(R.id.form_input);
        mFormInputConfirm = view.findViewById(R.id.form_input_confirm);

        if (mDialogType.equals(emailForm)) {
            title.setText(R.string.email_title);
            mFormInput.setHint(R.string.email_hint);
            mFormInputConfirm.setHint(R.string.email_confirm_hint);
        } else if (mDialogType.equals(passwordForm)) {
            title.setText(R.string.password_title);
            mFormInput.setHint(R.string.password_hint);
            mFormInputConfirm.setHint(R.string.password_confirm_hint);
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fieldsMatch()) {
                    if (mDialogType.equals(emailForm)) {
                        String newEmail = mFormInput.getEditText().getText().toString();
                        Log.d(TAG, "Submitting email change");
                        mListener.onSubmitEmailChange(newEmail);
                    }
                    if (mDialogType.equals(passwordForm)) {
                        String newPass = mFormInput.getEditText().getText().toString();
                        Log.d(TAG, "Submitting password change");
                        mListener.onSubmitPasswordChange(newPass);
                    }
                }
                else {
                    mFormInputConfirm.setErrorEnabled(true);
                    Toast.makeText(getActivity(), "Please make sure both fields match before continuing", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public boolean fieldsMatch() {
        if (mFormInput.getEditText() != null && mFormInputConfirm.getEditText() != null) {
            return mFormInput.getEditText().getText().toString().equals(
                    mFormInputConfirm.getEditText().toString()
            );
        }
        return false;
    }

    public interface FormEventListener {
        void onSubmitEmailChange(String newEmail);
        void onSubmitPasswordChange(String newPass);
    }
}
