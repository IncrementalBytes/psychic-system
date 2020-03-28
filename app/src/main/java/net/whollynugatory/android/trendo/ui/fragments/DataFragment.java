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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.whollynugatory.android.trendo.R;
import net.whollynugatory.android.trendo.ui.BaseActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class DataFragment extends Fragment {

  private static final String TAG = BaseActivity.BASE_TAG + "DataFragment";

  private ImageView mConferencesStatusImage;
  private TextView mConferencesStatusText;
  private ImageView mMatchSummariesStatusImage;
  private TextView mMatchSummariesStatusText;
  private ImageView mTeamsStatusImage;
  private TextView mTeamsStatusText;
  private ImageView mTrendsStatusImage;
  private TextView mTrendsStatusText;

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
  }

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);

    Log.d(TAG, "++onAttach(Context)");
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_data, container, false);
    mConferencesStatusImage = view.findViewById(R.id.data_image_conferences);
    mConferencesStatusText = view.findViewById(R.id.data_text_conferences_status);
    mMatchSummariesStatusImage = view.findViewById(R.id.data_image_match_summaries);
    mMatchSummariesStatusText = view.findViewById(R.id.data_text_match_summaries_status);
    mTeamsStatusImage = view.findViewById(R.id.data_image_teams);
    mTeamsStatusText = view.findViewById(R.id.data_text_teams_status);
    mTrendsStatusImage = view.findViewById(R.id.data_image_trends);
    mTrendsStatusText = view.findViewById(R.id.data_text_trends_status);
    return view;
  }

  @Override
  public void onDetach() {
    super.onDetach();

    Log.d(TAG, "++onDetach()");
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

  public void setConferencesStatusImage(Drawable drawable) {

    if (mConferencesStatusImage != null) {
      mConferencesStatusImage.setImageDrawable(drawable);
    }
  }

  public void setConferencesStatusText(String statusText) {

    if (mConferencesStatusText != null) {
      mConferencesStatusText.setText(statusText);
    }
  }

  public void setMatchSummariesStatusImage(Drawable drawable) {

    if (mMatchSummariesStatusImage != null) {
      mMatchSummariesStatusImage.setImageDrawable(drawable);
    }
  }

  public void setMatchSummariesStatusText(String statusText) {

    if (mMatchSummariesStatusText != null) {
      mMatchSummariesStatusText.setText(statusText);
    }
  }

  public void setTeamStatusImage(Drawable drawable) {

    if (mTeamsStatusImage != null) {
      mTeamsStatusImage.setImageDrawable(drawable);
    }
  }

  public void setTeamStatusText(String statusText) {

    if (mTeamsStatusText != null) {
      mTeamsStatusText.setText(statusText);
    }
  }

  public void setTrendsStatusImage(Drawable drawable) {

    if (mTrendsStatusImage != null) {
      mTrendsStatusImage.setImageDrawable(drawable);
    }
  }

  public void setTrendsStatusText(String statusText) {

    if (mTeamsStatusText != null) {
      mTrendsStatusText.setText(statusText);
    }
  }
}
