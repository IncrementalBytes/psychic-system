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
import net.whollynugatory.android.trendo.db.entity.MatchSummaryEntity;
import net.whollynugatory.android.trendo.db.entity.TeamEntity;
import net.whollynugatory.android.trendo.db.entity.TrendEntity;
import net.whollynugatory.android.trendo.db.repository.ConferenceRepository;
import net.whollynugatory.android.trendo.db.repository.MatchSummaryDetailsRepository;
import net.whollynugatory.android.trendo.db.repository.MatchSummaryRepository;
import net.whollynugatory.android.trendo.db.repository.TeamRepository;
import net.whollynugatory.android.trendo.db.repository.TrendDetailsRepository;
import net.whollynugatory.android.trendo.db.repository.TrendRepository;
import net.whollynugatory.android.trendo.db.views.MatchSummaryDetails;
import net.whollynugatory.android.trendo.db.views.TrendDetails;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class TrendoViewModel extends AndroidViewModel {

  private ConferenceRepository mConferenceRepository;
  private MatchSummaryDetailsRepository mMatchSummaryDetailsRepository;
  private MatchSummaryRepository mMatchSummaryRepository;
  private TeamRepository mTeamRepository;
  private TrendDetailsRepository mTrendDetailsRepository;
  private TrendRepository mTrendRepository;

  public TrendoViewModel(Application application) {
    super(application);

    mConferenceRepository = ConferenceRepository.getInstance(TrendoDatabase.getInstance(application).conferenceDao());
    mMatchSummaryDetailsRepository = MatchSummaryDetailsRepository.getInstance(TrendoDatabase.getInstance(application).matchSummaryDetailsDao());
    mMatchSummaryRepository = MatchSummaryRepository.getInstance(TrendoDatabase.getInstance(application).matchSummaryDao());
    mTeamRepository = TeamRepository.getInstance(TrendoDatabase.getInstance(application).teamDao());
    mTrendDetailsRepository = TrendDetailsRepository.getInstance(TrendoDatabase.getInstance(application).trendDetailsDao());
    mTrendRepository = TrendRepository.getInstance(TrendoDatabase.getInstance(application).trendDao());
  }

  public int countConferences() {

    return mConferenceRepository.count();
  }

  public LiveData<List<ConferenceEntity>> getAllConferences() {

    return mConferenceRepository.getAll();
  }

  public void insertConference(ConferenceEntity conference) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mConferenceRepository.insert(conference));
  }

  public void insertAllConferences(List<ConferenceEntity> conferences) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mConferenceRepository.insertAll(conferences));
  }

  public LiveData<List<MatchSummaryDetails>> getAllMatchSummaryDetails(String bySeason) {

    return mMatchSummaryDetailsRepository.getAll(bySeason);
  }

  public LiveData<List<MatchSummaryDetails>> getAllMatchSummaryDetails(String teamId, String bySeason) {

    return mMatchSummaryDetailsRepository.getAll(teamId, bySeason);
  }

  public void insertMatchSummary(MatchSummaryEntity matchSummary) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mMatchSummaryRepository.insert(matchSummary));
  }

  public void insertAllMatchSummaries(List<MatchSummaryEntity> matchSummaries) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mMatchSummaryRepository.insertAll(matchSummaries));
  }

  public int countTeams() {

    return mTeamRepository.count();
  }

  public LiveData<List<TeamEntity>> getAllTeams() {

    return mTeamRepository.getAll();
  }

  public void insertTeam(TeamEntity team) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mTeamRepository.insert(team));
  }

  public void insertAllTeams(List<TeamEntity> teams) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mTeamRepository.insertAll(teams));
  }

  public LiveData<List<TrendDetails>> getAllTrends(String teamId, int year) {

    return mTrendDetailsRepository.getAll(teamId, year);
  }

  public int countTrends(int year) {

    return mTrendRepository.count(year);
  }

  public int countTrends(String teamId, int year) {

    return mTrendRepository.count(teamId, year);
  }

  public void insert(TrendEntity trendEntity) {

    TrendoDatabase.databaseWriteExecutor.execute(() -> mTrendRepository.insert(trendEntity));
  }
}
