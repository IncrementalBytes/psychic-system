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

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
import net.whollynugatory.android.trendo.db.entity.MatchSummaryEntity;

@Dao
public interface MatchSummaryDao {

  @Query("SELECT COUNT(*) FROM MatchSummaryDetails WHERE Year == :season")
  int count(int season);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(MatchSummaryEntity matchSummary);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(List<MatchSummaryEntity> matchSummaries);
}
