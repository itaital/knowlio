package com.example.knowlio.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.knowlio.R;
import com.example.knowlio.BuildConfig;
import com.example.knowlio.fragments.HomeFragment;
import com.example.knowlio.work.DailyBundleWorker;
import com.example.knowlio.work.DailyReminderWorker;

import java.util.concurrent.TimeUnit;
import androidx.preference.PreferenceManager;
import android.content.SharedPreferences;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.contains("pref_lang")) {
            prefs.edit()
                    .putString("pref_lang", Locale.getDefault().getLanguage())
                    .apply();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_history) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new com.example.knowlio.fragments.HistoryFragment())
                    .addToBackStack(null)
                    .commit();
            return true;
        } else if (id == R.id.menu_language) {
            showLanguageDialog();
            return true;
        } else if (id == R.id.menu_feedback) {
            sendFeedback();
            return true;
        } else if (id == R.id.menu_about) {
            showAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLanguageDialog() {
        final String[] codes = {"en", "he", "es", "fr", "de", "pt"};
        final String[] items = {"English", "Hebrew", "Spanish", "French", "German", "Portuguese"};
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String current = prefs.getString("pref_lang", codes[0]);
        int checked = 0;
        for (int i = 0; i < codes.length; i++) if (codes[i].equals(current)) checked = i;

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.menu_language)
                .setSingleChoiceItems(items, checked, (d, which) -> {
                    prefs.edit().putString("pref_lang", codes[which]).apply();
                    d.dismiss();
                    recreate();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void sendFeedback() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@knowlio.example"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Knowlio feedback");
        intent.putExtra(Intent.EXTRA_TEXT, "Hi, ");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.no_mail, Toast.LENGTH_LONG).show();
        }
    }

    private void showAboutDialog() {
        String msg = "Version " + BuildConfig.VERSION_NAME + "\n" +
                "Daily quotes & knowledge nuggets in 6 languages";
        new MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.ic_launcher)
                .setTitle(R.string.app_name)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null)
                .show();
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
