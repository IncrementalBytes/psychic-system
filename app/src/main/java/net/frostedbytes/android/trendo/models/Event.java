package net.frostedbytes.android.trendo.models;

import android.util.Log;
import com.google.firebase.database.Exclude;
import java.util.HashMap;
import java.util.Map;
import net.frostedbytes.android.trendo.BaseActivity;

public class Event {

  private static final String TAG = "Event";

  public String Name;
  public String Id;

  @SuppressWarnings("unused")
  public Event() {

    // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    this.Name = "";
    this.Id = BaseActivity.DEFAULT_ID;
  }

  public Event(String name, String id) {

    this.Name = name;
    this.Id = id;
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
          this.Name.equals(compareToEvent.Name)) {
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

    return this.Name;
  }

  @Exclude
  public Map<String, Object> toMap() {

    HashMap<String, Object> result = new HashMap<>();
    result.put("Name", Name);
    result.put("Id", Id);

    return result;
  }
}
