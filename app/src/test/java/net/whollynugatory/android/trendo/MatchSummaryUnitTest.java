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

import static org.junit.Assert.assertEquals;

import net.whollynugatory.android.trendo.db.entity.MatchSummaryEntity;
import net.whollynugatory.android.trendo.ui.BaseActivity;

import org.junit.Test;

public class MatchSummaryUnitTest {

  @Test
  public void constructorTestDefaults() {

    MatchSummaryEntity testSummary = new MatchSummaryEntity();
    assertEquals(testSummary.AwayScore, 0);
    assertEquals(testSummary.HomeScore, 0);
    assertEquals(testSummary.MatchDate, BaseActivity.DEFAULT_DATE);
    assertEquals(testSummary.HomeId, "");
    assertEquals(testSummary.AwayId, "");
  }
}
