package my.edu.utar.person.utils;

import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class ToastHelper {

    public static void showToast(Fragment fragment, String msg){
        Toast.makeText(fragment.requireContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Fragment fragment, String msg){
        Toast.makeText(fragment.requireContext(), msg, Toast.LENGTH_LONG).show();
    }
}
