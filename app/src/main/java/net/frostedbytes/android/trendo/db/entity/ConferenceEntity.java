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
package net.frostedbytes.android.trendo.db.entity;

import com.google.gson.annotations.SerializedName;

import net.frostedbytes.android.trendo.ui.BaseActivity;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "conference_table")
public class ConferenceEntity implements Serializable {

  @NonNull
  @PrimaryKey
  @ColumnInfo(name = "id")
  @SerializedName("id")
  public String Id;

  @ColumnInfo(name = "name")
  @SerializedName("name")
  public String Name;

  @ColumnInfo(name = "defunct")
  @SerializedName("defunct")
  public boolean Defunct;

  public ConferenceEntity() {

    Id = BaseActivity.DEFAULT_ID;
    Name = "";
    Defunct = false;
  }
}
