package net.frostedbytes.android.trendo.models;

import android.util.Log;
import java.util.Locale;

import com.google.firebase.database.IgnoreExtraProperties;
import net.frostedbytes.android.trendo.BaseActivity;

@IgnoreExtraProperties
public class Team {

  private static final String TAG = "Team";

  public String ConferenceId;
  public long CreateDateUTC;
  public String FullName;
  public String Id;
  public boolean IsDefunct;
  public String ParentId;
  public String ShortName;

  @SuppressWarnings("unused")
  public Team() {

    // Default constructor required for calls to DataSnapshot.getValue(Team.class)
    this.ConferenceId = BaseActivity.DEFAULT_ID;
    this.CreateDateUTC = 0;
    this.FullName = "";
    this.Id = BaseActivity.DEFAULT_ID;
    this.IsDefunct = false;
    this.ParentId = BaseActivity.DEFAULT_ID;
    this.ShortName = "";
  }

  public Team(String conferenceId, long createDateUTC, String fullName, String id, boolean isDefunct, String parentId, String shortName) {

    this.ConferenceId = conferenceId;
    this.CreateDateUTC = createDateUTC;
    this.FullName = fullName;
    this.Id = id;
    this.IsDefunct = isDefunct;
    this.ParentId = parentId;
    this.ShortName = shortName;
  }

  @Override
  public boolean equals(Object compareTo) throws ClassCastException {

    if (compareTo == null) {
      return false;
    }

    //check for self-comparison
    if (this == compareTo) {
      return true;
    }

    if ((compareTo instanceof Team)) {
      //cast to native object is now safe
      try {
        Team compareToTeam = (Team) compareTo;
        if (this.Id.equals(compareToTeam.Id) &&
            this.ParentId.equals(compareToTeam.ParentId) &&
            this.ConferenceId.equals(compareToTeam.ConferenceId) &&
            this.FullName.equals(compareToTeam.FullName) &&
            this.ShortName.equals(compareToTeam.ShortName) &&
            this.IsDefunct == compareToTeam.IsDefunct) {
          return true;
        }
      } catch (ClassCastException cce) {
        Log.e(TAG, "Could not cast object to Team class: " + cce.getMessage());
      }
    }

    return false;
  }

  @Override
  public String toString() {
    return String.format(
        Locale.getDefault(),
        "%s (%s)%s",
        this.FullName,
        this.ShortName,
        this.IsDefunct ? " (DEFUNCT)" : "");
  }
}
