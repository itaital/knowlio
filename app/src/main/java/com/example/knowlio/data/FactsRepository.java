package com.example.knowlio.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;

import com.example.knowlio.data.db.DailyFactDao;
import com.example.knowlio.data.db.DailyFactEntity;
import com.example.knowlio.data.db.DailyBundleDao;
import com.example.knowlio.data.db.DailyBundleEntity;
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
    private final DailyBundleDao bundleDao;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public FactsRepository(Context ctx) {
        KnowlioDb db = KnowlioDb.getInstance(ctx);
        dao = db.dailyFactDao();
        bundleDao = db.dailyBundleDao();
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
    }

    /* שמירת fact — כמו קודם, אבל ברקע */
    public void saveFact(DailyFact fact) {
        DailyFactEntity e = new DailyFactEntity(fact);
        Executors.newSingleThreadExecutor().execute(() -> dao.insert(e));
    }

    /* שמירת חבילת ציטוטים עבור תאריך נתון */
    public void saveBundle(LocalDate date, DailyQuoteBundle bundle) {
        DailyBundleEntity e = new DailyBundleEntity();
        e.date = date.toString();
        e.json = gson.toJson(bundle);
        Executors.newSingleThreadExecutor().execute(() -> bundleDao.insert(e));
    }

    /** גרסת נוחות המשמרת את חבילת היום */
    public void saveBundle(DailyQuoteBundle bundle) {
        saveBundle(LocalDate.now(), bundle);
    }

    /** שמירת היסטוריית חבילות */
    public void saveHistory(List<DailyQuoteBundle> bundles) {
        Executors.newSingleThreadExecutor().execute(() -> {
            for (DailyQuoteBundle b : bundles) {
                if (b != null && b.date != null) {
                    DailyBundleEntity e = new DailyBundleEntity();
                    e.date = b.date;
                    e.json = gson.toJson(b);
                    bundleDao.insert(e);
                }
            }
        });
    }

    /** Observe bundle for a given date as LiveData. */
    public LiveData<DailyQuoteBundle> observeBundle(LocalDate date) {
        return androidx.lifecycle.Transformations.map(
                bundleDao.observeJson(date.toString()),
                json -> {
                    if (json == null) return null;
                    try {
                        return gson.fromJson(json, DailyQuoteBundle.class);
                    } catch (Exception e) {
                        return null;
                    }
                });
    }

    /** Convenience wrapper used by the UI */
    public LiveData<DailyQuoteBundle> getBundleLive(LocalDate date) {
        return observeBundle(date);
    }

    @Nullable
    private DailyQuoteBundle loadBundle() {
        return getBundle(LocalDate.now());
    }

    @Nullable
    public DailyQuoteBundle getBundle(LocalDate date) {
        DailyBundleEntity e = bundleDao.getByDate(date.toString());
        if (e == null || e.json == null) return null;
        try {
            return gson.fromJson(e.json, DailyQuoteBundle.class);
        } catch (Exception ex) {
            return null;
        }
    }

    public List<LocalDate> listAvailableDates() {
        List<String> ds = bundleDao.listDatesSync();
        List<LocalDate> list = new ArrayList<>();
        for (String d : ds) {
            try {
                list.add(LocalDate.parse(d));
            } catch (Exception ignored) { }
        }
        return list;
    }

    /** Live list of all available dates, newest first. */
    public LiveData<List<LocalDate>> listDatesLive() {
        return androidx.lifecycle.Transformations.map(
                bundleDao.listDates(),
                ds -> {
                    List<LocalDate> list = new ArrayList<>();
                    if (ds != null) {
                        for (String d : ds) {
                            try {
                                list.add(LocalDate.parse(d));
                            } catch (Exception ignored) {}
                        }
                    }
                    return list;
                });
    }

    /** Return all dates with bundles, newest first. */
    public List<LocalDate> listDates() {
        List<LocalDate> list = new ArrayList<>(listAvailableDates());
        Collections.sort(list, Collections.reverseOrder());
        return list;
    }

    /** Latest saved bundle or null. */
    @Nullable
    public DailyQuoteBundle getLatestBundle() {
        List<LocalDate> dates = listDates();
        if (dates.isEmpty()) return null;
        return getBundle(dates.get(0));
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
