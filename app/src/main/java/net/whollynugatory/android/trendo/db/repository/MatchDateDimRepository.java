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

import net.whollynugatory.android.trendo.db.dao.MatchDateDimDao;
import net.whollynugatory.android.trendo.db.entity.MatchDateDim;
import net.whollynugatory.android.trendo.ui.BaseActivity;

public class MatchDateDimRepository {

  private static final String TAG = BaseActivity.BASE_TAG + "MatchDateDimRepository";

  private static MatchDateDimRepository sInstance;

  private MatchDateDimDao mMatchDateDimDao;

  private MatchDateDimRepository(MatchDateDimDao matchDateDimDao) {

    Log.d(TAG, "++MatchDateDimRepository(MatchDateDimDao)");
    mMatchDateDimDao = matchDateDimDao;
  }

  public static MatchDateDimRepository getInstance(final MatchDateDimDao matchDateDimDao) {

    if (sInstance == null) {
      synchronized (MatchDateDimRepository.class) {
        if (sInstance == null) {
          sInstance = new MatchDateDimRepository(matchDateDimDao);
        }
      }
    }

    return sInstance;
  }

  public void insert(MatchDateDim matchDateDim) {

    mMatchDateDimDao.insert(matchDateDim);
  }
}
