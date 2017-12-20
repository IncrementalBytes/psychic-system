package net.frostedbytes.android.trendo;

import java.util.UUID;
import org.junit.Test;

import static org.junit.Assert.*;

public class MatchUnitTest {

  @Test
  public void constructor_idIsSet() {
    UUID testUUID = UUID.randomUUID();
    Match newMatch = new Match(testUUID);
    assertEquals(newMatch.getId(), testUUID);
  }
}
