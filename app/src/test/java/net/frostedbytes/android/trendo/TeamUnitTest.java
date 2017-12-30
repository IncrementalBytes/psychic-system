package net.frostedbytes.android.trendo;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.util.UUID;
import org.junit.Test;

public class TeamUnitTest {

  @Test
  public void constructor_idIsSet() {
    UUID testUUID = UUID.randomUUID();
    Team newTeam = new Team(testUUID);
    assertEquals(newTeam.getId(), testUUID);
  }

  @Test
  public void constructor_defaults() {
    UUID testUUID = UUID.randomUUID();
    Team newTeam = new Team(testUUID);
    assertNotNull(newTeam.getConferenceId());
    assertEquals(newTeam.getIsDefunct(), false);
    assertEquals(newTeam.getFullName(), "");
    assertNotNull(newTeam.getParentId());
    assertEquals(newTeam.getShortName(), "");
  }

  @Test
  public void team_isNotEqual() {
    UUID firstUUID = UUID.randomUUID();
    Team team1 = new Team(firstUUID);
    UUID secondUUID = UUID.randomUUID();
    Team team2 = new Team(secondUUID);
    assertEquals(team1.equals(team2), false);
  }
}
