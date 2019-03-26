package com.example.moneymanager.DAO;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;
@Entity
public class TimeStamp {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String timeCategory;


    public TimeStamp(int id, String timeCategory) {
        this.id = id;
        this.timeCategory = timeCategory;
    }

    @Ignore
    public TimeStamp() {
    }

    @Ignore
    public TimeStamp(String timeCategory) {
        this.timeCategory = timeCategory;
    }

    public int getId() {
        return id;
    }

    public String getTimeCategory() {
        return timeCategory;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimeCategory(String timeCategory) {
        this.timeCategory = timeCategory;
    }
}
