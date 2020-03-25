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
import androidx.room.PrimaryKey;

@Entity(tableName = "matchdatedim")
public class MatchDateDim implements Serializable {

  @NonNull
  @PrimaryKey
  @ColumnInfo(name = "id")
  @SerializedName("id")
  public String Id;

  @ColumnInfo(name = "day")
  @SerializedName("day")
  public int Day;

  @ColumnInfo(name = "month")
  @SerializedName("month")
  public int Month;

  @ColumnInfo(name = "year")
  @SerializedName("year")
  public int Year;

  public MatchDateDim() {

    Id = BaseActivity.DEFAULT_DATE;
    Day = 1;
    Month = 1;
    Year = 0;
  }

  public static MatchDateDim generate(String matchDate) {

    MatchDateDim matchDateDim = new MatchDateDim();
    matchDateDim.Id = matchDate;
    matchDateDim.Day = Integer.parseInt(matchDate.substring(0, 2));
    matchDateDim.Month = Integer.parseInt(matchDate.substring(2, 4));
    matchDateDim.Year = Integer.parseInt(matchDate.substring(4));
    return matchDateDim;
  }
}
