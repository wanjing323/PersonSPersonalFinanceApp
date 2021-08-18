package my.edu.utar.person.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

import my.edu.utar.person.MainUserActivity;
import my.edu.utar.person.R;
import my.edu.utar.person.databinding.FragmentMainBinding;

public class MainFragment extends Fragment {

    public MainFragment() {
        super(R.layout.fragment_main);
    }

    private FragmentMainBinding binding;

    private SharedPreferences sharedPrefs;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentMainBinding.bind(view);

        sharedPrefs = requireActivity().getSharedPreferences("default", Context.MODE_PRIVATE);


        String email = sharedPrefs.getString("email", "");
        String username = sharedPrefs.getString("uid", "");
        String phone = sharedPrefs.getString("phone", "");

        binding.tvEmail.setText(email);
        binding.tvPhone.setText(phone);
        binding.tvUsername.setText(username);

        if(phone.isEmpty()){
            binding.phoneLayout.setVisibility(View.GONE);
        } else {
            binding.phoneLayout.setVisibility(View.VISIBLE);
        }

        if(email.isEmpty()){
            binding.emailLayout.setVisibility(View.GONE);
        } else {
            binding.emailLayout.setVisibility(View.VISIBLE);
        }


        binding.btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireActivity())
                    .setTitle("Logout Confirmation")
                    .setMessage("Do you want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> signOut())
                    .setNegativeButton("No", (dialog, which) -> { })
                    .show();
        });
    }

    private void signOut() {
        HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAuthorizationCode().createParams();
        HuaweiIdAuthService service = HuaweiIdAuthManager.getService(requireActivity(), authParams);

        String authType = sharedPrefs.getString("loginType", "");
        if (authType.equals("HUAWEI")) {
            sharedPrefs.edit().clear().commit();
            service.cancelAuthorization();
            startActivity(new Intent(requireActivity(), MainUserActivity.class));
            requireActivity().finish();
        } else {
            sharedPrefs.edit().clear().commit();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(requireActivity(), MainUserActivity.class));
            requireActivity().finish();
        }
    }
}
