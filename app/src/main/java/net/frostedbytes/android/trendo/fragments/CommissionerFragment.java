package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.utils.LogUtils;

import static net.frostedbytes.android.trendo.BaseActivity.BASE_TAG;

public class CommissionerFragment extends Fragment {

    private static final String TAG = BASE_TAG + CommissionerFragment.class.getSimpleName();

    private String mMessage;

    public static CommissionerFragment newInstance() {

        LogUtils.debug(TAG, "++newInstance()");
        CommissionerFragment fragment = new CommissionerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        LogUtils.debug(TAG, "++onAttach(Context)");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LogUtils.debug(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
        return inflater.inflate(R.layout.fragment_commissioner, container, false);
    }
}
