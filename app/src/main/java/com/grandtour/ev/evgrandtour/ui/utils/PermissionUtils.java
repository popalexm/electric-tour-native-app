package com.grandtour.ev.evgrandtour.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

    public static void requestPermissionsInActivity(@NonNull Activity activity, int permissionId, @NonNull String... permissions) {
        ActivityCompat.requestPermissions(activity, permissions, permissionId);
    }

    public static void requestPermissionsInFragment(@NonNull Fragment fragment, int permissionId, @NonNull String... permissions) {
        fragment.requestPermissions(permissions, permissionId);
    }
}
