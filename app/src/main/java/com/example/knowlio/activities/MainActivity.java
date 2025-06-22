package com.example.knowlio.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.knowlio.R;
import com.example.knowlio.fragments.HomeFragment;
import com.example.knowlio.work.DailyBundleWorker;
import com.example.knowlio.work.DailyReminderWorker;

import java.util.concurrent.TimeUnit;

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
                        DailyBundleWorker.class,
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
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "daily_reminder",
                ExistingPeriodicWorkPolicy.REPLACE,
                reminderRequest);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new HomeFragment())
                    .commit();
        }
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
