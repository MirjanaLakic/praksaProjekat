package com.example.moneymanager.DAO;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class LastUser {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String email;

    public LastUser(int id, String email) {
        this.id = id;
        this.email = email;
    }

    public LastUser() {
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
