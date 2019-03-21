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

package net.frostedbytes.android.trendo.models;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import net.frostedbytes.android.trendo.BaseActivity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;

import static net.frostedbytes.android.trendo.BaseActivity.BASE_TAG;

@IgnoreExtraProperties
public class User implements Serializable {

    private static final String TAG = BASE_TAG + User.class.getSimpleName();

    @Exclude
    public static final String ROOT = "Users";

    @Exclude
    private boolean mIsBarChart;

    @Exclude
    private boolean mIsLineChart;

    /**
     * Unique identifier of team ahead of TeamId.
     */
    @Exclude
    public String AheadTeamId;

    /**
     * Unique identifier of team behind TeamId.
     */
    @Exclude
    public String BehindTeamId;

    /**
     * Email for the user object.
     */
    @Exclude
    public String Email;

    /**
     * Full name of the user object.
     */
    @Exclude
    public String FullName;
    /**
     * Unique identifier of user.
     */
    @Exclude
    public String Id;

    /**
     * Value indicating whether or not this user has commissioner rights.
     */
    public boolean IsCommissioner;

    /**
     * Unique identifier for team.
     */
    public String TeamId;

    /**
     * Year of results.
     */
    public int Season;

    /**
     * Constructs a new User object with default values.
     */
    @SuppressWarnings("unused")
    public User() {

        this.Email = "";
        this.FullName = "";
        this.mIsBarChart = false;
        this.mIsLineChart = true;
        this.AheadTeamId = BaseActivity.DEFAULT_ID;
        this.BehindTeamId = BaseActivity.DEFAULT_ID;
        this.Id = BaseActivity.DEFAULT_ID;
        this.IsCommissioner = false;
        this.TeamId = BaseActivity.DEFAULT_ID;
        this.Season = Calendar.getInstance().get(Calendar.YEAR);
    }

    @NonNull
    @Override
    public String toString() {

        return String.format(Locale.ENGLISH, "%s (%s) TeamId: %s Season: %d", this.FullName, this.Email, this.TeamId, this.Season);
    }

    public boolean getIsBarChart() {

        return this.mIsBarChart;
    }

    public boolean getIsLineChart() {

        return this.mIsLineChart;
    }

    public void setIsBarChart() {

        this.mIsBarChart = true;
        this.mIsLineChart = false;
    }

    public void setIsLineChart() {

        this.mIsBarChart = false;
        this.mIsLineChart = true;
    }
}
