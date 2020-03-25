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

package net.whollynugatory.android.trendo.common;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.whollynugatory.android.trendo.db.entity.MatchDateDim;
import net.whollynugatory.android.trendo.db.entity.MatchSummaryEntity;
import net.whollynugatory.android.trendo.db.repository.MatchDateDimRepository;
import net.whollynugatory.android.trendo.db.repository.MatchSummaryRepository;
import net.whollynugatory.android.trendo.ui.BaseActivity;
import net.whollynugatory.android.trendo.ui.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.UUID;

public class MatchSummaryTableAsync extends AsyncTask<Void, Void, Integer> {

  private static final String TAG = BaseActivity.BASE_TAG + "MatchSummaryTableAsync";

  private WeakReference<MainActivity> mWeakReference;

  private final MatchDateDimRepository mMatchDateDimRepository;
  private final MatchSummaryRepository mMatchSummaryRepository;
  private File mMatchSummaryData;

  public MatchSummaryTableAsync(MainActivity context, MatchSummaryRepository repository, MatchDateDimRepository dimRepository, int season) {

    mWeakReference = new WeakReference<>(context);
    mMatchDateDimRepository = dimRepository;
    mMatchSummaryRepository = repository;
    try (InputStream inputStream = context.getAssets().open(season + ".json")) {
      mMatchSummaryData = File.createTempFile(UUID.randomUUID().toString(), ".json");
      try (FileOutputStream outputStream = new FileOutputStream(mMatchSummaryData)) {
        byte[] buf = new byte[1024];
        int len;
        while ((len = inputStream.read(buf)) > 0) {
          outputStream.write(buf, 0, len);
        }
      } catch (IOException ioe) {
        Log.w(TAG, "Could not get output stream.", ioe);
      }
    } catch (IOException ioe) {
      Log.w(TAG, "Could not access data for: " + season);
    }
  }

  @Override
  protected Integer doInBackground(final Void... params) {

    int count = 0;
    PackagedData packagedData = new PackagedData();
    if (mMatchSummaryData != null && mMatchSummaryData.exists() && mMatchSummaryData.canRead()) {
      Log.d(TAG, "Loading " + mMatchSummaryData.getAbsolutePath());
      try (Reader reader = new FileReader(mMatchSummaryData.getAbsolutePath())) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<PackagedData>() {
        }.getType();
        packagedData = gson.fromJson(reader, collectionType);
      } catch (FileNotFoundException e) {
        Log.w(TAG, "Source data not found locally.");
      } catch (IOException e) {
        Log.w(TAG, "Could not read the source data.");
      }

      if (packagedData.MatchSummaries != null) {
        String message = "MatchSummary data processing:";
        try {
          for (MatchSummaryEntity matchSummary : packagedData.MatchSummaries) {
            mMatchDateDimRepository.insert(MatchDateDim.generate(matchSummary.MatchDate));
            mMatchSummaryRepository.insert(matchSummary);
            count++;
          }

          Log.d(TAG, String.format(Locale.US, "%s %d...", message, count));
        } catch (Exception e) {
          Log.w(TAG, "Could not process MatchSummary data.", e);
        }
      } else {
        Log.d(TAG, "MatchSummary data does not exists.");
      }
    } else {
      Log.w(TAG, "Could not find data to process.");
    }

    return count;
  }

  protected void onPostExecute(Integer matchCount) {

    Log.d(TAG, "++onPostExecute(Integer)");
    MainActivity activity = mWeakReference.get();
    if (activity == null) {
      Log.e(TAG, "MainActivity is null or detached.");
      return;
    }

    activity.matchSummaryTableSynced(matchCount);
  }
}
