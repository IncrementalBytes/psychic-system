package net.frostedbytes.android.trendo;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.UUID;
import net.frostedbytes.android.trendo.models.Team;
import org.junit.Test;

public class TeamUnitTest {

  @Test
  public void constructor_idIsSet() {

    UUID testUUID = UUID.randomUUID();
    Team newTeam = new Team();
    assertNotEquals(newTeam.Id, testUUID);
  }

  @Test
  public void constructor_defaults() {

    Team newTeam = new Team();
    assertNotNull(newTeam.ConferenceId);
    assertEquals(newTeam.IsDefunct, false);
    assertEquals(newTeam.FullName, "");
    assertNotNull(newTeam.ParentId);
    assertEquals(newTeam.ShortName, "");
  }

  @Test
  public void team_isNotEqual() {

    Team team1 = new Team();
    Team team2 = new Team();
    assertEquals(team1.equals(team2), false);
  }
}
