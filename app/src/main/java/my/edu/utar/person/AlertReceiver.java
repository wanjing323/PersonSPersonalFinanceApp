package my.edu.utar.person;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notificationId", 0);


        Intent i = new Intent(context, ExpensesActivity.class);
        Intent i1 = new Intent(context, IncomeActivity.class);
        PendingIntent pendingI = PendingIntent.getActivity(context, 1, i, 0);
        PendingIntent pendingI1 = PendingIntent.getActivity(context, 2, i1, 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        if(notificationId == 1){
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Reminder")
                    .setContentText("Remember to upload your daily expenses!")
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentIntent(pendingI)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_ALL);
            notificationManager.notify(notificationId, builder.build());
        }
        if(notificationId == 2){
            Notification.Builder builder1 = new Notification.Builder(context);
            builder1.setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Reminder")
                    .setContentText("Time to save money!")
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentIntent(pendingI1)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_ALL);
            notificationManager.notify(notificationId, builder1.build());
        }

    }
}
