package net.frostedbytes.android.trendo.models;

import com.google.firebase.database.Exclude;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import net.frostedbytes.android.trendo.BaseActivity;

public class Trend implements Serializable {

  @Exclude
  public static final String ROOT = "Trends";

  /**
   * List containing the accumulated goals scored against.
   */
  public HashMap<String, Long> GoalsAgainstMap;

  /**
   * List containing the accumulated goal differential.
   */
  public HashMap<String, Long> GoalDifferentialMap;

  /**
   * List containing the accumulated goals scored.
   */
  public HashMap<String, Long> GoalsForMap;

  /**
   * Date of match (in ticks).
   */
  public long MatchDate;

  /**
   * Match identifier for this trend.
   */
  public String MatchId;

  /**
   * List containing the accumulated points per game.
   */
  public HashMap<String, Double> PointsPerGameMap;

  /**
   * Identifier of team this trend is for.
   */
  public String TeamName;

  /**
   * List containing the accumulated total points.
   */
  public HashMap<String, Long> TotalPointsMap;

  /**
   * Constructs a new Trend object with default values.
   */
  public Trend() {

    // Default constructor required for calls to DataSnapshot.getValue(Trend.class)
    this.GoalsAgainstMap = new HashMap<>();
    this.GoalDifferentialMap = new HashMap<>();
    this.GoalsForMap = new HashMap<>();
    this.MatchDate = 0;
    this.MatchId = BaseActivity.DEFAULT_ID;
    this.PointsPerGameMap = new HashMap<>();
    this.TeamName = "";
    this.TotalPointsMap = new HashMap<>();
  }

  /**
   * Creates a mapped object based on values of this trend object
   * @return A mapped object of trend
   */
  public Map<String, Object> toMap() {

    HashMap<String, Object> result = new HashMap<>();
    result.put("GoalsAgainstMap", GoalsAgainstMap);
    result.put("GoalDifferentialMap", GoalDifferentialMap);
    result.put("GoalsForMap", GoalsForMap);
    result.put("MatchDate", MatchDate);
    result.put("PointsPerGameMap", PointsPerGameMap);
    result.put("TeamName", TeamName);
    result.put("TotalPointsMap", TotalPointsMap);

    return result;
  }
}
