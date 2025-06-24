package com.example.knowlio.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DailyFactEntity.class, DailyBundleEntity.class}, version = 2)
public abstract class KnowlioDb extends RoomDatabase {
    private static volatile KnowlioDb INSTANCE;

    public abstract DailyFactDao dailyFactDao();
    public abstract DailyBundleDao dailyBundleDao();

    public static KnowlioDb getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (KnowlioDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            KnowlioDb.class, "knowlio.db")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
