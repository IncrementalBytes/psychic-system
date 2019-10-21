package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.utils.LogUtils;

import java.util.Locale;

import static net.frostedbytes.android.trendo.BaseActivity.BASE_TAG;

public class ForcedReloadFragment  extends Fragment {

    private static final String TAG = BASE_TAG + ForcedReloadFragment.class.getSimpleName();

    public interface OnForcedReloadDataListener {

        void onForcedReloadComplete();
    }

    private OnForcedReloadDataListener mCallback;

    public static ForcedReloadFragment newInstance() {

        LogUtils.debug(TAG, "++newInstance()");
        ForcedReloadFragment fragment = new ForcedReloadFragment();
        return fragment;
    }

    /*
        Fragment Override(s)
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        LogUtils.debug(TAG, "++onAttach(Context)");
        try {
            mCallback = (OnForcedReloadDataListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                String.format(Locale.ENGLISH, "Missing interface implementations for %s", context.toString()));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LogUtils.debug(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
        View view = inflater.inflate(R.layout.fragment_default, container, false);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LogUtils.debug(TAG, "++onDestroy()");
    }

    @Override
    public void onResume() {
        super.onResume();

        LogUtils.debug(TAG, "++onResume()");
    }
}
