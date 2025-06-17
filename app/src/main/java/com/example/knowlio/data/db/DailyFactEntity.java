package com.example.knowlio.data.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DailyFactEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String date;
    public String en;
    public String he;
}
