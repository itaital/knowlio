package com.example.knowlio.activities;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.knowlio.R;
import com.example.knowlio.data.models.DailyFact;
import com.example.knowlio.data.network.FactsApi;
import com.example.knowlio.work.DailyFetchWorker;
import com.example.knowlio.work.DailyReminderWorker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{ Manifest.permission.POST_NOTIFICATIONS },
                    123);
        }

        /* ---------- 1.  הורדת fact אוטומטית פעם ביום ---------- */
        PeriodicWorkRequest fetchRequest =
                new PeriodicWorkRequest.Builder(
                        DailyFetchWorker.class,
                        24, TimeUnit.HOURS)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "daily_fetch",
                ExistingPeriodicWorkPolicy.KEEP,
                fetchRequest);

        /* ---------- 2.  התראת תזכורת יומית ---------- */
        PeriodicWorkRequest reminderRequest =
                new PeriodicWorkRequest.Builder(
                        DailyReminderWorker.class,
                        24, TimeUnit.HOURS)
                        .setInitialDelay(millisUntilNext14h(), TimeUnit.MILLISECONDS)
                        //.setInitialDelay(millisUntilNext14h(), TimeUnit.MILLISECONDS) // לשימוש קבוע
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "daily_reminder",
                ExistingPeriodicWorkPolicy.REPLACE,   // בזמן בדיקה; החזר ל-KEEP אחרי שמוודא
                reminderRequest);

        /* ---------- 3.  הצגת fact במסך ---------- */
        TextView tvContent = findViewById(R.id.tvDailyContent);
        TextView tvTitle   = findViewById(R.id.tvDateTitle);

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gist.githubusercontent.com/itaital/734a548a728191c298d71e60afcd0dd2/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        FactsApi api = retrofit.create(FactsApi.class);

        api.getFact().enqueue(new Callback<DailyFact>() {
            @Override public void onResponse(Call<DailyFact> call, Response<DailyFact> res) {
                if (res.isSuccessful() && res.body() != null) {
                    DailyFact fact = res.body();
                    tvTitle.setText(getString(R.string.daily_fact_title) + " – " + fact.date);

                    SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                    String lang = prefs.getString("pref_lang", Locale.getDefault().getLanguage());
                    tvContent.setText(lang.equals("he") ? fact.he : fact.en);
                } else {
                    tvContent.setText("⚠️ ‎Error: empty body");
                }
            }
            @Override public void onFailure(Call<DailyFact> call, Throwable t) {
                tvContent.setText("⚠️ " + t.getClass().getSimpleName() + " : " + t.getMessage());
            }
        });
    }

    /** כמה מילישניות נשארו עד 14:00 הקרוב. */
    private long millisUntilNext14h() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        long now = cal.getTimeInMillis();

        cal.set(java.util.Calendar.HOUR_OF_DAY, 14);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);

        if (now >= cal.getTimeInMillis()) {
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }
        return cal.getTimeInMillis() - now;
    }
}
