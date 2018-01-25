package net.frostedbytes.android.trendo;

import static org.junit.Assert.assertEquals;

import java.util.UUID;
import net.frostedbytes.android.trendo.models.MatchEvent;
import org.junit.Test;

public class MatchEventUnitTest {

  @Test
  public void  constructor_Defaults() {

    MatchEvent testMatchEvent = new MatchEvent();
    assertEquals(testMatchEvent.EventName, "");
    assertEquals(testMatchEvent.Id, BaseActivity.DEFAULT_ID);
    assertEquals(testMatchEvent.IsAdditionalExtraTime, false);
    assertEquals(testMatchEvent.IsStoppageTime, false);
    assertEquals(testMatchEvent.MinuteOfEvent, 0);
    assertEquals(testMatchEvent.PlayerName, "");
  }

  @Test
  public void constructor_WithParameters() {
    String eventName = UUID.randomUUID().toString();
    String id = UUID.randomUUID().toString();
    int minuteOfEvent = 13;
    String playerName = UUID.randomUUID().toString();
    String teamShortName = UUID.randomUUID().toString();

    MatchEvent testMatchEvent = new MatchEvent(eventName, id, minuteOfEvent, true, true, playerName, teamShortName);
    assertEquals(testMatchEvent.EventName, eventName);
    assertEquals(testMatchEvent.Id, id);
    assertEquals(testMatchEvent.IsAdditionalExtraTime, true);
    assertEquals(testMatchEvent.IsStoppageTime, true);
    assertEquals(testMatchEvent.MinuteOfEvent, minuteOfEvent);
    assertEquals(testMatchEvent.PlayerName, playerName);
    assertEquals(testMatchEvent.TeamShortName, teamShortName);
  }
}
