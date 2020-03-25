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

import net.whollynugatory.android.trendo.R;
import net.whollynugatory.android.trendo.db.entity.TeamEntity;
import net.whollynugatory.android.trendo.db.viewmodel.TrendoViewModel;
import net.whollynugatory.android.trendo.db.views.MatchSummaryDetails;
import net.whollynugatory.android.trendo.ui.BaseActivity;
import net.whollynugatory.android.trendo.utils.PreferenceUtils;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class BrokerageFragment extends Fragment {

  private static final String TAG = BaseActivity.BASE_TAG + "BrokerageFragment";

  public enum BrokerageType {
    Matches,
    Teams
  }

  public interface OnBrokerageListener {

    void onBrokerageMatchesMissing();
    void onBrokerageMatchesRetrieved(List<MatchSummaryDetails> matchSummaryDetailsList);
    void onBrokerageTeamsRetrieved(List<TeamEntity> teamEntityList);
  }

  private OnBrokerageListener mCallback;

  private BrokerageType mBrokerageType;

  public static BrokerageFragment newInstance(BrokerageType brokerageType) {

    Log.d(TAG, "++newInstance()");
    BrokerageFragment fragment = new BrokerageFragment();
    Bundle arguments = new Bundle();
    arguments.putSerializable(BaseActivity.ARG_BROKERAGE_TYPE, brokerageType);
    fragment.setArguments(arguments);
    return fragment;
  }

  /*
    Fragment Override(s)
 */
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    Log.d(TAG, "++onActivityCreated(Bundle)");
    TrendoViewModel trendoViewModel = new ViewModelProvider(this).get(TrendoViewModel.class);
    switch (mBrokerageType) {
      case Matches:
        trendoViewModel.getAllMatchSummaryDetails(PreferenceUtils.getSeason(getContext())).observe(
          getViewLifecycleOwner(),
          matchSummaryDetails -> {
            if (matchSummaryDetails != null && matchSummaryDetails.size() > 0) {
              Log.d(TAG, "Match data found in local database.");
              mCallback.onBrokerageMatchesRetrieved(matchSummaryDetails);
            } else {
              Log.w(TAG, "No match data found in local database.");
              mCallback.onBrokerageMatchesMissing();
            }
          });

        break;
      case Teams:
        trendoViewModel.getAllTeams().observe(getViewLifecycleOwner(), teamEntities -> {

          if (teamEntities != null && teamEntities.size() > 0) {
            Log.d(TAG, "Team data found in local database.");
            mCallback.onBrokerageTeamsRetrieved(teamEntities);
          } else { // TODO: are we still populating the db?
            Log.w(TAG, "No team data found in local database.");
          }
        });

        break;
    }
  }

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);

    Log.d(TAG, "++onAttach(Context)");
    try {
      mCallback = (OnBrokerageListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(
        String.format(Locale.ENGLISH, "Missing interface implementations for %s", context.toString()));
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    Bundle arguments = getArguments();
    if (arguments != null) {
      if (arguments.containsKey(BaseActivity.ARG_BROKERAGE_TYPE)) {
        mBrokerageType = (BrokerageType)arguments.getSerializable(BaseActivity.ARG_BROKERAGE_TYPE);
      }
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    return inflater.inflate(R.layout.fragment_brokerage, container, false);
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
}
