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

package net.whollynugatory.android.trendo.db.dao;

import net.whollynugatory.android.trendo.db.entity.TrendEntity;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface TrendDao {

  @Query("SELECT COUNT(*) FROM trend_table WHERE team_id == :teamId AND year == :year")
  int count(String teamId, int year);

  @Query("SELECT COUNT(*) FROM trend_table WHERE year == :year")
  int count(int year);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(TrendEntity trend);
}
