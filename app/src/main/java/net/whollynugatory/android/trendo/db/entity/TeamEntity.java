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

import android.util.Log;

import net.whollynugatory.android.trendo.ui.BaseActivity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

@Entity(
  tableName = "team_table",
  foreignKeys = {@ForeignKey(entity = ConferenceEntity.class, parentColumns = "id", childColumns = "conference_id")},
  indices = {@Index(value = {"conference_id"})})
public class TeamEntity implements Serializable {

  private static final String TAG = BaseActivity.BASE_TAG + "Team";

  @NonNull
  @PrimaryKey
  @ColumnInfo(name = "id")
  @SerializedName("id")
  public String Id;

  @ColumnInfo(name = "conference_id")
  @SerializedName("conference_id")
  public String ConferenceId;

  @NonNull
  @ColumnInfo(name = "name")
  @SerializedName("name")
  public String Name;

  @NonNull
  @ColumnInfo(name = "short_name")
  @SerializedName("short_name")
  public String ShortName;

  @ColumnInfo(name = "established")
  @SerializedName("established")
  public int Established;

  @ColumnInfo(name = "defunct")
  public boolean Defunct;

  @Ignore
  public long GoalDifferential;

  @Ignore
  public long GoalsScored;

  @Ignore
  public int TablePosition;

  @Ignore
  public long TotalPoints;

  @Ignore
  public long TotalWins;

  public TeamEntity() {

    Id = BaseActivity.DEFAULT_ID;
    ConferenceId = BaseActivity.DEFAULT_ID;
    Name = "";
    ShortName = "";
    Established = -1;
    Defunct = false;
    GoalDifferential = 0;
    GoalsScored = 0;
    TablePosition = -1;
    TotalPoints = -1;
    TotalWins = 0;
  }

  /**
   * Compares this Team with another Team.
   *
   * @param compareTo Team to compare this Team against
   * @return TRUE if this Team equals the other Team, otherwise FALSE
   * @throws ClassCastException if object parameter cannot be cast into Team object
   */
  @Override
  public boolean equals(Object compareTo) throws ClassCastException {

    if (compareTo == null) {
      return false;
    }

    if (this == compareTo) {
      return true;
    }

    //cast to native object is now safe
    if ((compareTo instanceof TeamEntity)) {
      try {
        TeamEntity compareToTeam = (TeamEntity) compareTo;
        if (Id.equals(compareToTeam.Id) &&
          ConferenceId.equals(compareToTeam.ConferenceId) &&
          Name.equals(compareToTeam.Name) &&
          ShortName.equals(compareToTeam.ShortName) &&
          Established == compareToTeam.Established &&
          Defunct == compareToTeam.Defunct) {
          return true;
        }
      } catch (ClassCastException cce) {
        Log.e(TAG, "Could not cast object to Team class.", cce);
      }
    }

    return false;
  }
}
