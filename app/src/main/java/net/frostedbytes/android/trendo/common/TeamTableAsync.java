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

import net.frostedbytes.android.trendo.db.TrendoDatabase;
import net.frostedbytes.android.trendo.db.dao.TeamDao;
import net.frostedbytes.android.trendo.db.entity.TeamEntity;
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

public class TeamTableAsync extends AsyncTask<Void, Void, List<TeamEntity>> {

  private static final String TAG = BaseActivity.BASE_TAG + "TeamTableAsync";

  private WeakReference<DataActivity> mActivityWeakReference;

  private final File mTeamData;
  private final TeamDao mTeamDao;

  public TeamTableAsync(DataActivity context, TrendoDatabase db, File teamData) {

    mActivityWeakReference = new WeakReference<>(context);
    mTeamData = teamData;
    mTeamDao = db.teamDao();
  }

  @Override
  protected List<TeamEntity> doInBackground(final Void... params) {

    PackagedData packagedData = new PackagedData();
    if (mTeamData.exists() && mTeamData.canRead()) {
      Log.d(TAG, "Loading " + mTeamData.getAbsolutePath());
      try (Reader reader = new FileReader(mTeamData.getAbsolutePath())) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<PackagedData>() {
        }.getType();
        packagedData = gson.fromJson(reader, collectionType);
      } catch (FileNotFoundException e) {
        Log.w(TAG, "Source data not found locally.");
      } catch (IOException e) {
        Log.w(TAG, "Could not read the source data.");
      }

      if (packagedData.Teams != null && packagedData.Teams.size() != mTeamDao.count()) {
        String message = "Team data processing:";
        try {
          mTeamDao.insertAll(packagedData.Teams);
          message = String.format(Locale.US, "%s %d...", message, packagedData.Teams.size());
        } catch (Exception e) {
          Log.w(TAG, "Could not process Team data.", e);
        } finally {
          Log.d(TAG, message);
        }
      } else {
        Log.e(TAG, "Team source data was incomplete.");
      }
    } else {
      Log.e(TAG, "Does not exist yet " + mTeamData.getAbsoluteFile());
    }

    return packagedData.Teams;
  }

  protected void onPostExecute(List<TeamEntity> teams) {

    Log.d(TAG, "++onPostExecute()");
    DataActivity activity = mActivityWeakReference.get();
    if (activity == null) {
      Log.e(TAG, "DataActivity is null or detached.");
      return;
    }

    activity.teamTableSynced(teams);
  }
}
