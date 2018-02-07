package net.frostedbytes.android.trendo;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Map;
import net.frostedbytes.android.trendo.models.UserSetting;
import org.junit.Test;

public class UserSettingUnitTest {

  @Test
  public void constructorTestDefaults() {

    UserSetting testSettings = new UserSetting();
    Calendar calendar = Calendar.getInstance();
    assertEquals(testSettings.Year, calendar.get(Calendar.YEAR));
    assertEquals(testSettings.TeamShortName, "");
    assertEquals(testSettings.Id, BaseActivity.DEFAULT_ID);
  }

  @Test
  public void toMapTest() {

    UserSetting testSettings = new UserSetting();
    Map<String, Object> mappedTrend = testSettings.toMap();
  }
}
