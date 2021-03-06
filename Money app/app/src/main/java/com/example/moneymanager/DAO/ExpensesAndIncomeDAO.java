package com.example.moneymanager.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface ExpensesAndIncomeDAO {

    @Query("SELECT * FROM ExpensesAndIncomes")
    LiveData<List<ExpensesAndIncomes>> getAll();

    @Query("SELECT * FROM ExpensesAndIncomes WHERE type = 'EXPENSES'")
    LiveData<List<ExpensesAndIncomes>> getAllExpenses();

    @Query("SELECT * FROM ExpensesAndIncomes WHERE type = 'EXPENSES'")
    List<ExpensesAndIncomes> getExpenses();

    @Query("SELECT * FROM ExpensesAndIncomes WHERE type = 'INCOME'")
    LiveData<List<ExpensesAndIncomes>> getAllIncome();

    @Query("SELECT * FROM ExpensesAndIncomes WHERE type = 'INCOME'")
    List<ExpensesAndIncomes> getIncome();

    @Query("SELECT * FROM ExpensesAndIncomes")
    List<ExpensesAndIncomes> getall();

    @Query("SELECT * FROM ExpensesAndIncomes WHERE category =:idCategory")
    List<ExpensesAndIncomes> getExpensesForOneCategory(int idCategory);

    @Insert
    void addNew(ExpensesAndIncomes expensesAndIncomes);

    @Query("SELECT * FROM ExpensesAndIncomes WHERE id =:id")
    ExpensesAndIncomes findById(int id);


    @Query("SELECT * FROM ExpensesAndIncomes WHERE note =:note AND price =:price AND date =:date AND type =:type AND category =:category")
    ExpensesAndIncomes find(String note, float price, Date date, String type, int category);

    @Query("SELECT * FROM ExpensesAndIncomes WHERE id =:id")
    LiveData<ExpensesAndIncomes> findItem(int id);

    @Delete
    void delete(ExpensesAndIncomes item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void edit(ExpensesAndIncomes item);
}
