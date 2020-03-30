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

import net.whollynugatory.android.trendo.db.TrendoDatabase;
import net.whollynugatory.android.trendo.db.dao.MatchSummaryDao;
import net.whollynugatory.android.trendo.db.entity.MatchSummaryEntity;
import net.whollynugatory.android.trendo.ui.BaseActivity;

import java.util.List;

public class MatchSummaryRepository {

  private static final String TAG = BaseActivity.BASE_TAG + "MatchSummaryRepository";

  private static MatchSummaryRepository sInstance;

  private final MatchSummaryDao mMatchSummaryDao;

  private MatchSummaryRepository(MatchSummaryDao matchSummaryDao) {

    Log.d(TAG, "++MatchSummaryRepository(MatchSummaryDao)");
    mMatchSummaryDao = matchSummaryDao;
  }

  public static MatchSummaryRepository getInstance(final MatchSummaryDao matchSummaryDao) {

    if (sInstance == null) {
      synchronized (MatchSummaryRepository.class) {
        if (sInstance == null) {
          sInstance = new MatchSummaryRepository(matchSummaryDao);
        }
      }
    }

    return sInstance;
  }

  public int count(int season) {

    return mMatchSummaryDao.count(season);
  }

  public void insert(MatchSummaryEntity matchSummary) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mMatchSummaryDao.insert(matchSummary));
  }

  public void insertAll(List<MatchSummaryEntity> matchSummaries) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mMatchSummaryDao.insertAll(matchSummaries));
  }
}
