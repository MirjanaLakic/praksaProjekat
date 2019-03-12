package com.example.moneymanager.DAO;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "ExpensesAndIncomes")
public class ExpensesAndIncomes {

    @PrimaryKey(autoGenerate = true)
    private int id;
    String name;
    String note;
    float price;
    Date date;
    String category;

    public ExpensesAndIncomes(int id,String name, String note, float price, Date date, String category) {
        this.name = name;
        this.id = id;
        this.note = note;
        this.price = price;
        this.date = date;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public String getCategory() {
        return category;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setCategory(String category) {
        this.category = category;
    }
}
