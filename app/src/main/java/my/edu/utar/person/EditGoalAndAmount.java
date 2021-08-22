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

public class EditGoalAndAmount extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";
    private static String goalKey;
    private static PiggyBankActivity piggyActivity;
    private DatabaseReference editReference;
    private PiggyBank piggy;
    private TextView edittedAmounttv;
    private TextView edittedGoaltv;
    private Button saveEditGoal;
    private Button cancelEditGoal;
    private double oldAmountSaved;
    private double newAmountSaved;
    private double oldAmountLeft;
    private double newAmountLeft;
    private double totalAmount;
    private double amount;
    private double percentage;

    public static EditGoalAndAmount newInstance(String currentGoalKey, PiggyBankActivity piggyBankActivity){
        goalKey = currentGoalKey;
        piggyActivity = piggyBankActivity;
        return new EditGoalAndAmount();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_goal_amount, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edittedGoaltv = requireView().findViewById(R.id.edittedGoalTitle);
        edittedAmounttv = requireView().findViewById(R.id.edittedAmount);
        saveEditGoal = getView().findViewById(R.id.editSavingBtn);
        cancelEditGoal = getView().findViewById(R.id.editCancelBtn);

        editReference = FirebaseDatabase.getInstance().getReference("Goal");
        editReference.child(goalKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                piggy = snapshot.getValue(PiggyBank.class);
                if(piggy !=null){
                    edittedGoaltv.setText(piggy.getGoalTitle());
                    edittedAmounttv.setText(piggy.getGoalAmount());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        saveEditGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(edittedGoaltv.getText().toString())) {
                    Toast.makeText(getContext(), "Goal title is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edittedAmounttv.getText().toString())) {
                    Toast.makeText(getContext(), "Saving amount is required", Toast.LENGTH_SHORT).show();
                    return;
                }
               editReference.child(goalKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        piggy = snapshot.getValue(PiggyBank.class);
                        if (piggy != null) {
                            oldAmountSaved = Double.parseDouble(piggy.getAmountSaved());
                            oldAmountLeft=Double.parseDouble(piggy.getAmountLeft());
                            totalAmount=Double.parseDouble(piggy.getGoalAmount());

                            amount=Double.parseDouble(edittedAmounttv.getText().toString());
                            totalAmount=amount;
                                newAmountSaved =oldAmountSaved;
                                newAmountLeft = totalAmount-newAmountSaved;
                                percentage = (newAmountSaved / totalAmount)*100.0 ;

                                if(percentage>=100){
                                    editReference.child(goalKey).child("status").setValue("1");
                                }
                                Calendar calendar = Calendar.getInstance();
                                String currentDate = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
                                String[] splitText = currentDate.split(" ");
                                String monthText=splitText[1];
                                String yearText = splitText[2];
                                String monthYearText= monthText+","+yearText;
                                String category_monthYear = "Savings_"+monthYearText;
                                editReference.child(goalKey).child("goalTitle").setValue(edittedGoaltv.getText().toString());
                                editReference.child(goalKey).child("goalAmount").setValue(edittedAmounttv.getText().toString());
                                editReference.child(goalKey).child("latestUpdate").setValue(currentDate);
                                editReference.child(goalKey).child("amountSaved").setValue(String.valueOf(newAmountSaved));
                                editReference.child(goalKey).child("amountLeft").setValue(String.valueOf(newAmountLeft));
                                editReference.child(goalKey).child("progressPerctg").setValue(String.valueOf(percentage));


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                dismiss();
            }
        });

        cancelEditGoal.setOnClickListener(new View.OnClickListener() {
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
