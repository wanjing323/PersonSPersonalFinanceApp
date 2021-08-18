package my.edu.utar.person.ui.login;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import my.edu.utar.person.application.UserModel;

public class LoginViewModel extends AndroidViewModel {

    private final SharedPreferences sharedPrefs;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        sharedPrefs = getApplication().getSharedPreferences("default", Context.MODE_PRIVATE);
    }

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

    public MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    public MutableLiveData<LoginResult> loginStatus = new MutableLiveData<>(null);

    public void login(String useremail, String password){
        loading.postValue(true);

        usersRef.orderByChild("email").equalTo(useremail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot userSnap: snapshot.getChildren()){
                        UserModel user = userSnap.getValue(UserModel.class);
                        if(user != null && user.getEmail().equals(useremail)){
                            auth.signInWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(task -> {
                                loading.postValue(false);
                                if(task.isSuccessful()){
                                    saveDataToDatabase(task.getResult().getUser().getUid(), user.getUsername(), useremail, user.getPhone());
                                    loginStatus.postValue(new LoginSuccessful());
                                } else {
                                    if(task.getException() != null && task.getException().getMessage() != null) {
                                        loginStatus.postValue(new LoginFailed(task.getException().getMessage()));
                                    } else{
                                        loginStatus.postValue(new LoginFailed("Registration Failed"));
                                    }
                                }
                            });

                            break;
                        } else {
                            loading.postValue(false);
                            loginStatus.postValue(new LoginFailed("Username not found"));
                        }
                    }
                } else {
                    loading.postValue(false);
                    loginStatus.postValue(new LoginFailed("Username not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading.postValue(false);
                loginStatus.postValue(new LoginFailed(error.getMessage()));
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
            sharedPrefs.edit().putString("uid", uid).commit();
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

    public void onLoginResultHandled(){
        loginStatus.postValue(null);
    }
}
