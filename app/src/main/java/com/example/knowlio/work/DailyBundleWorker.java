package com.example.knowlio.work;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.knowlio.data.FactsRepository;
import com.example.knowlio.data.models.DailyQuoteBundle;
import com.example.knowlio.data.network.FactsApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DailyBundleWorker extends Worker {

    /** RAW-base (מסתיים ב-/raw/) */
    private static final String BASE =
            "https://gist.githubusercontent.com/itaital/d2a78fdf63a5112ba58e530982d9f823/raw/";

    public DailyBundleWorker(@NonNull Context ctx,
                             @NonNull WorkerParameters params) {
        super(ctx, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        /* 1.  daily_knowledge_YYYY_MM_DD.json */
        LocalDate today   = LocalDate.now();
        String fileName   = "daily_knowledge_" +
                today.format(DateTimeFormatter.ofPattern("yyyy_MM_dd")) + ".json";

        /* 2.  Retrofit + logging interceptor -------------------------------- */
        HttpLoggingInterceptor log = new HttpLoggingInterceptor();
        log.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient ok = new OkHttpClient.Builder()
                .addInterceptor(log)          //  <-- log every request/response
                .build();

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE)                // חייב להסתיים ב-raw/
                .client(ok)                   //  <-- using the client with logs
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        FactsApi api = retrofit.create(FactsApi.class);

        try {
            Response<DailyQuoteBundle> res = api.getBundle(fileName).execute();
            if (!res.isSuccessful() || res.body() == null) return Result.retry();

            /* 3.  Room */
            FactsRepository repo = new FactsRepository(getApplicationContext());
            repo.saveBundle(res.body());

            /* 4.  Prefs (אופציונלי לתצוגה מיידית) */
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            prefs.edit()
                    .putString("cached_fact_date", res.body().date)
                    .apply();

            return Result.success();

        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}
