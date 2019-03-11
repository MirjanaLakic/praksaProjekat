package com.example.moneymanager.DAO;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CategoryDAO {

    @Query("SELECT * FROM Categories WHERE type = 'EXPENSES'")
    List<Category> loadAllExpences();

    @Query("SELECT * FROM Categories WHERE type = 'INCOME'")
    List<Category> loadAllIncomes();

    @Query("SELECT * FROM Categories WHERE id =:id")
    Category findById(int id);

    @Insert
    void addCategory(Category category);

    @Delete
    void deleteCateogry(Category category);
}
