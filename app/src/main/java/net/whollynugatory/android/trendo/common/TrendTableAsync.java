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

import net.whollynugatory.android.trendo.db.entity.MatchDateDim;
import net.whollynugatory.android.trendo.db.entity.MatchSummaryEntity;
import net.whollynugatory.android.trendo.db.entity.TrendEntity;
import net.whollynugatory.android.trendo.db.repository.TrendRepository;
import net.whollynugatory.android.trendo.ui.BaseActivity;
import net.whollynugatory.android.trendo.ui.DataActivity;

import java.lang.ref.WeakReference;
import java.util.List;

public class TrendTableAsync extends AsyncTask<Void, Void, Void> {

  private static final String TAG = BaseActivity.BASE_TAG + "TrendTableAsync";

  private List<MatchSummaryEntity> mMatchSummaryEntityList;
  private String mTeamId;
  private TrendRepository mTrendRepository;
  private WeakReference<DataActivity> mWeakReference;

  public TrendTableAsync(
    DataActivity context,
    TrendRepository repository,
    List<MatchSummaryEntity> matchSummaryEntityList,
    String teamId,
    int season) {

    mMatchSummaryEntityList = matchSummaryEntityList;
    mTeamId = teamId;
    mTrendRepository = repository;
    mWeakReference = new WeakReference<>(context);
  }

  @Override
  protected Void doInBackground(final Void... params) {

    Log.d(TAG, "Generating trends");
    long prevGoalAgainst = 0;
    long prevGoalDifferential = 0;
    long prevGoalFor = 0;
    long prevTotalPoints = 0;
    long totalMatches = 34;
    long matchesRemaining = totalMatches;
    int matchNumber = 0;
    long totalWins = 0;
    long totalDraws = 0;
    long totalLosses = 0;

    for (MatchSummaryEntity entity : mMatchSummaryEntityList) {
      long pointsFromMatch;
      long goalsAgainst;
      long goalDifferential;
      long goalsFor;
      if (entity.HomeId.equals(mTeamId)) { // targetTeam is the home team
        Log.d(
          TAG,
          String.format("Processing match for %s on %s", mTeamId, entity.MatchDate));
        goalsAgainst = entity.AwayScore;
        goalDifferential = entity.HomeScore - entity.AwayScore;
        goalsFor = entity.HomeScore;
        if (entity.HomeScore > entity.AwayScore) {
          pointsFromMatch = (long) 3;
          totalWins++;
          Log.d(TAG, String.format("%s won: %d", mTeamId, pointsFromMatch));
        } else if (entity.HomeScore < entity.AwayScore) {
          pointsFromMatch = (long) 0;
          totalLosses++;
          Log.d(TAG, String.format("%s lost: %d", mTeamId, pointsFromMatch));
        } else {
          pointsFromMatch = (long) 1;
          totalDraws++;
          Log.d(TAG, String.format("%s tied: %d", mTeamId, pointsFromMatch));
        }
      } else if (entity.AwayId.equals(mTeamId)) { // targetTeam is the away team
        Log.d(TAG, String.format("Processing match for %s on %s", mTeamId, entity.MatchDate));
        goalsAgainst = entity.HomeScore;
        goalDifferential = entity.AwayScore - entity.HomeScore;
        goalsFor = entity.AwayScore;
        if (entity.AwayScore > entity.HomeScore) {
          pointsFromMatch = (long) 3;
          totalWins++;
          Log.d(TAG, String.format("%s won: %d", mTeamId, pointsFromMatch));
        } else if (entity.AwayScore < entity.HomeScore) {
          pointsFromMatch = (long) 0;
          totalLosses++;
          Log.d(TAG, String.format("%s lost: %d", mTeamId, pointsFromMatch));
        } else {
          pointsFromMatch = (long) 1;
          totalDraws++;
          Log.d(TAG, String.format("%s tied: %d", mTeamId, pointsFromMatch));
        }
      } else { // not a match where team.Id played
        continue;
      }

      matchNumber++;
      TrendEntity newTrend = new TrendEntity();
      newTrend.TeamId = mTeamId;
      MatchDateDim matchDateDim = MatchDateDim.generate(entity.MatchDate);
      newTrend.Year = matchDateDim.Year;
      newTrend.MatchNumber = matchNumber;
      newTrend.GoalsAgainst = goalsAgainst + prevGoalAgainst;
      Log.d(TAG, String.format("Calculating Goals Against, was %d, now %d", prevGoalAgainst, newTrend.GoalsAgainst));
      newTrend.GoalDifferential = goalDifferential + prevGoalDifferential;
      Log.d(TAG, String.format("Calculating Goal Differential, was %d, now %d", prevGoalDifferential, newTrend.GoalDifferential));
      newTrend.GoalsFor = goalsFor + prevGoalFor;
      Log.d(TAG, String.format("Calculating Goals For, was %d, now %d", prevGoalFor, newTrend.GoalsFor));
      newTrend.TotalPoints = pointsFromMatch + prevTotalPoints;
      Log.d(TAG, String.format("Calculating Total Points, was %d, now %d", prevTotalPoints, newTrend.TotalPoints));
      long remainingMatches = --matchesRemaining;
      newTrend.MaxPointsPossible = (pointsFromMatch + prevTotalPoints) + (remainingMatches * 3);
      Log.d(TAG, String.format("Calculating Max Possible Points, was %d, now %d", prevTotalPoints, newTrend.MaxPointsPossible));
      newTrend.TotalWins = totalWins;
      newTrend.TotalDraws = totalDraws;
      newTrend.TotalLosses = totalLosses;

      double totalPoints = (double) pointsFromMatch + prevTotalPoints;
      if (totalPoints > 0) {
        totalPoints = (pointsFromMatch + prevTotalPoints) / (double) (matchNumber);
      }

      newTrend.PointsPerGame = totalPoints;
      newTrend.PointsByAverage = (long) (totalPoints * totalMatches);

      mTrendRepository.insert(newTrend);

      // update previous values for next pass
      prevGoalAgainst = goalsAgainst + prevGoalAgainst;
      prevGoalDifferential = goalDifferential + prevGoalDifferential;
      prevGoalFor = goalsFor + prevGoalFor;
      prevTotalPoints = pointsFromMatch + prevTotalPoints;
    }

    // TODO:
//    team.GoalDifferential = prevGoalDifferential;
//    team.GoalsScored = prevGoalFor;
//    team.TotalPoints = prevTotalPoints;
//
//    mTeams.sort(new SortUtils.ByTotalPoints());
//    int tablePosition = mTeams.size() + 1;
//    for(TeamEntity team :mTeams) {
//      team.TablePosition = --tablePosition;
//      TODO: mTrendRepository.insert(team);
//    }

    return null;
  }


  protected void onPostExecute(Void nothingReally) {

    Log.d(TAG, "++onPostExecute()");
    DataActivity activity = mWeakReference.get();
    if (activity == null) {
      Log.e(TAG, "DataActivity is null or detached.");
      return;
    }

    activity.trendTableSynced();
  }
}
