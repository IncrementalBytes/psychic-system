package net.frostedbytes.android.trendo.models;

import android.util.Log;
import com.google.firebase.database.Exclude;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.google.firebase.database.IgnoreExtraProperties;
import java.util.Map;

@IgnoreExtraProperties
public class Team implements Serializable {

  private static final String TAG = "Team";

  public String FullName;
  public Map<String, Map<String, Object>> MatchSummaries;
  public Map<String, List<String>> Rosters;
  @Exclude
  public String ShortName; // acts as identifier

  @SuppressWarnings("unused")
  public Team() {

    // Default constructor required for calls to DataSnapshot.getValue(Team.class)
    this.FullName = "";
    this.MatchSummaries = new HashMap<>();
    this.Rosters = new HashMap<>();
    this.ShortName = "";
  }

  public Team(String fullName, Map<String, Map<String, Object>> matchSummaries, Map<String, List<String>> rosters, String shortName) {

    this.FullName = fullName;
    this.MatchSummaries = matchSummaries;
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
        if (this.FullName.equals(compareToTeam.FullName) && this.ShortName.equals(compareToTeam.ShortName)) {
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
      "%s (%s)",
      this.FullName,
      this.ShortName);
  }

  @Exclude
  public Map<String, Object> toMap() {

    HashMap<String, Object> result = new HashMap<>();
    result.put("FullName", FullName);
    if (!MatchSummaries.isEmpty()) {
      result.put("MatchSummaries", MatchSummaries);
    }

    if (!Rosters.isEmpty()) {
      result.put("Rosters", Rosters);
    }

    return result;
  }
}
