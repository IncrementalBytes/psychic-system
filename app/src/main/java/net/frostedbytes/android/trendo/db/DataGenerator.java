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
 *    https://github.com/android/architecture-components-samples/blob/master/BasicSample/app/src/main/java/com/example/android/persistence/db/DataGenerator.java
 */
package net.frostedbytes.android.trendo.db;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.frostedbytes.android.trendo.ui.BaseActivity;
import net.frostedbytes.android.trendo.common.PackagedData;
import net.frostedbytes.android.trendo.db.entity.ConferenceEntity;
import net.frostedbytes.android.trendo.db.entity.MatchSummaryEntity;
import net.frostedbytes.android.trendo.db.entity.TeamEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DataGenerator {

  private static final String TAG = BaseActivity.BASE_TAG + "DataGenerator";

  public static List<ConferenceEntity> generateConferences(File conferenceData) {

    Log.d(TAG, "++generateConferences(File)");
    List<ConferenceEntity> conferenceEntityList = new ArrayList<>();
    PackagedData packagedData = null;
    if (conferenceData.exists() && conferenceData.canRead()) {
      Log.d(TAG, "Loading " + conferenceData.getAbsolutePath());
      try (Reader reader = new FileReader(conferenceData.getAbsolutePath())) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<PackagedData>() {
        }.getType();
        packagedData = gson.fromJson(reader, collectionType);
      } catch (FileNotFoundException e) {
        Log.w(TAG, "Source data not found locally.");
      } catch (IOException e) {
        Log.w(TAG, "Could not read the source data.");
      }
    }

    if (packagedData != null && packagedData.Conferences != null) {
      String message = "Conference data processing:";
      int count = 0;
      try {
        for (ConferenceEntity conference : packagedData.Conferences) {
          conferenceEntityList.add(conference);
          message = String.format(Locale.US, "%s %d...", message, ++count);
        }
      } catch (Exception e) {
        Log.w(TAG, "Could not process Conference data.", e);
      } finally {
        Log.d(TAG, message);
      }
    }

    return conferenceEntityList;
  }

  public static List<MatchSummaryEntity> generateMatchSummaries(File matchSummaryData) {

    Log.d(TAG, "++generateMatchSummaries(File)");
    List<MatchSummaryEntity> matchSummaryEntityList = new ArrayList<>();
    PackagedData packagedData = null;
    if (matchSummaryData.exists() && matchSummaryData.canRead()) {
      Log.d(TAG, "Loading " + matchSummaryData.getAbsolutePath());
      try (Reader reader = new FileReader(matchSummaryData.getAbsolutePath())) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<PackagedData>() {
        }.getType();
        packagedData = gson.fromJson(reader, collectionType);
      } catch (FileNotFoundException e) {
        Log.w(TAG, "Source data not found locally.");
      } catch (IOException e) {
        Log.w(TAG, "Could not read the source data.");
      }
    }

    if (packagedData != null && packagedData.Conferences != null) {
      String message = "MatchSummary data processing:";
      int count = 0;
      try {
        for (MatchSummaryEntity matchSummaryEntity : packagedData.MatchSummaries) {
          matchSummaryEntityList.add(matchSummaryEntity);
          count++;
        }

        message = String.format(Locale.US, "%s %d...", message, count);
      } catch (Exception e) {
        Log.w(TAG, "Could not process MatchSummary data.", e);
      } finally {
        Log.d(TAG, message);
      }
    }

    return matchSummaryEntityList;
  }

  public static List<TeamEntity> generateTeams(File teamData) {

    Log.d(TAG, "++generateTeams(File)");
    List<TeamEntity> teamEntityList = new ArrayList<>();
    PackagedData packagedData = null;
    if (teamData.exists() && teamData.canRead()) {
      Log.d(TAG, "Loading " + teamData.getAbsolutePath());
      try (Reader reader = new FileReader(teamData.getAbsolutePath())) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<PackagedData>() {
        }.getType();
        packagedData = gson.fromJson(reader, collectionType);
      } catch (FileNotFoundException e) {
        Log.w(TAG, "Source data not found locally.");
      } catch (IOException e) {
        Log.w(TAG, "Could not read the source data.");
      }
    }

    if (packagedData != null && packagedData.Conferences != null) {
      String message = "Team data processing:";
      int count = 0;
      try {
        for (TeamEntity team : packagedData.Teams) {
          teamEntityList.add(team);
          message = String.format(Locale.US, "%s %d...", message, ++count);
        }
      } catch (Exception e) {
        Log.w(TAG, "Could not process Team data.", e);
      } finally {
        Log.d(TAG, message);
      }
    }

    return teamEntityList;
  }
}
