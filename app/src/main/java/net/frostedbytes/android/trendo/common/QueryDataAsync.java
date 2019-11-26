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
package net.frostedbytes.android.trendo.common;

import android.os.AsyncTask;
import android.util.Log;

import net.frostedbytes.android.trendo.db.TrendoDatabase;
import net.frostedbytes.android.trendo.ui.BaseActivity;
import net.frostedbytes.android.trendo.ui.MainActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class QueryDataAsync extends AsyncTask<Void, Void, PackagedData> {

  private static final String TAG = BaseActivity.BASE_TAG + "QueryDataAsync";

  private WeakReference<MainActivity> mActivityWeakReference;
  private TrendoDatabase mDatabase;
  private String mTeamId;
  private int mYear;

  public QueryDataAsync(MainActivity context, TrendoDatabase db, String teamId, int year) {

    Log.d(TAG, "QueryDataAsync(MainActivity, TrendoDatabase, String, int)");
    mActivityWeakReference = new WeakReference<>(context);
    mDatabase = db;
    mTeamId = teamId;
    mYear = year;
  }

  @Override
  protected PackagedData doInBackground(final Void... params) {

    PackagedData packagedData = new PackagedData();
    packagedData.Conferences = new ArrayList<>(mDatabase.conferenceDao().getAll());
    packagedData.Teams = new ArrayList<>(mDatabase.teamDao().getAll());

    if (!mTeamId.isEmpty() && !mTeamId.equals(BaseActivity.DEFAULT_ID)) {
      packagedData.MatchSummaries = new ArrayList<>(mDatabase.matchSummaryDao().getAll(mTeamId, mYear));
      packagedData.Trends = new ArrayList<>(mDatabase.trendDao().getAll(mTeamId, mYear));
    }

    return packagedData;
  }

  protected void onPostExecute(PackagedData packagedData) {

    Log.d(TAG, "++onPostExecute()");
    MainActivity activity = mActivityWeakReference.get();
    if (activity == null) {
      Log.e(TAG, "MainActivity is null or detached.");
      return;
    }

    activity.dataQueryComplete(packagedData);
  }
}
