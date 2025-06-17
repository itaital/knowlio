package com.example.knowlio.work;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationHelper {
    public static final String CHANNEL_ID = "daily_fact_channel";
    public static final String REMINDER_CHANNEL_ID = "daily_reminder_channel";

    public static void createDailyFactChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = context.getSystemService(NotificationManager.class);
            if (nm != null && nm.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Daily Fact",
                        NotificationManager.IMPORTANCE_DEFAULT);
                nm.createNotificationChannel(channel);
            }
        }
    }

    public static void createDailyReminderChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = context.getSystemService(NotificationManager.class);
            if (nm != null && nm.getNotificationChannel(REMINDER_CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(
                        REMINDER_CHANNEL_ID,
                        "Knowlio Reminders",
                        NotificationManager.IMPORTANCE_DEFAULT);
                nm.createNotificationChannel(channel);
            }
        }
    }
}
