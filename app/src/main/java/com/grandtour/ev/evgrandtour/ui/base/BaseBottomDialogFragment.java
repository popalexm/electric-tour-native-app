package com.grandtour.ev.evgrandtour.ui.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

public class BaseBottomDialogFragment extends BottomSheetDialogFragment implements DialogInterface.OnShowListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(this);
        return dialog;
    }

    public void setupTransparentDialogBackground() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow()
                    .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow()
                    .requestFeature(Window.FEATURE_NO_TITLE);
        }
    }

    public void setupBottomSheetToExpanded() {
        if (getDialog() != null) {
            getDialog().setOnShowListener(dialog -> {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View bottomSheetView = d.findViewById(android.support.design.R.id.design_bottom_sheet);
                if (bottomSheetView != null) {
                    BottomSheetBehavior.from(bottomSheetView)
                            .setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            });
        }
    }

    /**
     * Workaround for bottom sheet not expanding to full height in landscape mode
     */
    @Override
    public void onShow(DialogInterface dialog) {
        BottomSheetDialog d = (BottomSheetDialog) dialog;
        FrameLayout bottomSheet = d.findViewById(android.support.design.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
}
