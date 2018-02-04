package net.frostedbytes.android.trendo;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import net.frostedbytes.android.trendo.models.Trend;
import org.junit.Test;

public class TrendUnitTest {

  @Test
  public void  constructorTestDefaults() {

    Trend testTrend = new Trend();
//    assertArrayEquals(testTrend.GoalsAgainst, new ArrayList<>());
//    assertArrayEquals(testTrend.GoalDifferential, new ArrayList<>());
//    assertArrayEquals(testTrend.GoalsFor, new ArrayList<>());
    Calendar calendar = Calendar.getInstance();
    long matchDate = new GregorianCalendar(
      calendar.get(Calendar.YEAR),
      calendar.get(Calendar.MONTH),
      calendar.get(Calendar.DAY_OF_MONTH),
      0,
      0,
      0).getTimeInMillis();
    assertEquals(testTrend.MatchDate, matchDate);
    assertEquals(testTrend.MatchId, BaseActivity.DEFAULT_ID);
//    assertArrayEquals(testTrend.PointsPerGame, new ArrayList<>());
    assertEquals(testTrend.TeamName, "");
//    assertArrayEquals(testTrend.TotalPoints, new ArrayList<>());
  }

  @Test
  public void toMapTest() {

    Trend testTrend = new Trend();
    Map<String, Object> mappedTrend = testTrend.toMap();

  }
}
