package net.frostedbytes.android.trendo.models;

import android.util.Log;
import java.util.Locale;

public class Conference {

  private static final String TAG = "Conference";

  public long CreateDateUTC;
  public String Id;
  public boolean IsDefunct;
  public String Name;
  public String ParentId;

  @SuppressWarnings("unused")
  public Conference() {

    // Default constructor required for calls to DataSnapshot.getValue(Team.class)
    this.CreateDateUTC = 0;
    this.Id = "000000000-0000-0000-0000-000000000000";
    this.IsDefunct = false;
    this.Name = "";
    this.ParentId = "000000000-0000-0000-0000-000000000000";
  }

  public Conference(long createDateUTC, String id, boolean isDefunct, String name, String parentId) {

    this.CreateDateUTC = createDateUTC;
    this.Id = id;
    this.IsDefunct = isDefunct;
    this.Name = name;
    this.ParentId = parentId;
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

    if ((compareTo instanceof Conference)) {
      //cast to native object is now safe
      try {
        Conference compareToConference = (Conference) compareTo;
        if (this.Id.equals(compareToConference.Id) &&
            this.ParentId.equals(compareToConference.ParentId) &&
            this.Name.equals(compareToConference.Name) &&
            this.IsDefunct == compareToConference.IsDefunct) {
          return true;
        }
      } catch (ClassCastException cce) {
        Log.e(TAG, "Could not cast object to Conference class: " + cce.getMessage());
      }
    }

    return false;
  }

  @Override
  public String toString() {
    return String.format(
        Locale.getDefault(),
        "%s %s",
        this.Name,
        this.IsDefunct ? " (DEFUNCT)" : "");
  }
}
