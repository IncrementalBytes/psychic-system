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

package net.whollynugatory.android.trendo.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import java.util.ArrayList;
import java.util.List;

import net.whollynugatory.android.trendo.BuildConfig;
import net.whollynugatory.android.trendo.R;
import net.whollynugatory.android.trendo.ui.BaseActivity;
import net.whollynugatory.android.trendo.db.entity.TeamEntity;
import net.whollynugatory.android.trendo.utils.SortUtils;

public class UserPreferencesFragment extends PreferenceFragmentCompat {

  private static final String TAG = BaseActivity.BASE_TAG + "UserPreferencesFragment";

  private ArrayList<TeamEntity> mTeams;

  public static UserPreferencesFragment newInstance(ArrayList<TeamEntity> teamEntities) {

    Log.d(TAG, "++newInstance()");
    UserPreferencesFragment fragment = new UserPreferencesFragment();
    Bundle arguments = new Bundle();
    arguments.putSerializable(BaseActivity.ARG_TEAMS, teamEntities);
    fragment.setArguments(arguments);
    return fragment;
  }

  /*
      Fragment Override(s)
   */
  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);

    Log.d(TAG, "++onAttach(Context)");
    Bundle arguments = getArguments();
    if (arguments != null && arguments.containsKey(BaseActivity.ARG_TEAMS)) {
      mTeams = (ArrayList<TeamEntity>)arguments.getSerializable(BaseActivity.ARG_TEAMS);
    } else {
      mTeams = new ArrayList<>();
    }
  }

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    Log.d(TAG, "++onCreatePreferences(Bundle, String)");
    addPreferencesFromResource(R.xml.app_preferences);
    setupAppVersionPreference();
    setupAvailableTeamsPreference();
    setupSeasonsPreference();
  }

  /*
    Private Method(s)
   */
  private void setupAppVersionPreference() {

    Log.d(TAG, "++setupAppVersionPreference()");
    EditTextPreference editTextPreference = findPreference(getString(R.string.pref_key_app_version));
    if (editTextPreference != null) {
      editTextPreference.setSummary(BuildConfig.VERSION_NAME);
    }
  }

  private void setupAvailableTeamsPreference() {

    Log.d(TAG, "++setupAvailableTeamsPreference()");
    ListPreference teamsPreference = findPreference(getString(R.string.pref_key_team));
    if (teamsPreference == null) {
      Log.e(TAG, "Could not find the team list preference object.");
      return;
    }

    List<String> entries = new ArrayList<>();
    List<String> entryValues = new ArrayList<>();
    if (mTeams != null && mTeams.size() > 0) {
      mTeams.sort(new SortUtils.ByTeamName());
      for (TeamEntity team : mTeams) {
        entries.add(team.Name);
        entryValues.add(team.Id);
      }
    }

    teamsPreference.setEntries(entries.toArray(new String[0]));
    teamsPreference.setEntryValues(entryValues.toArray(new String[0]));
  }

  private void setupSeasonsPreference() {

    Log.d(TAG, "++setupSeasonsPreference()");
    SeekBarPreference seasonsPreference = findPreference(getString(R.string.pref_key_season));
    if (seasonsPreference == null) {
      Log.e(TAG, "Could not find the seasons preference object.");
      return;
    }

    String[] seasons = getResources().getStringArray(R.array.seasons);
    seasonsPreference.setMin(Integer.parseInt(seasons[0]));
    seasonsPreference.setMax(Integer.parseInt(seasons[seasons.length - 1]));
    seasonsPreference.setDefaultValue(Integer.parseInt(seasons[seasons.length - 1]));
  }
}
