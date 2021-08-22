package my.edu.utar.person;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Calendar;

public class ChangeGoal extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";
    private static PiggyBankActivity piggyActivity;
    private DatabaseReference changeGoalReference;
    private PiggyBank piggy;
    private TextView newAmounttv;
    private TextView newGoaltv;
    private Button saveNewGoal;
    private Button cancelNewGoal;
    private static String userid;

    public static ChangeGoal newInstance(PiggyBankActivity piggyBankActivity,String uid){
        piggyActivity = piggyBankActivity;
        userid=uid;
        return new ChangeGoal();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_goal, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newGoaltv = requireView().findViewById(R.id.newGoalTitle);
        newAmounttv = requireView().findViewById(R.id.newGoalAmount);
        saveNewGoal = getView().findViewById(R.id.newGoalSaveBtn);
        cancelNewGoal = getView().findViewById(R.id.newGoalCancelBtn);

        changeGoalReference = FirebaseDatabase.getInstance().getReference("Goal");

        saveNewGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(newGoaltv.getText().toString())){
                    Toast.makeText(getContext(), "Goal title is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(newAmounttv.getText().toString())){
                    Toast.makeText(getContext(), "Goal amount is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                changeGoalReference.orderByChild("uid").equalTo(userid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            PiggyBank pb = ds.getValue(PiggyBank.class);
                            changeGoalReference.child(pb.getKey()).child("latest").setValue("-");
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });

                String key = changeGoalReference.push().getKey();
                Calendar calendar = Calendar.getInstance();
                String currentDate = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
                changeGoalReference.child(key).child("latestUpdate").setValue(currentDate);
                changeGoalReference.child(key).child("goalTitle").setValue(newGoaltv.getText().toString());
                changeGoalReference.child(key).child("goalAmount").setValue(newAmounttv.getText().toString());
                changeGoalReference.child(key).child("amountLeft").setValue(newAmounttv.getText().toString());
                changeGoalReference.child(key).child("amountSaved").setValue("0");
                changeGoalReference.child(key).child("progressPerctg").setValue("0");
                changeGoalReference.child(key).child("status").setValue("0");
                changeGoalReference.child(key).child("latest").setValue("latest");
                changeGoalReference.child(key).child("uid").setValue(userid);
                changeGoalReference.child(key).child("key").setValue(key);

                dismiss();
            }
        });

        cancelNewGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener)
            ((DialogCloseListener)activity).handleDialogClose(dialog);
    }
}
