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

import android.text.format.DateFormat;
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

        time = (TextView) findViewById(R.id.textView2);
        Reminder reminder = db.reminderDAO().get();
        if (reminder != null){
            time.setText("Reminder set for: " + reminder.getHour() + ":" + reminder.getMin());
        }
    }

    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        String hour = Integer.toString(hourOfDay);
        String min = Integer.toString(minute);
        Intent intent = new Intent(AddReminder.this, AlarmReciver.class);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        boolean format = DateFormat.is24HourFormat(getApplicationContext());


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        final Reminder reminder1 = db.reminderDAO().get();

        if (reminder1 == null){
            final Reminder reminder2 = new Reminder();
            reminder2.setHour(hourOfDay);
            reminder2.setMin(minute);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    db.reminderDAO().add(reminder2);
                }
            });
        }else {
            reminder1.setHour(hourOfDay);
            reminder1.setMin(minute);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    db.reminderDAO().edit(reminder1);
                }
            });
        }

        time.setText("Reminder set for: " + hourOfDay + ":" + minute);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
