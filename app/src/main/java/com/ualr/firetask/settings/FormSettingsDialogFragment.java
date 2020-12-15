package com.ualr.firetask.settings;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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

            mFormInput.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            mFormInputConfirm.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

            mFormInput.setHint(R.string.email_hint);
            mFormInputConfirm.setHint(R.string.email_confirm_hint);
        } else if (mDialogType.equals(passwordForm)) {
            title.setText(R.string.password_title);

            mFormInput.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mFormInputConfirm.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

            mFormInput.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            mFormInputConfirm.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

            mFormInput.setHint(R.string.password_hint);
            mFormInputConfirm.setHint(R.string.password_confirm_hint);
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fieldsEmpty()) {

                    if (fieldsMatch()) {
                        if (mDialogType.equals(emailForm)) {
                            String newEmail = mFormInput.getEditText().getText().toString();
                            Log.d(TAG, "Submitting email change");
                            mListener.onSubmitEmailChange(newEmail);
                            dismiss();
                        }
                        if (mDialogType.equals(passwordForm)) {
                            String newPass = mFormInput.getEditText().getText().toString();
                            Log.d(TAG, "Submitting password change");
                            mListener.onSubmitPasswordChange(newPass);
                            dismiss();
                        }
                    }

                }
                else {
                    mFormInput.setError("Fields don't match!");
                    mFormInputConfirm.setError("Fields don't match!");
                }
            }
        });
    }

    private boolean fieldsEmpty() {
        String input1Contents = mFormInput.getEditText().getText().toString();
        String input2Contents = mFormInput.getEditText().getText().toString();

        return input1Contents.equals("") || input2Contents.equals("");
    }

    private boolean fieldsMatch() {
        String input1Contents = mFormInput.getEditText().getText().toString();
        String input2Contents = mFormInput.getEditText().getText().toString();

        return input1Contents.equals(input2Contents);
    }

    public interface FormEventListener {
        void onSubmitEmailChange(String newEmail);
        void onSubmitPasswordChange(String newPass);
    }
}
