package my.edu.utar.person;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Scheduler extends AppCompatActivity {

    private TextView tv;
    private  int notificationId = 1;
    private SQLiteAdapter mySQLiteAdapter;
    private RecyclerView recyclerView;
    private recycleAdapter nrAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_list);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#CBB4B3"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color=\"white\">" + "Scheduler" + "</font>"));

        mySQLiteAdapter = new SQLiteAdapter(this);

        recyclerView = findViewById(R.id.recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        ArrayList<TimeNotification> allNotification = mySQLiteAdapter.NotificationDataList();

        if(allNotification.size() > 0){

            recyclerView.setVisibility(View.VISIBLE);
            nrAdapter = new recycleAdapter(Scheduler.this, allNotification);
            recyclerView.setAdapter(nrAdapter);

        }
        else{
            recyclerView.setVisibility(View.GONE);
            Toast.makeText(this, "There is no notification.", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu,menu);


        menu.add("Home Page").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            Intent i;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                i = new Intent(Scheduler.this, NewActivity.class);
                startActivity(i);
                return false;
            }
        });
        menu.add("Delete All")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        mySQLiteAdapter.deleteAllNotification();
                        Intent i =new Intent(Scheduler.this, Scheduler.class);
                        startActivity(i);
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

}
