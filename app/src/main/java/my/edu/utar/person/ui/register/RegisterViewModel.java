package my.edu.utar.person.ui.register;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterViewModel extends AndroidViewModel {

    private final SharedPreferences sharedPrefs;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        sharedPrefs = getApplication().getSharedPreferences("default", Context.MODE_PRIVATE);
    }

    public MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    public MutableLiveData<RegistrationResult> registrationStatus = new MutableLiveData<>(null);

    public void register(String userName, String email, String phone, String password) {
        loading.postValue(true);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    loading.postValue(false);

                    if (task.isSuccessful()) {
                        saveDataToDatabase(task.getResult().getUser().getUid(), userName, email, phone);
                        registrationStatus.postValue(new RegistrationSuccessful());
                    } else {
                        if(task.getException() != null) {
                            registrationStatus.postValue(new RegistrationFailed(task.getException().getMessage()));
                        } else{
                            registrationStatus.postValue(new RegistrationFailed("Registration Failed"));
                        }
                    }
                });
    }

    private void saveDataToDatabase(
            String uid,
            String userName,
            String email,
            String phone
    ) {
        if (uid != null) {
            sharedPrefs.edit().putString("username", userName).commit();
            sharedPrefs.edit().putString("email", email).commit();
            sharedPrefs.edit().putString("phone", phone).commit();
            sharedPrefs.edit().putString("loginType", "EmailAndPassword").commit();

            Map<String, Object> map = new HashMap<>();
            map.put("username", userName);
            map.put("email", email);
            map.put("phone", phone);
            map.put("uid", uid);
            map.put("loginType", "EmailAndPassword");

            usersRef.child(uid).updateChildren(map);
        }
    }

    public void onRegistrationResultHandled() {
        registrationStatus.postValue(null);
    }
}
