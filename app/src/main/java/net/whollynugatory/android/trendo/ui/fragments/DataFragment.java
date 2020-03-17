/*
 * Copyright 2020 Ryan Ward
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.whollynugatory.android.trendo.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.whollynugatory.android.trendo.R;
import net.whollynugatory.android.trendo.db.viewmodel.TrendoViewModel;
import net.whollynugatory.android.trendo.ui.BaseActivity;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class DataFragment extends Fragment {

  private static final String TAG = BaseActivity.BASE_TAG + "DataFragment";

  public interface OnDataListener {

    void onConferenceDataExists();
    void onMatchesDataExists(String season);
    void onTeamDataExists();
  }

  private OnDataListener mCallback;

  private TextView mStatusText;

  public static DataFragment newInstance() {

    Log.d(TAG, "++newInstance()");
    return new DataFragment();
  }

  /*
    Fragment Override(s)
 */
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    Log.d(TAG, "++onActivityCreated(Bundle)");
    checkConferenceData();
  }

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);

    Log.d(TAG, "++onAttach(Context)");
    try {
      mCallback = (OnDataListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(
        String.format(Locale.ENGLISH, "Missing interface implementations for %s", context.toString()));
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    final View view = inflater.inflate(R.layout.fragment_data, container, false);
    mStatusText = view.findViewById(R.id.data_text);
    return view;
  }

  @Override
  public void onDetach() {
    super.onDetach();

    Log.d(TAG, "++onDetach()");
    mCallback = null;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.d(TAG, "++onDestroy()");
  }

  @Override
  public void onResume() {
    super.onResume();

    Log.d(TAG, "++onResume()");
  }

  /*
    Private Method(s)
   */
  private void checkConferenceData() {

    Log.d(TAG, "++checkConferenceData()");
    mStatusText.setText("Checking conference data...");
    TrendoViewModel trendViewModel = new ViewModelProvider(this).get(TrendoViewModel.class);
    trendViewModel.getAllConferences().observe(getViewLifecycleOwner(), conferenceEntities -> {

      if (conferenceEntities != null && conferenceEntities.size() > 0) {
        mCallback.onConferenceDataExists();
        checkTeamData();
      }
    });
  }

  private void checkMatchData() {

    Log.d(TAG, "++checkMatchData()");
    mStatusText.setText("Checking match data...");
    String[] seasons = getResources().getStringArray(R.array.seasons);
    TrendoViewModel trendoViewModel = new ViewModelProvider(this).get(TrendoViewModel.class);
    for (String season : seasons) {
      int seasonToCheck = Integer.parseInt(season);
      trendoViewModel.getAllMatchSummaryDetails(seasonToCheck).observe(getViewLifecycleOwner(), matchSummaryDetails -> {

        if (matchSummaryDetails != null && matchSummaryDetails.size() > 0) {
          mCallback.onMatchesDataExists(season);
        }
      });
    }
  }

  private void checkTeamData() {

    Log.d(TAG, "++checkTeamData()");
    mStatusText.setText("Checking team data...");
    TrendoViewModel trendViewModel = new ViewModelProvider(this).get(TrendoViewModel.class);
    trendViewModel.getAllTeams().observe(getViewLifecycleOwner(), teamEntities -> {

      if (teamEntities != null && teamEntities.size() > 0) {
        mCallback.onTeamDataExists();
        checkMatchData();
      }
    });
  }
}
