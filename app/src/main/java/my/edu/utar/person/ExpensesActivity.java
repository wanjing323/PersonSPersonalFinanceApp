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

public class ExpensesActivity extends AppCompatActivity {

    private SQLiteAdapter mySQLiteAdapter;
    private  int notificationId = 1;
    TextView expenseTV;
    EditText expenseET;
    Button saveBtn;
//    ImageButton camBtnOpen;
    ImageButton chooseImg;
    ImageView imageView;
    TextView datetv;
    ImageButton preday;
    ImageButton nextday;
    DatePickerDialog.OnDateSetListener setListener;
    StorageReference storageReference;
    DatabaseReference expenseReference;
    AutoCompleteTextView expensesAutoCompleteTV;

    String workings="";
    String description = "";
    String expenseText;
    String expenseAmount;
    String dateText;
    String monthText;
    String yearText;
    String monthYearText;
    String category_monthYear;
    String[] expensesCategoryList;
    String expensesCategory;
    String uid;

    private StorageTask uploadTask;
    public Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        mySQLiteAdapter = new SQLiteAdapter(this);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#C9BD9D"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color=\"white\">" + "Expenses" + "</font>"));

//        Objects.requireNonNull(getSupportActionBar()).hide();
        storageReference = FirebaseStorage.getInstance().getReference("ExpensesImages");
        expenseReference = FirebaseDatabase.getInstance().getReference().child("Expenses");
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        expenseTV = (TextView)findViewById(R.id.expensetv);
        expenseET = (EditText)findViewById(R.id.resultstv);
        saveBtn = (Button)findViewById(R.id.savesBtn);
        imageView = findViewById(R.id.exImg);
        //camBtnOpen = findViewById(R.id.exCam);
        chooseImg = findViewById(R.id.exChs);
        datetv = findViewById(R.id.ex_date);
        preday = findViewById(R.id.ex_preday);
        nextday = findViewById(R.id.ex_nextday);
        expensesAutoCompleteTV=findViewById(R.id.ExpensesAutoCompleteTextView);
        expensesAutoCompleteTV.setInputType(0);

