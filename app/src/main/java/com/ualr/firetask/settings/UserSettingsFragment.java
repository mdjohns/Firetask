package com.ualr.firetask.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.ualr.firetask.R;

public class UserSettingsFragment extends Fragment {
    private static final String TAG = UserSettingsFragment.class.getSimpleName();

    private UserSettingsListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof UserSettingsListener) {
            mListener = (UserSettingsListener) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton changePasswordBtn = view.findViewById(R.id.change_password_btn);
        MaterialButton changeEmailBtn = view.findViewById(R.id.change_email_btn);
        MaterialButton deleteAccountBtn = view.findViewById(R.id.close_account_btn);

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onChangePasswordClicked();
            }
        });

        changeEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onChangeEmailClicked();
            }
        });

        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDeleteAccountClicked();
            }
        });
    }

    public interface UserSettingsListener {
        void onChangePasswordClicked();
        void onChangeEmailClicked();
        void onDeleteAccountClicked();
    }
}
