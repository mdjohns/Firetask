package com.ualr.firetask.auth;

import android.content.Context;
import android.os.Bundle;
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

public class SignInFragment extends Fragment {
    private OnRegisterClickedListener mRegisterListener;
    private OnSignInListener mSignInListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnRegisterClickedListener) {
            mRegisterListener = (OnRegisterClickedListener) context;
        }
        if (context instanceof  OnSignInListener) {
            mSignInListener = (OnSignInListener) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextInputLayout emailET = view.findViewById(R.id.email_edit_text);
        final TextInputLayout passET = view.findViewById(R.id.password_edit_text);
        MaterialButton signInBtn = view.findViewById(R.id.sign_in_btn);
        TextView registerLink = view.findViewById(R.id.register_link);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to register fragment
                mRegisterListener.onRegisterClicked();
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailET.getEditText().getText().toString();
                String pass = passET.getEditText().getText().toString();

                if (email.equals("") || pass.equals("")) {
                    displayToast("Please fill out both fields before submitting");
                }
                else {
                    mSignInListener.onSignIn(email, pass);
                }
            }
        });
    }

    public void displayToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    public interface OnRegisterClickedListener {
        void onRegisterClicked();
    }

    public interface OnSignInListener {
        void onSignIn(String email, String pass);
    }
}
