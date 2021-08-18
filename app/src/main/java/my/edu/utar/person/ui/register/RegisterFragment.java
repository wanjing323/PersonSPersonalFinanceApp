package my.edu.utar.person.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import my.edu.utar.person.NewActivity;
import my.edu.utar.person.R;
import my.edu.utar.person.databinding.FragmentRegisterBinding;
import my.edu.utar.person.utils.ToastHelper;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;

    private RegisterViewModel viewModel;

    public RegisterFragment() {
        super(R.layout.fragment_register);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentRegisterBinding.bind(view);

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        binding.btnBackToLogin.setOnClickListener(v -> Navigation.findNavController(requireView()).navigateUp());

        binding.btnRegister.setOnClickListener(v -> validateAndRegister());

        viewModel.loading.observe(getViewLifecycleOwner(), loading -> {
            showOrHideView(binding.progressBar, loading);
            showOrHideView(binding.btnRegister, !loading);
            shouldEnableOrDisableView(binding.btnBackToLogin, !loading);
            shouldEnableOrDisableView(binding.etUsername, !loading);
            shouldEnableOrDisableView(binding.etEmail, !loading);
            shouldEnableOrDisableView(binding.etPhone, !loading);
            shouldEnableOrDisableView(binding.etPassword, !loading);
        });

        viewModel.registrationStatus.observe(getViewLifecycleOwner(), registrationResult -> {
            if(registrationResult != null){

                if(registrationResult instanceof RegistrationFailed){
                    ToastHelper.showLongToast(RegisterFragment.this, ((RegistrationFailed) registrationResult).getErrorMsg());
                } else if(registrationResult instanceof  RegistrationSuccessful) {
                    ToastHelper.showToast(RegisterFragment.this,"Login Successful");
                    Intent intent = new Intent(getActivity(), NewActivity.class);
                    startActivity(intent);
                }
                viewModel.onRegistrationResultHandled();
            }
        });

    }

    private void showOrHideView(View view, boolean shouldShow){
        if(shouldShow){
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void shouldEnableOrDisableView(View view, boolean shouldShow){
        view.setEnabled(shouldShow);
    }

    private void validateAndRegister() {
        String username = binding.etUsername.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String password = binding.etPassword.getText().toString();

        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            ToastHelper.showToast(this, "All Fields Required");
            return;
        }

        if (username.contains(" ")) {
            ToastHelper.showToast(this, "Username shouldn't contain spaces");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ToastHelper.showToast(this, "Invalid Email Format");
            return;
        }

        if (password.length() < 8) {
            ToastHelper.showToast(this, "Password should be 8 or more characters");
            return;
        }

        viewModel.register(username, email, phone, password);
    }
}
