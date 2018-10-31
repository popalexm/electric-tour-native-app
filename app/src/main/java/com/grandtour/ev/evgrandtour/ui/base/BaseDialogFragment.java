package com.grandtour.ev.evgrandtour.ui.base;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.DialogFragment;
import android.view.Window;

public class BaseDialogFragment extends DialogFragment {

    public void setupTransparentDialogBackground() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow()
                    .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow()
                    .requestFeature(Window.FEATURE_NO_TITLE);
        }
    }
}
