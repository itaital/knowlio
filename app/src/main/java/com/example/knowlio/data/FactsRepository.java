package com.example.knowlio.data;

import android.content.Context;

import com.example.knowlio.data.db.DailyFactDao;
import com.example.knowlio.data.db.DailyFactEntity;
import com.example.knowlio.data.db.KnowlioDb;
import com.example.knowlio.data.models.DailyFact;

import java.util.List;

public class FactsRepository {
    private final DailyFactDao dao;

    public FactsRepository(Context context) {
        KnowlioDb db = KnowlioDb.getInstance(context);
        this.dao = db.dailyFactDao();
    }

    public void saveFact(DailyFact fact) {
        DailyFactEntity entity = new DailyFactEntity();
        entity.date = fact.date;
        entity.en = fact.en;
        entity.he = fact.he;
        dao.insert(entity);
    }

    public List<DailyFactEntity> getHistory() {
        return dao.getAll();
    }
}
