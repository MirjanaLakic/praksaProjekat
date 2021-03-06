package com.example.moneymanager.DAO;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CategoryDAO {

    @Query("SELECT * FROM Categories WHERE type = 'EXPENSES' ORDER BY id DESC")
    LiveData<List<Category>> loadAllExpences();

    @Query("SELECT * FROM Categories WHERE type = 'INCOME' ORDER BY id DESC")
    LiveData<List<Category>> loadAllIncomes();

    @Query("SELECT * FROM Categories WHERE type = 'INCOME' ORDER BY id DESC")
    List<Category> loadIncomes();

    @Query("SELECT * FROM Categories WHERE type = 'EXPENSES' ORDER BY id DESC")
    List<Category> loadExpenses();

    @Query("SELECT * FROM Categories WHERE type = 'EXPENSES' ORDER BY id DESC")
    List<Category> loadIcons();

    @Query("SELECT * FROM Categories WHERE type = 'INCOME' ORDER BY id DESC")
    List<Category> loadIconsIncome();

    @Query("SELECT * FROM Categories")
    List<Category> getAll();


    @Query("SELECT * FROM Categories WHERE id =:id")
    Category findById(int id);

    @Query("SELECT * FROM Categories WHERE name =:name AND photo =:photo AND type =:type")
    Category find(String name, int photo, String type);

    @Insert
    void addCategory(Category category);

    @Delete
    void deleteCateogry(Category category);
}
