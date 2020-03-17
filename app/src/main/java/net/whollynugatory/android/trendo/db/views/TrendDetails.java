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
package net.whollynugatory.android.trendo.db.views;

import net.whollynugatory.android.trendo.ui.BaseActivity;

import java.io.Serializable;

import androidx.room.DatabaseView;

@DatabaseView(
  "SELECT TeamTable.id AS TeamId," +
    "TeamTable.short_name AS ShortName, " +
    "TrendTable.year AS Year, " +
    "TrendTable.match_number AS MatchNumber, " +
    "TrendTable.goals_against AS GoalsAgainst, " +
    "TrendTable.goal_differential AS GoalDifferential, " +
    "TrendTable.goals_for AS GoalsFor, " +
    "TrendTable.max_points AS MaxPointsPossible, " +
    "TrendTable.points_by_average AS PointsByAverage, " +
    "TrendTable.points_per_game AS PointsPerGame, " +
    "TrendTable.total_points AS TotalPoints " +
    "FROM trend_table AS TrendTable " +
    "INNER JOIN team_table AS TeamTable ON TrendTable.team_id = TeamTable.id")
public class TrendDetails implements Serializable {

  public String TeamId;
  public String ShortName;
//  public int TablePosition;
  public int Year;
  public int MatchNumber;
  public long GoalsAgainst;
  public long GoalDifferential;
  public long GoalsFor;
  public long MaxPointsPossible;
  public long PointsByAverage;
  public double PointsPerGame;
  public long TotalPoints;

  public TrendDetails() {

    TeamId = BaseActivity.DEFAULT_ID;
    ShortName = "";
//    TablePosition = 0;
    Year = 1990;
    MatchNumber = 0;
    GoalsAgainst = 0;
    GoalDifferential = 0;
    GoalsFor = 0;
    MaxPointsPossible = 0;
    PointsByAverage = 0;
    PointsPerGame = 0.0;
    TotalPoints = 0;
  }
}
