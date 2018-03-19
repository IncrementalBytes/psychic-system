package net.frostedbytes.android.trendo;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import net.frostedbytes.android.trendo.models.UserPreference;
import org.junit.Test;

public class UserPreferenceUnitTest {

  @Test
  public void constructorTestDefaults() {

    UserPreference testPreferences = new UserPreference();
    assertEquals(testPreferences.Season, Calendar.getInstance().get(Calendar.YEAR));
    assertEquals(testPreferences.TeamShortName, "");
    assertEquals(testPreferences.TeamFullName, "");
  }
}
