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
import net.frostedbytes.android.trendo.db.dao.ConferenceDao;
import net.frostedbytes.android.trendo.db.entity.ConferenceEntity;
import net.frostedbytes.android.trendo.ui.BaseActivity;
import net.frostedbytes.android.trendo.ui.DataActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.Locale;

public class ConferenceTableAsync  extends AsyncTask<Void, Void, Void> {

  private static final String TAG = BaseActivity.BASE_TAG + "ConferenceTableAsync";

  private WeakReference<DataActivity> mActivityWeakReference;

  private final ConferenceDao mConferenceDao;
  private final File mConferenceData;

  public ConferenceTableAsync(DataActivity context, TrendoDatabase db, File conferenceData) {

    mActivityWeakReference = new WeakReference<>(context);
    mConferenceDao = db.conferenceDao();
    mConferenceData = conferenceData;
  }

  @Override
  protected Void doInBackground(final Void... params) {

    PackagedData packagedData = null;
    if (mConferenceData.exists() && mConferenceData.canRead()) {
      Log.d(TAG, "Loading " + mConferenceData.getAbsolutePath());
      try (Reader reader = new FileReader(mConferenceData.getAbsolutePath())) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<PackagedData>() {
        }.getType();
        packagedData = gson.fromJson(reader, collectionType);
      } catch (FileNotFoundException e) {
        Log.w(TAG, "Source data not found locally.");
      } catch (IOException e) {
        Log.w(TAG, "Could not read the source data.");
      }

      if (packagedData != null && packagedData.Conferences != null) {
        if (packagedData.Conferences.size() != mConferenceDao.count()) {
          String message = "Conference data processing:";
          try {
            mConferenceDao.insertAll(packagedData.Conferences);
            message = String.format(Locale.US, "%s %d...", message, packagedData.Conferences.size());
          } catch (Exception e) {
            Log.w(TAG, "Could not process Conference data.", e);
          } finally {
            Log.d(TAG, message);
          }
        } else {
          Log.d(TAG, "Conference data exists.");
        }
      } else {
        Log.e(TAG, "Conference source data was incomplete.");
      }
    } else {
      Log.e(TAG, "Does not exist yet " + mConferenceData.getAbsoluteFile());
    }

    return null;
  }

  protected void onPostExecute(Void nothingReally) {

    Log.d(TAG, "++onPostExecute()");
    DataActivity activity = mActivityWeakReference.get();
    if (activity == null) {
      Log.e(TAG, "DataActivity is null or detached.");
      return;
    }

    activity.conferenceTableSynced();
  }
}
