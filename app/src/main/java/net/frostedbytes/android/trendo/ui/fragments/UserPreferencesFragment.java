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

package net.frostedbytes.android.trendo.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.frostedbytes.android.trendo.ui.BaseActivity;
import net.frostedbytes.android.trendo.BuildConfig;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.db.entity.TeamEntity;
import net.frostedbytes.android.trendo.utils.SortUtils;

public class UserPreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = BaseActivity.BASE_TAG + "UserPreferencesFragment";

    public static final String KEY_YEAR_PREFERENCE = "preference_list_year";
    public static final String KEY_TEAM_PREFERENCE = "preference_list_team";
    private static final String KEY_VERSION_PREFERENCE = "preference_edit_version";

    public interface OnPreferencesListener {

        void onPreferenceChanged();
    }

    private OnPreferencesListener mCallback;

    private ArrayList<TeamEntity> mTeams;

    public static UserPreferencesFragment newInstance(ArrayList<TeamEntity> teamList) {

        Log.d(TAG, "++newInstance()");
        UserPreferencesFragment fragment = new UserPreferencesFragment();
        Bundle args = new Bundle();
        args.putSerializable(BaseActivity.ARG_TEAMS, teamList);
        fragment.setArguments(args);
        return fragment;
    }

    /*
        Fragment Override(s)
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.d(TAG, "++onAttach(Context)");
        try {
            mCallback = (OnPreferencesListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                String.format(Locale.ENGLISH, "Missing interface implementations for %s", context.toString()));
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            mTeams = (ArrayList<TeamEntity>)arguments.getSerializable(BaseActivity.ARG_TEAMS);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        Log.d(TAG, "++onCreatePreferences(Bundle, String)");
        addPreferencesFromResource(R.xml.app_preferences);

        EditTextPreference editTextPreference = findPreference(KEY_VERSION_PREFERENCE);
        if (editTextPreference != null) {
            editTextPreference.setSummary(BuildConfig.VERSION_NAME);
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

        final ListPreference listPreference = findPreference(KEY_TEAM_PREFERENCE);
        if (listPreference != null) {
            listPreference.setEntries(entries.toArray(new String[0]));
            listPreference.setEntryValues(entryValues.toArray(new String[0]));
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "++onPause()");
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "++onResume()");
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String keyName) {

        Log.d(TAG, "++onSharedPreferenceChanged(SharedPreferences, String)");
        getPreferenceScreen().getSharedPreferences().edit().apply();
        mCallback.onPreferenceChanged();
    }
}
