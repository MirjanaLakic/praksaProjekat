package com.example.moneymanager.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface ExpensesAndIncomeDAO {

    @Query("SELECT * FROM ExpensesAndIncomes")
    LiveData<List<ExpensesAndIncomes>> getAll();

    @Insert
    void addNew(ExpensesAndIncomes expensesAndIncomes);
}
