package com.example.knowlio.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DailyFactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DailyFactEntity... facts);

    @Query("SELECT * FROM DailyFactEntity ORDER BY date DESC")
    List<DailyFactEntity> getAll();
}
