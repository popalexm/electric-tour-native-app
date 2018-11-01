package com.grandtour.ev.evgrandtour.ui.base;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class BaseFragment extends Fragment {

    public void showDialog(@NonNull DialogFragment dialogFrag, @NonNull Fragment targetFrag, @NonNull String tag, int reqCode) {
        FragmentManager fm = getFragmentManager();
        if (fm != null) {
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            Fragment previousDialog = getFragmentManager().findFragmentByTag(tag);
            if (previousDialog != null) {
                fragmentTransaction.remove(previousDialog);
            }
            fragmentTransaction.addToBackStack(null);
            dialogFrag.setTargetFragment(targetFrag, reqCode);
            dialogFrag.show(fragmentTransaction, tag);
        }
    }
}
