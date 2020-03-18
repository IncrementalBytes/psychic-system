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
package net.whollynugatory.android.trendo.common;

import android.os.AsyncTask;
import android.util.Log;

import net.whollynugatory.android.trendo.db.entity.MatchSummaryEntity;
import net.whollynugatory.android.trendo.db.entity.TeamEntity;
import net.whollynugatory.android.trendo.db.repository.TrendRepository;
import net.whollynugatory.android.trendo.ui.BaseActivity;
import net.whollynugatory.android.trendo.ui.MainActivity;

import java.lang.ref.WeakReference;
import java.util.List;

public class TrendTableAsync extends AsyncTask<Void, Void, Void> {

  private static final String TAG = BaseActivity.BASE_TAG + "TrendTableAsync";

  private WeakReference<MainActivity> mActivityWeakReference;
  private List<MatchSummaryEntity> mMatchSummaries;
  private TrendRepository mTrendRepository;
  private List<TeamEntity> mTeams;
  private int mYear;

  public TrendTableAsync(
    MainActivity context,
    TrendRepository repository,
    List<MatchSummaryEntity> matchSummaries,
    List<TeamEntity> teams,
    int year) {

    mActivityWeakReference = new WeakReference<>(context);
    mTrendRepository = repository;
    mMatchSummaries = matchSummaries;
    mTeams = teams;
    mYear = year;
  }

  @Override
  protected Void doInBackground(final Void... params) {

    Log.d(TAG, "Generating trends");
//    if (mTrendRepository.count(mYear) > 0) {
//      Log.d(TAG, "Trend data exists.");
//      return null;
//    }
//
//    for (TeamEntity team : mTeams) {
//      long prevGoalAgainst = 0;
//      long prevGoalDifferential = 0;
//      long prevGoalFor = 0;
//      long prevTotalPoints = 0;
//      long totalMatches = 34;
//      long matchesRemaining = totalMatches;
//      int matchNumber = 0;
//      for (MatchSummaryEntity summary : mMatchSummaries) {
//        long pointsFromMatch;
//        long goalsAgainst;
//        long goalDifferential;
//        long goalsFor;
//        if (summary.HomeId.equals(team.Id)) { // targetTeam is the home team
//          Log.d(
//            TAG,
//            String.format("Processing match for %s on %s", team.Id, summary.MatchDate));
//          goalsAgainst = summary.AwayScore;
//          goalDifferential = summary.HomeScore - summary.AwayScore;
//          goalsFor = summary.HomeScore;
//          if (summary.HomeScore > summary.AwayScore) {
//            pointsFromMatch = (long) 3;
//            team.TotalWins++;
//            Log.d(TAG, String.format("%s won: %d", team.ShortName, pointsFromMatch));
//          } else if (summary.HomeScore < summary.AwayScore) {
//            pointsFromMatch = (long) 0;
//            Log.d(TAG, String.format("%s lost: %d", team.ShortName, pointsFromMatch));
//          } else {
//            pointsFromMatch = (long) 1;
//            Log.d(TAG, String.format("%s tied: %d", team.ShortName, pointsFromMatch));
//          }
//        } else if (summary.AwayId.equals(team.Id)) { // targetTeam is the away team
//          Log.d(TAG, String.format("Processing match for %s on %s", team.ShortName, summary.MatchDate));
//          goalsAgainst = summary.HomeScore;
//          goalDifferential = summary.AwayScore - summary.HomeScore;
//          goalsFor = summary.AwayScore;
//          if (summary.AwayScore > summary.HomeScore) {
//            pointsFromMatch = (long) 3;
//            team.TotalWins++;
//            Log.d(TAG, String.format("%s won: %d", team.ShortName, pointsFromMatch));
//          } else if (summary.AwayScore < summary.HomeScore) {
//            pointsFromMatch = (long) 0;
//            Log.d(TAG, String.format("%s lost: %d", team.ShortName, pointsFromMatch));
//          } else {
//            pointsFromMatch = (long) 1;
//            Log.d(TAG, String.format("%s tied: %d", team.ShortName, pointsFromMatch));
//          }
//        } else { // not a match where team.Id played
//          continue;
//        }
//
//        matchNumber++;
//        TrendEntity newTrend = new TrendEntity();
//        newTrend.TeamId = team.Id;
//        newTrend.Year = summary.Year;
//        newTrend.MatchNumber = matchNumber;
//        newTrend.GoalsAgainst = goalsAgainst + prevGoalAgainst;
//        Log.d(
//          TAG,
//          String.format("Calculating Goals Against, was %d, now %d", prevGoalAgainst, newTrend.GoalsAgainst));
//        newTrend.GoalDifferential = goalDifferential + prevGoalDifferential;
//        Log.d(
//          TAG,
//          String.format("Calculating Goal Differential, was %d, now %d", prevGoalDifferential, newTrend.GoalDifferential));
//        newTrend.GoalsFor = goalsFor + prevGoalFor;
//        Log.d(TAG, String.format("Calculating Goals For, was %d, now %d", prevGoalFor, newTrend.GoalsFor));
//        newTrend.TotalPoints = pointsFromMatch + prevTotalPoints;
//        Log.d(TAG, String.format("Calculating Total Points, was %d, now %d", prevTotalPoints, newTrend.TotalPoints));
//        long remainingMatches = --matchesRemaining;
//        newTrend.MaxPointsPossible = (pointsFromMatch + prevTotalPoints) + (remainingMatches * 3);
//        Log.d(TAG, String.format("Calculating Max Possible Points, was %d, now %d", prevTotalPoints, newTrend.MaxPointsPossible));
//
//        double totalPoints = (double) pointsFromMatch + prevTotalPoints;
//        if (totalPoints > 0) {
//          totalPoints = (pointsFromMatch + prevTotalPoints) / (double) (matchNumber);
//        }
//
//        newTrend.PointsPerGame = totalPoints;
//        newTrend.PointsByAverage = (long) (totalPoints * totalMatches);
//
//        mTrendRepository.insert(newTrend);
//
//        // update previous values for next pass
//        prevGoalAgainst = goalsAgainst + prevGoalAgainst;
//        prevGoalDifferential = goalDifferential + prevGoalDifferential;
//        prevGoalFor = goalsFor + prevGoalFor;
//        prevTotalPoints = pointsFromMatch + prevTotalPoints;
//      }
//
//      team.GoalDifferential = prevGoalDifferential;
//      team.GoalsScored = prevGoalFor;
//      team.TotalPoints = prevTotalPoints;
//    }
//
//    mTeams.sort(new SortUtils.ByTotalPoints());
//    int tablePosition = mTeams.size() + 1;
//    for (TeamEntity team : mTeams) {
//      team.TablePosition = --tablePosition;
////      TODO: mTrendRepository.insert(team);
//    }

    return null;
  }

  protected void onPostExecute(Void nothingReally) {

    Log.d(TAG, "++onPostExecute()");
//    DataActivity activity = mActivityWeakReference.get();
//    if (activity == null) {
//      Log.e(TAG, "DataActivity is null or detached.");
//      return;
//    }
//
//    activity.trendTableSynced();
  }
}
