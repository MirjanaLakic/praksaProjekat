package com.example.moneymanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

public class AlarmReciver extends BroadcastReceiver {

    public static final String REMINDER_INTENT_ID = "reminder_notification_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(REMINDER_INTENT_ID, "Primery", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, REMINDER_INTENT_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.investments)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.reminder_title))
                .setContentText(context.getString(R.string.reminder_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.reminder_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(100, builder.build());
    }

    private static PendingIntent contentIntent(Context context){
        Intent intent = new Intent(context, AddNewExpense.class);
        intent.putExtra("item", "EXPENSES");
        intent.putExtra("edit", "add");
        intent.putExtra("reminder", "reminder");
        return PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context){
        Resources resources = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(resources, R.drawable.investments);
        return largeIcon;
    }
}
