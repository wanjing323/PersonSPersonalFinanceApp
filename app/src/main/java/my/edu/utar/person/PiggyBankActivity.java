package my.edu.utar.person;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;

public class PiggyBankActivity extends AppCompatActivity implements DialogCloseListener{

    TextView goalTitle;
    TextView goalAmount;
    ImageButton editGoalBtn;
    TextView latestUpdate;
    TextView progressPerctg;
    TextView amountLeft;
    TextView amountSaved;
    Button saveMoneyBtn;
    Button chgNewGoalBtn;
    Button viewCompGoal;
    DatabaseReference goalReference;
    CircleProgress circleProgress;
    private String uid;

    PiggyBank piggyBank;
    private String currentKey="A1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piggy_bank);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#CBB4B3"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color=\"white\">" + "Piggy Bank" + "</font>"));

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        goalTitle= findViewById(R.id.goal);
        goalAmount= findViewById(R.id.goalAmount);
        editGoalBtn=findViewById(R.id.editGoal);
        latestUpdate = findViewById(R.id.latest_update);
        //progressPerctg = findViewById(R.id.progress_tv1);
        amountLeft = findViewById(R.id.progress_tv2);
        amountSaved = findViewById(R.id.progress_tv3);
        saveMoneyBtn = findViewById(R.id.saveMoneyBtn);
        chgNewGoalBtn = findViewById(R.id.chgGoalBtn);
       // viewCompGoal = findViewById(R.id.viewSavingBtn);
        editGoalBtn = findViewById(R.id.editGoal);
        circleProgress = findViewById(R.id.circle_progress);

        DecimalFormat df = new DecimalFormat("###.##");
        DecimalFormat df1 = new DecimalFormat("###");


        goalReference = FirebaseDatabase.getInstance().getReference("Goal");

        Query query1 = goalReference.orderByChild("uid").equalTo(uid);

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    }
                }
                else {

                    String key = goalReference.push().getKey();
                    Calendar calendar = Calendar.getInstance();
                    String currentDate = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
                    goalReference.child(key).child("latestUpdate").setValue(currentDate);
                    goalReference.child(key).child("goalTitle").setValue("Default Goal");
                    goalReference.child(key).child("goalAmount").setValue("1000");
                    goalReference.child(key).child("amountLeft").setValue("1000");
                    goalReference.child(key).child("amountSaved").setValue("0");
                   goalReference.child(key).child("progressPerctg").setValue("0");
                    goalReference.child(key).child("status").setValue("0");
                    goalReference.child(key).child("latest").setValue("latest");
                    goalReference.child(key).child("uid").setValue(uid);
                    goalReference.child(key).child("key").setValue(key);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });


        Query query2 = goalReference.orderByChild("uid").equalTo(uid);
        query2.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PiggyBank model = ds.getValue(PiggyBank.class);
                    if (model.getLatest().equals("latest")) {
                        currentKey = model.getKey();
                    }
                }
                goalReference.child(currentKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        piggyBank = snapshot.getValue(PiggyBank.class);
                        if (piggyBank != null) {
                            goalTitle.setText("Goal: " + piggyBank.getGoalTitle());
                            goalAmount.setText("RM " + piggyBank.getGoalAmount());
                            latestUpdate.setText(piggyBank.getLatestUpdate());
                            circleProgress.setProgress(Double.valueOf(piggyBank.getProgressPerctg()).intValue());
                            amountLeft.setText("RM " + df.format(Double.parseDouble(piggyBank.getAmountLeft())));
                            amountSaved.setText("Congratulation!\nYou had saved RM" + df.format(Double.parseDouble(
                                    piggyBank.getAmountSaved())));

                            if (piggyBank.getStatus().equals("1")) {
                                saveMoneyBtn.setEnabled(false);
                                saveMoneyBtn.setTextColor(R.color.light_purple_2);
                                Toast.makeText(PiggyBankActivity.this,
                                        "Congratulation! You had achieved your goal. You can change the goal now.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                saveMoneyBtn.setEnabled(true);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }

        });

        editGoalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditGoalAndAmount.newInstance(currentKey,PiggyBankActivity.this )
                        .show(getSupportFragmentManager(), EditGoalAndAmount.TAG);
            }
        });

        chgNewGoalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeGoal.newInstance( PiggyBankActivity.this,uid)
                        .show(getSupportFragmentManager(), ChangeGoal.TAG);
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            PiggyBank model = ds.getValue(PiggyBank.class);
                            if (model.getLatest().equals("latest")) {
                                currentKey = model.getKey();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
            }
        });
        saveMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveMoreMoney.newInstance(currentKey,PiggyBankActivity.this,uid )
                        .show(getSupportFragmentManager(), SaveMoreMoney.TAG);
            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        goalTitle= findViewById(R.id.goal);
        goalAmount= findViewById(R.id.goalAmount);
        editGoalBtn=findViewById(R.id.editGoal);
        latestUpdate = findViewById(R.id.latest_update);
        //progressPerctg = findViewById(R.id.progress_tv1);
        amountLeft = findViewById(R.id.progress_tv2);
        amountSaved = findViewById(R.id.progress_tv3);
        saveMoneyBtn = findViewById(R.id.saveMoneyBtn);
        chgNewGoalBtn = findViewById(R.id.chgGoalBtn);
        // viewCompGoal = findViewById(R.id.viewSavingBtn);
        editGoalBtn = findViewById(R.id.editGoal);
        circleProgress = findViewById(R.id.circle_progress);

        DecimalFormat df = new DecimalFormat("###.##");
        DecimalFormat df1 = new DecimalFormat("###");

        Query query2 = goalReference.orderByChild("uid").equalTo(uid);
        query2.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PiggyBank model = ds.getValue(PiggyBank.class);
                    if (model.getLatest().equals("latest")) {
                        currentKey = model.getKey();
                    }
                }
                goalReference.child(currentKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        piggyBank = snapshot.getValue(PiggyBank.class);
                        if (piggyBank != null) {
                            goalTitle.setText("Goal: " + piggyBank.getGoalTitle());
                            goalAmount.setText("RM " + piggyBank.getGoalAmount());
                            latestUpdate.setText(piggyBank.getLatestUpdate());
                            circleProgress.setProgress(Double.valueOf(piggyBank.getProgressPerctg()).intValue());

                            //progressPerctg.setText(df.format(Double.parseDouble(piggyBank.getProgressPerctg()))+"%");
                            amountLeft.setText("RM " + df.format(Double.parseDouble(piggyBank.getAmountLeft())));
                            amountSaved.setText("Congratulation!\nYou had saved RM" +
                                    df.format(Double.parseDouble(piggyBank.getAmountSaved())));

                            if (piggyBank.getStatus().equals("1")) {
                                saveMoneyBtn.setEnabled(false);
                                saveMoneyBtn.setTextColor(R.color.light_purple_2);
                                Toast.makeText(PiggyBankActivity.this,
                                        "Congratulation! You had achieved your goal. You can change the goal now.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                saveMoneyBtn.setEnabled(true);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu,menu);

        menu.add("Home Page").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(PiggyBankActivity.this, NewActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Transaction").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(PiggyBankActivity.this, TransactionActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Income").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(PiggyBankActivity.this, IncomeActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Expenses").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(PiggyBankActivity.this, ExpensesActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Scheduler").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(PiggyBankActivity.this, Scheduler.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Exit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                finishAffinity();
                return true;
            }
        });

        return true;
    }

}