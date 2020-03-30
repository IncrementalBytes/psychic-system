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

import net.whollynugatory.android.trendo.db.dao.TrendDetailsDao;
import net.whollynugatory.android.trendo.db.views.TrendDetails;
import net.whollynugatory.android.trendo.ui.BaseActivity;

import java.util.List;

import androidx.lifecycle.LiveData;

public class TrendDetailsRepository {

  private static final String TAG = BaseActivity.BASE_TAG + "TrendDetailsRepository";

  private static TrendDetailsRepository sInstance;

  private final TrendDetailsDao mTrendDetailsDao;

  private TrendDetailsRepository(TrendDetailsDao trendDetailsDao) {

    Log.d(TAG, "++TrendDetailsRepository(TrendDetailsDao)");
    mTrendDetailsDao = trendDetailsDao;
  }

  public static TrendDetailsRepository getInstance(final TrendDetailsDao trendDetailsDao) {

    if (sInstance == null) {
      synchronized (TrendDetailsRepository.class) {
        if (sInstance == null) {
          sInstance = new TrendDetailsRepository(trendDetailsDao);
        }
      }
    }

    return sInstance;
  }

  public LiveData<List<TrendDetails>> getAll(String teamId, int year) {

    return mTrendDetailsDao.getAll(teamId, year);
  }
}
