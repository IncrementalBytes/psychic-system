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

package net.whollynugatory.android.trendo.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.whollynugatory.android.trendo.BuildConfig;
import net.whollynugatory.android.trendo.R;
import net.whollynugatory.android.trendo.ui.BaseActivity;
import net.whollynugatory.android.trendo.db.entity.TeamEntity;
import net.whollynugatory.android.trendo.utils.PreferenceUtils;
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
  @SuppressWarnings("unchecked")
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

    Map<String, String> teamEntries = new HashMap<>();
    if (mTeams != null && mTeams.size() > 0) {
      mTeams.sort(new SortUtils.ByTeamName());
      for (TeamEntity team : mTeams) {
        teamEntries.put(team.Id, team.Name);
      }
    }

    String team = PreferenceUtils.getTeam(getContext());
    if (team != null && !team.equals(BaseActivity.DEFAULT_ID) && team.length() == BaseActivity.DEFAULT_ID.length()) {
      teamsPreference.setSummary(teamEntries.get(team));
    }

    teamsPreference.setEntries(teamEntries.values().toArray(new String[0]));
    teamsPreference.setEntryValues(teamEntries.keySet().toArray(new String[0]));
    teamsPreference.setOnPreferenceChangeListener(
      (preference, newValue) -> {
        String newTeamValue = (String) newValue;
        teamsPreference.setSummary(teamEntries.get(newTeamValue));
        PreferenceUtils.saveStringPreference(getActivity(), R.string.pref_key_team, newTeamValue);
        return true;
      });
  }

  private void setupSeasonsPreference() {

    Log.d(TAG, "++setupSeasonsPreference()");
    ListPreference seasonsPreference = findPreference(getString(R.string.pref_key_season));
    if (seasonsPreference == null) {
      Log.e(TAG, "Could not find the seasons preference object.");
      return;
    }

    int season = PreferenceUtils.getSeason(getContext());
    if (season > 0) {
      seasonsPreference.setSummary(String.valueOf(season));
    }

    String[] seasons = getResources().getStringArray(R.array.seasons);
    seasonsPreference.setEntries(seasons);
    seasonsPreference.setEntryValues(seasons);
    seasonsPreference.setOnPreferenceChangeListener(
      (preference, newValue) -> {
        String newSeasonValue = (String) newValue;
        seasonsPreference.setSummary(newSeasonValue);
        PreferenceUtils.saveStringPreference(getActivity(), R.string.pref_key_season, newSeasonValue);
        return true;
      });
  }
}
