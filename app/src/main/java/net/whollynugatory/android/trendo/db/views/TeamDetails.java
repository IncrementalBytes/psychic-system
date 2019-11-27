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
  "SELECT TeamTable.id AS Id, " +
    "TeamTable.name AS FullName, " +
    "TeamTable.short_name AS ShortName, " +
    "ConferenceTable.name AS ConferenceName " +
    "FROM team_table AS TeamTable " +
    "INNER JOIN conference_table AS ConferenceTable ON TeamTable.conference_id = ConferenceTable.id")
public class TeamDetails implements Serializable {

  public String Id;
  public String FullName;
  public String ShortName;
  public String ConferenceName;

  public TeamDetails() {

    Id = BaseActivity.DEFAULT_ID;
    FullName = "";
    ShortName = "";
    ConferenceName = "";
  }
}
