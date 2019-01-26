package com.grandtour.ev.evgrandtour.ui.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.DialogFragment;
import android.view.Window;

public abstract class BaseDialogFragment<T extends BaseContract.Presenter> extends DialogFragment implements BaseContract.View {

    private T presenter;

    public void setupTransparentDialogBackground() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow()
                    .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow()
                    .requestFeature(Window.FEATURE_NO_TITLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getPresenter() != null) {
            presenter.onAttachView();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getPresenter() != null) {
            presenter.onDetachView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getPresenter() != null) {
            presenter.onDestroyView();
        }
    }

    public T getPresenter() {
        if (presenter == null) {
            presenter = createPresenter();
        }
        return presenter;
    }

    public abstract T createPresenter();

}
