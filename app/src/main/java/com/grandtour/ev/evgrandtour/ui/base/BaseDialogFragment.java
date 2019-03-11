package com.grandtour.ev.evgrandtour.ui.base;

import com.grandtour.ev.evgrandtour.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public abstract class BaseDialogFragment<T extends BaseContract.Presenter> extends DialogFragment implements BaseContract.View {

    private T presenter;

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

    @Override
    public void showMessage(@NonNull String message) {
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.toast_message_layout, null);
        TextView textView = view.findViewById(R.id.txtToastMessage);
        textView.setText(message);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void setupTransparentDialogBackground() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow()
                    .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow()
                    .requestFeature(Window.FEATURE_NO_TITLE);
        }
    }
}
