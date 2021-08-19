package my.edu.utar.person;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TransactionActivity2 extends AppCompatActivity {

    DatabaseReference incomeReference;
    private TextView datetv;
    private RecyclerView transactionRecyclerView2;
    private TransactionRecyclerAdapter2 adapter2;
    private TextView emptyIncome;
    private ImageButton toExpensesBtn;
    private String uid;
    private SearchTransactionAdapter2 searchTransactionAdapter2;
    private FirebaseUser user;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction2);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#B89088"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color=\"white\">" + "Transaction" + "</font>"));

//        Intent intent = getIntent();
//        uid = intent.getStringExtra("uid");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        datetv = findViewById(R.id.dateTitle);
        transactionRecyclerView2 = findViewById(R.id.incomeRecyclerView);
        emptyIncome=findViewById(R.id.noIncome);
        toExpensesBtn=findViewById(R.id.toExpenses);
        incomeReference=FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Income");
        transactionRecyclerView2.setLayoutManager(new LinearLayoutManager(this));
        final Handler handler = new Handler(getMainLooper());
        final ArrayList<Income> incomeList=new ArrayList<>();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                incomeReference.orderByChild("date").equalTo(datetv.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            Income model2 = ds.getValue(Income.class);
                            if(model2.getDate().equals(datetv.getText().toString())){
                                incomeList.add(model2);
                            }

                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(incomeList.isEmpty()){
                                    emptyIncome.setVisibility(View.VISIBLE);
                                }else{
                                    emptyIncome.setVisibility(View.GONE);
                                }
                                adapter2 = new TransactionRecyclerAdapter2(incomeList, TransactionActivity2.this);
                                transactionRecyclerView2.setAdapter(adapter2);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        };
        Thread myThread = new Thread(runnable);
        myThread.start();

        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
        Calendar calendar = Calendar.getInstance();
        String currentDate = df.format(calendar.getTime());
        datetv.setText(currentDate);

//        Intent intentInto = getIntent();
//        datetv.setText(intentInto.getStringExtra("currentDate"));

        //date picker
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        datetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TransactionActivity2.this, R.style.DateDialogThemeIncome,new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        String date=day+" "+new DateFormatSymbols().getMonths()[month]+" "+year;
                        datetv.setText(date);
                        incomeList.clear();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                incomeReference.orderByChild("date").equalTo(datetv.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot ds : snapshot.getChildren()){
                                            Income model2 = ds.getValue(Income.class);
                                            if(model2.getDate().equals(datetv.getText().toString())){
                                                incomeList.add(model2);
                                            }

                                        }

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(incomeList.isEmpty()){
                                                    emptyIncome.setVisibility(View.VISIBLE);
                                                }else{
                                                    emptyIncome.setVisibility(View.GONE);
                                                }
                                                adapter2 = new TransactionRecyclerAdapter2(incomeList, TransactionActivity2.this);
                                                transactionRecyclerView2.setAdapter(adapter2);
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        };
                        Thread myThread = new Thread(runnable);
                        myThread.start();

                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        toExpensesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionActivity2.this, TransactionActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        FirebaseRecyclerOptions<Income> options = new FirebaseRecyclerOptions.Builder<Income>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Income"), Income.class)
                .build();

        searchTransactionAdapter2 = new SearchTransactionAdapter2(options);
        transactionRecyclerView2.setAdapter(searchTransactionAdapter2);

        SearchView searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                Toast.makeText(TransactionActivity2.this, "Searching...", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                Toast.makeText(TransactionActivity2.this, "Searching...", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void search (String s) {

        FirebaseRecyclerOptions<Income> options = new FirebaseRecyclerOptions.Builder<Income>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Income").orderByChild("description").startAt(s).endAt(s+"~"), Income.class)
                .build();

        searchTransactionAdapter2 = new SearchTransactionAdapter2(options);
        searchTransactionAdapter2.startListening();
        transactionRecyclerView2.setAdapter(searchTransactionAdapter2);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu,menu);

        menu.add("Home Page").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(TransactionActivity2.this,NewActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Piggy Bank").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(TransactionActivity2.this, PiggyBankActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Income").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(TransactionActivity2.this, IncomeActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Expenses").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(TransactionActivity2.this, ExpensesActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Scheduler").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(TransactionActivity2.this, Scheduler.class);
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