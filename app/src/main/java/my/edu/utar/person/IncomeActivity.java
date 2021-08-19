package my.edu.utar.person;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class IncomeActivity extends AppCompatActivity {

    private SQLiteAdapter mySQLiteAdapter;
    private  int notificationId = 2;
    TextView incomeTV;
    EditText incomeET;
    Button saveBtn;
    ImageView imageView;
    ImageButton chsImgBtn;
    TextView datetv;
    ImageButton preday;
    ImageButton nextday;
    DatePickerDialog.OnDateSetListener setListener;
    StorageReference storageReference2;
    DatabaseReference incomeReference;
    AutoCompleteTextView incomeAutoCompleteTV;

    String workings="";
    String description = "";
    String incomeText;
    String monthText;
    String yearText;
    String monthYearText;
    String dateText;
    String[] incomeCategoryList;
    String incomeCategory;
    String uid;

    private StorageTask uploadTask;
    public Uri imgUri2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
//        Objects.requireNonNull(getSupportActionBar()).hide();

        mySQLiteAdapter = new SQLiteAdapter(this);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#CBB4B3"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color=\"white\">" + "Income" + "</font>"));

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        storageReference2 = FirebaseStorage.getInstance().getReference("IncomeImages");
        incomeReference = FirebaseDatabase.getInstance().getReference().child("Income");

        incomeTV = findViewById(R.id.incometv);
        incomeET = findViewById(R.id.resulttv);
        saveBtn = findViewById(R.id.saveBtn);
        imageView = findViewById(R.id.inc_img);
        chsImgBtn = findViewById(R.id.incChsImg);
        datetv = findViewById(R.id.inc_date);
        preday = findViewById(R.id.inc_preday);
        nextday = findViewById(R.id.inc_nextday);
        incomeAutoCompleteTV=findViewById(R.id.IncomeAutoCompleteTextView);
        incomeAutoCompleteTV.setInputType(0);

        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
        Calendar calendar = Calendar.getInstance();
        String currentDate = df.format(calendar.getTime());
        datetv.setText(currentDate);

        ArrayAdapter<String> stringArrayAdapter;
        incomeCategoryList=getResources().getStringArray(R.array.income_categories);
        stringArrayAdapter=new ArrayAdapter<String>(this, R.layout.dropdown_item,incomeCategoryList);
        incomeAutoCompleteTV.setAdapter(stringArrayAdapter);

        chsImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Imagechooser();
            }
        });

        preday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String curDate = datetv.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                try {
                    Date d = sdf.parse(curDate);
                    calendar.setTime(d);
                    calendar.add(Calendar.DAY_OF_MONTH, -1); //Goes to previous day
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String preday = sdf.format(calendar.getTime());
                datetv.setText(preday);
            }
        });

        nextday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curDate = datetv.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                try {
                    Date d = sdf.parse(curDate);
                    calendar.setTime(d);
                    calendar.add(Calendar.DAY_OF_MONTH, 1); //Goes to previous day
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String nextday = sdf.format(calendar.getTime());
                datetv.setText(nextday);
            }

        });

        //date picker
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        datetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        IncomeActivity.this, R.style.DateDialogThemeIncome,new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String date=day+" "+new DateFormatSymbols().getMonths()[month]+" "+year;
                        datetv.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        //save data into firebase
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uploadTask != null && uploadTask.isInProgress()){
                    Toast.makeText(IncomeActivity.this, "Saving data...", Toast.LENGTH_LONG).show();
                }
                else{
                    saveContent();
                }
            }
        });



}
    private void saveContent() {

        if (TextUtils.isEmpty(incomeET.getText().toString())){
            Toast.makeText(getApplicationContext(), "Income description is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (incomeAutoCompleteTV.getText().toString().equals("Categories")){
            Toast.makeText(getApplicationContext(), "Please select an income category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(incomeTV.getText().toString())){
            Toast.makeText(getApplicationContext(), "Income amount is required", Toast.LENGTH_SHORT).show();
            return;
        }

        //save other data into realtime database
        description = incomeET.getText().toString().trim();
        incomeText = incomeTV.getText().toString().trim();
        dateText = datetv.getText().toString().trim();
        incomeCategory = incomeAutoCompleteTV.getText().toString().trim();

        String[] splitText = dateText.split(" ");
        monthText=splitText[1];
        yearText = splitText[2];
        monthYearText= monthText+","+yearText;


        if(incomeText.contains("+")||incomeText.contains("-")||incomeText.contains("*")||incomeText.contains("/")){
            Toast.makeText(IncomeActivity.this, "The equation is not calculated", Toast.LENGTH_SHORT).show();
        }else {
            //save to firebase storage
            String key = incomeReference.push().getKey();
            if(imageView.getDrawable()==null){
                Income income = new Income(key, incomeText, description, dateText,"N/A",incomeCategory,monthYearText,uid);
                incomeReference.child(key).setValue(income);
                finish();
                startActivity(getIntent());
            }
            else{
                //save image into storage
                String imageId;
                imageId = System.currentTimeMillis()+"."+getExtension(imgUri2);
                Income income = new Income(key, incomeText, description, dateText,imageId,incomeCategory,monthYearText,uid);
                incomeReference.child(key).setValue(income);

                StorageReference reff = storageReference2.child(imageId);

                uploadTask =reff.putFile(imgUri2).
                        addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess (UploadTask.TaskSnapshot taskSnapshot){
                                //Uri uploadUri = taskSnapshot.getUploadSessionUri();
                                Toast.makeText(IncomeActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                            }
                        }).
                        addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure (@NonNull Exception e){
                                Toast.makeText(IncomeActivity.this, "Fail to upload image", Toast.LENGTH_SHORT).show();
                            }
                        });
                finish();
                startActivity(getIntent());

            }
            }

            Toast.makeText(IncomeActivity.this, "Data saved successfully.", Toast.LENGTH_LONG).show();
        }


    private String getExtension(Uri uri){
        ContentResolver cr2 = getContentResolver();
        MimeTypeMap mimeTypeMap2=MimeTypeMap.getSingleton();
        return mimeTypeMap2.getExtensionFromMimeType(cr2.getType(uri));
    }

    private void Imagechooser() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 100) {
//            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
//            imgUri = data.getData();
//            imageView.setImageBitmap(captureImage);
//        }

        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imgUri2 = data.getData();
            imageView.setImageURI(imgUri2);
        }
    }

    private void setWorkings(String givenValue)
    {
        workings = workings+givenValue;
        incomeTV.setText(workings);
    }

    public void clearOnClick(View view) {
        incomeTV.setText("");
        workings = "";
        //resultTV.setText(workings);
    }

    public void divideOnClick(View view) {
        setWorkings("/");
    }

    public void multOnCLick(View view) {
        setWorkings("*");
    }

    public void minusOnClick(View view) {
        setWorkings("-");
    }

    public void equalOnClick(View view) {

        Double result = null;
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");

        try {
            result = (double)engine.eval(workings);
        } catch (ScriptException e) {
            Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
        }

        if(result != null)
            workings = "";
            incomeTV.setText(String.valueOf(result.doubleValue()));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu,menu);

        menu.add("Home Page").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(IncomeActivity.this, NewActivity.class);
                startActivity(i);
                i.putExtra("uid", uid);
                return false;
            }
        });
        menu.add("Expenses").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(IncomeActivity.this, ExpensesActivity.class);
                startActivity(i);
                i.putExtra("uid", uid);
                return false;
            }
        });
        menu.add("Set notification alarm")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        inputDialog();
                        return true;
                    }
                });
        menu.add("Close notification alarm")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int min = calendar.get(Calendar.MINUTE);

                        String title = "Cancellation";
                        String content = "The income notification alarm is cancelled!";
                        TimeNotification newData = new TimeNotification(title, content, hour, min, year, month, day);
                        mySQLiteAdapter.addNotificationList(newData);

                        Intent i = new Intent(IncomeActivity.this, AlertReceiver.class);
                        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                        PendingIntent alarmIntent = PendingIntent.getBroadcast(IncomeActivity.this, 2, i, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarm.cancel(alarmIntent);
                        Toast.makeText(IncomeActivity.this, "Alarm is closed!", Toast.LENGTH_SHORT).show();
                        return true;
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

    public void inputDialog() {

        AlertDialog.Builder alertb = new AlertDialog.Builder(IncomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(IncomeActivity.this);
        View v = inflater.inflate(R.layout.timepicker, null);
        AlertDialog alert = alertb.create();
        alert.setView(v);

        TimePicker timePicker = v.findViewById(R.id.timePicker1);
        Button btnSet = v.findViewById(R.id.set);
        Button btnCancel = v.findViewById(R.id.cancel);


        Intent i = new Intent(IncomeActivity.this, AlertReceiver.class);
        i.putExtra("notificationId", notificationId);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(IncomeActivity.this, 2, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

        btnSet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int hour = timePicker.getCurrentHour();
                int min = timePicker.getCurrentMinute();
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, hour);
                startTime.set(Calendar.MINUTE, min);
                startTime.set(Calendar.SECOND, 0);

                String title = "INCOME REMINDER";
                String content = "Time to save money!";
                TimeNotification newData = new TimeNotification(title, content, hour, min, year, month, day);
                mySQLiteAdapter.addNotificationList(newData);

                long alarmStartTime = startTime.getTimeInMillis();
                alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime, AlarmManager.INTERVAL_DAY, alarmIntent);
                Toast.makeText(IncomeActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
                alert.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(IncomeActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());

            }
        });

        alert.show();

    }

    public void dotOnClick(View view) {
        setWorkings(".");
    }

    public void zeroOnClick(View view) {
        setWorkings("0");
    }

    public void oneOnClick(View view) {
        setWorkings("1");
    }

    public void twoOnClick(View view) {
        setWorkings("2");
    }

    public void threeOnClick(View view) {
        setWorkings("3");
    }

    public void fourOnClick(View view) {
        setWorkings("4");
    }

    public void fiveOnClick(View view) {
        setWorkings("5");
    }

    public void sixOnClick(View view) {
        setWorkings("6");
    }

    public void sevenOnClick(View view) {
        setWorkings("7");
    }

    public void eightOnClick(View view) {
        setWorkings("8");
    }

    public void nineOnClick(View view) {
        setWorkings("9");
    }

    public void plusOnClick(View view) {
        setWorkings("+");
    }
}