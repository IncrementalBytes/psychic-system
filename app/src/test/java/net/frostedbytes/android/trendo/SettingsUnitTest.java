package net.frostedbytes.android.trendo;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Map;
import net.frostedbytes.android.trendo.models.Settings;
import org.junit.Test;

public class SettingsUnitTest {

  @Test
  public void constructorTestDefaults() {

    Settings testSettings = new Settings();
    Calendar calendar = Calendar.getInstance();
    assertEquals(testSettings.Year, calendar.get(Calendar.YEAR));
    assertEquals(testSettings.TeamShortName, "");
    assertEquals(testSettings.Id, BaseActivity.DEFAULT_ID);
  }

  @Test
  public void toMapTest() {

    Settings testSettings = new Settings();
    Map<String, Object> mappedTrend = testSettings.toMap();
  }
}
