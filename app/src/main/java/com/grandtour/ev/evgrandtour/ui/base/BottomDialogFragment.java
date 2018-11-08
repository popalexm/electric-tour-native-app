package com.grandtour.ev.evgrandtour.ui.base;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.view.Window;

public class BottomDialogFragment extends BottomSheetDialogFragment {

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
}
