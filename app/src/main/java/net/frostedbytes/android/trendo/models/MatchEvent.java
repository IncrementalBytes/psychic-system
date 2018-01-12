package net.frostedbytes.android.trendo.models;

import android.util.Log;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MatchEvent {

  private static final String TAG = "MatchEvent";

  public long CreateDateUTC;
  public String EventId;
  public String Id;
  public boolean IsAdditionalExtraTime;
  public boolean IsStoppageTime;
  public String MatchId;
  public int MinuteOfEvent;
  public String TeamId;

  @SuppressWarnings("unused")
  public MatchEvent() {

    // Default constructor required for calls to DataSnapshot.getValue(MatchEvent.class)
    this.CreateDateUTC = 0;
    this.EventId = "000000000-0000-0000-0000-000000000000";
    this.Id = "000000000-0000-0000-0000-000000000000";
    this.IsAdditionalExtraTime = false;
    this.IsStoppageTime = false;
    this.MatchId = "000000000-0000-0000-0000-000000000000";
    this.MinuteOfEvent = 0;
    this.TeamId = "000000000-0000-0000-0000-000000000000";
  }

  public MatchEvent(long createDateUTC, String eventId, String id, boolean isAdditionalExtraTime, boolean isStoppageTime, String matchId, int minuteOfEvent, String teamId) {

    this.CreateDateUTC = createDateUTC;
    this.EventId = eventId;
    this.Id = id;
    this.IsAdditionalExtraTime = isAdditionalExtraTime;
    this.IsStoppageTime = isStoppageTime;
    this.MatchId = matchId;
    this.MinuteOfEvent = minuteOfEvent;
    this.TeamId = teamId;
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
            this.EventId.equals(compareToMatchEvent.EventId) &&
            this.MatchId.equals(compareToMatchEvent.MatchId) &&
            this.TeamId.equals(compareToMatchEvent.TeamId) &&
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
}
