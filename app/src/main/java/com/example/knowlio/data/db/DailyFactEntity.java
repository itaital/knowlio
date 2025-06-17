package com.example.knowlio.data.db;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.knowlio.data.models.DailyFact;

@Entity
public class DailyFactEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String date;
    public String en;
    public String he;

    /** קונסטרוקטור ריק – חובה עבור Room */
    public DailyFactEntity() { }

    /** קונסטרוקטור נוח ל-Repository – Room מתעלם ממנו */
    @Ignore
    public DailyFactEntity(DailyFact f) {
        this.date = f.date;
        this.en   = f.en;
        this.he   = f.he;
    }
}
