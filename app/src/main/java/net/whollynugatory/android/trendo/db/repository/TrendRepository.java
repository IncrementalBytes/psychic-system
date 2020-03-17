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
import net.whollynugatory.android.trendo.db.dao.TrendDao;
import net.whollynugatory.android.trendo.db.entity.TrendEntity;
import net.whollynugatory.android.trendo.ui.BaseActivity;

public class TrendRepository {

  private static final String TAG = BaseActivity.BASE_TAG + "TrendRepository";

  private static TrendRepository sInstance;

  private TrendDao mTrendDao;

  private TrendRepository(TrendDao trendDao) {

    Log.d(TAG, "++TrendRepository(TrendDao)");
    mTrendDao = trendDao;
  }

  public static TrendRepository getInstance(final TrendDao trendDao) {

    if (sInstance == null) {
      synchronized (ConferenceRepository.class) {
        if (sInstance == null) {
          sInstance = new TrendRepository(trendDao);
        }
      }
    }

    return sInstance;
  }

  public int count(int year) {

    return mTrendDao.count(year);
  }

  public int count(String teamId, int year) {

    return mTrendDao.count(teamId, year);
  }

  public void insert(TrendEntity trendEntity) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mTrendDao.insert(trendEntity));
  }
}
