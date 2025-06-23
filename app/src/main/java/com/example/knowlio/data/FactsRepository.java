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

import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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

    /* שמירת חבילת ציטוטים עבור תאריך נתון */
    public void saveBundle(LocalDate date, DailyQuoteBundle bundle) {
        prefs.edit().putString("bundle_" + date.toString(), gson.toJson(bundle)).apply();
    }

    /** גרסת נוחות המשמרת את חבילת היום */
    public void saveBundle(DailyQuoteBundle bundle) {
        saveBundle(LocalDate.now(), bundle);
    }

    @Nullable
    private DailyQuoteBundle loadBundle() {
        return getBundle(LocalDate.now());
    }

    @Nullable
    public DailyQuoteBundle getBundle(LocalDate date) {
        String json = prefs.getString("bundle_" + date.toString(), null);
        if (json == null) return null;
        try {
            return gson.fromJson(json, DailyQuoteBundle.class);
        } catch (Exception e) {
            return null;
        }
    }

    public LocalDate[] listAvailableDates() {
        List<LocalDate> list = new ArrayList<>();
        for (String k : prefs.getAll().keySet()) {
            if (k.startsWith("bundle_")) {
                String d = k.substring(7);
                try {
                    list.add(LocalDate.parse(d));
                } catch (Exception ignored) { }
            }
        }
        Collections.sort(list);
        return list.toArray(new LocalDate[0]);
    }

    /** Return all dates with bundles, newest first. */
    public LocalDate[] listDates() {
        LocalDate[] arr = listAvailableDates();
        List<LocalDate> list = new ArrayList<>();
        Collections.addAll(list, arr);
        Collections.sort(list, Collections.reverseOrder());
        return list.toArray(new LocalDate[0]);
    }

    /** Latest saved bundle or null. */
    @Nullable
    public DailyQuoteBundle getLatestBundle() {
        LocalDate[] dates = listDates();
        if (dates.length == 0) return null;
        return getBundle(dates[0]);
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
