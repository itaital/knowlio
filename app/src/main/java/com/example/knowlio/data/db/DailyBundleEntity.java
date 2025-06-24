package com.example.knowlio.data.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DailyBundleEntity {
    @PrimaryKey
    @NonNull
    public String date;

    public String json;
}
