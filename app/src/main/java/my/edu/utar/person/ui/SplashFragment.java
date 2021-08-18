package my.edu.utar.person.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import my.edu.utar.person.NewActivity;
import my.edu.utar.person.R;


public class SplashFragment extends Fragment {

    private final Handler handler = new Handler(Looper.getMainLooper());

    public SplashFragment() {
        super(R.layout.fragment_splash);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPrefs = requireActivity().getSharedPreferences("default", Context.MODE_PRIVATE);

        String authType = sharedPrefs.getString("loginType", "");

        handler.postDelayed(() -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                Intent intent = new Intent(getActivity(), NewActivity.class);
                startActivity(intent);
            } else {
                if (authType.equals("HUAWEI")) {
                    Intent intent = new Intent(getActivity(), NewActivity.class);
                    startActivity(intent);
                } else {
                    Navigation.findNavController(requireView()).navigate(R.id.action_splashFragment_to_loginFragment);
                }
            }
        }, 1500L);
    }
}
