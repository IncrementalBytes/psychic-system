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
 *
 *    Referencing:
 *    https://github.com/android/architecture-components-samples/blob/master/BasicSample/app/src/main/java/com/example/android/persistence/db/AppDatabase.java
 */
package net.frostedbytes.android.trendo.db;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import net.frostedbytes.android.trendo.ui.BaseActivity;
import net.frostedbytes.android.trendo.db.dao.ConferenceDao;
import net.frostedbytes.android.trendo.db.dao.MatchSummaryDao;
import net.frostedbytes.android.trendo.db.dao.TeamDao;
import net.frostedbytes.android.trendo.db.dao.TrendDao;
import net.frostedbytes.android.trendo.db.entity.ConferenceEntity;
import net.frostedbytes.android.trendo.db.entity.MatchSummaryEntity;
import net.frostedbytes.android.trendo.db.entity.TeamEntity;
import net.frostedbytes.android.trendo.db.entity.TrendEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
  entities = {ConferenceEntity.class, MatchSummaryEntity.class, TeamEntity.class, TrendEntity.class},
  version = 1,
  exportSchema = false)
public abstract class TrendoDatabase extends RoomDatabase {

  private static final String TAG = BaseActivity.BASE_TAG + "TrendoDatabase";
  private static final int NUMBER_OF_THREADS = 4;

  private static TrendoDatabase sInstance;

  public abstract ConferenceDao conferenceDao();
  public abstract MatchSummaryDao matchSummaryDao();
  public abstract TeamDao teamDao();
  public abstract TrendDao trendDao();

  static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

  public static TrendoDatabase getInstance(final Context context) {

    if (sInstance == null) {
      synchronized (TrendoDatabase.class) {
        if (sInstance == null) {
          Log.d(TAG, "Building RoomDatabase object.");
          sInstance = Room.databaseBuilder(context, TrendoDatabase.class, BaseActivity.DATABASE_NAME).build();
        }
      }
    }

    return sInstance;
  }
}
