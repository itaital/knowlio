package com.example.knowlio.work;

import android.content.Context;
import android.content.SharedPreferences;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import android.util.Log;

import com.example.knowlio.data.FactsRepository;
import com.example.knowlio.data.models.DailyQuoteBundle;
import com.example.knowlio.data.models.GistResponse;
import com.example.knowlio.data.network.FactsApi;
import com.example.knowlio.data.network.GithubApi;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DailyBundleWorker extends Worker {
    
    private static final String TAG = "DailyBundleWorker";

    /** GitHub Gist ID */
    private static final String GIST_ID = "d2a78fdf63a5112ba58e530982d9f823";
    
    /** GitHub API base URL */
    private static final String GITHUB_API_BASE = "https://api.github.com/";
    
    /** Gist raw content base */
    private static final String GIST_RAW_BASE = "https://gist.githubusercontent.com/itaital/" + GIST_ID + "/raw/";

    public DailyBundleWorker(@NonNull Context ctx,
                             @NonNull WorkerParameters params) {
        super(ctx, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "🔄 Starting daily bundle sync work with GitHub API");
        
        // Show notification that sync is starting
        showNotification("🔄 Starting daily sync...", "Fetching latest content via GitHub API");

        /* 1.  Compute today's filename in UTC */
        LocalDate today = LocalDate.now(java.time.ZoneOffset.UTC);
        String todayFileName = "daily_knowledge_" +
                today.format(DateTimeFormatter.ofPattern("yyyy_MM_dd")) + ".json";
        
        Log.d(TAG, "📅 Today's date (UTC): " + today + ", expected file: " + todayFileName);
        
        // Log what bundles are currently available locally
        FactsRepository repo = new FactsRepository(getApplicationContext());
        LocalDate[] availableDates = repo.listDates();
        Log.d(TAG, "📱 Currently available local bundles: " + java.util.Arrays.toString(availableDates));

        /* 2.  Setup Retrofit with GitHub API headers */
        HttpLoggingInterceptor log = new HttpLoggingInterceptor();
        log.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Add GitHub API headers interceptor
        okhttp3.Interceptor githubHeadersInterceptor = chain -> {
            okhttp3.Request original = chain.request();
            okhttp3.Request.Builder builder = original.newBuilder();
            
            // Add GitHub API headers
            if (original.url().toString().contains("api.github.com")) {
                builder.header("Accept", "application/vnd.github+json");
                builder.header("X-GitHub-Api-Version", "2022-11-28");
                Log.d(TAG, "🔧 Added GitHub API headers to request");
            }
            
            // Add no-cache headers for Gist raw content
            if (original.url().toString().contains("gist.githubusercontent.com")) {
                builder.header("Cache-Control", "no-cache");
                builder.header("Pragma", "no-cache");
                builder.cacheControl(okhttp3.CacheControl.FORCE_NETWORK);
                Log.d(TAG, "🔧 Added no-cache headers to Gist request");
            }
            
            return chain.proceed(builder.build());
        };

        OkHttpClient ok = new OkHttpClient.Builder()
                .addInterceptor(log)
                .addInterceptor(githubHeadersInterceptor)
                .build();

        Gson gson = new GsonBuilder().setLenient().create();

        // GitHub API client
        Retrofit githubRetrofit = new Retrofit.Builder()
                .baseUrl(GITHUB_API_BASE)
                .client(ok)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // Gist raw content client (for fetching actual bundle files)
        Retrofit gistRetrofit = new Retrofit.Builder()
                .baseUrl(GIST_RAW_BASE)
                .client(ok)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        GithubApi githubApi = githubRetrofit.create(GithubApi.class);
        FactsApi gistApi = gistRetrofit.create(FactsApi.class);

        try {
            /* 3.  Call GitHub API to get Gist metadata */
            Log.d(TAG, "🔍 Fetching Gist metadata from GitHub API...");
            Response<GistResponse> gistResponse = githubApi.getGist(GIST_ID).execute();
            
            if (!gistResponse.isSuccessful() || gistResponse.body() == null) {
                Log.e(TAG, "❌ Failed to fetch Gist metadata. Code: " + gistResponse.code());
                if (gistResponse.errorBody() != null) {
                    try {
                        String errorBody = gistResponse.errorBody().string();
                        Log.e(TAG, "Error response: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Could not read error body", e);
                    }
                }
                return Result.retry();
            }

            GistResponse gist = gistResponse.body();
            Log.d(TAG, "✅ Gist metadata fetched successfully. Updated: " + gist.updatedAt);
            Log.d(TAG, "📁 Available files: " + gist.files.keySet());

            /* 4.  Find the best bundle file to fetch */
            String bestFileName = findBestBundleFile(gist.files, today);
            if (bestFileName == null) {
                Log.e(TAG, "❌ No suitable bundle files found in Gist");
                showNotification("❌ No bundle files found", "Gist is empty or has no daily knowledge files");
                return Result.retry();
            }

            GistResponse.GistFile bestFile = gist.files.get(bestFileName);
            String rawUrl = bestFile.rawUrl;
            
            Log.d(TAG, "🎯 Selected file: " + bestFileName);
            Log.d(TAG, "🔗 Raw URL: " + rawUrl);
            Log.d(TAG, "📏 File size: " + bestFile.size + " bytes");
            
            // Verify the raw URL contains a commit SHA (should be revisioned)
            if (rawUrl != null && rawUrl.contains("/raw/")) {
                String[] urlParts = rawUrl.split("/");
                for (int i = 0; i < urlParts.length; i++) {
                    if ("raw".equals(urlParts[i]) && i + 1 < urlParts.length) {
                        String possibleSHA = urlParts[i + 1];
                        Log.d(TAG, "🔍 Raw URL commit SHA: " + possibleSHA + " (length: " + possibleSHA.length() + ")");
                        if (possibleSHA.length() == 40) {
                            Log.d(TAG, "✅ Raw URL is properly versioned with commit SHA");
                        } else {
                            Log.w(TAG, "⚠️ WARNING: Raw URL may not contain proper commit SHA!");
                        }
                        break;
                    }
                }
            }

            /* 5.  Fetch the bundle with no-cache headers */
            Log.d(TAG, "📥 Fetching bundle with no-cache headers...");
            DailyQuoteBundle bundle = fetchBundleWithNoCache(rawUrl, gistApi);
            
            if (bundle == null) {
                Log.e(TAG, "❌ Failed to fetch bundle content");
                return Result.retry();
            }

            /* 6.  Save bundle to database */
            repo.saveBundle(bundle);
            Log.d(TAG, "💾 Successfully saved bundle for date: " + bundle.date);
            showNotification("✅ Sync successful!", "Bundle saved: " + bundle.date + " (from " + bestFileName + ")");

            /* 7.  Update preferences and send broadcast */
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            prefs.edit()
                    .putString("cached_fact_date", bundle.date)
                    .putBoolean("force_refresh_ui", true)
                    .putString("last_synced_bundle_date", bundle.date)
                    .putString("last_synced_filename", bestFileName)
                    .putLong("last_sync_timestamp", System.currentTimeMillis())
                    .apply();
            
            // Send broadcast to notify MainActivity that sync is complete
            sendSyncCompleteBroadcast(bundle.date);

            Log.d(TAG, "🎉 Daily bundle sync completed successfully!");
            return Result.success();

        } catch (Exception e) {
            Log.e(TAG, "❌ Error during daily bundle sync", e);
            showNotification("❌ Sync failed", "Error: " + e.getMessage());
            return Result.retry();
        }
    }
    
    /**
     * Finds the best bundle file to fetch from the Gist
     * Priority: today's file > most recent daily_knowledge_*.json file
     */
    private String findBestBundleFile(Map<String, GistResponse.GistFile> files, LocalDate today) {
        try {
            Log.d(TAG, "🔍 Finding best bundle file from " + files.size() + " available files...");
            
            // First, try to find today's file
            String todayFileName = "daily_knowledge_" + 
                    today.format(DateTimeFormatter.ofPattern("yyyy_MM_dd")) + ".json";
            
            if (files.containsKey(todayFileName)) {
                Log.d(TAG, "✅ Found today's file: " + todayFileName);
                return todayFileName;
            }
            
            // If today's file not found, find the most recent daily_knowledge_*.json file
            String mostRecentFile = null;
            LocalDate mostRecentDate = null;
            
            for (Map.Entry<String, GistResponse.GistFile> entry : files.entrySet()) {
                String filename = entry.getKey();
                
                // Only consider daily knowledge files
                if (!filename.startsWith("daily_knowledge_") || !filename.endsWith(".json")) {
                    continue;
                }
                
                // Extract date from filename (daily_knowledge_YYYY_MM_DD.json)
                try {
                    String dateStr = filename.substring(16, 26); // Extract YYYY_MM_DD part
                    String dateParseable = dateStr.replace('_', '-'); // Convert to YYYY-MM-DD
                    LocalDate fileDate = LocalDate.parse(dateParseable);
                    
                    if (mostRecentDate == null || fileDate.isAfter(mostRecentDate)) {
                        mostRecentDate = fileDate;
                        mostRecentFile = filename;
                        Log.d(TAG, "📅 New candidate: " + filename + " (date: " + fileDate + ")");
                    }
                } catch (Exception e) {
                    Log.w(TAG, "⚠️ Could not parse date from filename: " + filename + " - " + e.getMessage());
                }
            }
            
            if (mostRecentFile != null) {
                Log.d(TAG, "🎯 Selected most recent file: " + mostRecentFile + " (date: " + mostRecentDate + ")");
                return mostRecentFile;
            }
            
            Log.w(TAG, "⚠️ No suitable daily knowledge files found");
            return null;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error finding best bundle file", e);
            return null;
        }
    }
    
    /**
     * Fetches a bundle from the given raw URL with hard no-cache headers
     */
    private DailyQuoteBundle fetchBundleWithNoCache(String rawUrl, FactsApi gistApi) {
        try {
            Log.d(TAG, "📥 Fetching bundle from: " + rawUrl);
            
            // Extract filename and verify commit SHA from raw URL
            String filename = rawUrl.substring(rawUrl.lastIndexOf('/') + 1);
            Log.d(TAG, "📁 Filename: " + filename);
            
            // Extract and verify commit SHA from URL (should be 40 hex chars)
            String commitSHA = "unknown";
            try {
                // URL format: https://gist.githubusercontent.com/user/gist_id/raw/COMMIT_SHA/filename
                String[] urlParts = rawUrl.split("/");
                for (int i = 0; i < urlParts.length; i++) {
                    if ("raw".equals(urlParts[i]) && i + 1 < urlParts.length) {
                        commitSHA = urlParts[i + 1];
                        break;
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Could not extract commit SHA from URL", e);
            }
            Log.d(TAG, "🔗 Commit SHA: " + commitSHA + " (length: " + commitSHA.length() + ")");
            
            // Verify we have a proper versioned URL (commit SHA should be 40 chars)
            if (commitSHA.length() != 40) {
                Log.w(TAG, "⚠️ WARNING: Commit SHA is not 40 characters, may not be properly versioned!");
            }
            
            // Create strongest possible no-cache control
            okhttp3.CacheControl noCache = new okhttp3.CacheControl.Builder()
                    .noCache()
                    .noStore()
                    .maxAge(0, java.util.concurrent.TimeUnit.SECONDS)
                    .build();
            
            // Add timestamp to URL to bypass any caching
            String noCacheUrl = rawUrl + "?t=" + System.currentTimeMillis();
            Log.d(TAG, "🔧 No-cache URL: " + noCacheUrl);
            
            // Create direct HTTP request with strongest no-cache headers
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(noCacheUrl)
                    .header("Cache-Control", "no-cache, no-store, max-age=0")
                    .header("Pragma", "no-cache")
                    .cacheControl(noCache)
                    .build();
            
            // Create OkHttpClient with no cache at all
            HttpLoggingInterceptor log = new HttpLoggingInterceptor();
            log.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            
            OkHttpClient directClient = new OkHttpClient.Builder()
                    .cache(null)  // Completely disable caching
                    .addInterceptor(log)
                    .build();
            
            Log.d(TAG, "🌐 Executing HTTP request with no-cache client...");
            
            // Execute the request
            okhttp3.Response response = directClient.newCall(request).execute();
            
            if (!response.isSuccessful() || response.body() == null) {
                Log.e(TAG, "❌ Failed to fetch bundle. Code: " + response.code());
                if (response.body() != null) {
                    try {
                        String errorBody = response.body().string();
                        Log.e(TAG, "Error response: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Could not read error body", e);
                    }
                }
                response.close();
                return null;
            }
            
            // Log response headers for debugging (including Age header)
            Log.d(TAG, "📋 Response headers:");
            if (response.header("ETag") != null) {
                Log.d(TAG, "  ETag: " + response.header("ETag"));
            }
            if (response.header("Date") != null) {
                Log.d(TAG, "  Date: " + response.header("Date"));
            }
            if (response.header("Cache-Control") != null) {
                Log.d(TAG, "  Cache-Control: " + response.header("Cache-Control"));
            }
            if (response.header("Age") != null) {
                Log.d(TAG, "  Age: " + response.header("Age") + " seconds");
            } else {
                Log.d(TAG, "  Age: (not present - content is fresh)");
            }
            
            // Parse JSON response
            String jsonString = response.body().string();
            response.close();
            
            Log.d(TAG, "📄 Response size: " + jsonString.length() + " characters");
            
            // Log first 80 chars of quoteOfTheDay to verify content freshness
            try {
                if (jsonString.contains("\"quoteOfTheDay\"")) {
                    int quoteStart = jsonString.indexOf("\"quoteOfTheDay\"");
                    int valueStart = jsonString.indexOf(":", quoteStart);
                    if (valueStart != -1) {
                        String quotePreview = jsonString.substring(valueStart + 1, 
                                Math.min(valueStart + 81, jsonString.length()));
                        Log.d(TAG, "🔍 Quote preview (80 chars): " + quotePreview.trim());
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Could not extract quote preview", e);
            }
            
            // Parse with Gson
            Gson gson = new GsonBuilder().setLenient().create();
            DailyQuoteBundle bundle = gson.fromJson(jsonString, DailyQuoteBundle.class);
            
            if (bundle == null) {
                Log.e(TAG, "❌ Failed to parse JSON response");
                return null;
            }
            
            Log.d(TAG, "✅ Bundle fetched successfully. Date: " + bundle.date);
            Log.d(TAG, "📊 Bundle contains " + 
                    (bundle.languages != null ? bundle.languages.size() : 0) + " languages");
            
            return bundle;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error fetching bundle with no-cache", e);
            return null;
        }
    }
    
    // Removed old fetchRecentAvailableBundle method - now using GitHub API instead
    
    // Removed old methods - now using GitHub API instead
    
    /**
     * Shows a notification to inform user about sync progress
     */
    private void showNotification(String title, String message) {
        try {
            NotificationManager notificationManager = 
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            
            // Create notification channel for Android 8.0+1
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                    "daily_sync", "Daily Sync", NotificationManager.IMPORTANCE_LOW);
                notificationManager.createNotificationChannel(channel);
            }
            
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "daily_sync")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true);
            
            notificationManager.notify(1001, builder.build());
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification", e);
        }
    }
    
    /**
     * Sends a broadcast to notify MainActivity that sync is complete
     */
    private void sendSyncCompleteBroadcast(String bundleDate) {
        try {
            Log.d(TAG, "📡 Sending sync complete broadcast for bundle: " + bundleDate);
            
            Intent intent = new Intent("com.example.knowlio.SYNC_COMPLETE");
            intent.putExtra("bundle_date", bundleDate);
            intent.putExtra("sync_timestamp", System.currentTimeMillis());
            
            getApplicationContext().sendBroadcast(intent);
            
            Log.d(TAG, "📡 Sync complete broadcast sent successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ CRASH sending broadcast!", e);
            // Fallback: just update preferences and let the app refresh normally
            try {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit()
                        .putBoolean("force_refresh_ui", true)
                        .putString("last_synced_bundle_date", bundleDate)
                        .apply();
                Log.d(TAG, "✅ Fallback: updated preferences for UI refresh");
            } catch (Exception fallbackError) {
                Log.e(TAG, "❌ Even fallback failed!", fallbackError);
            }
        }
    }
}
