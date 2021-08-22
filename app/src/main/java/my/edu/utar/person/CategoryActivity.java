package my.edu.utar.person;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CategoryActivity extends AppCompatActivity {

    private AutoCompleteTextView spendCatAutoCompleteTV;
    private RecyclerView spendCatRecyclerView;
    private TextView noSpendCat;
    private DatabaseReference expensesReference;
    private String[] incomeCategoryList;
    private TextView goButton;
    private CategoryRecyclerAdapter adapter;
    private ImageButton nextMonthBtn;
    private ImageButton preMonthBtn;
    private TextView currentMonthTv;
    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#DBCACA"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color=\"white\">" + "Spending by Category" + "</font>"));

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        spendCatAutoCompleteTV = findViewById(R.id.spendingCatAutoCompleteTextView);
        spendCatRecyclerView = findViewById(R.id.categoryRecyclerView);
        goButton=findViewById(R.id.goBtn);
        noSpendCat = findViewById(R.id.noExpensesOnCategory);
        currentMonthTv = findViewById(R.id.current_month);
        nextMonthBtn = findViewById(R.id.nextmonth);
        preMonthBtn = findViewById(R.id.premonth);

        Calendar cal = Calendar.getInstance();
        currentMonthTv.setText(getCurrentMonthYear());
        nextMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curDate = currentMonthTv.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM,yyyy");
                try {
                    Date d = sdf.parse(curDate);
                    cal.setTime(d);
                    cal.add(Calendar.MONTH, +1); //Goes to previous day
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String preday = sdf.format(cal.getTime());
                currentMonthTv.setText(preday);
            }
        });

        preMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curDate = currentMonthTv.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM,yyyy");
                try {
                    Date d = sdf.parse(curDate);
                    cal.setTime(d);
                    cal.add(Calendar.MONTH, -1); //Goes to previous day
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String preday = sdf.format(cal.getTime());
                currentMonthTv.setText(preday);
            }
        });

        spendCatAutoCompleteTV.setInputType(0);
        ArrayAdapter<String> stringArrayAdapter;
        incomeCategoryList=getResources().getStringArray(R.array.expenses_categories);
        stringArrayAdapter=new ArrayAdapter<String>(this, R.layout.dropdown_item,incomeCategoryList);
        spendCatAutoCompleteTV.setAdapter(stringArrayAdapter);

        final Handler handler = new Handler(getMainLooper());
        final ArrayList<Expenses> expensesList = new ArrayList<>();


        spendCatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expensesList.clear();
                expensesReference = FirebaseDatabase.getInstance().getReference().child("Expenses");
                String month_category = spendCatAutoCompleteTV.getText().toString()+"_"+currentMonthTv.getText().toString();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        expensesReference.orderByChild("uid").equalTo(uid)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    Expenses model1 = ds.getValue(Expenses.class);
                                    if(model1.getExpensesCategory_Month().equals(month_category)){
                                        expensesList.add(model1);
                                    }
                                }
                                //handler
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (expensesList.isEmpty()) {
                                            noSpendCat.setVisibility(View.VISIBLE);
                                        } else {
                                            noSpendCat.setVisibility(View.GONE);
                                        }
                                        adapter = new CategoryRecyclerAdapter(expensesList, CategoryActivity.this);
                                        spendCatRecyclerView.setAdapter(adapter);
                                    }
                                });
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(CategoryActivity.this, "read fail", Toast.LENGTH_SHORT).show();
                            }

                        });
                    }
                };

                Thread myThread = new Thread(runnable);
                myThread.start();
            }
        });

    }
    private String getCurrentMonthYear() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_year_date = new SimpleDateFormat("MMMM,yyyy");
        return month_year_date.format(cal.getTime());
    }

}