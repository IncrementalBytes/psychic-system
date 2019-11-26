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
package net.frostedbytes.android.trendo.viewmodel;

import android.app.Application;
import android.util.Log;

import net.frostedbytes.android.trendo.db.TrendoDatabase;
import net.frostedbytes.android.trendo.ui.BaseActivity;
import net.frostedbytes.android.trendo.db.TrendoRepository;
import net.frostedbytes.android.trendo.db.entity.ConferenceEntity;
import net.frostedbytes.android.trendo.db.entity.TeamEntity;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class TrendoViewModel extends AndroidViewModel {

  private static final String TAG = BaseActivity.BASE_TAG + "TrendoViewModel";

  private final TrendoRepository mRepository;

  private List<ConferenceEntity> mAllConferences;
  private List<TeamEntity> mAllTeams;

  public TrendoViewModel(Application application) {
    super(application);

    Log.d(TAG, "++TrendoViewModel(Application)");
    mRepository = TrendoRepository.getInstance(TrendoDatabase.getInstance(application));

    mAllConferences = mRepository.getAllConferences();
    mAllTeams = mRepository.getAllTeams();
  }

  /*
    Conference
   */
  public List<ConferenceEntity> getAllConferences() {

    return mAllConferences;
  }

  /*
    MatchSummary
   */

  /*
    Team
   */
  public List<TeamEntity> getAllTeams() {

    return mAllTeams;
  }

  /*
    Trend
   */

}
