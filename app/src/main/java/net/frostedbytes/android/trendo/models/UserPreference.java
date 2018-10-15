package net.frostedbytes.android.trendo.models;

import android.support.annotation.NonNull;

import static net.frostedbytes.android.trendo.BaseActivity.BASE_TAG;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.utils.LogUtils;

@IgnoreExtraProperties
public class UserPreference implements Serializable {

  private static final String TAG = BASE_TAG + UserPreference.class.getSimpleName();

  @Exclude
  private boolean mIsBarChart;

  @Exclude
  private boolean mIsLineChart;

  /**
   * Unique identifier of team ahead of TeamId.
   */
  @Exclude
  public String AheadTeamId;

  /**
   * Unique identifier of team behind TeamId.
    */
  @Exclude
  public String BehindTeamId;

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

    this.mIsBarChart = false;
    this.mIsLineChart = true;
    this.AheadTeamId = BaseActivity.DEFAULT_ID;
    this.BehindTeamId = BaseActivity.DEFAULT_ID;
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
        UserPreference compareToPreference = (UserPreference) compareTo;
        if (this.UserId.equals(compareToPreference.UserId) &&
          this.TeamId.equals(compareToPreference.TeamId) &&
          this.Season == compareToPreference.Season) {
          return true;
        }
      } catch (ClassCastException cce) {
        LogUtils.error(TAG, "Could not cast object to UserPreference class: %s", cce.getMessage());
      }
    }

    return false;
  }

  @NonNull
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
