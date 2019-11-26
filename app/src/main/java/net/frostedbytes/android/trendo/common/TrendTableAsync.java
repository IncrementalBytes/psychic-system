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

import net.frostedbytes.android.trendo.db.entity.MatchSummaryEntity;
import net.frostedbytes.android.trendo.db.entity.TeamEntity;
import net.frostedbytes.android.trendo.db.entity.TrendEntity;
import net.frostedbytes.android.trendo.ui.BaseActivity;
import net.frostedbytes.android.trendo.ui.DataActivity;
import net.frostedbytes.android.trendo.db.TrendoDatabase;
import net.frostedbytes.android.trendo.db.dao.TrendDao;

import java.lang.ref.WeakReference;
import java.util.List;

public class TrendTableAsync extends AsyncTask<Void, Void, Void> {

  private static final String TAG = BaseActivity.BASE_TAG + "TrendTableAsync";

  private WeakReference<DataActivity> mActivityWeakReference;

  private final TrendDao mTrendDao;

  private List<MatchSummaryEntity> mMatchSummaries;
  private List<TeamEntity> mTeams;

  public TrendTableAsync(DataActivity context, TrendoDatabase db, List<MatchSummaryEntity> matchSummaries, List<TeamEntity> teams) {

    mActivityWeakReference = new WeakReference<>(context);
    mTrendDao = db.trendDao();
    mMatchSummaries = matchSummaries;
    mTeams = teams;
  }

  @Override
  protected Void doInBackground(final Void... params) {

    Log.d(TAG, "Generating trends");
    // TODO: skip if already done
    for (TeamEntity team : mTeams) {
      long prevGoalAgainst = 0;
      long prevGoalDifferential = 0;
      long prevGoalFor = 0;
      long prevTotalPoints = 0;
      long totalMatches = 34;
      long matchesRemaining = totalMatches;
      int matchDay = 0;
      for (MatchSummaryEntity summary : mMatchSummaries) {
        matchDay++;
        long pointsFromMatch;
        long goalsAgainst;
        long goalDifferential;
        long goalsFor;
        if (summary.HomeId.equals(team.Id)) { // targetTeam is the home team
          Log.d(
            TAG,
            String.format("Processing match for %s on %s", team.Id, summary.MatchDate));
          goalsAgainst = summary.AwayScore;
          goalDifferential = summary.HomeScore - summary.AwayScore;
          goalsFor = summary.HomeScore;
          if (summary.HomeScore > summary.AwayScore) {
            pointsFromMatch = (long) 3;
            Log.d(TAG, String.format("%s won: %d", team.ShortName, pointsFromMatch));
          } else if (summary.HomeScore < summary.AwayScore) {
            pointsFromMatch = (long) 0;
            Log.d(TAG, String.format("%s lost: %d", team.ShortName, pointsFromMatch));
          } else {
            pointsFromMatch = (long) 1;
            Log.d(TAG, String.format("%s tied: %d", team.ShortName, pointsFromMatch));
          }
        } else if (summary.AwayId.equals(team.Id)) { // targetTeam is the away team
          Log.d(TAG, String.format("Processing match for %s on %s", team.ShortName, summary.MatchDate));
          goalsAgainst = summary.HomeScore;
          goalDifferential = summary.AwayScore - summary.HomeScore;
          goalsFor = summary.AwayScore;
          if (summary.AwayScore > summary.HomeScore) {
            pointsFromMatch = (long) 3;
            Log.d(TAG, String.format("%s won: %d", team.ShortName, pointsFromMatch));
          } else if (summary.AwayScore < summary.HomeScore) {
            pointsFromMatch = (long) 0;
            Log.d(TAG, String.format("%s lost: %d", team.ShortName, pointsFromMatch));
          } else {
            pointsFromMatch = (long) 1;
            Log.d(TAG, String.format("%s tied: %d", team.ShortName, pointsFromMatch));
          }
        } else { // not a match where team.Id played
          continue;
        }

        TrendEntity newTrend = new TrendEntity();
        newTrend.TeamId = team.Id;
        newTrend.Year = summary.Year;
        newTrend.Match = matchDay;
        newTrend.GoalsAgainst = goalsAgainst + prevGoalAgainst;
        Log.d(
          TAG,
          String.format("Calculating Goals Against, was %d, now %d", prevGoalAgainst, newTrend.GoalsAgainst));
        newTrend.GoalDifferential = goalDifferential + prevGoalDifferential;
        Log.d(
          TAG,
          String.format("Calculating Goal Differential, was %d, now %d", prevGoalDifferential, newTrend.GoalDifferential));
        newTrend.GoalsFor = goalsFor + prevGoalFor;
        Log.d(TAG, String.format("Calculating Goals For, was %d, now %d", prevGoalFor, newTrend.GoalsFor));
        newTrend.TotalPoints = pointsFromMatch + prevTotalPoints;
        Log.d(TAG, String.format("Calculating Total Points, was %d, now %d", prevTotalPoints, newTrend.TotalPoints));
        long remainingMatches = --matchesRemaining;
        newTrend.MaxPointsPossible = (pointsFromMatch + prevTotalPoints) + (remainingMatches * 3);
        Log.d(TAG, String.format("Calculating Max Possible Points, was %d, now %d", prevTotalPoints, newTrend.MaxPointsPossible));

        double totalPoints = (double) pointsFromMatch + prevTotalPoints;
        if (totalPoints > 0) {
          totalPoints = (pointsFromMatch + prevTotalPoints) / (double) (matchDay);
        }

        newTrend.PointsPerGame = totalPoints;
        newTrend.PointsByAverage = (long) (totalPoints * totalMatches);

        mTrendDao.insert(newTrend);

        // update previous values for next pass
        prevGoalAgainst = goalsAgainst + prevGoalAgainst;
        prevGoalDifferential = goalDifferential + prevGoalDifferential;
        prevGoalFor = goalsFor + prevGoalFor;
        prevTotalPoints = pointsFromMatch + prevTotalPoints;
      }
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

    activity.trendTableSynced();
  }
}
