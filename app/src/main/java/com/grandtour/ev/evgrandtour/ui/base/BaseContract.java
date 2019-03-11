package com.grandtour.ev.evgrandtour.ui.base;

import androidx.annotation.NonNull;

public class BaseContract {

    public interface View {

        void showLoadingView(boolean isLoading);

        void showMessage(@NonNull String msg);
    }

    public interface Presenter {

        void onAttachView();

        void onDetachView();

        void onDestroyView();
    }
}
