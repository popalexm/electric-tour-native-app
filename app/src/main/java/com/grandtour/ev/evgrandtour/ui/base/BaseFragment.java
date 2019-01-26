package com.grandtour.ev.evgrandtour.ui.base;

import com.grandtour.ev.evgrandtour.R;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public abstract class BaseFragment<T extends BaseContract.Presenter> extends Fragment implements BaseContract.View {

    private T presenter;

    public void showDialog(@NonNull DialogFragment dialogFrag, @NonNull Fragment targetFrag, @NonNull String tag, int reqCode) {
        FragmentManager childFragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
        Fragment previousDialog = childFragmentManager.findFragmentByTag(tag);
        if (previousDialog != null) {
            fragmentTransaction.remove(previousDialog);
        }
        fragmentTransaction.addToBackStack(null);
        dialogFrag.setTargetFragment(targetFrag.getParentFragment(), reqCode);
        dialogFrag.show(fragmentTransaction, tag);
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

    public T getPresenter() {
        if (presenter == null) {
            presenter = createPresenter();
        }
        return presenter;
    }

    @Nullable
    protected abstract T createPresenter();

}
