package my.edu.utar.person;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NewActivity extends AppCompatActivity {

    private SharedPreferences sharedPrefs;
    private PieChart pieChart;
    private Expenses expenses;
    private TextView viewmoretv;
    private TextView totalExtv;
    private TextView totalIntv;
    private ArrayList<Expenses> expensesList;
    private TextView monthYr;
    String category;
    String uid;
    double housingAmt=0.00;
    double transAmt=0.00;
    double foodAmt=0.00;
    double mediAmt=0.00;
    double clothAmt=0.00;
    double insAmt=0.00;
    double hhAmt=0.00;
    double savingAmt=0.00;
    double otherAmt=0.00;
    double totalIncome=0.00;
    double totalExpenses=0.00;

    DatabaseReference expenseReference;
    DatabaseReference incomeReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#AAA18F"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color=\"white\">" + "Person$" + "</font>"));

        sharedPrefs = getApplication().getSharedPreferences("default", Context.MODE_PRIVATE);

        uid = sharedPrefs.getString("uid", "");

        expensesList=new ArrayList<>();
        viewmoretv=findViewById(R.id.viewmore);
        viewmoretv.setPaintFlags(viewmoretv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        expenseReference = FirebaseDatabase.getInstance().getReference().child("Expenses");
        incomeReference=FirebaseDatabase.getInstance().getReference().child("Income");
        totalExtv=findViewById(R.id.total_Expenses);
        totalIntv=findViewById(R.id.total_Income);
        monthYr=findViewById(R.id.monthYearTv);
        monthYr.setText(getCurrentMonthYear());


        pieChart = findViewById(R.id.chart);
        setupPieChart();
        loadPieChartData();

        viewmoretv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NewActivity.this, CategoryActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
            }
        });

        getTotalIncomeExpensesPerMonth();



        ImageView ivin = new ImageView(this);
        ivin = findViewById(R.id.addincomepng);
        ImageView ivexp = new ImageView(this);
        ivexp = findViewById(R.id.addexpensespng);

        ivin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NewActivity.this, IncomeActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
            }
        });

        ivexp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NewActivity.this, ExpensesActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
            }
        });
    }

    private void getTotalIncomeExpensesPerMonth() {



        expenseReference.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Expenses exModel = ds.getValue(Expenses.class);
                    if(exModel.getMonthYear().equals(getCurrentMonthYear())){
                        totalExpenses += Double.parseDouble(exModel.getAmount());
                    }

                }


                totalExtv.setText("RM "+totalExpenses);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NewActivity.this, "Read fail.", Toast.LENGTH_SHORT).show();
            }
        });

        incomeReference.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Income inModel = ds.getValue(Income.class);
                    if(inModel.getMonthYear().equals(getCurrentMonthYear())) {
                        totalIncome += Double.parseDouble(inModel.getIncome());
                    }
                }
                totalIntv.setText("RM "+ totalIncome);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NewActivity.this, "Read fail.", Toast.LENGTH_SHORT).show();
            }
        });



    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu,menu);

        menu.add("Transaction").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(NewActivity.this, TransactionActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Piggy Bank").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(NewActivity.this, PiggyBankActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Income").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(NewActivity.this, IncomeActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Expenses").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(NewActivity.this, ExpensesActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Scheduler").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(NewActivity.this, Scheduler.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Log Out").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewActivity.this);
                builder.setTitle("Logout Confirmation");
                builder.setMessage("Do you want to log out?");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                signOut();
                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });

        return true;
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(9);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Spending by Category");
        pieChart.setCenterTextSize(15);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setWordWrapEnabled(true);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadPieChartData() {
        expenseReference.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Expenses expModel = ds.getValue(Expenses.class);
                    if(expModel.getMonthYear().equals(getCurrentMonthYear())){
                        category=expModel.getExpensesCategory();
                        DecimalFormat df = new DecimalFormat("###");
                        if(category.equals("Housing")){
                            housingAmt=housingAmt+Double.parseDouble(expModel.getAmount());
                        }
                        if(category.equals("Transportation")){
                            transAmt+=Double.parseDouble(expModel.getAmount());
                        }
                        if(category.equals("Food")){
                            foodAmt+=Double.parseDouble(expModel.getAmount());
                        }
//
                        if(category.equals("Medical/Healthcare")){
                            mediAmt+=Double.parseDouble(expModel.getAmount());
                        }
                        if(category.equals("Clothing")){
                            clothAmt+=Double.parseDouble(expModel.getAmount());
                        }
                        if(category.equals("Insurance")){
                            insAmt+=Double.parseDouble(expModel.getAmount());
                        }
                        if(category.equals("Household Items")){
                            hhAmt+=Double.parseDouble(expModel.getAmount());
                        }
//
                        if(category.equals("Saving")){
                            savingAmt=(Double.parseDouble(expModel.getAmount()));
                        }
                        if(category.equals("Other")){
                            otherAmt+=Double.parseDouble(expModel.getAmount());
                        }
                    }

                }


                //PieEntry
                ArrayList<PieEntry> entries = new ArrayList<>();
                if(housingAmt>0){
                    entries.add(new PieEntry((float)housingAmt, "Housing"));
                }
                if(transAmt>0){
                    entries.add(new PieEntry((float)transAmt, "Transportation"));
                }
                if(foodAmt>0){
                    entries.add(new PieEntry((float)foodAmt, "Food"));
                }
                if(mediAmt>0){
                    entries.add(new PieEntry((float)mediAmt, "Medical/Health Care"));
                }
                if(clothAmt>0){
                    entries.add(new PieEntry((float)clothAmt, "Clothes"));
                }
                if(insAmt>0){
                    entries.add(new PieEntry((float)insAmt, "Insurans"));
                }
                if(hhAmt>0){
                    entries.add(new PieEntry((float)hhAmt, "Household"));
                }
                if(savingAmt>0){
                    entries.add(new PieEntry((float)savingAmt, "Saving"));
                }
                if(otherAmt>0){
                    entries.add(new PieEntry((float)otherAmt, "Other"));
                }

                ArrayList<Integer> colors = new ArrayList<>();
                for(int color: ColorTemplate.MATERIAL_COLORS) {
                    colors.add(color);
                }

                for (int color: ColorTemplate.VORDIPLOM_COLORS) {
                    colors.add(color);
                }

                PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
                dataSet.setColors(colors);

                PieData data = new PieData(dataSet);
                data.setDrawValues(true);
                data.setValueFormatter(new PercentFormatter(pieChart));
                data.setValueTextSize(12f);
                data.setValueTextColor(Color.BLACK);

                pieChart.setData(data);
                pieChart.invalidate();
                pieChart.animateX(1000, Easing.EaseInOutQuad);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NewActivity.this, "Read fail.", Toast.LENGTH_SHORT).show();
            }
        });


    }



    private String getCurrentMonthYear() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_year_date = new SimpleDateFormat("MMMM,yyyy");
        return month_year_date.format(cal.getTime());
    }

    private void signOut() {
        HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAuthorizationCode().createParams();
        HuaweiIdAuthService service = HuaweiIdAuthManager.getService(getApplicationContext(), authParams);

        String authType = sharedPrefs.getString("loginType", "");
        if (authType.equals("HUAWEI")) {
            sharedPrefs.edit().clear().commit();
            service.cancelAuthorization();
            startActivity(new Intent(NewActivity.this, MainUserActivity.class));
            finish();
        } else {
            sharedPrefs.edit().clear().commit();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(NewActivity.this, MainUserActivity.class));
            finish();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}