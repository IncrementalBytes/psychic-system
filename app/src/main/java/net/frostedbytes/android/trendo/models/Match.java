package net.frostedbytes.android.trendo.models;

import android.util.Log;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.frostedbytes.android.trendo.BaseActivity;

@IgnoreExtraProperties
public class Match {

  private static final String TAG = "Match";

  public Team AwayTeam;
  public Team HomeTeam;
  public String Id;
  public boolean IsFinal;
  public long MatchDate;
  public Map<String, MatchEvent> MatchEvents;

  @SuppressWarnings("unused")
  public Match() {

    // Default constructor required for calls to DataSnapshot.getValue(Match.class)
    this.AwayTeam = new Team();
    this.HomeTeam = new Team();
    this.Id = BaseActivity.DEFAULT_ID;
    this.IsFinal = false;
    this.MatchDate = Calendar.getInstance().getTimeInMillis();
    this.MatchEvents = new HashMap<>();
  }

  public Match(Team awayTeam, Team homeTeam, String id, boolean isFinal, long matchDate, Map<String, MatchEvent> matchEvents) {

    this.AwayTeam = awayTeam;
    this.HomeTeam = homeTeam;
    this.Id = id;
    this.IsFinal = isFinal;
    this.MatchDate = matchDate;
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
          this.HomeTeam.equals(compareToMatch.HomeTeam) &&
          this.AwayTeam.equals(compareToMatch.AwayTeam) &&
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
    return String.format(
      Locale.getDefault(),
      "%s vs. %s - %s",
      this.HomeTeam.FullName,
      this.AwayTeam.FullName,
      formatDateForDisplay(this.MatchDate));
  }

  @Exclude
  public Map<String, Object> toMap() {

    HashMap<String, Object> result = new HashMap<>();
    result.put("AwayTeam", AwayTeam);
    result.put("HomeTeam", HomeTeam);
    result.put("Id", Id);
    result.put("IsFinal", IsFinal);
    result.put("MatchDate", MatchDate);
    result.put("MatchEvents", MatchEvents);

    return result;
  }

  public static String formatDateForDisplay(long date) {
    Date temp = new Date(date);
    DateFormat dateFormat = SimpleDateFormat.getDateInstance();
    return dateFormat.format(temp);
  }
}
