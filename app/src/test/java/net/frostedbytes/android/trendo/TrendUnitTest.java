package net.frostedbytes.android.trendo;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import net.frostedbytes.android.trendo.models.Trend;
import org.junit.Test;

public class TrendUnitTest {

  @Test
  public void  constructorTestDefaults() {

    Trend testTrend = new Trend();
    assertEquals(testTrend.GoalsAgainst, new HashMap<>());
    assertEquals(testTrend.GoalDifferential, new HashMap<>());
    assertEquals(testTrend.GoalsFor, new HashMap<>());
    assertEquals(testTrend.PointsPerGame, new HashMap<>());
    assertEquals(testTrend.TotalPoints, new HashMap<>());
  }

  @Test
  public void toMapTest() {

    Trend testTrend = new Trend();
    Map<String, Object> mappedTrend = testTrend.toMap();
    assertEquals(mappedTrend.get("GoalsAgainst"), new HashMap<>());
    assertEquals(mappedTrend.get("GoalDifferential"), new HashMap<>());
    assertEquals(mappedTrend.get("GoalsFor"), new HashMap<>());
    assertEquals(mappedTrend.get("PointsPerGame"), new HashMap<>());
    assertEquals(mappedTrend.get("TotalPoints"), new HashMap<>());
  }
}
