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

public class TransactionActivity extends AppCompatActivity {

    DatabaseReference expensesReference;
    private TextView datetv;
    private RecyclerView transactionRecyclerView;
    private TransactionRecyclerAdapter adapter;
    private TextView emptyExpenses;
    private ImageButton toIncomeBtn;
    private String uid;
    private SearchTransactionAdapter searchTransactionAdapter;
    private FirebaseUser user;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#7DAE6C"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color=\"white\">" + "Transaction" + "</font>"));

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        datetv = findViewById(R.id.dateTitle);
        transactionRecyclerView= findViewById(R.id.expensesRecyclerView);
        emptyExpenses=findViewById(R.id.noExpenses);
        toIncomeBtn=findViewById(R.id.toIncome);
        expensesReference=FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Expenses");
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        final Handler handler = new Handler(getMainLooper());
        final ArrayList<Expenses> expensesList = new ArrayList<>();

        //Runnable:get data from firebase
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                expensesReference.orderByChild("expDate").equalTo(datetv.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            Expenses model1 = ds.getValue(Expenses.class);

                            if(model1.getExpDate().equals(datetv.getText().toString())){
                                expensesList.add(model1);
                            }

                        }

                        //handler
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(expensesList.isEmpty()){
                                    emptyExpenses.setVisibility(View.VISIBLE);
                                }else{
                                    emptyExpenses.setVisibility(View.GONE);
                                }
                                adapter = new TransactionRecyclerAdapter(expensesList, TransactionActivity.this);
                                transactionRecyclerView.setAdapter(adapter);
                            }
                        });

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(TransactionActivity.this, "read fail", Toast.LENGTH_SHORT).show();
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

        //date picker
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        datetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TransactionActivity.this, R.style.DateDialogThemeExpensesTransaction,new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        String date=day+" "+new DateFormatSymbols().getMonths()[month]+" "+year;
                        datetv.setText(date);

                        expensesList.clear();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                expensesReference.orderByChild("expDate").equalTo(datetv.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot ds : snapshot.getChildren()) {
                                            Expenses model1 = ds.getValue(Expenses.class);

                                            if (model1.getExpDate().equals(datetv.getText().toString())) {
                                                expensesList.add(model1);
                                            }
                                        }

                                        //handler
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(expensesList.isEmpty()){
                                                    emptyExpenses.setVisibility(View.VISIBLE);
                                                }else{
                                                    emptyExpenses.setVisibility(View.GONE);
                                                }
                                                adapter = new TransactionRecyclerAdapter(expensesList, TransactionActivity.this);
                                                transactionRecyclerView.setAdapter(adapter);
                                            }
                                        });

                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(TransactionActivity.this, "read fail", Toast.LENGTH_SHORT).show();
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
        toIncomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionActivity.this, TransactionActivity2.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        FirebaseRecyclerOptions<Expenses> options = new FirebaseRecyclerOptions.Builder<Expenses>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Expenses"), Expenses.class)
                .build();

        searchTransactionAdapter = new SearchTransactionAdapter(options);
        transactionRecyclerView.setAdapter(searchTransactionAdapter);

        SearchView searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                Toast.makeText(TransactionActivity.this, "Searching...", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                Toast.makeText(TransactionActivity.this, "Searching...", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void search (String s) {

        FirebaseRecyclerOptions<Expenses> options = new FirebaseRecyclerOptions.Builder<Expenses>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Expenses").orderByChild("description").startAt(s).endAt(s+"~"), Expenses.class)
                .build();

        searchTransactionAdapter = new SearchTransactionAdapter(options);
        searchTransactionAdapter.startListening();
        transactionRecyclerView.setAdapter(searchTransactionAdapter);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu,menu);

        menu.add("Home Page").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(TransactionActivity.this, NewActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Piggy Bank").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(TransactionActivity.this, PiggyBankActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Income").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(TransactionActivity.this, IncomeActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Expenses").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(TransactionActivity.this, ExpensesActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Scheduler").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(TransactionActivity.this, Scheduler.class);
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