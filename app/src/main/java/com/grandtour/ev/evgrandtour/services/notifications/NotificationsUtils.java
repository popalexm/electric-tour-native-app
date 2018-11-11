package com.grandtour.ev.evgrandtour.services.notifications;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.ui.mainActivity.MainActivity;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

public final class NotificationsUtils {

    @NonNull
    private static final String NOTIFICATION_ID_CHANNEL_HIGH = "11111";
    @NonNull
    private static final String NOTIFICATION_ID_CHANNEL_LOW = "10000";
    @NonNull
    private static final String NOTIFICATION_DESCRIPTION = "GrandTourChannel";
    private static final long[] VIBRATION_PATTERN = new long[]{1000, 1000, 1000, 1000, 1000};

    private NotificationsUtils() { }

    @NonNull
    public static Notification createHighPriorityNotification(@NonNull Context context, @NonNull CharSequence notificationMessage,
            @NonNull CharSequence notificationTitle) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (notificationManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationsUtils.setHighPriorityNotificationChannel(notificationManager);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationsUtils.NOTIFICATION_ID_CHANNEL_HIGH);
        builder.setSmallIcon(R.drawable.ic_location_on_amber_a400_24dp)
                .setColor(Injection.provideGlobalContext()
                        .getResources()
                        .getColor(R.color.colorPrimary))
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMessage))
                .setVibrate(NotificationsUtils.VIBRATION_PATTERN)
                .setSound(soundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 10, NotificationsUtils.getStartCurrentMainActivityIntent(context), 0);
        return builder.setContentIntent(contentIntent)
                .build();
    }

    @NonNull
    public static Notification createLowPriorityNotification(@NonNull Context context, @NonNull CharSequence notificationMessage,
            @NonNull CharSequence notificationTitle) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationsUtils.setLowPriorityNotificationChannel(notificationManager);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationsUtils.NOTIFICATION_ID_CHANNEL_LOW);
        builder.setSmallIcon(R.drawable.ic_location_on_amber_a400_24dp)
                .setColor(Injection.provideGlobalContext()
                        .getResources()
                        .getColor(R.color.colorPrimary))
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMessage))
                .setPriority(NotificationCompat.PRIORITY_LOW);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 10, NotificationsUtils.getStartCurrentMainActivityIntent(context), 0);
        return builder.setContentIntent(contentIntent).build();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void setHighPriorityNotificationChannel(@NonNull NotificationManager notificationManager) {
        NotificationChannel notificationChannel = new NotificationChannel(NotificationsUtils.NOTIFICATION_ID_CHANNEL_HIGH,
                NotificationsUtils.NOTIFICATION_DESCRIPTION, NotificationManager.IMPORTANCE_HIGH);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .build();
        notificationChannel.setSound(soundUri, audioAttributes);
        notificationChannel.setVibrationPattern(NotificationsUtils.VIBRATION_PATTERN);
        notificationChannel.enableLights(true);
        notificationChannel.setShowBadge(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void setLowPriorityNotificationChannel(@NonNull NotificationManager notificationManager) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NotificationsUtils.NOTIFICATION_ID_CHANNEL_LOW,
                    NotificationsUtils.NOTIFICATION_DESCRIPTION, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(notificationChannel);
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
