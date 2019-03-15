package com.example.moneymanager.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface ExpensesAndIncomeDAO {

    @Query("SELECT * FROM ExpensesAndIncomes WHERE type = 'EXPENSE'")
    LiveData<List<ExpensesAndIncomes>> getAllExpenses();

    @Query("SELECT * FROM ExpensesAndIncomes WHERE type = 'INCOME'")
    LiveData<List<ExpensesAndIncomes>> getAllIncome();

    @Query("SELECT * FROM ExpensesAndIncomes WHERE category =:idCategory")
    List<ExpensesAndIncomes> getExpensesForOneCategory(int idCategory);

    @Insert
    void addNew(ExpensesAndIncomes expensesAndIncomes);

    @Query("SELECT * FROM ExpensesAndIncomes WHERE id =:id")
    ExpensesAndIncomes findById(int id);

    @Delete
    void delete(ExpensesAndIncomes item);
}
