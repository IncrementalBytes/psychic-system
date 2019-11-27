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
    "team_id", "year", "match"
  },
  foreignKeys = {
    @ForeignKey(entity = TeamEntity.class, parentColumns = "id", childColumns = "team_id")
  },
  indices = {
    @Index(value = {"team_id","year","match"})
  })
public class TrendEntity implements Serializable {

  @NonNull
  @ColumnInfo(name = "team_id")
  @SerializedName("team_id")
  public String TeamId;

  @NonNull
  @ColumnInfo(name = "year")
  @SerializedName("year")
  public int Year;

  @NonNull
  @ColumnInfo(name = "match")
  @SerializedName("match")
  public int Match;

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

  @ColumnInfo(name = "total_points")
  @SerializedName("total_points")
  public long TotalPoints;

  public TrendEntity() {

    GoalsAgainst = 0;
    GoalDifferential = 0;
    GoalsFor = 0;
    Match = 0;
    MaxPointsPossible = 0;
    PointsByAverage = 0;
    PointsPerGame = 0.0;
    TeamId = BaseActivity.DEFAULT_ID;
    TotalPoints = 0;
    Year = 1990;
  }
}
