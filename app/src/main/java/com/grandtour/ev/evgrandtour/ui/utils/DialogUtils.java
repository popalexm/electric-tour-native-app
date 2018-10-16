package com.grandtour.ev.evgrandtour.ui.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import java.util.List;

public final class DialogUtils {

    private DialogUtils() { }

    @NonNull
    public static AlertDialog.Builder getAlertDialogBuilder(@NonNull Context context, @NonNull CharSequence message, @NonNull CharSequence title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle(title);
        return builder;
    }

    @NonNull
    public static AlertDialog.Builder getMultipleChoicesAlertDialogBuilder(@NonNull Context context,
            @NonNull String[] selectionChoices, @NonNull CharSequence title, @Nullable DialogChoiceCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(selectionChoices, (dialog, selectedPosition) -> {
            if (callback != null) {
                callback.onDialogChoiceSelected(selectionChoices[selectedPosition]);
            }
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        return builder;
    }

    public interface DialogChoiceCallback{

        void onDialogChoiceSelected(String choice);
    }
}
