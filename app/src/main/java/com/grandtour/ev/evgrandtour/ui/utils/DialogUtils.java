package com.grandtour.ev.evgrandtour.ui.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;

public final class DialogUtils {

    private DialogUtils() { }

    @NonNull
    public static AlertDialog.Builder getAlertDialogBuilder(@NonNull Context context, @NonNull CharSequence message, @NonNull CharSequence title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle(title);
        return builder;
    }
}
