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

package net.whollynugatory.android.trendo.db.entity;

import com.google.gson.annotations.SerializedName;

import net.whollynugatory.android.trendo.ui.BaseActivity;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
  tableName = "trend_table",
  primaryKeys = {
    "team_id", "year", "match_number"
  },
  foreignKeys = {
    @ForeignKey(entity = TeamEntity.class, parentColumns = "id", childColumns = "team_id")
  },
  indices = {
    @Index(value = {"team_id","year","match_number"})
  })
public class TrendEntity implements Serializable {

  @NonNull
  @ColumnInfo(name = "team_id")
  @SerializedName("team_id")
  public String TeamId;

  @ColumnInfo(name = "year")
  @SerializedName("year")
  public int Year;

  @ColumnInfo(name = "match_number")
  @SerializedName("match_number")
  public int MatchNumber;

  @ColumnInfo(name = "goals_against")
  @SerializedName("goals_against")
  public long GoalsAgainst;

  @ColumnInfo(name = "goal_differential")
  @SerializedName("goal_differential")
  public long GoalDifferential;

  @ColumnInfo(name = "goals_for")
  @SerializedName("goals_for")
  public long GoalsFor;

  @ColumnInfo(name = "max_points")
  @SerializedName("max_points")
  public long MaxPointsPossible;

  @ColumnInfo(name = "points_by_average")
  @SerializedName("points_by_average")
  public long PointsByAverage;

  @ColumnInfo(name = "points_per_game")
  @SerializedName("points_per_game")
  public double PointsPerGame;

  @ColumnInfo(name = "total_draws")
  @SerializedName("total_draws")
  public long TotalDraws;

  @ColumnInfo(name = "total_losses")
  @SerializedName("total_losses")
  public long TotalLosses;

  @ColumnInfo(name = "total_wins")
  @SerializedName("total_wins")
  public long TotalWins;

  @ColumnInfo(name = "total_points")
  @SerializedName("total_points")
  public long TotalPoints;

  public TrendEntity() {

    TeamId = BaseActivity.DEFAULT_ID;
    Year = 1990;
    MatchNumber = 0;

    GoalsAgainst = 0;
    GoalDifferential = 0;
    GoalsFor = 0;
    MaxPointsPossible = 0;
    PointsByAverage = 0;
    PointsPerGame = 0.0;

    TotalWins = 0;
    TotalDraws = 0;
    TotalLosses = 0;
    TotalPoints = 0;
  }
}
