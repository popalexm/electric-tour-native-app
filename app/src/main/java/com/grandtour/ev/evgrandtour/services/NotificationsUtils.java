package com.grandtour.ev.evgrandtour.services;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.ui.maps.MapsFragmentView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

public final class NotificationsUtils {

    private static final String NOTIFICATION_CHANNEL_ID = "10001";
    private static final String NOTIFICATION_DESCRIPTION = "GrandTourChannel";

    private NotificationsUtils() { }

    public static Notification createNotification(@NonNull Context context, @NonNull CharSequence notificationMessage, @NonNull CharSequence notificationTitle) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 10,
                new Intent(context, MapsFragmentView.class), 0);

        if (notificationManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationsUtils.setOreoNotificationChannel(notificationManager);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationsUtils.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_location_searching_indigo_600_24dp)
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
    private static void setOreoNotificationChannel(@NonNull android.app.NotificationManager mNotificationManager) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NotificationsUtils.NOTIFICATION_CHANNEL_ID,
                    NotificationsUtils.NOTIFICATION_DESCRIPTION, IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
