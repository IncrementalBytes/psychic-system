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

package net.frostedbytes.android.trendo.fragments;

import static net.frostedbytes.android.trendo.BaseActivity.BASE_TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.models.Team;
import net.frostedbytes.android.trendo.utils.LogUtils;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.utils.SortUtils;

public class UserPreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = BASE_TAG + UserPreferencesFragment.class.getSimpleName();

    public static final String KEY_SEASON_PREFERENCE = "preference_list_season";
    public static final String KEY_TEAM_PREFERENCE = "preference_list_team";

    public interface OnPreferencesListener {

        void onPreferenceChanged();
    }

    private OnPreferencesListener mCallback;

    private ArrayList<Team> mTeams;

    public static UserPreferencesFragment newInstance(ArrayList<Team> teams) {

        LogUtils.debug(TAG, "++newInstance(ArrayList<>)");
        UserPreferencesFragment fragment = new UserPreferencesFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(BaseActivity.ARG_TEAMS, teams);
        fragment.setArguments(args);
        return fragment;
    }

    /*
        Fragment Override(s)
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        LogUtils.debug(TAG, "++onAttach(Context)");
        try {
            mCallback = (OnPreferencesListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                String.format(Locale.ENGLISH, "Missing interface implementations for %s", context.toString()));
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            mTeams = arguments.getParcelableArrayList(BaseActivity.ARG_TEAMS);
            mTeams.sort(new SortUtils.ByTeamName());
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        LogUtils.debug(TAG, "++onCreatePreferences(Bundle, String)");
        addPreferencesFromResource(R.xml.app_preferences);

        List<String> entries = new ArrayList<>();
        List<String> entryValues = new ArrayList<>();
        for (Team team : mTeams) {
            entries.add(team.FullName);
            entryValues.add(team.Id);
        }

        final ListPreference listPreference = (ListPreference) findPreference(KEY_TEAM_PREFERENCE);
        listPreference.setEntries(entries.toArray(new String[0]));
        listPreference.setEntryValues(entryValues.toArray(new String[0]));
    }

    @Override
    public void onPause() {
        super.onPause();

        LogUtils.debug(TAG, "++onPause()");
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        LogUtils.debug(TAG, "++onResume()");
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String keyName) {

        LogUtils.debug(TAG, "++onSharedPreferenceChanged(SharedPreferences, String)");
        getPreferenceScreen().getSharedPreferences().edit().apply();
        if (keyName.equals(KEY_TEAM_PREFERENCE) || keyName.equals(KEY_SEASON_PREFERENCE)) {
            mCallback.onPreferenceChanged();
        } else {
            LogUtils.error(TAG, "Unknown key: ", keyName);
        }
    }
}
