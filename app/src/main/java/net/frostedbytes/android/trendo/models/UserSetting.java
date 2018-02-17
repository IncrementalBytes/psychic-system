package net.frostedbytes.android.trendo.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.utils.LogUtils;

@IgnoreExtraProperties
public class UserSetting implements Serializable {

  private static final String TAG = "UserSetting";

  @Exclude
  public static final String ROOT = "UserSettings";

  /**
   * Unique identifier of user.
   */
  @Exclude
  public String Id;

  /**
   * Short name of team.
   */
  public String TeamShortName;

  /**
   * Year of results.
   */
  public int Year;

  /**
   * Constructs a new UserSetting object with default values.
   */
  @SuppressWarnings("unused")
  public UserSetting() {

    // Default constructor required for calls to DataSnapshot.getValue(Settings.class)
    this.Id = BaseActivity.DEFAULT_ID;
    this.TeamShortName = "";
    Calendar calendar = Calendar.getInstance();
    this.Year = calendar.get(Calendar.YEAR);
  }

  /**
   * Compares this UserSetting with another UserSetting.
   * @param compareTo UserSetting to compare this UserSetting against
   * @return TRUE if this UserSetting equals the other UserSetting, otherwise FALSE
   * @throws ClassCastException if object parameter cannot be cast into UserSetting object
   */
  @Override
  public boolean equals(Object compareTo)  throws ClassCastException {

    if (compareTo == null) {
      return false;
    }

    if (this == compareTo) {
      return true;
    }

    //cast to native object is now safe
    if ((compareTo instanceof UserSetting)) {
      try {
        UserSetting compareToSettings = (UserSetting) compareTo;
        if (this.TeamShortName.equals(compareToSettings.TeamShortName) &&
          this.Year == compareToSettings.Year) {
          return true;
        }
      } catch (ClassCastException cce) {
        LogUtils.error(TAG, "Could not cast object to UserSetting class: " + cce.getMessage());
      }
    }

    return false;
  }

  /**
   * Creates a mapped object based on values of this settings object.
   * @return A mapped object of settings
   */
  @Exclude
  public Map<String, Object> toMap() {

    HashMap<String, Object> result = new HashMap<>();
    result.put("TeamShortName", TeamShortName);
    result.put("Year", Year);

    return result;
  }
}
