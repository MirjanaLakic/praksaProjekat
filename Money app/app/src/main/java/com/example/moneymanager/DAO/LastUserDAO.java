package com.example.moneymanager.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

@Dao
public interface LastUserDAO {

    @Query("SELECT * FROM LastUser")
    LastUser getLastUser();

    @Insert
    void newLastUser(LastUser lastUser);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void edit(LastUser lastUser);

}
