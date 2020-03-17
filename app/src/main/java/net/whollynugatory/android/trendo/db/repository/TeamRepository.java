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
import net.whollynugatory.android.trendo.db.dao.TeamDao;
import net.whollynugatory.android.trendo.db.entity.TeamEntity;
import net.whollynugatory.android.trendo.ui.BaseActivity;

import java.util.List;

import androidx.lifecycle.LiveData;

public class TeamRepository {

  private static final String TAG = BaseActivity.BASE_TAG + "TeamRepository";

  private static TeamRepository sInstance;

  private TeamDao mTeamDao;

  private TeamRepository(final TeamDao teamDao) {

    Log.d(TAG, "++TeamRepository(TeamDao)");
    mTeamDao = teamDao;
  }

  public static TeamRepository getInstance(final TeamDao teamDao) {

    if (sInstance == null) {
      synchronized (TeamRepository.class) {
        if (sInstance == null) {
          sInstance = new TeamRepository(teamDao);
        }
      }
    }

    return sInstance;
  }

  public int count() {

    return mTeamDao.count();
  }

  public LiveData<List<TeamEntity>> getAll() {

    return mTeamDao.getAll();
  }

  public void insert(TeamEntity team) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mTeamDao.insert(team));
  }

  public void insertAll(List<TeamEntity> teams) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mTeamDao.insertAll(teams));
  }
}
