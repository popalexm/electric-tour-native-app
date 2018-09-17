package com.grandtour.ev.evgrandtour.ui.base;

import android.support.annotation.NonNull;

public class BaseContract {

    public interface View {

        void showLoadingStatus(boolean isLoading ,@NonNull String msg);

        void showMessage(@NonNull String msg);

    }


    public interface Presenter {

        void onAttach();

        void onDetach();

    }

}
