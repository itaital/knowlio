package com.example.knowlio.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.knowlio.data.db.DailyFactDao;
import com.example.knowlio.data.db.DailyFactEntity;
import com.example.knowlio.data.db.KnowlioDb;
import com.example.knowlio.data.models.DailyFact;

import java.util.List;
import java.util.concurrent.Executors;

public class FactsRepository {

    private final DailyFactDao dao;

    public FactsRepository(Context ctx) {
        KnowlioDb db = KnowlioDb.getInstance(ctx);
        dao = db.dailyFactDao();
    }

    /* שמירת fact — כמו קודם, אבל ברקע */
    public void saveFact(DailyFact fact) {
        DailyFactEntity e = new DailyFactEntity(fact);
        Executors.newSingleThreadExecutor().execute(() -> dao.insert(e));
    }

    /* החזר היסטוריה כ-LiveData — לא חוסם UI */
    public LiveData<List<DailyFactEntity>> getHistory() {
        return dao.getAll();
    }
}
