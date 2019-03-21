package com.example.moneymanager;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneymanager.fragments.TimePicker;

public class AddReminder extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private TextView textAdd;
    private ImageView imgAdd;
    private TextView time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder);

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
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        time = (TextView) findViewById(R.id.textView2);
        String hour = Integer.toString(hourOfDay);
        String min = Integer.toString(minute);
        time.setText(hour +":" + min);
    }
}
