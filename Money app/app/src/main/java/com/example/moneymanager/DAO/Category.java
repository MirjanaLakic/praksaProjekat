package com.example.moneymanager.DAO;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Categories")
public class Category {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int photo;
    private String type;

    public Category() {
    }

    @Ignore
    public Category(String name, int photo, String type) {
        this.name = name;
        this.photo = photo;
        this.type = type;
    }

    public Category(int id, String name, int photo, String type) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getPhoto() {
        return photo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public void setId(int id) {
        this.id = id;
    }
}
