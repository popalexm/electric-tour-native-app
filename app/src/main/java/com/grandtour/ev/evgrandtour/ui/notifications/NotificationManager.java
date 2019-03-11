package com.grandtour.ev.evgrandtour.ui.notifications;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;

import android.app.Notification;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

public class NotificationManager {

    private static NotificationManager instance;

    @NonNull
    public static NotificationManager getInstance() {
        if (NotificationManager.instance == null) {
            NotificationManager.instance = new NotificationManager();
            return NotificationManager.instance;
        }
        return NotificationManager.instance;
    }

    public void notifyAboutRouteDeviation() {
        Context ctx = Injection.provideGlobalContext();
        Notification notification = NotificationsUtils.createHighPriorityNotification(ctx, ctx.getString(R.string.message_notify_deviation),
                ctx.getString(R.string.title_warning));
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        notificationManager.notify(1200, notification);
    }
}
