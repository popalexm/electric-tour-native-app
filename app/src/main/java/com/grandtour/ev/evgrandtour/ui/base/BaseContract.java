package com.grandtour.ev.evgrandtour.ui.base;

import android.support.annotation.NonNull;

public class BaseContract {

    public interface View {

        void showLoadingView(boolean isLoading, boolean isCancelable, @NonNull String msg);

        void showMessage(@NonNull String msg);
    }

    public interface Presenter {

        void onAttach();

        void onDetach();

        void onDestroy();
    }
}
