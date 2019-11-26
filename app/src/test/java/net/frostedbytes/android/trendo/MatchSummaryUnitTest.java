package net.frostedbytes.android.trendo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Map;

import net.frostedbytes.android.trendo.ui.BaseActivity;

import org.junit.Test;

public class MatchSummaryUnitTest {

  @Test
  public void constructorTestDefaults() {

    MatchSummary testSummary = new MatchSummary();
    assertEquals(testSummary.AwayScore, 0);
    assertEquals(testSummary.HomeScore, 0);
    assertEquals(testSummary.Id, BaseActivity.DEFAULT_ID);
    assertFalse(testSummary.IsFinal);
    assertEquals(testSummary.MatchDate, BaseActivity.DEFAULT_DATE);
    assertEquals(testSummary.HomeId, "");
    assertEquals(testSummary.AwayId, "");
  }

  @Test
  public void toMapTest() {

    MatchSummary testSummary = new MatchSummary();
    Map<String, Object> mappedSummary = testSummary.toMap();
    assertEquals((long)mappedSummary.get("AwayScore"), 0);
    assertEquals(mappedSummary.get("AwayId"), "");
    assertEquals((long)mappedSummary.get("HomeScore"), 0);
    assertEquals(mappedSummary.get("HomeId"), "");
    assertEquals(mappedSummary.get("IsFinal"), false);
    assertEquals((long)mappedSummary.get("MatchDate"), 0);
  }
}
