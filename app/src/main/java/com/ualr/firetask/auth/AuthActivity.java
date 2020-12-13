package com.ualr.firetask.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ualr.firetask.R;
import com.ualr.firetask.tasks.HomeTaskActivity;

public class AuthActivity extends AppCompatActivity
        implements
        SignInFragment.OnRegisterClickedListener,
        SignInFragment.OnSignInListener,
        RegisterFragment.OnSignInClickedListener,
        RegisterFragment.OnRegisterListener {

    private static final String TAG = AuthActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // By default navigate to sign in
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new SignInFragment());
        ft.commit();

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {
                    sendToTasks();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuthStateListener != null) {
            mAuth.addAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    public void sendToTasks() {
        Intent intent = new Intent(this, HomeTaskActivity.class);
        startActivity(intent);
    }

    public void sendToRegister() {
        RegisterFragment registerFragment = new RegisterFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, registerFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        //ft.addToBackStack(null);
        ft.commit();
    }

    public void sendToSignIn() {
        SignInFragment signInFragment = new SignInFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, signInFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        //ft.addToBackStack(null);
        ft.commit();
    }

    // Navigation between register/sign-in fragments
    @Override
    public void onRegisterClicked() { sendToRegister(); }

    @Override
    public void onSignInClicked() {
        sendToSignIn();
    }

    // Firebase actions from fragments
    @Override
    public void onSignIn(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User signed in with email, redirecting to tasks...");
                        } else {
                            Toast.makeText(getApplicationContext(), "There was an error signing in", Toast.LENGTH_LONG).show();
                            Log.w(TAG, "Failed to sign in: " + task.getException());
                        }
                    }
                });
    }

    @Override
    public void onRegister(String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User registered, redirecting to tasks");
                        } else {
                            Toast.makeText(getApplicationContext(), "There was an error registering", Toast.LENGTH_LONG).show();
                            Log.w(TAG, "Failed to register: " + task.getException());
                        }
                    }
                });
    }
}
