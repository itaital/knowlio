package com.example.knowlio.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.preference.PreferenceManager;

import com.example.knowlio.data.db.DailyBundleDao;
import com.example.knowlio.data.db.DailyBundleEntity;
import com.example.knowlio.data.db.DailyFactDao;
import com.example.knowlio.data.db.DailyFactEntity;
import com.example.knowlio.data.db.KnowlioDb;
import com.example.knowlio.data.models.DailyFact;
import com.example.knowlio.data.models.DailyQuoteBundle;
import com.example.knowlio.data.models.LanguageContent;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;

public class FactsRepository {

    private final DailyFactDao   factDao;
    private final DailyBundleDao bundleDao;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public FactsRepository(Context ctx) {
        KnowlioDb db = KnowlioDb.getInstance(ctx);
        factDao    = db.dailyFactDao();
        bundleDao  = db.dailyBundleDao();
        prefs      = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
    }

    /* ---------------------- FACTS ---------------------- */

    public void saveFact(DailyFact fact) {
        DailyFactEntity e = new DailyFactEntity(fact);
        Executors.newSingleThreadExecutor().execute(() -> factDao.insert(e));
    }

    public LiveData<List<DailyFactEntity>> getHistory()            { return factDao.getAll(); }
    public LiveData<DailyFactEntity>      getLatest()             { return factDao.getLatest(); }

    /* -------------------- BUNDLES ---------------------- */

    public void saveBundle(@NonNull LocalDate date, @NonNull DailyQuoteBundle bundle) {
        DailyBundleEntity e = new DailyBundleEntity();
        e.date = date.toString();
        e.json = gson.toJson(bundle);
        Executors.newSingleThreadExecutor().execute(() -> bundleDao.insert(e));
    }

    public void saveBundle(@NonNull DailyQuoteBundle bundle) {
        saveBundle(LocalDate.now(), bundle);
    }

    public void saveHistory(@NonNull List<DailyQuoteBundle> bundles) {
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

    /* Live-Data wrapper used ע״י HomeFragment */
    public LiveData<DailyQuoteBundle> getBundleLive(@NonNull LocalDate date) {
        return Transformations.map(
                bundleDao.observeJson(date.toString()),
                json -> json == null ? null : gson.fromJson(json, DailyQuoteBundle.class));
    }

    /* סינכרוני – HistoryFragment משתמש בזה */
    @Nullable
    public DailyQuoteBundle getBundle(@NonNull LocalDate date) {
        DailyBundleEntity e = bundleDao.getByDate(date.toString());
        return e == null ? null : gson.fromJson(e.json, DailyQuoteBundle.class);
    }

    /* -------------------- DATE HELPERS ----------------- */

    /** כל התאריכים שיש להם חבילה (יורד). */
    public List<LocalDate> getAllBundleDates() {
        List<String> raw = bundleDao.listDatesSync();    // DAO מחזיר List<String>
        List<LocalDate> list = new ArrayList<>();
        for (String d : raw) {
            try { list.add(LocalDate.parse(d)); } catch (Exception ignore) {}
        }
        list.sort(Comparator.reverseOrder());
        return list;
    }

    /** החבילה האחרונה / null. */
    @Nullable
    public DailyQuoteBundle getLatestBundle() {
        List<LocalDate> dates = getAllBundleDates();
        return dates.isEmpty() ? null : getBundle(dates.get(0));
    }

    /** החבילה של היום, בשפה המבוקשת. */
    public LanguageContent getTodayBundle(String lang) {
        DailyQuoteBundle b = getBundle(LocalDate.now());
        if (b == null || b.languages == null) return null;
        LanguageContent c = b.languages.get(lang);
        return c != null ? c : b.languages.get("en");
    }
}
