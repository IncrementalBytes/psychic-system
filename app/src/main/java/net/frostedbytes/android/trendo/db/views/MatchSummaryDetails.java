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
package net.frostedbytes.android.trendo.db.views;

import net.frostedbytes.android.trendo.ui.BaseActivity;

import androidx.room.DatabaseView;
import java.io.Serializable;
import java.util.Locale;

@DatabaseView(
  "SELECT Summary.home_score AS HomeScore, " +
    "Summary.away_score AS AwayScore, " +
    "HomeTeam.Name AS HomeName, " +
    "AwayTeam.Name AS AwayName " +
  "FROM match_summary_table AS Summary " +
  "INNER JOIN team_table AS AwayTeam ON Summary.away_id = AwayTeam.id " +
  "INNER JOIN team_table AS HomeTeam ON Summary.home_id = HomeTeam.id")
public class MatchSummaryDetails implements Serializable {

  public String Id;
  public String HomeTeam;
  public String AwayTeam;
  public String MatchDate;
  public int HomeScore;
  public int AwayScore;

  public MatchSummaryDetails() {

    Id = BaseActivity.DEFAULT_ID;
    HomeTeam = "";
    AwayTeam = "";
    HomeScore = 0;
    AwayScore = 0;
    MatchDate = BaseActivity.DEFAULT_DATE;
  }

  /*
    Object Override(s)
   */
  @Override
  public String toString() {

    return String.format(
      Locale.US,
      "%s vs %s - %d-%d",
      HomeTeam,
      AwayTeam,
      HomeScore,
      AwayScore);
  }
}
