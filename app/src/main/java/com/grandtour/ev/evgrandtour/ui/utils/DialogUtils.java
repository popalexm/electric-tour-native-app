package com.grandtour.ev.evgrandtour.ui.utils;

import com.grandtour.ev.evgrandtour.data.database.models.Tour;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

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
    public static AlertDialog.Builder getMultipleChoicesAlertDialogBuilder(@NonNull Context context, @NonNull List<Tour> selectionChoices,
            @NonNull CharSequence title, @Nullable DialogChoiceCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ListAdapter adapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_singlechoice, selectionChoices);
        builder.setTitle(title);
        builder.setSingleChoiceItems(adapter, -1, (dialog, selectedPosition) -> {
            if (callback != null) {
                callback.onDialogChoiceSelected(selectionChoices.get(selectedPosition)
                        .getTourId());
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        return builder;
    }

    public interface DialogChoiceCallback{

        void onDialogChoiceSelected(String choice);
    }
}
