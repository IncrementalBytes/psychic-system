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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
   * Value indicating whether or not this match is final; read-only
   */
  public boolean IsFinal;

  /**
   * Number of ticks representing the year, month, and date of this match.
   */
  public long MatchDate;

  /**
   * Collection of match events; formatted for json
   */
  public Map<String, MatchEvent> MatchEvents;

  /**
   * Constructs a new Match object with default values.
   */
  public Match() {
    this("", "", BaseActivity.DEFAULT_ID, false, 0, new HashMap<String, MatchEvent>());
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
   * @param matchEvents - Collection of events occurring during the match
   */
  public Match(String homeTeamShortName, String awayTeamShortName, String id, boolean isFinal, long matchDate, Map<String, MatchEvent> matchEvents) {

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

    this.MatchEvents = matchEvents;
  }

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

  @Exclude
  public Map<String, Object> toMap() {

    HashMap<String, Object> result = new HashMap<>();
    result.put("AwayTeamShortName", AwayTeamShortName);
    result.put("HomeTeamShortName", HomeTeamShortName);
    result.put("IsFinal", IsFinal);
    result.put("MatchDate", MatchDate);
    result.put("MatchEvents", MatchEvents);

    return result;
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
