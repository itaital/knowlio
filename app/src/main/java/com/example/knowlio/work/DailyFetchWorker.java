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

import com.example.knowlio.R;
import com.example.knowlio.activities.MainActivity;
import com.example.knowlio.data.models.DailyFact;
import com.example.knowlio.data.FactsRepository;
import com.example.knowlio.data.network.FactsApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DailyFetchWorker extends Worker {

    public DailyFetchWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gist.githubusercontent.com/itaital/734a548a728191c298d71e60afcd0dd2/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        FactsApi api = retrofit.create(FactsApi.class);
        FactsRepository repo = new FactsRepository(getApplicationContext());

        try {
            Response<DailyFact> res = api.getFact().execute();
            if (res.isSuccessful() && res.body() != null) {
                DailyFact fact = res.body();
                repo.saveFact(fact);
                prefs.edit()
                        .putString("cached_fact_date", fact.date)
                        .putString("cached_fact_en", fact.en)
                        .putString("cached_fact_he", fact.he)
                        .apply();
                showNotification(fact, prefs);
                return Result.success();
            } else {
                return Result.retry();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }

    private void showNotification(DailyFact fact, SharedPreferences prefs) {
        NotificationHelper.createDailyFactChannel(getApplicationContext());
        String lang = prefs.getString("pref_lang", Locale.getDefault().getLanguage());
        boolean isHeb = "he".equals(lang);
        String text = isHeb ? fact.he : fact.en;

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NotificationHelper.CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Knowlio • Daily Knowledge")
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat.from(getApplicationContext()).notify(1001, builder.build());
    }
}
