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
package net.frostedbytes.android.trendo.db;

import android.content.Context;
import android.util.Log;

import net.frostedbytes.android.trendo.db.views.MatchSummaryDetail;
import net.frostedbytes.android.trendo.ui.BaseActivity;
import net.frostedbytes.android.trendo.db.entity.ConferenceEntity;
import net.frostedbytes.android.trendo.db.entity.MatchSummaryEntity;
import net.frostedbytes.android.trendo.db.entity.TeamEntity;
import net.frostedbytes.android.trendo.db.entity.TrendEntity;

import java.util.List;

public class TrendoRepository {

  private static final String TAG = BaseActivity.BASE_TAG + "TrendoRepository";

  private static TrendoRepository sInstance;

  private final TrendoDatabase mDatabase;

  private TrendoRepository(final Context context) {

    Log.d(TAG, "++TrendoRepository()");
    mDatabase = TrendoDatabase.getInstance(context);
  }

  public static TrendoRepository getInstance(final Context context) {

    if (sInstance == null) {
      synchronized (TrendoRepository.class) {
        if (sInstance == null) {
          sInstance = new TrendoRepository(context);
        }
      }
    }

    return sInstance;
  }

  /*
    Conference methods
   */
  public int countConferences() {

    return mDatabase.conferenceDao().count();
  }

  public List<ConferenceEntity> getAllConferences() {

    return mDatabase.conferenceDao().getAll();
  }

  public void insertConference(ConferenceEntity conference) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mDatabase.conferenceDao().insert(conference));
  }

  public void insertAllConferences(List<ConferenceEntity> conferences) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mDatabase.conferenceDao().insertAll(conferences));
  }

  /*
    MatchSummary methods
   */
  public int countMatchSummaries(String teamId, int year) {

    return mDatabase.matchSummaryDao().count(teamId, year);
  }

  public int countMatchSummaries(int year) {

    return mDatabase.matchSummaryDao().count(year);
  }

  public List<MatchSummaryDetail> getAllMatchSummaries(String teamId, int year) {

    return mDatabase.matchSummaryDao().getAll(teamId, year);
  }

  public void insertMatchSummary(MatchSummaryEntity matchSummary) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mDatabase.matchSummaryDao().insert(matchSummary));
  }

  public void insertAllMatchSummaries(List<MatchSummaryEntity> matchSummaries) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mDatabase.matchSummaryDao().insertAll(matchSummaries));
  }

  /*
  Team methods
 */
  public int countTeams() {

    return mDatabase.teamDao().count();
  }

  public List<TeamEntity> getAllTeams() {

    return mDatabase.teamDao().getAll();
  }

  public void insertTeam(TeamEntity team) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mDatabase.teamDao().insert(team));
  }

  public void insertAllTeams(List<TeamEntity> teams) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mDatabase.teamDao().insertAll(teams));
  }
  /*
    Trend method(s)
   */
  public int countTrends(String teamId, int year) {

    return mDatabase.trendDao().count(teamId, year);
  }

  public int countTrends(int year) {

    return mDatabase.trendDao().count(year);
  }

  public List<TrendEntity> getAllTrends(String teamId, int year) {

    return mDatabase.trendDao().getAll(teamId, year);
  }

  public void insertTrend(TrendEntity trend) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mDatabase.trendDao().insert(trend));
  }
}
