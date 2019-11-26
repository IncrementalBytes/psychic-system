/*
 * Copyright 2019 Ryan Ward
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
package net.frostedbytes.android.trendo.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.common.ConferenceTableAsync;
import net.frostedbytes.android.trendo.common.MatchSummaryTableAsync;
import net.frostedbytes.android.trendo.common.TeamTableAsync;
import net.frostedbytes.android.trendo.common.TrendTableAsync;
import net.frostedbytes.android.trendo.db.TrendoDatabase;
import net.frostedbytes.android.trendo.db.TrendoRepository;
import net.frostedbytes.android.trendo.db.entity.MatchSummaryEntity;
import net.frostedbytes.android.trendo.db.entity.TeamEntity;
import net.frostedbytes.android.trendo.ui.fragments.UserPreferencesFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DataActivity extends BaseActivity {

  private static final String TAG = BASE_TAG + "DataActivity";

  private ImageView mConferencesStatusImage;
  private ImageView mMatchSummariesStatusImage;
  private ImageView mTeamsStatusImage;
  private ImageView mTrendsStatusImage;
  private TextView mConferencesStatusText;
  private TextView mMatchSummariesStatusText;
  private TextView mTeamsStatusText;
  private TextView mTrendsStatusText;

  private List<MatchSummaryEntity> mMatchSummaryList;
  private List<TeamEntity> mTeamList;
  private int mYear;
  private String mUserId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_data);

    mConferencesStatusImage = findViewById(R.id.data_image_conferences);
    mMatchSummariesStatusImage = findViewById(R.id.data_image_match_summaries);
    mTeamsStatusImage = findViewById(R.id.data_image_teams);
    mTrendsStatusImage = findViewById(R.id.data_image_trends);
    mConferencesStatusText = findViewById(R.id.data_text_conferences_status);
    mMatchSummariesStatusText = findViewById(R.id.data_text_match_summaries_status);
    mTeamsStatusText = findViewById(R.id.data_text_teams_status);
    mTrendsStatusText = findViewById(R.id.data_text_trends_status);

    mMatchSummaryList = new ArrayList<>();
    mTeamList = new ArrayList<>();
    mUserId = getIntent().getStringExtra(BaseActivity.ARG_USER_ID);

    File conferenceData = new File(getCacheDir(), BaseActivity.DEFAULT_CONFERENCE_DATA);
    try {
      Log.d(TAG, "Using " + BaseActivity.DEFAULT_CONFERENCE_DATA);
      try (InputStream inputStream = getAssets().open(BaseActivity.DEFAULT_CONFERENCE_DATA)) {
        try (FileOutputStream outputStream = new FileOutputStream(conferenceData)) {
          byte[] buf = new byte[1024];
          int len;
          while ((len = inputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, len);
          }
        }
      }

      mConferencesStatusImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_success_dark));
      mConferencesStatusText.setText(getString(R.string.complete));
    } catch (IOException ioe) {
      Log.w(TAG, "Could not get assets.", ioe);
    }

    new ConferenceTableAsync(this, TrendoRepository.getInstance(this), conferenceData).execute();
  }

  public void conferenceTableSynced() {

    Log.d(TAG, "++conferenceTableSynced()");
    mConferencesStatusText.setText(getString(R.string.complete));
    mConferencesStatusImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_success_dark, getTheme()));

    File teamData = new File(getCacheDir(), BaseActivity.DEFAULT_TEAM_DATA);
    try {
      Log.d(TAG, "Using " + BaseActivity.DEFAULT_TEAM_DATA);
      try (InputStream inputStream = getAssets().open(BaseActivity.DEFAULT_TEAM_DATA)) {
        try (FileOutputStream outputStream = new FileOutputStream(teamData)) {
          byte[] buf = new byte[1024];
          int len;
          while ((len = inputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, len);
          }
        }
      }

      mTeamsStatusImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_success_dark));
      mTeamsStatusText.setText(getString(R.string.complete));
    } catch (IOException ioe) {
      Log.w(TAG, "Could not get assets.", ioe);
    }

    new TeamTableAsync(this, TrendoRepository.getInstance(this), teamData).execute();
  }

  public void matchSummaryTableSynced(List<MatchSummaryEntity> matchSummaries) {

    Log.d(TAG, "++matchSummaryTableSynced()");
    mMatchSummaryList = matchSummaries;
    mMatchSummariesStatusText.setText(getString(R.string.complete));
    mMatchSummariesStatusImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_success_dark, getTheme()));
    new TrendTableAsync(this, TrendoRepository.getInstance(this), mMatchSummaryList, mTeamList, mYear).execute();
  }

  public void teamTableSynced(List<TeamEntity> teamList) {

    Log.d(TAG, "++teamTableSynced()");
    mTeamList = teamList;
    mTeamsStatusText.setText(getString(R.string.complete));
    mTeamsStatusImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_success_dark, getTheme()));
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    mYear = Calendar.getInstance().get(Calendar.YEAR);
    if (sharedPreferences.contains(UserPreferencesFragment.KEY_YEAR_PREFERENCE)) {
      String preference = sharedPreferences.getString(UserPreferencesFragment.KEY_YEAR_PREFERENCE, getString(R.string.none));
      if (preference != null && !preference.equals(getString(R.string.none))) {
        try {
          mYear = Integer.parseInt(preference);
        } catch (Exception ex) {
          mYear = Calendar.getInstance().get(Calendar.YEAR);
        }
      } else {
        mYear = Calendar.getInstance().get(Calendar.YEAR);
      }
    }

    String dataFileName = String.format(Locale.US, "%d.json", mYear);
    File seasonalData = new File(getCacheDir(), dataFileName);
    try {
      Log.d(TAG, "Using " + dataFileName);
      try (InputStream inputStream = getAssets().open(dataFileName)) {
        try (FileOutputStream outputStream = new FileOutputStream(seasonalData)) {
          byte[] buf = new byte[1024];
          int len;
          while ((len = inputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, len);
          }
        }
      }

      mMatchSummariesStatusImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_success_dark));
      mMatchSummariesStatusText.setText(getString(R.string.complete));
    } catch (IOException ioe) {
      Log.w(TAG, "Could not get assets.", ioe);
    }

    new MatchSummaryTableAsync(this, TrendoRepository.getInstance(this), seasonalData, mYear).execute();
  }

  public void trendTableSynced() {

    Log.d(TAG, "++trendTableSynced()");
    mTrendsStatusText.setText(getString(R.string.complete));
    mTrendsStatusImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_success_dark, getTheme()));
    Intent intent = new Intent(DataActivity.this, MainActivity.class);
    intent.putExtra(BaseActivity.ARG_USER_ID, mUserId);
    startActivity(intent);
    finish();
  }
}