        ArrayAdapter<String> stringArrayAdapter;
        expensesCategoryList=getResources().getStringArray(R.array.expenses_categories);
        stringArrayAdapter=new ArrayAdapter<String>(this, R.layout.dropdown_item,expensesCategoryList);
        expensesAutoCompleteTV.setAdapter(stringArrayAdapter);

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
                        ExpensesActivity.this, R.style.DateDialogThemeExpenses, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        String date=day+" "+new DateFormatSymbols().getMonths()[month]+" "+year;
                        datetv.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
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

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Imagechooser();
            }
        });

        //save data into firebase
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uploadTask != null && uploadTask.isInProgress()){
                    Toast.makeText(ExpensesActivity.this, "Saving data...", Toast.LENGTH_LONG).show();
                }
                else{
                    saveContent();
                }
            }
        });

    }

    private void saveContent() {
        if (TextUtils.isEmpty(expenseET.getText().toString())){
            Toast.makeText(getApplicationContext(), "Expenses description is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (expensesAutoCompleteTV.getText().toString().equals("Categories")){
            Toast.makeText(getApplicationContext(), "Please select an expenses category", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(expenseTV.getText().toString())){
            Toast.makeText(getApplicationContext(), "Expenses amount is required", Toast.LENGTH_SHORT).show();
            return;
        }

        //save other data into realtime database
        description = expenseET.getText().toString().trim();
        expenseText = expenseTV.getText().toString().trim();
        dateText = datetv.getText().toString().trim();
        expensesCategory = expensesAutoCompleteTV.getText().toString().trim();
        String[] splitText = dateText.split(" ");
        monthText=splitText[1];
        yearText = splitText[2];
        monthYearText= monthText+","+yearText;
        category_monthYear = expensesCategory+"_"+monthYearText;

        if(expenseText.contains("+")||expenseText.contains("-")||expenseText.contains("*")||expenseText.contains("/")){
            Toast.makeText(ExpensesActivity.this, "The equation is not calculated", Toast.LENGTH_SHORT).show();
        }else {
            String key = expenseReference.push().getKey();
            if(imageView.getDrawable()==null){
                Expenses expenses = new Expenses(key, expenseText, description, "N/A",dateText,expensesCategory,monthYearText,category_monthYear,uid);
                expenseReference.child(key).setValue(expenses);
                finish();
                startActivity(getIntent());
            }else{
                String imageId;
                imageId = System.currentTimeMillis()+"."+getExtension(imgUri);
                Expenses expenses = new Expenses(key, expenseText, description, imageId,dateText,expensesCategory,monthYearText,category_monthYear,uid);
                expenseReference.child(key).setValue(expenses);
                StorageReference ref = storageReference.child(imageId);

                uploadTask =ref.putFile(imgUri).
                        addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess (UploadTask.TaskSnapshot taskSnapshot){
                                //Uri uploadUri = taskSnapshot.getUploadSessionUri();
                                Toast.makeText(ExpensesActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                            }
                        }).
                        addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure (@NonNull Exception e){
                                Toast.makeText(ExpensesActivity.this, "Fail to upload image", Toast.LENGTH_SHORT).show();
                            }
                        });

                finish();
                startActivity(getIntent());

            }
            //save to firebase storage
            Toast.makeText(ExpensesActivity.this, "Data saved successfully.", Toast.LENGTH_LONG).show();
        }

    }


    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
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
            imgUri = data.getData();
            imageView.setImageURI(imgUri);
        }
    }

    private void setWorkings(String givenValue)
    {
        workings = workings+givenValue;
        expenseTV.setText(workings);
    }

    public void clearsOnClick(View view) {
        expenseTV.setText("");
        workings = "";

    }

    public void dividesOnClick(View view) {
        setWorkings("/");
    }

    public void multsOnCLick(View view) {
        setWorkings("*");
    }

    public void minussOnClick(View view) {
        setWorkings("-");
    }

    public void equalsOnClick(View view) {
        Double result = null;
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");

        try {
            result = (double)engine.eval(workings);
        } catch (ScriptException e) {
            Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
        }

        if(result != null)
            expenseTV.setText(String.valueOf(result.doubleValue()));
            workings = "";
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu,menu);

        menu.add("Home Page").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(ExpensesActivity.this, NewActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
                return false;
            }
        });
        menu.add("Income").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(ExpensesActivity.this, IncomeActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
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
                        String content = "The expenses notification alarm is cancelled!";
                        TimeNotification newData = new TimeNotification(title, content, hour, min, year, month, day);
                        mySQLiteAdapter.addNotificationList(newData);

                        Intent i = new Intent(ExpensesActivity.this, AlertReceiver.class);
                        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                        PendingIntent alarmIntent = PendingIntent.getBroadcast(ExpensesActivity.this, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarm.cancel(alarmIntent);
                        Toast.makeText(ExpensesActivity.this, "Alarm is closed!", Toast.LENGTH_SHORT).show();
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

        AlertDialog.Builder alertb = new AlertDialog.Builder(ExpensesActivity.this);
        LayoutInflater inflater = LayoutInflater.from(ExpensesActivity.this);
        View v = inflater.inflate(R.layout.timepicker, null);
        AlertDialog alert = alertb.create();
        alert.setView(v);

        TimePicker timePicker = v.findViewById(R.id.timePicker1);
        Button btnSet = v.findViewById(R.id.set);
        Button btnCancel = v.findViewById(R.id.cancel);

        Intent i = new Intent(ExpensesActivity.this, AlertReceiver.class);
        i.putExtra("notificationId", notificationId);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(ExpensesActivity.this, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);

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

                String title = "EXPENSES REMINDER";
                String content = "Remember to upload your daily expenses!";
                TimeNotification newData = new TimeNotification(title, content, hour, min, year, month, day);
                mySQLiteAdapter.addNotificationList(newData);

                long alarmStartTime = startTime.getTimeInMillis();
                alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime, AlarmManager.INTERVAL_DAY, alarmIntent);
                Toast.makeText(ExpensesActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
                alert.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(ExpensesActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());

            }
        });

        alert.show();

    }

    public void dotsOnClick(View view) {
        setWorkings(".");
    }

    public void zerosOnClick(View view) {
        setWorkings("0");
    }

    public void onesOnClick(View view) {
        setWorkings("1");
    }

    public void twosOnClick(View view) {
        setWorkings("2");
    }

    public void threesOnClick(View view) {
        setWorkings("3");
    }

    public void foursOnClick(View view) {
        setWorkings("4");
    }

    public void fivesOnClick(View view) {
        setWorkings("5");
    }

    public void sixsOnClick(View view) {
        setWorkings("6");
    }

    public void sevensOnClick(View view) {
        setWorkings("7");
    }

    public void eightsOnClick(View view) {
        setWorkings("8");
    }

    public void ninesOnClick(View view) {
        setWorkings("9");
    }

    public void plussOnClick(View view) {
        setWorkings("+");
    }

}