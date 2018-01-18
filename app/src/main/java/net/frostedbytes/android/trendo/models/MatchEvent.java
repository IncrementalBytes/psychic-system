package net.frostedbytes.android.trendo.models;

import android.util.Log;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.frostedbytes.android.trendo.BaseActivity;

@IgnoreExtraProperties
public class MatchEvent {

  private static final String TAG = "MatchEvent";

  public String EventName;
  public String Id;
  public boolean IsAdditionalExtraTime;
  public boolean IsStoppageTime;
  public int MinuteOfEvent;
  public String PlayerName;
  public String TeamShortName;

  @SuppressWarnings("unused")
  public MatchEvent() {

    // Default constructor required for calls to DataSnapshot.getValue(MatchEvent.class)
    this.EventName = "";
    this.Id = BaseActivity.DEFAULT_ID;
    this.IsAdditionalExtraTime = false;
    this.IsStoppageTime = false;
    this.MinuteOfEvent = 0;
    this.PlayerName = "";
    this.TeamShortName = "";
  }

  public MatchEvent(String eventName, String id, boolean isAdditionalExtraTime, boolean isStoppageTime, int minuteOfEvent, String playerName, String teamShortName) {

    this.EventName = eventName;
    this.Id = id;
    this.IsAdditionalExtraTime = isAdditionalExtraTime;
    this.IsStoppageTime = isStoppageTime;
    this.MinuteOfEvent = minuteOfEvent;
    this.PlayerName = playerName;
    this.TeamShortName = teamShortName;
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
    if ((compareTo instanceof MatchEvent))
      try {
        MatchEvent compareToMatchEvent = (MatchEvent) compareTo;
        if (this.Id.equals(compareToMatchEvent.Id) &&
            this.EventName.equals(compareToMatchEvent.EventName) &&
            this.TeamShortName.equals(compareToMatchEvent.TeamShortName) &&
            (this.PlayerName.equals(compareToMatchEvent.PlayerName)) &&
            this.MinuteOfEvent == compareToMatchEvent.MinuteOfEvent &&
            this.IsAdditionalExtraTime == compareToMatchEvent.IsAdditionalExtraTime &&
            this.IsStoppageTime == compareToMatchEvent.IsStoppageTime) {
          return true;
        }
      } catch (ClassCastException cce) {
        Log.e(TAG, "Could not cast object to MatchEvent class: " + cce.getMessage());
      }

    return false;
  }

  @Override
  public String toString() {
    return String.format(
      Locale.getDefault(),
      "%s for %s%s in the %d''",
      this.EventName,
      this.TeamShortName,
      this.PlayerName.isEmpty() ? "" : " by " + this.PlayerName,
      this.MinuteOfEvent);
  }

  @Exclude
  public Map<String, Object> toMap() {

    HashMap<String, Object> result = new HashMap<>();
    result.put("EventName", EventName);
    result.put("Id", Id);
    result.put("IsAdditionalExtraTime", IsAdditionalExtraTime);
    result.put("IsStoppageTime", IsStoppageTime);
    result.put("MinuteOfEvent", MinuteOfEvent);
    result.put("PlayerName", PlayerName);
    result.put("TeamShortName", TeamShortName);

    return result;
  }
}
