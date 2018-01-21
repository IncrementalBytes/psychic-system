package net.frostedbytes.android.trendo.models;

import android.util.Log;
import com.google.firebase.database.Exclude;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.frostedbytes.android.trendo.BaseActivity;

public class Player implements Serializable {

  private static final String TAG = "Player";

  public String Id;
  public boolean IsDefunct;
  public String Name;
  public Team Team;

  @SuppressWarnings("unused")
  public Player() {

    // Default constructor required for calls to DataSnapshot.getValue(MatchEvent.class)
    this.Id = BaseActivity.DEFAULT_ID;
    this.IsDefunct = false;
    this.Name = "";
    this.Team = new Team();
  }

  public Player(String id, boolean isDefunct, String name, Team team) {

    this.Id = id;
    this.IsDefunct = isDefunct;
    this.Name = name;
    this.Team = team;
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
    if ((compareTo instanceof Player))
      try {
        Player compareToPlayer = (Player) compareTo;
        if (this.Id.equals(compareToPlayer.Id) &&
          this.Name.equals(compareToPlayer.Name) &&
          this.Team.ShortName.equals(compareToPlayer.Team.ShortName) &&
          this.IsDefunct == compareToPlayer.IsDefunct) {
          return true;
        }
      } catch (ClassCastException cce) {
        Log.e(TAG, "Could not cast object to Player class: " + cce.getMessage());
      }

    return false;
  }

  @Override
  public String toString() {

    return String.format(
      Locale.getDefault(),
      "%s (%s)%s",
      this.Name,
      this.Team.ShortName,
      this.IsDefunct ? " (DEFUNCT)" : "");
  }

  @Exclude
  public Map<String, Object> toMap() {

    HashMap<String, Object> result = new HashMap<>();
    result.put("Id", Id);
    result.put("IsDefunct", IsDefunct);
    result.put("Name", Name);
    result.put("Team", Team);

    return result;
  }
}
