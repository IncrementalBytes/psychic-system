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

  private boolean mIsBarChart;
  private boolean mIsLineChart;

  /**
   * Year to compare Year results against.
   */
  public int Compare;

  /**
   * Unique identifier for team.
   */
  public String TeamId;

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
    this.mIsBarChart = false;
    this.mIsLineChart = true;
    this.Compare = 0;
    this.TeamId = BaseActivity.DEFAULT_ID;
    this.UserId = BaseActivity.DEFAULT_ID;
    this.Season = Calendar.getInstance().get(Calendar.YEAR);
  }

  /**
   * Compares this UserPreference with another UserPreference.
   *
   * @param compareTo UserPreference to compare this UserPreference against
   * @return TRUE if this UserPreference equals the other UserPreference, otherwise FALSE
   * @throws ClassCastException if object parameter cannot be cast into UserPreference object
   */
  @Override
  public boolean equals(Object compareTo) throws ClassCastException {

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
          this.TeamId.equals(compareToSettings.TeamId) &&
          this.Season == compareToSettings.Season &&
          this.Compare == ((UserPreference) compareTo).Compare) {
          return true;
        }
      } catch (ClassCastException cce) {
        LogUtils.error(TAG, "Could not cast object to UserSetting class: %s", cce.getMessage());
      }
    }

    return false;
  }

  @Override
  public String toString() {

    if (this.Season == 0) {
      return "";
    }

    return String.format(Locale.ENGLISH, "%d", this.Season);
  }

  public boolean getIsBarChart() {

    return this.mIsBarChart;
  }

  public boolean getIsLineChart() {

    return this.mIsLineChart;
  }

  public void setIsBarChart() {

    this.mIsBarChart = true;
    this.mIsLineChart = false;
  }

  public void setIsLineChart() {

    this.mIsBarChart = false;
    this.mIsLineChart = true;
  }
}
