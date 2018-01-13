package net.frostedbytes.android.trendo.models;

import android.util.Log;
import java.util.Locale;
import net.frostedbytes.android.trendo.BaseActivity;

public class Event {

  private static final String TAG = "Event";

  public long CreateDateUTC;
  public String Id;
  public boolean IsDefunct;
  public String Name;

  @SuppressWarnings("unused")
  public Event() {

    // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    this.CreateDateUTC = 0;
    this.Id = BaseActivity.DEFAULT_ID;
    this.IsDefunct = false;
    this.Name = "";
  }

  public Event(long createDateUTC, String id, boolean isDefunct, String name) {

    this.CreateDateUTC = createDateUTC;
    this.Id = id;
    this.IsDefunct = isDefunct;
    this.Name = name;
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

    if ((compareTo instanceof Event)) {
      //cast to native object is now safe
      try {
        Event compareToEvent = (Event) compareTo;
        if (this.Id.equals(compareToEvent.Id) &&
            this.Name.equals(compareToEvent.Name) &&
            this.IsDefunct == compareToEvent.IsDefunct) {
          return true;
        }
      } catch (ClassCastException cce) {
        Log.e(TAG, "Could not cast object to Event class: " + cce.getMessage());
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
