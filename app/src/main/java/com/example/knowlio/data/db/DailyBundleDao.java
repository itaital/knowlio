package com.example.knowlio.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DailyBundleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DailyBundleEntity entity);

    @Query("SELECT json FROM DailyBundleEntity WHERE date = :date LIMIT 1")
    String getJson(String date);

    @Query("SELECT json FROM DailyBundleEntity WHERE date = :date LIMIT 1")
    LiveData<String> observeJson(String date);

    @Query("SELECT date FROM DailyBundleEntity ORDER BY date DESC")
    List<String> listDates();
}
