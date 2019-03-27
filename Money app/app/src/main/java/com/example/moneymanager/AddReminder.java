package com.example.moneymanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Reminder;
import com.example.moneymanager.fragments.TimePicker;

import java.util.Calendar;

public class AddReminder extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private TextView textAdd;
    private ImageView imgAdd;
    private TextView time;
    private AppDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder);

        db = AppDatabase.getInstance(this);

        textAdd = (TextView) findViewById(R.id.add_rem);
        textAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePicker();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        imgAdd = (ImageView) findViewById(R.id.img_add_rem);
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePicker();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
    }

    @Override
    public void onTimeSet(android.widget.TimePicker view, final int hourOfDay, final int minute) {
        time = (TextView) findViewById(R.id.textView2);
        String hour = Integer.toString(hourOfDay);
        String min = Integer.toString(minute);
        time.setText(hour +":" + min);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Reminder reminder = new Reminder(hourOfDay, minute);
                db.reminderDAO().add(reminder);
            }
        });

        Intent intent = new Intent(AddReminder.this, AlarmReciver.class);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void testNotification(View view){
        NotificationUtils.remindUSer(this);
    }
}
