package net.frostedbytes.android.trendo;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import net.frostedbytes.android.trendo.models.MatchSummary;
import org.junit.Test;

public class MatchSummaryUnitTest {

  @Test
  public void constructorTestDefaults() {

    MatchSummary testSummary = new MatchSummary();
    assertEquals(testSummary.AwayScore, 0);
    assertEquals(testSummary.HomeScore, 0);
    assertEquals(testSummary.MatchId, BaseActivity.DEFAULT_ID);
    assertEquals(testSummary.IsFinal, false);
    Calendar calendar = Calendar.getInstance();
    long matchDate = new GregorianCalendar(
      calendar.get(Calendar.YEAR),
      calendar.get(Calendar.MONTH),
      calendar.get(Calendar.DAY_OF_MONTH),
      0,
      0,
      0).getTimeInMillis();
    assertEquals(testSummary.MatchDate, matchDate);
    assertEquals(testSummary.HomeTeamName, "");
    assertEquals(testSummary.AwayTeamName, "");
  }

  @Test
  public void toMapTest() {

    MatchSummary testSummary = new MatchSummary();
    Map<String, Object> mappedSummary = testSummary.toMap();
  }
}
