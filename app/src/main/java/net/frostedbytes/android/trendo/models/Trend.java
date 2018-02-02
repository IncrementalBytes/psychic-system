package net.frostedbytes.android.trendo.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frostedbytes.android.trendo.BaseActivity;

public class Trend implements Serializable {

  /**
   * List containing the accumulated goals scored against.
   */
  public List<Long> GoalsAgainst;

  /**
   * List containing the accumulated goal differential.
   */
  public List<Long> GoalDifferential;

  /**
   * List containing the accumulated goals scored.
   */
  public List<Long> GoalsFor;

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
  public List<Double> PointsPerGame;

  /**
   * Identifier of team this trend is for.
   */
  public String TeamName;

  /**
   * List containing the accumulated total points.
   */
  public List<Long> TotalPoints;

  /**
   * Constructs a new Trend object with default values.
   */
  public Trend() {

    // Default constructor required for calls to DataSnapshot.getValue(Trend.class)
    this.GoalsAgainst = new ArrayList<>();
    this.GoalDifferential = new ArrayList<>();
    this.GoalsFor = new ArrayList<>();
    this.MatchDate = 0;
    this.MatchId = BaseActivity.DEFAULT_ID;
    this.PointsPerGame = new ArrayList<>();
    this.TeamName = "";
    this.TotalPoints = new ArrayList<>();
  }

  /**
   * Creates a mapped object based on values of this trend object
   * @return A mapped object of trend
   */
  public Map<String, Object> toMap() {

    HashMap<String, Object> result = new HashMap<>();
    result.put("GoalsAgainst", GoalsAgainst);
    result.put("GoalDifferential", GoalDifferential);
    result.put("GoalsFor", GoalsFor);
    result.put("MatchDate", MatchDate);
    result.put("PointsPerGame", PointsPerGame);
    result.put("TeamName", TeamName);
    result.put("TotalPoints", TotalPoints);

    return result;
  }
}
