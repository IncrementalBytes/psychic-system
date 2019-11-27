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
package net.whollynugatory.android.trendo;

import net.whollynugatory.android.trendo.db.entity.TrendEntity;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

public class TrendUnitTest {

  @Test
  public void  constructorTestDefaults() {

    TrendEntity testTrend = new TrendEntity();
    assertEquals(testTrend.GoalsAgainst, new HashMap<>());
    assertEquals(testTrend.GoalDifferential, new HashMap<>());
    assertEquals(testTrend.GoalsFor, new HashMap<>());
    assertEquals(testTrend.PointsPerGame, new HashMap<>());
    assertEquals(testTrend.TotalPoints, new HashMap<>());
  }
}
