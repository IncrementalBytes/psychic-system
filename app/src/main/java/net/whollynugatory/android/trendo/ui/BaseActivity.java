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

package net.whollynugatory.android.trendo.ui;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    public static final String BASE_TAG = "Trendo::";

    public static final String ARG_BROKERAGE_TYPE = "brokerage_type";
    public static final String ARG_TEAMS = "teams";
    public static final String ARG_TREND = "trend";
    public static final String ARG_TRENDS = "trends";
    public static final String ARG_TRENDS_AHEAD = "ahead";
    public static final String ARG_TRENDS_BEHIND = "behind";
    public static final String ARG_UID = "user_id";

    public static final String DEFAULT_DATA_FILE = "ConferencesAndTeams.json";
    public static final String DEFAULT_DATE = "00000101";
    public static final String DEFAULT_ID = "00000000-0000-0000-0000-000000000000";
    public static final String DEFAULT_UID = "0000000000000000000000000000";

    public static final String DATABASE_NAME = "trendo-db.sqlite";
}
