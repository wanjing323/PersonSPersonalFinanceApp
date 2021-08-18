package my.edu.utar.person.ui;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

import my.edu.utar.person.R;
import my.edu.utar.person.databinding.FragmentForgotPasswordBinding;
import my.edu.utar.person.utils.ToastHelper;

public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotPasswordBinding binding;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public ForgotPasswordFragment() {
        super(R.layout.fragment_forgot_password);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentForgotPasswordBinding.bind(view);

        binding.btnSentLink.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();

            if(email.isEmpty()){
                ToastHelper.showToast(this,"Email is empty");
                return;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                ToastHelper.showToast(this, "Invalid Email Format");
                return;
            }

            showLoading();

            auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                hideLoading();
                if(task.isSuccessful()){
                    ToastHelper.showLongToast(this, "Password Reset Email send to "+email);
                    Navigation.findNavController(requireView()).navigateUp();
                } else {
                    if(task.getException() != null && task.getException().getMessage() != null){
                        ToastHelper.showLongToast(this, task.getException().getMessage());
                    } else {
                        ToastHelper.showLongToast(this, "Password Reset Email send to "+email);
                    }
                }
            });
        });
    }

    private void showLoading(){
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSentLink.setEnabled(false);
    }

    private void hideLoading(){
        binding.progressBar.setVisibility(View.GONE);
        binding.btnSentLink.setEnabled(true);
    }
}
