package com.example.knowlio.work;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.knowlio.R;
import com.example.knowlio.activities.MainActivity;

public class DailyReminderWorker extends Worker {

    public DailyReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        /* 1. ערוץ (API 26+) */
        final String CH = "daily_reminder_channel";
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            NotificationChannel c = new NotificationChannel(
                    CH, "Knowlio Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT);
            c.enableVibration(false);          // לבטל רטט
            c.setSound(null, null);
            getApplicationContext()
                    .getSystemService(NotificationManager.class)
                    .createNotificationChannel(c);
        }

        /* 2. PendingIntent לפתיחת MainActivity */
        PendingIntent pi = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_IMMUTABLE);

        /* 3. ההתראה עצמה */
        Notification n = new NotificationCompat.Builder(getApplicationContext(), CH)
                .setSmallIcon(android.R.drawable.ic_dialog_info)  // אייקון מערכת מהיר
                .setContentTitle("Knowlio")
                .setContentText("✨ Your daily knowledge is ready! Tap to read.")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat.from(getApplicationContext()).notify(42, n);

        return Result.success();
    }
}
