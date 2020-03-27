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

package net.whollynugatory.android.trendo.db.viewmodel;

import android.app.Application;

import net.whollynugatory.android.trendo.db.TrendoDatabase;
import net.whollynugatory.android.trendo.db.entity.ConferenceEntity;
import net.whollynugatory.android.trendo.db.entity.TeamEntity;
import net.whollynugatory.android.trendo.db.repository.ConferenceRepository;
import net.whollynugatory.android.trendo.db.repository.MatchSummaryDetailsRepository;
import net.whollynugatory.android.trendo.db.repository.TeamRepository;
import net.whollynugatory.android.trendo.db.repository.TrendDetailsRepository;
import net.whollynugatory.android.trendo.db.views.MatchSummaryDetails;
import net.whollynugatory.android.trendo.db.views.TrendDetails;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class TrendoViewModel extends AndroidViewModel {

  private ConferenceRepository mConferenceRepository;
  private MatchSummaryDetailsRepository mMatchSummaryDetailsRepository;
  private TeamRepository mTeamRepository;
  private TrendDetailsRepository mTrendDetailsRepository;

  public TrendoViewModel(Application application) {
    super(application);

    mConferenceRepository = ConferenceRepository.getInstance(TrendoDatabase.getInstance(application).conferenceDao());
    mMatchSummaryDetailsRepository = MatchSummaryDetailsRepository.getInstance(TrendoDatabase.getInstance(application).matchSummaryDetailsDao());
    mTeamRepository = TeamRepository.getInstance(TrendoDatabase.getInstance(application).teamDao());
    mTrendDetailsRepository = TrendDetailsRepository.getInstance(TrendoDatabase.getInstance(application).trendDetailsDao());
  }

  public LiveData<List<ConferenceEntity>> getAllConferences() {

    return mConferenceRepository.getAll();
  }

  public LiveData<List<MatchSummaryDetails>> getAllMatchSummaryDetails(int season) {

    return mMatchSummaryDetailsRepository.getAll(season);
  }

  public LiveData<List<MatchSummaryDetails>> getAllMatchSummaryDetails(String teamId, int season) {

    return mMatchSummaryDetailsRepository.getAll(teamId, season);
  }

  public LiveData<List<TeamEntity>> getAllTeams() {

    return mTeamRepository.getAll();
  }

  public LiveData<List<TrendDetails>> getAllTrends(String teamId, int year) {

    return mTrendDetailsRepository.getAll(teamId, year);
  }
}
