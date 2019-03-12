package com.example.moneymanager.DAO;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "ExpensesAndIncomes")
public class ExpensesAndIncomes {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String note;
    private float price;
    private Date date;
    private int category;

    @Ignore
    public ExpensesAndIncomes(String note, float price, Date date, int category) {
        this.note = note;
        this.price = price;
        this.date = date;
        this.category = category;
    }

    public ExpensesAndIncomes(int id, String note, float price, Date date, int category) {
        this.id = id;
        this.note = note;
        this.price = price;
        this.date = date;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public float getPrice() {
        return price;
    }

    public Date getDate() {
        return date;
    }

    public int getCategory() {
        return category;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
