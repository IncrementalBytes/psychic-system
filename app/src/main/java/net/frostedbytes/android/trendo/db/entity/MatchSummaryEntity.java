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
package net.frostedbytes.android.trendo.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import net.frostedbytes.android.trendo.ui.BaseActivity;

import java.io.Serializable;

@Entity(
  tableName = "match_summary_table",
  foreignKeys = {@ForeignKey(entity = TeamEntity.class, parentColumns = "id", childColumns = "away_id"),
    @ForeignKey(entity = TeamEntity.class, parentColumns = "id", childColumns = "home_id")},
  indices = {@Index(value = {"away_id"}), @Index(value = {"home_id"})})
public class MatchSummaryEntity implements Serializable {

  public static final String ROOT = "MatchSummaries";

  @NonNull
  @PrimaryKey
  @ColumnInfo(name = "id")
  @SerializedName("id")
  public String Id;

  /**
   * Unique string of match date
   */
  @ColumnInfo(name = "match_date")
  @SerializedName("match_date")
  public String MatchDate;

  /**
   * Unique identifier for away team.
   */
  @NonNull
  @ColumnInfo(name = "away_id")
  @SerializedName("away_id")
  public String AwayId;

  /**
   * Goals scored by the away team.
   */
  @ColumnInfo(name = "away_score")
  @SerializedName("away_score")
  public long AwayScore;

  /**
   * Unique identifier for home team.
   */
  @NonNull
  @ColumnInfo(name = "home_id")
  @SerializedName("home_id")
  public String HomeId;

  /**
   * Goals scored by the home team.
   */
  @ColumnInfo(name = "home_score")
  @SerializedName("home_score")
  public long HomeScore;

  /**
   * Month of year match took place.
   */
  @ColumnInfo(name = "month")
  @SerializedName("month")
  public int Month;

  /**
   * Day of month match took place.
   */
  @ColumnInfo(name = "day")
  @SerializedName("day")
  public int Day;

  /**
   * Year match took place.
   */
  @ColumnInfo(name = "year")
  @SerializedName("year")
  public int Year;

  /**
   * Constructs a new MatchSummary object with default values.
   */
  public MatchSummaryEntity() {

    MatchDate = "";
    AwayId = BaseActivity.DEFAULT_ID;
    AwayScore = 0;
    HomeId = BaseActivity.DEFAULT_ID;
    HomeScore = 0;
    Id = BaseActivity.DEFAULT_ID;
    Month = 1;
    Day = 1;
    Year = 1990;
  }
}
