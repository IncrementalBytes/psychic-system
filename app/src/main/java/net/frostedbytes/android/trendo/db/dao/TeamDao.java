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
package net.frostedbytes.android.trendo.db.dao;

import net.frostedbytes.android.trendo.db.entity.TeamEntity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface TeamDao {

  @Query("SELECT COUNT(*) FROM team_table")
  int count();

  @Query("SELECT * FROM team_table")
  List<TeamEntity> getAll();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(TeamEntity team);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(List<TeamEntity> teams);
}
