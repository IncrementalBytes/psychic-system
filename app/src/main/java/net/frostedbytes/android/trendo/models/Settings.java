package net.frostedbytes.android.trendo.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import net.frostedbytes.android.trendo.BaseActivity;

@IgnoreExtraProperties
public class Settings implements Serializable {

  /**
   * Unique identifier of user.
   */
  @Exclude
  public String Id;

  /**
   * Short name of team.
   */
  public String TeamShortName;

  /**
   * Year of results.
   */
  public int Year;

  /**
   * Constructs a new Settings object with default values.
   */
  @SuppressWarnings("unused")
  public Settings() {

    // Default constructor required for calls to DataSnapshot.getValue(Settings.class)
    this.Id = BaseActivity.DEFAULT_ID;
    this.TeamShortName = "";
    Calendar calendar = Calendar.getInstance();
    this.Year = calendar.get(Calendar.YEAR);
  }

  /**
   * Default copy constructor for creating a new settings object based on an existing settings object.
   * @param settings Existing settings object
   */
  public Settings(Settings settings) {

    this.Id = settings.Id;
    this.TeamShortName = settings.TeamShortName;
    this.Year = settings.Year;
  }

  /**
   * Creates a mapped object based on values of this settings object.
   * @return A mapped object of settings
   */
  @Exclude
  public Map<String, Object> toMap() {

    HashMap<String, Object> result = new HashMap<>();
    result.put("TeamShortName", TeamShortName);
    result.put("Year", Year);

    return result;
  }
}
