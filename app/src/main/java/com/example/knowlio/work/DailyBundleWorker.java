package com.example.knowlio.work;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.knowlio.activities.MainActivity;
import com.example.knowlio.data.FactsRepository;
import com.example.knowlio.data.models.DailyQuoteBundle;
import com.example.knowlio.data.models.LanguageContent;
import com.example.knowlio.data.network.FactsApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* ⭐ אם NotificationHelper נמצא בחבילה אחרת – עדכן את ה-import */
import com.example.knowlio.work.NotificationHelper;

public class DailyBundleWorker extends Worker {

    private static final String BASE =
            "https://gist.githubusercontent.com/itaital/d2a78fdf63a5112ba58e530982d9f823/raw/";

    public DailyBundleWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        /* ---------- Retrofit ---------- */
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gist.githubusercontent.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        FactsApi api = retrofit.create(FactsApi.class);
        FactsRepository repo = new FactsRepository(getApplicationContext());

        /* ---------- URL של היום ---------- */
        Calendar cal = Calendar.getInstance();
        String formatted = String.format(Locale.US, "%04d_%02d_%02d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        String url = BASE + "daily_knowledge_" + formatted + ".json";

        try {
            Response<DailyQuoteBundle> res = api.getBundle(url).execute();

            if (res.isSuccessful() && res.body() != null) {
                /* ✓ הצלחה – שומרים ומציגים */
                DailyQuoteBundle bundle = res.body();
                repo.saveBundle(bundle);
                showNotification(bundle, prefs);
                return Result.success();

            } else {
                return Result.retry();           // שגיאה אחרת – ננסה שוב
            }

        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();               // כשל רשת – ננסה שוב
        }
    }   //  ←←←  כאן היה חסר הסוגר

    /* --------------------------------------------------------------------- */
    private void showNotification(DailyQuoteBundle bundle, SharedPreferences prefs) {

        NotificationHelper.createDailyFactChannel(getApplicationContext());

        String lang = prefs.getString("pref_lang",
                Locale.getDefault().getLanguage());

        LanguageContent c = bundle.languages.get(lang);
        if (c == null) c = bundle.languages.get("en");

        String text = "";
        if (c != null && c.quoteOfTheDay != null && !c.quoteOfTheDay.isEmpty()) {
            text = c.quoteOfTheDay.get(0);
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(),
                        NotificationHelper.CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Knowlio • Daily Knowledge")
                        .setContentText(text)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        NotificationManagerCompat.from(getApplicationContext())
                .notify(1001, builder.build());
    }
}
