package com.example.knowlio.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;

import com.example.knowlio.data.db.DailyFactDao;
import com.example.knowlio.data.db.DailyFactEntity;
import com.example.knowlio.data.db.KnowlioDb;
import com.example.knowlio.data.models.DailyFact;
import com.example.knowlio.data.models.DailyQuoteBundle;
import com.example.knowlio.data.models.LanguageContent;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.Executors;

public class FactsRepository {

    private final DailyFactDao dao;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public FactsRepository(Context ctx) {
        KnowlioDb db = KnowlioDb.getInstance(ctx);
        dao = db.dailyFactDao();
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
    }

    /* שמירת fact — כמו קודם, אבל ברקע */
    public void saveFact(DailyFact fact) {
        DailyFactEntity e = new DailyFactEntity(fact);
        Executors.newSingleThreadExecutor().execute(() -> dao.insert(e));
    }

    /* שמירת חבילת ציטוטים */
    public void saveBundle(DailyQuoteBundle bundle) {
        prefs.edit().putString("daily_bundle", gson.toJson(bundle)).apply();
    }

    private DailyQuoteBundle loadBundle() {
        String json = prefs.getString("daily_bundle", null);
        if (json == null) return null;
        try { return gson.fromJson(json, DailyQuoteBundle.class); } catch (Exception e) { return null; }
    }

    public LanguageContent getTodayBundle(String lang) {
        DailyQuoteBundle b = loadBundle();
        if (b == null || b.languages == null) return null;
        LanguageContent c = b.languages.get(lang);
        if (c == null) c = b.languages.get("en");
        return c;
    }

    /* החזר היסטוריה כ-LiveData — לא חוסם UI */
    public LiveData<List<DailyFactEntity>> getHistory() {
        return dao.getAll();
    }

    /* העובדה העדכנית ביותר */
    public LiveData<DailyFactEntity> getLatest() {
        return dao.getLatest();
    }
}
