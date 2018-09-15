package com.grandtour.ev.evgrandtour.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import java.util.Set;

public final class PermissionUtils {

    public static final int LOCATION_REQUEST_PERMISSION_ID = 1;

    private PermissionUtils() { }

    public static boolean checkPermissions(@NonNull Context context, String... permissions) {
        for (String permission : permissions) {
            if (!PermissionUtils.checkPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkPermission(@NonNull Context context, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermissions(@NonNull Activity activity, int permissionId, @NonNull String... permissions) {
        ActivityCompat.requestPermissions(activity, permissions, permissionId);
    }
}
