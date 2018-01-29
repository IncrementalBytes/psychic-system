package net.frostedbytes.android.trendo;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frostedbytes.android.trendo.models.Team;
import org.junit.Test;

public class TeamUnitTest {

  @Test
  public void constructor_Defaults() {

    Team newTeam = new Team();
    assertEquals(newTeam.FullName, "");
    assertEquals(newTeam.ShortName, "");
  }

  @Test
  public void constructor_WithParameters() {

    String fullName = UUID.randomUUID().toString();
    String shortName = UUID.randomUUID().toString();
    Team newTeam = new Team(fullName, new HashMap<String, Map<String, Object>>(), new HashMap<String, List<String>>(), shortName);
    assertEquals(newTeam.FullName, fullName);
    assertEquals(newTeam.ShortName, shortName);
  }
}
