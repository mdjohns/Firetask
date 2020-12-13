package com.ualr.firetask.auth;

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
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.ualr.firetask.R;

public class RegisterFragment extends Fragment {
    private static final String TAG = RegisterFragment.class.getSimpleName();
    private OnSignInClickedListener mSignInListener;
    private OnRegisterListener mRegisterListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnSignInClickedListener) {
            mSignInListener = (OnSignInClickedListener) context;
        }
        if (context instanceof OnRegisterListener) {
            mRegisterListener = (OnRegisterListener) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextInputLayout emailET = view.findViewById(R.id.email_edit_text);
        final TextInputLayout passET = view.findViewById(R.id.password_edit_text);
        MaterialButton registerBtn = view.findViewById(R.id.register_btn);
        TextView signInLink = view.findViewById(R.id.sign_in_link);

        signInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to sign in fragment
                mSignInListener.onSignInClicked();
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailET.getEditText().getText().toString();
                String pass = passET.getEditText().getText().toString();

                if (email.equals("") || pass.equals("")) {
                    displayToast("Please fill out both fields before submitting");
                }
                else {
                    Log.d(TAG, "Email " + email);
                    Log.d(TAG, "Pass " + pass);
                    mRegisterListener.onRegister(email, pass);
                }
            }
        });
    }

    public void displayToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public interface OnSignInClickedListener {
        void onSignInClicked();
    }
    public interface OnRegisterListener {
        void onRegister(String email, String pass);
    }
}
