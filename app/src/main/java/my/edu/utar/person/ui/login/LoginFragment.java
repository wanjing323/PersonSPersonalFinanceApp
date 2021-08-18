package my.edu.utar.person.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

import java.util.HashMap;
import java.util.Map;

import my.edu.utar.person.CategoryActivity;
import my.edu.utar.person.NewActivity;
import my.edu.utar.person.R;
import my.edu.utar.person.databinding.FragmentLoginBinding;
import my.edu.utar.person.utils.ToastHelper;

public class LoginFragment extends Fragment {

    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    private FragmentLoginBinding binding;

    private LoginViewModel viewModel;

    private SharedPreferences sharedPrefs;

    private final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentLoginBinding.bind(view);

        sharedPrefs = requireActivity().getSharedPreferences("default", Context.MODE_PRIVATE);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        binding.btnGotoSignUp.setOnClickListener( v-> {
            Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_registerFragment);
        });

        binding.btnForgotPassword.setOnClickListener ( v -> {
            Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
        });

        binding.btnLogin.setOnClickListener(v-> validateAndLogin());

        viewModel.loading.observe(getViewLifecycleOwner(), loading -> {

            showOrHideView(binding.progressBar, loading);
            showOrHideView(binding.btnLogin, !loading);
            shouldEnableOrDisableView(binding.btnHuaweiLogin, !loading);
            shouldEnableOrDisableView(binding.btnForgotPassword, !loading);
            shouldEnableOrDisableView(binding.etUseremail, !loading);
            shouldEnableOrDisableView(binding.etPassword, !loading);
        });

        viewModel.loginStatus.observe(getViewLifecycleOwner(), loginResult -> {
            if(loginResult != null){
                if(loginResult instanceof LoginFailed){
                    ToastHelper.showLongToast(LoginFragment.this, ((LoginFailed) loginResult).getErrorMsg());
                    viewModel.onLoginResultHandled();
                } else if(loginResult instanceof LoginSuccessful){
                    ToastHelper.showToast(LoginFragment.this, "Login Successful");
                    Intent intent = new Intent(getActivity(), NewActivity.class);
                    startActivity(intent);
                    viewModel.onLoginResultHandled();
                }
            }
        });

        binding.btnHuaweiLogin.setOnClickListener(v -> {
            HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAuthorizationCode().createParams();
            HuaweiIdAuthService service = HuaweiIdAuthManager.getService(requireActivity(), authParams);
            startActivityForResult(service.getSignInIntent(), 8888);
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

    private void validateAndLogin() {
        String useremail = binding.etUseremail.getText().toString().trim();
        String password = binding.etPassword.getText().toString();

        if (useremail.isEmpty() || password.isEmpty()) {
            ToastHelper.showToast(this, "All Fields Required");
            return;
        }

        if (useremail.contains(" ")) {
            ToastHelper.showToast(this,"User email shouldn't contain spaces");
            return;
        }

        if (password.length() < 8) {
            ToastHelper.showToast(this, "Password should be 8 or more characters");
            return;
        }

        viewModel.login(useremail, password);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8888) {
            Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            if (authHuaweiIdTask.isSuccessful()) {
                // The sign-in is successful, and the user's HUAWEI ID information and authorization code are obtained.
                AuthHuaweiId huaweiAccount = authHuaweiIdTask.getResult();
                saveToDatabase(huaweiAccount);
                Intent intent = new Intent(getActivity(), NewActivity.class);
                startActivity(intent);
                //Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_mainFragment);
                Log.i("112233", "Authorization code:" + huaweiAccount.getAuthorizationCode());
            } else {
                // The sign-in failed.
                Log.e("112233", "sign in failed : "+authHuaweiIdTask.getException().getMessage(), authHuaweiIdTask.getException());
            }
        }
    }

    private void saveToDatabase(AuthHuaweiId huaweiAccount) {
        if (huaweiAccount != null) {

            if (huaweiAccount.getDisplayName() != null) {
                sharedPrefs.edit().putString("username", huaweiAccount.getDisplayName()).commit();
            } else {
                sharedPrefs.edit().putString("username", "").commit();
            }

            if (huaweiAccount.getEmail() != null) {
                sharedPrefs.edit().putString("email", huaweiAccount.getEmail()).commit();
            } else {
                sharedPrefs.edit().putString("email", "").commit();
            }

            sharedPrefs.edit().putString("phone", "").commit();
            sharedPrefs.edit().putString("loginType", "HUAWEI").commit();
            sharedPrefs.edit().putString("uid", huaweiAccount.getUid()).commit();

            Map<String, Object> map = new HashMap<>();
            map.put("username",  huaweiAccount.getDisplayName());
            map.put("email",  huaweiAccount.getEmail());
            map.put("phone", "");
            map.put("uid",  huaweiAccount.getUid());
            map.put("loginType", "HUAWEI");


            usersRef.child(huaweiAccount.getDisplayName()).updateChildren(map);
        }
    }
}
