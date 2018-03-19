package net.frostedbytes.android.trendo.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.utils.LogUtils;

@IgnoreExtraProperties
public class UserPreference implements Serializable {

  private static final String TAG = UserPreference.class.getSimpleName();

  /**
   * Year to compare Year results against.
   */
  public int Compare;

  /**
   * Full name of team.
   */
  public String TeamFullName;

  /**
   * Short name of team.
   */
  public String TeamShortName;

  /**
   * Unique identifier of user.
   */
  @Exclude
  public String UserId;

  /**
   * Year of results.
   */
  public int Season;

  /**
   * Constructs a new UserPreference object with default values.
   */
  @SuppressWarnings("unused")
  public UserPreference() {

    // Default constructor required for calls to DataSnapshot.getValue(Settings.class)
    this.Compare = 0;
    this.UserId = BaseActivity.DEFAULT_ID;
    this.TeamFullName = "";
    this.TeamShortName = "";
    this.Season = Calendar.getInstance().get(Calendar.YEAR);
  }

  /**
   * Compares this UserPreference with another UserPreference.
   * @param compareTo UserPreference to compare this UserPreference against
   * @return TRUE if this UserPreference equals the other UserPreference, otherwise FALSE
   * @throws ClassCastException if object parameter cannot be cast into UserPreference object
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
    if ((compareTo instanceof UserPreference)) {
      try {
        UserPreference compareToSettings = (UserPreference) compareTo;
        if (this.UserId.equals(compareToSettings.UserId) &&
          this.TeamShortName.equals(compareToSettings.TeamShortName) &&
          this.TeamFullName.equals(compareToSettings.TeamFullName) &&
          this.Season == compareToSettings.Season &&
          this.Compare == ((UserPreference) compareTo).Compare) {
          return true;
        }
      } catch (ClassCastException cce) {
        LogUtils.error(TAG, "Could not cast object to UserSetting class: " + cce.getMessage());
      }
    }

    return false;
  }

  @Override
  public String toString() {

    if (this.TeamShortName.isEmpty() || this.Season == 0) {
      return "";
    }

    return String.format(Locale.ENGLISH, "%s-%d", this.TeamShortName, this.Season);
  }
}
