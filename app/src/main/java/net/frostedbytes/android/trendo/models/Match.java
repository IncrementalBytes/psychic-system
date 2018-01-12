package net.frostedbytes.android.trendo.models;

import android.util.Log;
import com.google.firebase.database.IgnoreExtraProperties;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@IgnoreExtraProperties
public class Match {

  private static final String TAG = "Match";

  public String AwayId;
  public String HomeId;
  public String Id;
  public boolean IsFinal;
  public long MatchDate;

  @SuppressWarnings("unused")
  public Match() {

    // Default constructor required for calls to DataSnapshot.getValue(Match.class)
    this.AwayId = "000000000-0000-0000-0000-000000000000";
    this.HomeId = "000000000-0000-0000-0000-000000000000";
    this.Id = "000000000-0000-0000-0000-000000000000";
    this.IsFinal = false;
    this.MatchDate = 0;
  }

  public Match(String awayId, String homeId, String id, boolean isFinal, long matchDate) {

    this.AwayId = awayId;
    this.HomeId = homeId;
    this.Id = id;
    this.IsFinal = isFinal;
    this.MatchDate = matchDate;
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
            this.HomeId.equals(compareToMatch.HomeId) &&
            this.AwayId.equals(compareToMatch.AwayId) &&
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
        this.HomeId,
        this.AwayId,
        formatDateForDisplay(this.MatchDate));
  }

  public static String formatDateForDisplay(long date) {
    Date temp = new Date(date);
    DateFormat dateFormat = SimpleDateFormat.getDateInstance();
    return dateFormat.format(temp);
  }
}
