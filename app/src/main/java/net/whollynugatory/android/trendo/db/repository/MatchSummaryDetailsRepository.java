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

package net.whollynugatory.android.trendo.db.repository;

import android.util.Log;

import net.whollynugatory.android.trendo.db.dao.MatchSummaryDetailsDao;
import net.whollynugatory.android.trendo.db.views.MatchSummaryDetails;
import net.whollynugatory.android.trendo.ui.BaseActivity;

import java.util.List;

import androidx.lifecycle.LiveData;

public class MatchSummaryDetailsRepository {

  private static final String TAG = BaseActivity.BASE_TAG + "MatchSummaryDetailsRepository";

  private static MatchSummaryDetailsRepository sInstance;

  private MatchSummaryDetailsDao mMatchSummaryDetailsDao;

  private MatchSummaryDetailsRepository(MatchSummaryDetailsDao matchSummaryDetailDao) {

    Log.d(TAG, "++MatchSummaryDetailRepository(MatchSummaryDetailDao)");
    mMatchSummaryDetailsDao = matchSummaryDetailDao;
  }

  public static MatchSummaryDetailsRepository getInstance(final MatchSummaryDetailsDao matchSummaryDetailDao) {

    if (sInstance == null) {
      synchronized (MatchSummaryDetailsRepository.class) {
        if (sInstance == null) {
          sInstance = new MatchSummaryDetailsRepository(matchSummaryDetailDao);
        }
      }
    }

    return sInstance;
  }

  public LiveData<List<MatchSummaryDetails>> getAll(int season) {

    return mMatchSummaryDetailsDao.getAll(season);
  }

  public LiveData<List<MatchSummaryDetails>> getAll(String teamId, int season) {

    return mMatchSummaryDetailsDao.getAll(teamId, season);
  }
}
