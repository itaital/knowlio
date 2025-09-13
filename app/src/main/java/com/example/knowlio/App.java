package com.example.knowlio;

import android.app.Application;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.example.knowlio.work.DailyBundleWorker;
import java.util.concurrent.TimeUnit;

public class App extends Application {
    
    private static final String TAG = "KnowlioApp";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Schedule daily periodic sync with network constraint
        PeriodicWorkRequest dailySyncRequest = new PeriodicWorkRequest.Builder(
                DailyBundleWorker.class,
                1, TimeUnit.DAYS,
                3, TimeUnit.HOURS) // Flex window for battery optimization
                .setConstraints(new androidx.work.Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build();
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "dailyBundle",
                ExistingPeriodicWorkPolicy.UPDATE,
                dailySyncRequest);
    }
}

