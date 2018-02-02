package net.frostedbytes.android.trendo.models;

import android.util.Log;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import net.frostedbytes.android.trendo.BaseActivity;

@IgnoreExtraProperties
public class Match implements Serializable {

  private static final String TAG = "Match";

  /**
   * Abbreviated name for away team; used as identifier for other classes.
   */
  public String AwayTeamShortName;

  /**
   * Abbreviated name for home team; used as identifier for other classes.
   */
  public String HomeTeamShortName;

  /**
   * Unique identifier for this object; used as key for json.
   */
  @Exclude
  public String Id;

  /**
   * Value indicating whether or not this match is final.
   */
  public boolean IsFinal;

  /**
   * Number of ticks representing the year, month, and date of this match.
   */
  public long MatchDate;

  /**
   * Constructs a new Match object with default values.
   */
  public Match() {
    this("", "", BaseActivity.DEFAULT_ID, false, 0);

    // Default constructor required for calls to DataSnapshot.getValue(Match.class)
  }

  /**
   * Constructs a new Match object with values from parameters.
   *
   * @param homeTeamShortName - Abbreviation of home team
   * @param awayTeamShortName - Abbreviation of away team
   * @param id - Unique identifier of this match; used as key in firebase
   * @param isFinal - Value indicating whether or not this match has completed
   * @param matchDate - Date of match; in ticks
   */
  public Match(String homeTeamShortName, String awayTeamShortName, String id, boolean isFinal, long matchDate) {

    this.AwayTeamShortName = awayTeamShortName;
    this.HomeTeamShortName = homeTeamShortName;
    this.Id = id;
    this.IsFinal = isFinal;
    if (matchDate == 0) {
      Calendar calendar = Calendar.getInstance();
      this.MatchDate = new GregorianCalendar(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
        0,
        0,
        0).getTimeInMillis();
    } else {
      this.MatchDate = matchDate;
    }
  }

  /**
   * Compares this match with another match.
   * @param compareTo Match to compare this match against
   * @return TRUE if this match equals the other match, otherwise FALSE
   * @throws ClassCastException if object parameter cannot be casted into Match object
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
    if ((compareTo instanceof Match))
      try {
        Match compareToMatch = (Match) compareTo;
        if (this.Id.equals(compareToMatch.Id) &&
          this.HomeTeamShortName.equals(compareToMatch.HomeTeamShortName) &&
          this.AwayTeamShortName.equals(compareToMatch.AwayTeamShortName) &&
          this.MatchDate == compareToMatch.MatchDate &&
          this.IsFinal == compareToMatch.IsFinal) {
          return true;
        }
      } catch (ClassCastException cce) {
        Log.e(TAG, "Could not cast object to Match class: " + cce.getMessage());
      }

    return false;
  }

  /**
   * Returns a user-friendly readable summary of this match.
   * @return User-friendly readable match summary
   */
  @Override
  public String toString() {

    if (!this.HomeTeamShortName.isEmpty() && !this.AwayTeamShortName.isEmpty()) {
      return String.format(
        Locale.getDefault(),
        "%s vs. %s - %s",
        this.HomeTeamShortName,
        this.AwayTeamShortName,
        formatDateForDisplay(this.MatchDate));
    }

    return "Match details unavailable.";
  }

  /**
   * Returns a user-friendly readable string of the date.
   *
   * @param date - Date; in ticks
   * @return - User-friendly readable string of the date; formatted YYYY-MM-DD
   */
  public static String formatDateForDisplay(long date) {
    Date temp = new Date(date);
    DateFormat dateFormat = SimpleDateFormat.getDateInstance();
    return dateFormat.format(temp);
  }
}
