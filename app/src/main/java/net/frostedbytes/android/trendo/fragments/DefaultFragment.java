package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.utils.LogUtils;

import static net.frostedbytes.android.trendo.BaseActivity.BASE_TAG;

public class DefaultFragment extends Fragment {

    private static final String TAG = BASE_TAG + DefaultFragment.class.getSimpleName();

    private String mMessage;

    public static DefaultFragment newInstance(String message) {

        LogUtils.debug(TAG, "++newInstance()");
        DefaultFragment fragment = new DefaultFragment();
        Bundle args = new Bundle();
        args.putString(BaseActivity.ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        LogUtils.debug(TAG, "++onAttach(Context)");
        Bundle arguments = getArguments();
        if (arguments != null) {
            mMessage = arguments.getString(BaseActivity.ARG_MESSAGE);
        } else {
            LogUtils.error(TAG, "Arguments were null.");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LogUtils.debug(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
        final View view = inflater.inflate(R.layout.fragment_default, container, false);
        TextView messageText = view.findViewById(R.id.default_text_message);
        messageText.setText(mMessage);
        return view;
    }
}
