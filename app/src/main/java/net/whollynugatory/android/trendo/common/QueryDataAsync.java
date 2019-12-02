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
package net.whollynugatory.android.trendo.common;

import android.os.AsyncTask;
import android.util.Log;

import net.whollynugatory.android.trendo.db.TrendoRepository;
import net.whollynugatory.android.trendo.db.entity.TeamEntity;
import net.whollynugatory.android.trendo.ui.BaseActivity;
import net.whollynugatory.android.trendo.ui.MainActivity;
import net.whollynugatory.android.trendo.utils.SortUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class QueryDataAsync extends AsyncTask<Void, Void, PackagedData> {

  private static final String TAG = BaseActivity.BASE_TAG + "QueryDataAsync";

  private WeakReference<MainActivity> mActivityWeakReference;
  private PackagedData mPackagedData;
  private TrendoRepository mRepository;
  private String mTeamAheadId;
  private String mTeamBehindId;
  private String mTeamId;
  private int mYear;

  public QueryDataAsync(
    MainActivity context,
    TrendoRepository repository,
    String teamId,
    int year) {

    Log.d(TAG, "QueryDataAsync(MainActivity, TrendoDatabase, String, String, String, int)");
    mActivityWeakReference = new WeakReference<>(context);
    mPackagedData = new PackagedData();
    mRepository = repository;
    mTeamAheadId = BaseActivity.DEFAULT_ID;
    mTeamBehindId = BaseActivity.DEFAULT_ID;
    mTeamId = teamId;
    mYear = year;
  }

  @Override
  protected PackagedData doInBackground(final Void... params) {

    mPackagedData.Conferences = new ArrayList<>(mRepository.getAllConferences());
    mPackagedData.Teams = new ArrayList<>(mRepository.getAllTeams());
    getNearestOpponents();
    if (!mTeamId.isEmpty() && !mTeamId.equals(BaseActivity.DEFAULT_ID)) {
      mPackagedData.MatchDetails = new ArrayList<>(mRepository.getAllMatchSummaries(mTeamId, mYear));
      mPackagedData.Trends = new ArrayList<>(mRepository.getAllTrends(mTeamId, mYear));
      if (!mTeamAheadId.isEmpty() && !mTeamAheadId.equals(BaseActivity.DEFAULT_ID)) {
        mPackagedData.TrendsAhead = new ArrayList<>(mRepository.getAllTrends(mTeamAheadId, mYear));
      }

      if (!mTeamBehindId.isEmpty() && !mTeamBehindId.equals(BaseActivity.DEFAULT_ID)) {
        mPackagedData.TrendsBehind = new ArrayList<>(mRepository.getAllTrends(mTeamBehindId, mYear));
      }
    }

    return mPackagedData;
  }

  protected void onPostExecute(PackagedData packagedData) {

    Log.d(TAG, "++onPostExecute(PackagedData)");
    MainActivity activity = mActivityWeakReference.get();
    if (activity == null) {
      Log.e(TAG, "MainActivity is null or detached.");
      return;
    }

    activity.dataQueryComplete(packagedData);
  }

  private void getNearestOpponents() {

    Log.d(TAG, "++getNearestOpponents()");
    ArrayList<TeamEntity> teams = new ArrayList<>(mPackagedData.Teams);
    teams.sort(new SortUtils.ByTablePosition());
    int targetIndex = 0;
    for (; targetIndex < teams.size(); targetIndex++) {
      if (teams.get(targetIndex).Id.equals(mTeamId)) {
        break;
      }
    }

    // get the team that is ahead of selected team
    String targetConferenceId = teams.get(targetIndex).ConferenceId;
    for (int index = 0; index < targetIndex; index++) {
      if (teams.get(index).ConferenceId.equals(targetConferenceId)) {
        mTeamAheadId = teams.get(index).Id;
      }
    }

    // get the team that is behind the selected team
    if (teams.size() > targetIndex + 1) {
      for (int index = targetIndex + 1; index < teams.size(); index++) {
        if (teams.get(index).ConferenceId.equals(targetConferenceId)) {
          mTeamBehindId = teams.get(index).Id;
          break;
        }
      }
    }
  }
}
