package net.frostedbytes.android.trendo;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.UUID;
import net.frostedbytes.android.trendo.models.Match;
import org.junit.Test;

public class MatchUnitTest {

  @Test
  public void constructor_idIsSet() {

    UUID testUUID = UUID.randomUUID();
    Match newMatch = new Match();
    assertNotEquals(newMatch.Id, testUUID);
  }

  @Test
  public void  constructor_defaults() {

    Match newMatch = new Match();
    assertNotNull(newMatch.AwayId);
    assertNotNull(newMatch.HomeId);
    assertEquals(newMatch.IsFinal, false);
    assertEquals(newMatch.MatchDate, 0);
  }

  @Test
  public void match_isNotEqual() {

    Match match1 = new Match();
    Match match2 = new Match();
    assertEquals(match1.equals(match2), false);
  }
}
