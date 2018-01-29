package net.frostedbytes.android.trendo.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Settings {

  public String TeamShortName;
  public int Year;

  @SuppressWarnings("unused")
  public Settings() {

    // Default constructor required for calls to DataSnapshot.getValue(Settings.class)
    this.TeamShortName = "";
    Calendar calendar = Calendar.getInstance();
    this.Year = calendar.get(Calendar.YEAR);
  }

  public Settings(String teamShortName, int year) {

    this.TeamShortName = teamShortName;
    this.Year = year;
  }

  @Exclude
  public Map<String, Object> toMap() {

    HashMap<String, Object> result = new HashMap<>();
    result.put("TeamShortName", TeamShortName);
    result.put("Year", Year);

    return result;
  }
}
