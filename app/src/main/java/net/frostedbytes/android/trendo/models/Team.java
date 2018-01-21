package net.frostedbytes.android.trendo.models;

import android.util.Log;
import com.google.firebase.database.Exclude;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;

import com.google.firebase.database.IgnoreExtraProperties;
import java.util.Map;
import net.frostedbytes.android.trendo.BaseActivity;

@IgnoreExtraProperties
public class Team implements Serializable {

  private static final String TAG = "Team";

  public String FullName;
  public String Id;
  public boolean IsDefunct;
  public Map<String, Player> Rosters;
  public String ShortName;

  @SuppressWarnings("unused")
  public Team() {

    // Default constructor required for calls to DataSnapshot.getValue(Team.class)
    this.FullName = "";
    this.Id = BaseActivity.DEFAULT_ID;
    this.IsDefunct = false;
    this.Rosters = new HashMap<>();
    this.ShortName = "";
  }

  public Team(String fullName, String id, boolean isDefunct, Map<String, Player> rosters, String shortName) {

    this.FullName = fullName;
    this.Id = id;
    this.IsDefunct = isDefunct;
    this.Rosters = rosters;
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
        if (this.FullName.equals(compareToTeam.FullName) &&
          this.ShortName.equals(compareToTeam.ShortName) &&
          this.Id.equals(compareToTeam.Id) &&
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

  @Exclude
  public Map<String, Object> toMap() {

    HashMap<String, Object> result = new HashMap<>();
    result.put("FullName", FullName);
    result.put("Id", Id);
    result.put("IsDefunct", IsDefunct);
    result.put("Rosters", Rosters);
    result.put("ShortName", ShortName);

    return result;
  }
}
