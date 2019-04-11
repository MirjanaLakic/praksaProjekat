package com.example.moneymanager.DAO;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.concurrent.TimeUnit;
@Entity
public class Reminder {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int hour;
    private int min;

    public Reminder(int id, int hour, int min) {
        this.id = id;
        this.hour = hour;
        this.min = min;
    }
    @Ignore
    public Reminder(int hour, int min) {
        this.hour = hour;
        this.min = min;
    }

    public Reminder() {
    }

    public int getId() {
        return id;
    }

    public int getHour() {
        return hour;
    }

    public int getMin() {
        return min;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
