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

package net.whollynugatory.android.trendo.db.views;

import net.whollynugatory.android.trendo.ui.BaseActivity;

import androidx.room.DatabaseView;
import java.io.Serializable;
import java.util.Locale;

@DatabaseView(
  "SELECT Summary.match_date AS MatchDate, " +
    "HomeTeam.Name AS HomeName, " +
    "HomeTeam.Id AS HomeId, " +
    "Summary.home_score AS HomeScore, " +
    "AwayTeam.Name AS AwayName, " +
    "AwayTeam.Id AS AwayId, " +
    "Summary.away_score AS AwayScore " +
  "FROM match_summary_table AS Summary " +
  "INNER JOIN team_table AS AwayTeam ON Summary.away_id = AwayTeam.id " +
  "INNER JOIN team_table AS HomeTeam ON Summary.home_id = HomeTeam.id")
public class MatchSummaryDetails implements Serializable {

  public String MatchDate;
  public String HomeName;
  public String HomeId;
  public int HomeScore;
  public String AwayName;
  public String AwayId;
  public int AwayScore;

  public MatchSummaryDetails() {

    MatchDate = BaseActivity.DEFAULT_DATE;
    HomeName = "";
    HomeId = BaseActivity.DEFAULT_ID;
    HomeScore = 0;
    AwayName = "";
    AwayId = BaseActivity.DEFAULT_ID;
    AwayScore = 0;
  }

  /*
    Object Override(s)
   */
  @Override
  public String toString() {

    return String.format(
      Locale.US,
      "%s vs %s - %d-%d",
      HomeName,
      AwayName,
      HomeScore,
      AwayScore);
  }
}
