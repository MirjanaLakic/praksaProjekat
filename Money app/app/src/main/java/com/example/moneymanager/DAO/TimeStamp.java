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
    private String timeCategoryIncome;
    private String timeExpenses;
    private String timeIncomes;


    public TimeStamp(int id, String timeCategory, String timeCategoryIncome) {
        this.id = id;
        this.timeCategory = timeCategory;
        this.timeCategoryIncome = timeCategoryIncome;
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

    public String getTimeCategoryIncome() {
        return timeCategoryIncome;
    }

    public String getTimeExpenses() {
        return timeExpenses;
    }

    public String getTimeIncomes() {
        return timeIncomes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimeCategory(String timeCategory) {
        this.timeCategory = timeCategory;
    }

    public void setTimeCategoryIncome(String timeCategoryIncome) {
        this.timeCategoryIncome = timeCategoryIncome;
    }

    public void setTimeExpenses(String timeExpenses) {
        this.timeExpenses = timeExpenses;
    }

    public void setTimeIncomes(String timeIncomes) {
        this.timeIncomes = timeIncomes;
    }
}
