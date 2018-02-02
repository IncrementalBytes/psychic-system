package net.frostedbytes.android.trendo.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import net.frostedbytes.android.trendo.BaseActivity;

public class MatchSummary implements Serializable {

  /**
   * Goals scored by the away team.
   */
  public long AwayScore;

  /**
   * Name for away team; used as identifier for other classes.
   */
  public String AwayTeamName;

  /**
   * Goals scored by the home team.
   */
  public long HomeScore;

  /**
   * Name for home team; used as identifier for other classes.
   */
  public String HomeTeamName;

  /**
   * Value indicating whether or not this match is final.
   */
  public boolean IsFinal;

  /**
   * Unique identifier for Match object; used as key for json.
   */
  public String MatchId;

  /**
   * Number of ticks representing the year, month, and date of this match.
   */
  public long MatchDate;

  /**
   * Constructs a new MatchSummary object with default values.
   */
  public MatchSummary() {

    // Default constructor required for calls to DataSnapshot.getValue(MatchSummary.class)
    this.AwayScore = 0;
    this.AwayTeamName = "";
    this.HomeScore = 0;
    this.HomeTeamName = "";
    this.IsFinal = false;
    this.MatchId = BaseActivity.DEFAULT_ID;
    this.MatchDate = 0;
  }

  /**
   * Creates a mapped object based on values of this match summary object
   * @return A mapped object of match summary
   */
  public Map<String, Object> toMap() {

    HashMap<String, Object> result = new HashMap<>();
    result.put("AwayScore", AwayScore);
    result.put("AwayTeamName", AwayTeamName);
    result.put("HomeScore", HomeScore);
    result.put("HomeTeamName", HomeTeamName);
    result.put("IsFinal", IsFinal);
    result.put("MatchDate", MatchDate);

    return result;
  }
}
