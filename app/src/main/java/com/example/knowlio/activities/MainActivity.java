package com.example.knowlio.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.content.Context;

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
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

import com.example.knowlio.R;
import com.example.knowlio.BuildConfig;
import com.example.knowlio.fragments.HomeFragment;
import com.example.knowlio.work.DailyBundleWorker;   // ← הוסף שורה זו

import com.example.knowlio.work.DailyReminderWorker;

import androidx.preference.PreferenceManager;
import android.content.SharedPreferences;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    
    private android.content.BroadcastReceiver syncCompleteReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Enqueue immediate sync on app start with network constraint and expedited execution
        OneTimeWorkRequest immediateSyncRequest = new OneTimeWorkRequest.Builder(DailyBundleWorker.class)
                .setConstraints(new androidx.work.Constraints.Builder()
                        .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                        .build())
                .setExpedited(androidx.work.OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build();
        
        WorkManager.getInstance(this).enqueueUniqueWork(
                "syncDailyNow",
                androidx.work.ExistingWorkPolicy.REPLACE,
                immediateSyncRequest);
        
        // Add manual sync button to toolbar
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_more);
        toolbar.setNavigationOnClickListener(v -> {
            // Manual sync button clicked
            OneTimeWorkRequest manualSyncRequest = new OneTimeWorkRequest.Builder(DailyBundleWorker.class)
                    .setConstraints(new androidx.work.Constraints.Builder()
                            .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                            .build())
                    .setExpedited(androidx.work.OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .build();
            
            WorkManager.getInstance(this).enqueueUniqueWork(
                    "manualSync",
                    androidx.work.ExistingWorkPolicy.REPLACE,
                    manualSyncRequest);
            
            Toast.makeText(this, "🔄 Manual sync started!", Toast.LENGTH_SHORT).show();
            
            // Try to register broadcast receiver for sync completion
            try {
                registerSyncCompleteReceiver();
            } catch (Exception e) {
                android.util.Log.e("MainActivity", "❌ Failed to register broadcast receiver, using fallback", e);
                // Fallback: schedule a delayed UI refresh
                scheduleFallbackUIRefresh();
            }
        });


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

        // Daily periodic sync is now handled in App.onCreate()
        // No need to schedule it here anymore

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
    
    /**
     * Registers broadcast receiver to listen for sync completion
     */
    private void registerSyncCompleteReceiver() {
        try {
            android.util.Log.d("MainActivity", "🔧 Starting to register sync complete receiver...");
            
            if (syncCompleteReceiver != null) {
                try {
                    unregisterReceiver(syncCompleteReceiver);
                    android.util.Log.d("MainActivity", "🔧 Unregistered previous receiver");
                } catch (Exception e) {
                    android.util.Log.e("MainActivity", "Error unregistering previous receiver", e);
                }
            }
            
            syncCompleteReceiver = new android.content.BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        android.util.Log.d("MainActivity", "📡 Broadcast received: " + intent.getAction());
                        
                        if ("com.example.knowlio.SYNC_COMPLETE".equals(intent.getAction())) {
                            String bundleDate = intent.getStringExtra("bundle_date");
                            long syncTimestamp = intent.getLongExtra("sync_timestamp", 0);
                            
                            android.util.Log.d("MainActivity", "📡 Received sync complete broadcast for bundle: " + bundleDate);
                            
                            // Refresh the HomeFragment immediately after sync completes
                            refreshUIAfterSync(bundleDate);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("MainActivity", "❌ CRASH in broadcast receiver!", e);
                        // Show error to user instead of crashing
                        Toast.makeText(MainActivity.this, "❌ Error processing sync: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            };
            
            // Register the receiver with proper export flag for Android 14+
            android.content.IntentFilter filter = new android.content.IntentFilter("com.example.knowlio.SYNC_COMPLETE");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // Android 14+ requires explicit export flag
                registerReceiver(syncCompleteReceiver, filter, android.content.Context.RECEIVER_NOT_EXPORTED);
            } else {
                // Older Android versions
                registerReceiver(syncCompleteReceiver, filter);
            }
            
            android.util.Log.d("MainActivity", "✅ Successfully registered sync complete broadcast receiver");
            
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "❌ CRASH registering broadcast receiver!", e);
            Toast.makeText(this, "❌ Error setting up sync: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Refreshes the UI after sync completion
     */
    private void refreshUIAfterSync(String bundleDate) {
        try {
            android.util.Log.d("MainActivity", "🔄 Starting UI refresh after sync for bundle: " + bundleDate);
            
            // Wait a moment for the database to be fully updated
            new android.os.Handler().postDelayed(() -> {
                try {
                    android.util.Log.d("MainActivity", "🔄 Executing delayed UI refresh...");
                    
                    if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof HomeFragment) {
                        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                        if (homeFragment != null) {
                            android.util.Log.d("MainActivity", "🔄 Found HomeFragment, starting refresh...");
                            
                            try {
                                // Force refresh by clearing cache and reloading
                                homeFragment.loadData();
                                android.util.Log.d("MainActivity", "✅ loadData() completed successfully");
                                
                                // Also force a fragment refresh
                                getSupportFragmentManager().beginTransaction()
                                        .detach(homeFragment)
                                        .attach(homeFragment)
                                        .commit();
                                android.util.Log.d("MainActivity", "✅ Fragment transaction completed successfully");
                                
                                Toast.makeText(this, "🔄 UI refreshed after sync! Bundle: " + bundleDate, Toast.LENGTH_LONG).show();
                                
                                android.util.Log.d("MainActivity", "✅ UI refresh completed for bundle: " + bundleDate);
                                
                            } catch (Exception e) {
                                android.util.Log.e("MainActivity", "❌ CRASH during fragment operations!", e);
                                Toast.makeText(this, "❌ Error refreshing UI: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            android.util.Log.w("MainActivity", "⚠️ HomeFragment is null");
                        }
                    } else {
                        android.util.Log.w("MainActivity", "⚠️ Container doesn't contain HomeFragment");
                    }
                    
                } catch (Exception e) {
                    android.util.Log.e("MainActivity", "❌ CRASH in delayed UI refresh!", e);
                    Toast.makeText(this, "❌ Error in delayed refresh: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }, 1000); // Wait 1 second for database to be fully updated
            
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "❌ CRASH in refreshUIAfterSync!", e);
            Toast.makeText(this, "❌ Error starting UI refresh: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Fallback UI refresh method when broadcast receiver fails
     */
    private void scheduleFallbackUIRefresh() {
        try {
            android.util.Log.d("MainActivity", "🔄 Scheduling fallback UI refresh...");
            
            // Wait for sync to complete, then refresh UI
            new android.os.Handler().postDelayed(() -> {
                try {
                    android.util.Log.d("MainActivity", "🔄 Executing fallback UI refresh...");
                    
                    if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof HomeFragment) {
                        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                        if (homeFragment != null) {
                            // Force refresh by clearing cache and reloading
                            homeFragment.loadData();
                            
                            // Also force a fragment refresh
                            getSupportFragmentManager().beginTransaction()
                                    .detach(homeFragment)
                                    .attach(homeFragment)
                                    .commit();
                            
                            Toast.makeText(this, "🔄 UI refreshed after sync (fallback)!", Toast.LENGTH_LONG).show();
                            android.util.Log.d("MainActivity", "✅ Fallback UI refresh completed");
                        }
                    }
                } catch (Exception e) {
                    android.util.Log.e("MainActivity", "❌ CRASH in fallback UI refresh!", e);
                    Toast.makeText(this, "❌ Error in fallback refresh: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }, 5000); // Wait 5 seconds for sync to complete
            
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "❌ CRASH scheduling fallback refresh!", e);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Unregister broadcast receiver
        if (syncCompleteReceiver != null) {
            try {
                unregisterReceiver(syncCompleteReceiver);
                android.util.Log.d("MainActivity", "📡 Unregistered sync complete broadcast receiver");
            } catch (Exception e) {
                android.util.Log.e("MainActivity", "Error unregistering broadcast receiver", e);
            }
        }
    }
}
