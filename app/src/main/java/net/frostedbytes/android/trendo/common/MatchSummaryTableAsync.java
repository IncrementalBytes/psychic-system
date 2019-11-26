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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.frostedbytes.android.trendo.db.TrendoRepository;
import net.frostedbytes.android.trendo.db.entity.MatchSummaryEntity;
import net.frostedbytes.android.trendo.ui.BaseActivity;
import net.frostedbytes.android.trendo.ui.DataActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MatchSummaryTableAsync extends AsyncTask<Void, Void, List<MatchSummaryEntity>> {

  private static final String TAG = BaseActivity.BASE_TAG + "MatchSummaryTableAsync";

  private WeakReference<DataActivity> mActivityWeakReference;

  private final TrendoRepository mRepository;
  private final File mMatchSummaryData;

  private int mYear;

  public MatchSummaryTableAsync(DataActivity context, TrendoRepository repository, File matchSummaryData, int year) {

    mActivityWeakReference = new WeakReference<>(context);
    mRepository = repository;
    mMatchSummaryData = matchSummaryData;
    mYear = year;
  }

  @Override
  protected List<MatchSummaryEntity> doInBackground(final Void... params) {

    PackagedData packagedData = new PackagedData();
    if (mMatchSummaryData.exists() && mMatchSummaryData.canRead()) {
      Log.d(TAG, "Loading " + mMatchSummaryData.getAbsolutePath());
      try (Reader reader = new FileReader(mMatchSummaryData.getAbsolutePath())) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<PackagedData>() {
        }.getType();
        packagedData = gson.fromJson(reader, collectionType);
      } catch (FileNotFoundException e) {
        Log.w(TAG, "Source data not found locally.");
      } catch (IOException e) {
        Log.w(TAG, "Could not read the source data.");
      }

      if (packagedData.MatchSummaries != null && packagedData.MatchSummaries.size() != mRepository.countMatchSummaries(mYear)) {
        String message = "MatchSummary data processing:";
        int count = 0;
        try {
          for (MatchSummaryEntity matchSummary : packagedData.MatchSummaries) {
            if (matchSummary.MatchDate == null ||
              (matchSummary.MatchDate.isEmpty() || matchSummary.MatchDate.equals(BaseActivity.DEFAULT_DATE))) {
              matchSummary.MatchDate =
                String.format(
                  Locale.US,
                  "%d%02d%02d",
                  matchSummary.Year,
                  matchSummary.Month,
                  matchSummary.Day);
            }

            // TODO: if we've updated a match summary (e.g. id, matchdate) we should write an updated json for future packaging
            mRepository.insertMatchSummary(matchSummary);
            count++;
          }

          message = String.format(Locale.US, "%s %d...", message, count);
        } catch (Exception e) {
          Log.w(TAG, "Could not process MatchSummary data.", e);
        } finally {
          Log.d(TAG, message);
        }
      } else {
        Log.e(TAG, "MatchSummary data exists.");
      }
    } else {
      Log.e(TAG, "Does not exist yet " + mMatchSummaryData.getAbsoluteFile());
    }

    return packagedData.MatchSummaries;
  }

  protected void onPostExecute(List<MatchSummaryEntity> matchSummaries) {

    Log.d(TAG, "++onPostExecute()");
    DataActivity activity = mActivityWeakReference.get();
    if (activity == null) {
      Log.e(TAG, "DataActivity is null or detached.");
      return;
    }

    activity.matchSummaryTableSynced(matchSummaries);
  }
}
