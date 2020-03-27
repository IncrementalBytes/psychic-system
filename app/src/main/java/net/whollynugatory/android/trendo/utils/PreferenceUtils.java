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

package net.whollynugatory.android.trendo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.whollynugatory.android.trendo.R;
import net.whollynugatory.android.trendo.ui.BaseActivity;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

/**
 *  Utility class to retrieve shared preferences.
 **/
public class PreferenceUtils {

  public static int getSeason(Context context) {

    String season = getStringPref(context, R.string.pref_key_season, String.valueOf(BaseActivity.DEFAULT_SEASON));
    return Integer.parseInt(season);
  }

  public static String getTeam(Context context) {

    return getStringPref(context, R.string.pref_key_team, BaseActivity.DEFAULT_ID);
  }

  /*
    Private Method(s)
   */
  private static String getStringPref(Context context, @StringRes int prefKeyId, String defaultValue) {

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    String prefKey = context.getString(prefKeyId);
    return sharedPreferences.getString(prefKey, defaultValue);
  }

  public static void saveStringPreference(

    Context context, @StringRes int prefKeyId, @Nullable String value) {
    PreferenceManager.getDefaultSharedPreferences(context)
      .edit()
      .putString(context.getString(prefKeyId), value)
      .apply();
  }
}
