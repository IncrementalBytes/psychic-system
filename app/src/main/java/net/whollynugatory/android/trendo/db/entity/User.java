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

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import net.whollynugatory.android.trendo.ui.BaseActivity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;

@IgnoreExtraProperties
public class User implements Serializable {

  @Exclude
  public static final String ROOT = "Users";

  @Exclude
  private boolean mIsBarChart;

  @Exclude
  private boolean mIsLineChart;

  /**
   * Unique identifier of team ahead of TeamId.
   */
  @Exclude
  public String AheadTeamId;

  /**
   * Unique identifier of team behind TeamId.
   */
  @Exclude
  public String BehindTeamId;

  /**
   * Unique identifier of user.
   */
  @Exclude
  public String Uid;

  /**
   * Unique identifier for team.
   */
  public String TeamId;

  /**
   * Year of results.
   */
  public int Year;

  /**
   * Constructs a new User object with default values.
   */
  @SuppressWarnings("unused")
  public User() {

    mIsBarChart = false;
    mIsLineChart = true;
    AheadTeamId = BaseActivity.DEFAULT_ID;
    BehindTeamId = BaseActivity.DEFAULT_ID;
    Uid = BaseActivity.DEFAULT_UID;
    TeamId = BaseActivity.DEFAULT_ID;
    Year = Calendar.getInstance().get(Calendar.YEAR);
  }

  @NonNull
  @Override
  public String toString() {

    return String.format(
      Locale.ENGLISH,
      "{Uid:%s, TeamId:%s, Year:%d}",
      Uid,
      TeamId,
      Year);
  }
}
