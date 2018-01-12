package net.frostedbytes.android.trendo.models;

import android.util.Log;

public class Player {

  private static final String TAG = "Player";

  public long CreateDateUTC;
  public String FirstName;
  public String Id;
  public boolean IsDefunct;
  public String LastName;
  public String TeamId;

  @SuppressWarnings("unused")
  public Player() {

    // Default constructor required for calls to DataSnapshot.getValue(MatchEvent.class)
    this.CreateDateUTC = 0;
    this.FirstName = "";
    this.Id = "000000000-0000-0000-0000-000000000000";
    this.IsDefunct= false;
    this.LastName = "";
    this.TeamId = "000000000-0000-0000-0000-000000000000";
  }

  public Player(long createDateUTC, String firstName, String id, boolean isDefunct, String lastName, String teamId) {

    this.CreateDateUTC = createDateUTC;
    this.FirstName = firstName;
    this.Id = id;
    this.IsDefunct = isDefunct;
    this.LastName = lastName;
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
    if ((compareTo instanceof Player))
      try {
        Player compareToPlayer = (Player) compareTo;
        if (this.Id.equals(compareToPlayer .Id) &&
            this.FirstName.equals(compareToPlayer.FirstName) &&
            this.LastName.equals(compareToPlayer.LastName) &&
            this.TeamId.equals(compareToPlayer .TeamId) &&
            this.IsDefunct == compareToPlayer .IsDefunct) {
          return true;
        }
      } catch (ClassCastException cce) {
        Log.e(TAG, "Could not cast object to Player class: " + cce.getMessage());
      }

    return false;
  }
}
