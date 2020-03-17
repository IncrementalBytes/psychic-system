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
 *
 *    Referencing:
 *    https://github.com/android/architecture-components-samples/blob/master/BasicSample/app/src/main/java/com/example/android/persistence/db/AppDatabase.java
 */

package net.whollynugatory.android.trendo.db;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import net.whollynugatory.android.trendo.R;
import net.whollynugatory.android.trendo.db.dao.TrendDetailsDao;
import net.whollynugatory.android.trendo.db.dao.MatchSummaryDetailsDao;
import net.whollynugatory.android.trendo.db.entity.RemoteData;
import net.whollynugatory.android.trendo.db.views.MatchSummaryDetails;
import net.whollynugatory.android.trendo.db.views.TrendDetails;
import net.whollynugatory.android.trendo.ui.BaseActivity;
import net.whollynugatory.android.trendo.db.dao.ConferenceDao;
import net.whollynugatory.android.trendo.db.dao.MatchSummaryDao;
import net.whollynugatory.android.trendo.db.dao.TeamDao;
import net.whollynugatory.android.trendo.db.dao.TrendDao;
import net.whollynugatory.android.trendo.db.entity.ConferenceEntity;
import net.whollynugatory.android.trendo.db.entity.MatchSummaryEntity;
import net.whollynugatory.android.trendo.db.entity.TeamEntity;
import net.whollynugatory.android.trendo.db.entity.TrendEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
  entities = {ConferenceEntity.class, MatchSummaryEntity.class, TeamEntity.class, TrendEntity.class},
  views = {MatchSummaryDetails.class, TrendDetails.class},
  version = 1,
  exportSchema = false)
public abstract class TrendoDatabase extends RoomDatabase {

  private static final String TAG = BaseActivity.BASE_TAG + "TrendoDatabase";
  private static final int NUMBER_OF_THREADS = 4;

  public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

  public abstract ConferenceDao conferenceDao();

  public abstract MatchSummaryDao matchSummaryDao();

  public abstract MatchSummaryDetailsDao matchSummaryDetailsDao();

  public abstract TeamDao teamDao();

  public abstract TrendDao trendDao();

  public abstract TrendDetailsDao trendDetailsDao();

  private static TrendoDatabase sInstance;

  private static volatile AssetManager sAssetManager;
  private static String[] sSeasons;

  public static TrendoDatabase getInstance(final Context context) {

    if (sInstance == null) {
      synchronized (TrendoDatabase.class) {
        if (sInstance == null) {
          sAssetManager = context.getAssets();
          sSeasons = context.getResources().getStringArray(R.array.seasons);
          sInstance = Room.databaseBuilder(context.getApplicationContext(), TrendoDatabase.class, BaseActivity.DATABASE_NAME)
            .addCallback(sRoomDatabaseCallback)
            .build();
        }
      }
    }

    return sInstance;
  }

  private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {

    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
      super.onCreate(db);

      Log.d(TAG, "++onCreate(SupportSQLiteDatabase)");
      new PopulateDbAsync(sInstance, sAssetManager).execute();
    }
  };

  private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

    private final AssetManager mAssetManager;
    private final ConferenceDao mConferenceDao;
    private final MatchSummaryDao mMatchSummaryDao;
    private final TeamDao mTeamDao;

    PopulateDbAsync(TrendoDatabase db, AssetManager assetManager) {

      mAssetManager = assetManager;
      mConferenceDao = db.conferenceDao();
      mMatchSummaryDao = db.matchSummaryDao();
      mTeamDao = db.teamDao();
    }

    @Override
    protected Void doInBackground(final Void... params) {

      try (InputStream inputStream = mAssetManager.open(BaseActivity.DEFAULT_DATA_FILE)) {
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".json");
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
          byte[] buf = new byte[1024];
          int len;
          while ((len = inputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, len);
          }

          RemoteData remoteData = null;
          try (Reader reader = new FileReader(tempFile.getAbsolutePath())) {
            Gson gson = new Gson();
            Type collectionType = new TypeToken<RemoteData>() {
            }.getType();
            remoteData = gson.fromJson(reader, collectionType);
          } catch (FileNotFoundException e) {
            Log.w(TAG, "Source data from server not found locally.");
          } catch (IOException e) {
            Log.w(TAG, "Could not read the source data.");
          }

          if (remoteData != null) {
            try {
              mConferenceDao.insertAll(remoteData.Conferences);
              Log.d(TAG, String.format(Locale.US, "Conference data processing: %d", remoteData.Conferences.size()));
            } catch (Exception e) {
              Log.w(TAG, "Could not process conference data.", e);
            }

            try {
              mTeamDao.insertAll(remoteData.Teams);
              Log.d(TAG, String.format(Locale.US, "Team data processed: %d", remoteData.Teams.size()));
            } catch (Exception e) {
              Log.w(TAG, "Could not process teams data.", e);
            }
          } else {
            Log.e(TAG, "Source data was incomplete.");
          }
        }
      } catch (IOException ioe) {
        Log.w(TAG, "Could not get asset manager.", ioe);
      }

      // process each season (but don't generate trends)
      for (String season : sSeasons) {
        String seasonData = String.format(Locale.US, "%s.json", season);
        try (InputStream inputStream = mAssetManager.open(seasonData)) {
          File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".json");
          try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
              outputStream.write(buf, 0, len);
            }

            RemoteData remoteData = null;
            try (Reader reader = new FileReader(tempFile.getAbsolutePath())) {
              Gson gson = new Gson();
              Type collectionType = new TypeToken<RemoteData>() {
              }.getType();
              remoteData = gson.fromJson(reader, collectionType);
            } catch (FileNotFoundException e) {
              Log.w(TAG, "Source data from server not found locally.");
            } catch (IOException e) {
              Log.w(TAG, "Could not read the source data.");
            }

            if (remoteData != null) {
              try {
                mMatchSummaryDao.insertAll(remoteData.Matches);
                Log.d(TAG, String.format(Locale.US, "%s Season data processing: %d...", season, remoteData.Matches.size()));
              } catch (Exception e) {
                Log.w(TAG, "Could not process conference data.", e);
              }
            }
          }
        } catch (IOException ioe) {
          Log.w(TAG, "Could not get asset manager.", ioe);
        }
      }

      return null;
    }
  }
}
