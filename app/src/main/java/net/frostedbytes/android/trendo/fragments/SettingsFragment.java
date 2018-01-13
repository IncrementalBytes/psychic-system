package net.frostedbytes.android.trendo.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.frostedbytes.android.trendo.R;

public class SettingsFragment extends Fragment {

  private static final String TAG = "SettingsFragment";

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle");
    return inflater.inflate(R.layout.fragment_settings, container, false);
  }
}
