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
import net.whollynugatory.android.trendo.db.dao.ConferenceDao;
import net.whollynugatory.android.trendo.db.entity.ConferenceEntity;
import net.whollynugatory.android.trendo.ui.BaseActivity;

import java.util.List;

import androidx.lifecycle.LiveData;

public class ConferenceRepository {

  private static final String TAG = BaseActivity.BASE_TAG + "ConferenceRepository";

  private static ConferenceRepository sInstance;

  private ConferenceDao mConferenceDao;

  private ConferenceRepository(ConferenceDao conferenceDao) {

    Log.d(TAG, "++ConferenceRepository(ConferenceDao)");
    mConferenceDao = conferenceDao;
  }

  public static ConferenceRepository getInstance(final ConferenceDao conferenceDao) {

    if (sInstance == null) {
      synchronized (ConferenceRepository.class) {
        if (sInstance == null) {
          sInstance = new ConferenceRepository(conferenceDao);
        }
      }
    }

    return sInstance;
  }

  public int count() {

    return mConferenceDao.count();
  }

  public LiveData<List<ConferenceEntity>> getAll() {

    return mConferenceDao.getAll();
  }

  public void insert(ConferenceEntity conference) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mConferenceDao.insert(conference));
  }

  public void insertAll(List<ConferenceEntity> conferences) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mConferenceDao.insertAll(conferences));
  }
}
