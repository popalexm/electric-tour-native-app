package com.grandtour.ev.evgrandtour.services.notifications;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.ui.main.MainActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

public final class NotificationsUtils {

    @NonNull
    private static final String NOTIFICATION_CHANNEL_ID = "10001";
    @NonNull
    private static final String NOTIFICATION_DESCRIPTION = "GrandTourChannel";

    private NotificationsUtils() { }

    @NonNull
    public static Notification createNotification(@NonNull Context context, @NonNull CharSequence notificationMessage, @NonNull CharSequence notificationTitle) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 10, NotificationsUtils.getStartCurrentMainActivityIntent(context), 0);
        if (notificationManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationsUtils.setOreoNotificationChannel(notificationManager);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationsUtils.NOTIFICATION_CHANNEL_ID).setSmallIcon(
                R.drawable.ic_location_on_amber_a400_24dp)
                .setColor(Injection.provideGlobalContext()
                        .getResources()
                        .getColor(R.color.colorPrimary))
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMessage))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setChannelId(NotificationsUtils.NOTIFICATION_CHANNEL_ID)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        return builder.setContentIntent(contentIntent).build();
    }

    /**
     * Notification channel creation for Android Oreo (8.0/8.1) and up
     */
    private static void setOreoNotificationChannel(@NonNull NotificationManager mNotificationManager) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NotificationsUtils.NOTIFICATION_CHANNEL_ID,
                    NotificationsUtils.NOTIFICATION_DESCRIPTION, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @NonNull
    private static Intent getStartCurrentMainActivityIntent(@NonNull Context context) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return notificationIntent;
    }
}
