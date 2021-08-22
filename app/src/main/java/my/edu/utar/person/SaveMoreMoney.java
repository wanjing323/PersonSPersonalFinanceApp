package my.edu.utar.person;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;

public class SaveMoreMoney extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private static String goalKey;
    private static PiggyBankActivity piggyActivity;
    private EditText newSavingAmount;
    private Button saveNewSaving;
    private Button cancelNewSaving;
    private DatabaseReference savingReference;
    private DatabaseReference expensesReference;
    private PiggyBank piggy;
    private Expenses expenses;
    private double oldAmountSaved;
    private double newAmountSaved;
    private double oldAmountLeft;
    private double newAmountLeft;
    private double totalAmount;
    private double amount;
    private double percentage;
    private static String userid;

    public static SaveMoreMoney newInstance(String currentGoalKey, PiggyBankActivity piggyBankActivity,String uid){
        goalKey = currentGoalKey;
        piggyActivity = piggyBankActivity;
        userid=uid;
        return new SaveMoreMoney();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_saving, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newSavingAmount = requireView().findViewById(R.id.newAmountText);
        saveNewSaving = getView().findViewById(R.id.newSavingBtn);
        cancelNewSaving = getView().findViewById(R.id.cancelBtn);
        saveNewSaving.setEnabled(false);
        saveNewSaving.setTextColor(Color.GRAY);

        //boolean isUpdate = false;

        newSavingAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    saveNewSaving.setEnabled(false);
                    saveNewSaving.setTextColor(Color.GRAY);
                }
                else{
                   saveNewSaving.setEnabled(true);
                    saveNewSaving.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        saveNewSaving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(newSavingAmount.getText().toString())) {
                    Toast.makeText(getContext(), "Saving amount is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                savingReference = FirebaseDatabase.getInstance().getReference("Goal");
                expensesReference = FirebaseDatabase.getInstance().getReference("Expenses");
                savingReference.child(goalKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        piggy = snapshot.getValue(PiggyBank.class);
                        if (piggy != null) {
                            oldAmountSaved = Double.parseDouble(piggy.getAmountSaved());
                            oldAmountLeft=Double.parseDouble(piggy.getAmountLeft());
                            totalAmount=Double.parseDouble(piggy.getGoalAmount());

                            amount=Double.parseDouble(newSavingAmount.getText().toString());
                            if(amount>oldAmountLeft){
                                Toast.makeText(piggyActivity, "Sorry! Only "+oldAmountLeft
                                        +" is needed to reach goal.", Toast.LENGTH_SHORT).show();
                                return;
                            }else{
                                newAmountSaved = amount+oldAmountSaved;
                                newAmountLeft = totalAmount-newAmountSaved;
                                percentage = (newAmountSaved / totalAmount)*100.0 ;

                                if(percentage>=100){
                                    savingReference.child(goalKey).child("status").setValue("1");
                                }
                                Calendar calendar = Calendar.getInstance();
                                String currentDate = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
                                String[] splitText = currentDate.split(" ");
                                String monthText=splitText[1];
                                String yearText = splitText[2];
                                String monthYearText= monthText+","+yearText;
                                String category_monthYear = "Savings_"+monthYearText;
                                savingReference.child(goalKey).child("latestUpdate").setValue(currentDate);
                                savingReference.child(goalKey).child("amountSaved").setValue(String.valueOf(newAmountSaved));
                                savingReference.child(goalKey).child("amountLeft").setValue(String.valueOf(newAmountLeft));
                                savingReference.child(goalKey).child("progressPerctg").setValue(String.valueOf(percentage));

                                //Update expenses database
                                String key = expensesReference.push().getKey();
                                Expenses expenses = new Expenses(key, String.valueOf(amount),
                                        "Saving in Person$ Piggy Bank", "N/A",currentDate,
                                        "Savings",monthYearText,category_monthYear,userid);
                                expensesReference.child(key).setValue(expenses);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                dismiss();
            }
        });

        cancelNewSaving.setOnClickListener(new View.OnClickListener() {
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
