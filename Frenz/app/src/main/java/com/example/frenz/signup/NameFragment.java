package com.example.frenz.signup;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.frenz.R;
import com.google.android.material.textfield.TextInputEditText;

public class NameFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextInputEditText firstName, lastName;
    private CardView confirmDetailBtn;
    private OnNameDetailsEnteredListener mListener;

    private SignUpViewModel signUpViewModel;

    private String mParam1;
    private String mParam2;

    public NameFragment() {
        // Required empty public constructor
    }

    public static NameFragment newInstance(String param1, String param2) {
        NameFragment fragment = new NameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        signUpViewModel = ((SignUpActivity) requireActivity()).getSignUpViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_name, container, false);
        confirmDetailBtn = view.findViewById(R.id.fragment_name_confirm_details_btn);
        firstName = view.findViewById(R.id.first_name_input_edit_text);
        lastName = view.findViewById(R.id.last_name_input_edit_text);

        // Add the following line to declare the TextWatcher for the last name field
        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setupButtonState();
            }
        });

        setupButtonState();

        // Button click listener
        confirmDetailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle button click, e.g., navigate to the next fragment
                    if (!firstName.getText().toString().isEmpty() &&
                            !lastName.getText().toString().isEmpty() &&
                            mListener != null) {
                        mListener.onNameDetailsEntered(
                                firstName.getText().toString(),
                                lastName.getText().toString()
                        );
                    }
                    if (getActivity() != null && getActivity() instanceof SignUpActivity) {
                        signUpViewModel.setFirstName(firstName.getText().toString());
                        signUpViewModel.setLastName(lastName.getText().toString());
                        ((SignUpActivity) getActivity()).navigateToNextFragment(
                                UsernamePasswordFragment.newInstance("", "")
                        );
                    }
                }
            });


        // Assuming you want to perform some action when the user types in the first name field
        firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // Call the method when first name is entered
                    notifyNameDetailsEntered(
                            firstName.getText().toString(),
                            lastName.getText().toString()
                    );
                }
            }
        });

        return view;
    }

    private void setupButtonState() {
        // Enable or disable button based on text input
        boolean areDetailsNotEmpty = !firstName.getText().toString().isEmpty() &&
                !lastName.getText().toString().isEmpty();
        confirmDetailBtn.setEnabled(areDetailsNotEmpty);

        // Change button color based on the text input
        int buttonColor = areDetailsNotEmpty ? R.color.confirm_enable : R.color.confirm_disable;
        confirmDetailBtn.setCardBackgroundColor(ContextCompat.getColor(requireContext(), buttonColor));
    }

    public interface OnNameDetailsEnteredListener {
        void onNameDetailsEntered(String firstName, String lastName);
    }

    private void notifyNameDetailsEntered(String firstName, String lastName) {
        if (mListener != null) {
            mListener.onNameDetailsEntered(firstName, lastName);
        }
    }
}
