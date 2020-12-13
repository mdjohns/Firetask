package com.ualr.firetask.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ualr.firetask.R;

public class UserGreetingFragment extends Fragment {
    private static final String TAG = UserGreetingFragment.class.getSimpleName();
    private static final String emailKey = "EMAIL";
    private static final String quoteKey = "QUOTE";
    private String mEmail;
    private String mQuote;

    public static UserGreetingFragment newInstance(String email, String quote) {
        UserGreetingFragment userGreetingFragment = new UserGreetingFragment();
        Bundle args = new Bundle();
        args.putString(emailKey, email);
        args.putString(quoteKey, quote);

        userGreetingFragment.setArguments(args);
        return userGreetingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEmail = getArguments().getString(emailKey);
        mQuote = getArguments().getString(quoteKey);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_greeting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView userGreeting = view.findViewById(R.id.user_greeting);
        TextView quote = view.findViewById(R.id.greeting_quote);
        String greetingText = String.format("Welcome, %s!", mEmail);
        userGreeting.setText(greetingText);
        quote.setText(mQuote);
    }
}
