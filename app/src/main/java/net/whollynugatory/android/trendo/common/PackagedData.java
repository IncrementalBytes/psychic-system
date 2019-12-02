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
package net.whollynugatory.android.trendo.common;

import com.google.gson.annotations.SerializedName;

import net.whollynugatory.android.trendo.db.entity.ConferenceEntity;
import net.whollynugatory.android.trendo.db.entity.MatchSummaryEntity;
import net.whollynugatory.android.trendo.db.entity.TeamEntity;
import net.whollynugatory.android.trendo.db.views.MatchSummaryDetail;
import net.whollynugatory.android.trendo.db.views.TrendDetails;

import java.util.ArrayList;

public class PackagedData {

  @SerializedName("conferences")
  public ArrayList<ConferenceEntity> Conferences;

  public ArrayList<MatchSummaryDetail> MatchDetails;

  @SerializedName("match_summaries")
  public ArrayList<MatchSummaryEntity> MatchSummaries;

  @SerializedName("teams")
  public ArrayList<TeamEntity> Teams;

  public ArrayList<TrendDetails> Trends;

  public ArrayList<TrendDetails> TrendsAhead;

  public ArrayList<TrendDetails> TrendsBehind;

  public PackagedData() {

    Conferences = new ArrayList<>();
    MatchDetails = new ArrayList<>();
    MatchSummaries = new ArrayList<>();
    Teams = new ArrayList<>();
    Trends = new ArrayList<>();
    TrendsAhead = new ArrayList<>();
    TrendsBehind = new ArrayList<>();
  }
}
