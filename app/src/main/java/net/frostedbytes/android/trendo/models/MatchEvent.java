package net.frostedbytes.android.trendo.models;

import android.util.Log;
import com.google.firebase.database.IgnoreExtraProperties;
import net.frostedbytes.android.trendo.BaseActivity;

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
  public String PlayerId;
  public String TeamId;

  @SuppressWarnings("unused")
  public MatchEvent() {

    // Default constructor required for calls to DataSnapshot.getValue(MatchEvent.class)
    this.CreateDateUTC = 0;
    this.EventId = BaseActivity.DEFAULT_ID;
    this.Id = BaseActivity.DEFAULT_ID;
    this.IsAdditionalExtraTime = false;
    this.IsStoppageTime = false;
    this.MatchId = BaseActivity.DEFAULT_ID;
    this.MinuteOfEvent = 0;
    this.PlayerId = BaseActivity.DEFAULT_ID;
    this.TeamId = BaseActivity.DEFAULT_ID;
  }

  public MatchEvent(long createDateUTC, String eventId, String id, boolean isAdditionalExtraTime, boolean isStoppageTime, String matchId, int minuteOfEvent, String playerId, String teamId) {

    this.CreateDateUTC = createDateUTC;
    this.EventId = eventId;
    this.Id = id;
    this.IsAdditionalExtraTime = isAdditionalExtraTime;
    this.IsStoppageTime = isStoppageTime;
    this.MatchId = matchId;
    this.MinuteOfEvent = minuteOfEvent;
    this.PlayerId = playerId;
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
            (this.PlayerId != null && this.PlayerId.equals(compareToMatchEvent.PlayerId)) &&
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
