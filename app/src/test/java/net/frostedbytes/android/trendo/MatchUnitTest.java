package net.frostedbytes.android.trendo;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.UUID;
import org.junit.Test;

public class MatchUnitTest {

  @Test
  public void constructor_idIsSet() {
    UUID testUUID = UUID.randomUUID();
    Match newMatch = new Match(testUUID);
    assertEquals(newMatch.getId(), testUUID);
  }

  @Test
  public void  constructor_defaults() {
    UUID testUUID = UUID.randomUUID();
    Match newMatch = new Match(testUUID);
    assertNotNull(newMatch.getAwayId());
    assertEquals(newMatch.getAwayScore(), 0);
    assertNotNull(newMatch.getHomeId());
    assertEquals(newMatch.getHomeScore(), 0);
    assertEquals(newMatch.getIsMatchFinal(), false);
    assertEquals(Match.formatDateForDisplay(newMatch.getMatchDate()), Match.formatDateForDisplay(new Date()));
  }

  @Test
  public void match_isNotEqual() {
    UUID testUUID = UUID.randomUUID();
    Match match1 = new Match(testUUID);
    UUID secondUUID = UUID.randomUUID();
    Match match2 = new Match(secondUUID);
    assertEquals(match1.equals(match2), false);
  }
}
