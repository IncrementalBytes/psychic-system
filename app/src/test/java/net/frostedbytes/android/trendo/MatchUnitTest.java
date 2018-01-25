package net.frostedbytes.android.trendo;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.UUID;
import net.frostedbytes.android.trendo.models.Match;
import org.junit.Test;

public class MatchUnitTest {

  @Test
  public void  constructor_Defaults() {

    Match testMatch = new Match();
    assertEquals(testMatch.AwayTeamShortName, "");
    assertEquals(testMatch.HomeTeamShortName, "");
    assertEquals(testMatch.Id, BaseActivity.DEFAULT_ID);
    assertEquals(testMatch.IsFinal, false);
    Calendar calendar = Calendar.getInstance();
    long matchDate = new GregorianCalendar(
      calendar.get(Calendar.YEAR),
      calendar.get(Calendar.MONTH),
      calendar.get(Calendar.DAY_OF_MONTH),
      0,
      0,
      0).getTimeInMillis();
    assertEquals(testMatch.MatchDate, matchDate);
    assertEquals(testMatch.MatchEvents.isEmpty(), true);
  }

  @Test
  public void constructor_WithParameters() {
    String homeTeam = UUID.randomUUID().toString();
    String awayTeam = UUID.randomUUID().toString();
    String id = UUID.randomUUID().toString();
    boolean isFinal = true;
    Calendar calendar = Calendar.getInstance();
    long matchDate = new GregorianCalendar(
      calendar.get(Calendar.YEAR),
      calendar.get(Calendar.MONTH),
      calendar.get(Calendar.DAY_OF_MONTH),
      0,
      0,
      0).getTimeInMillis();

    Match testMatch = new Match(homeTeam, awayTeam, id, isFinal, matchDate, new HashMap<String, Object>());
    assertEquals(testMatch.AwayTeamShortName, awayTeam);
    assertEquals(testMatch.HomeTeamShortName, homeTeam);
    assertEquals(testMatch.Id, id);
    assertEquals(testMatch.IsFinal, isFinal);
    assertEquals(testMatch.MatchDate, matchDate);
    assertEquals(testMatch.MatchEvents.isEmpty(), true);
  }

  @Test
  public void test_FormatDateForDisplay() {

    int year = 1999;
    int month = 11; // month is 0 based index
    int day = 31;
    long matchDate = new GregorianCalendar(
      year,
      month,
      day,
      0,
      0,
      0).getTimeInMillis();
    String formattedDate = Match.formatDateForDisplay(matchDate);
    assertEquals(formattedDate, "Dec 31, 1999");
  }
}
