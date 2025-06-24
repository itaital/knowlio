package com.example.knowlio.work;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.knowlio.activities.MainActivity;
import com.example.knowlio.data.FactsRepository;
import com.example.knowlio.data.models.DailyQuoteBundle;
import com.example.knowlio.data.models.LanguageContent;
import com.example.knowlio.data.models.QuoteSection;
import com.example.knowlio.data.network.FactsApi;
import com.example.knowlio.data.network.QuotableApi;
import com.example.knowlio.data.network.QuoteResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DailyBundleWorker extends Worker {

    /** root of your secret Gist **/
    private static final String BASE =
            "https://gist.githubusercontent.com/itaital/d2a78fdf63a5112ba58e530982d9f823/raw/";

    public DailyBundleWorker(@NonNull Context context,
                             @NonNull WorkerParameters params) {
        super(context, params);
    }

    /*───────────────────────────────────────────────────────────────────────*/
    @NonNull @Override
    public Result doWork() {

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        /* ---------- Retrofit wrappers ---------- */
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit gistRetrofit = new Retrofit.Builder()
                .baseUrl("https://gist.githubusercontent.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Retrofit quoteRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.quotable.io/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        FactsApi     factsApi  = gistRetrofit .create(FactsApi.class);
        QuotableApi  quoteApi  = quoteRetrofit.create(QuotableApi.class);
        FactsRepository repo   = new FactsRepository(getApplicationContext());

        // fetch history once per run
        try {
            retrofit2.Response<com.google.gson.JsonElement> hRes =
                    factsApi.getRaw(BASE + "cache_history.json").execute();
            if (hRes.isSuccessful() && hRes.body() != null) {
                DailyQuoteBundle[] arr = new Gson().fromJson(hRes.body(), DailyQuoteBundle[].class);
                repo.saveHistory(java.util.Arrays.asList(arr));
            }
        } catch (IOException ignored) { }

        /* ---------- build today’s URL ---------- */
        LocalDate today      = LocalDate.now();
        String    formatted  = today.format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
        String    bundleUrl  = BASE + "daily_knowledge_" + formatted + ".json";

        try {
            Response<DailyQuoteBundle> res = factsApi.getDaily(bundleUrl).execute();

            if (res.isSuccessful() && res.body() != null) {
                /* got the bundle of the day -------------------------------- */
                DailyQuoteBundle bundle = res.body();

                // make sure there are 2 quotes – top-up from Quotable if needed
                LanguageContent en = bundle.languages.get("en");
                if (en != null) {
                    if (en.quoteOfTheDay == null)
                        en.quoteOfTheDay = new QuoteSection();
                    int guard = 0;
                    while (en.quoteOfTheDay.size() < 2 && guard < 3) {
                        try {
                            Response<QuoteResponse> q = quoteApi.getRandom().execute();
                            if (q.isSuccessful() && q.body() != null) {
                                en.quoteOfTheDay.add(
                                        q.body().content + " – " + q.body().author);
                            }
                        } catch (IOException ignored) {}
                        guard++;
                    }
                }

                repo.saveBundle(bundle);
                prefs.edit().putString("last_fetch", today.toString()).apply();
                showNotification(bundle, prefs);
                return Result.success();

            } else if (res.code() == 404) {
                /* nothing for today – fallback to latest cached bundle ----- */
                DailyQuoteBundle cached = repo.getLatestBundle();
                if (cached != null) {
                    showNotification(cached, prefs);
                    return Result.success();
                }
                return Result.retry();
            } else {
                return Result.retry();          // unknown HTTP error
            }

        } catch (IOException io) {
            io.printStackTrace();
            return Result.retry();              // network trouble – try again later
        }
    }

    /*───────────────────────── helper ─────────────────────────*/
    private void showNotification(DailyQuoteBundle bundle, SharedPreferences prefs) {

        NotificationHelper.createDailyFactChannel(getApplicationContext());

        String lang = prefs.getString("pref_lang",
                Locale.getDefault().getLanguage());

        LanguageContent block = bundle.languages.getOrDefault(lang,
                bundle.languages.get("en"));

        String text = (block != null && block.quoteOfTheDay != null
                && !block.quoteOfTheDay.isEmpty())
                ? block.quoteOfTheDay.get(0)
                : "";

        Intent intent = new Intent(getApplicationContext(), MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pi = PendingIntent.getActivity(
                getApplicationContext(), 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder nb =
                new NotificationCompat.Builder(getApplicationContext(),
                        NotificationHelper.CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Knowlio • Daily Knowledge")
                        .setContentText(text)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                        .setContentIntent(pi)
                        .setAutoCancel(true);

        NotificationManagerCompat.from(getApplicationContext())
                .notify(1001, nb.build());
    }
}
