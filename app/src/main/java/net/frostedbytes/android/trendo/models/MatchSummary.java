package net.frostedbytes.android.trendo.models;

import com.google.firebase.database.Exclude;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import net.frostedbytes.android.trendo.BaseActivity;

public class MatchSummary implements Serializable {

  private static final String TAG = "MatchSummary";

  /**
   * Goals scored by the away team.
   */
  public int AwayScore;

  /**
   * Name for away team; used as identifier for other classes.
   */
  public String AwayTeamShortName;

  /**
   * Goals scored by the home team.
   */
  public int HomeScore;

  /**
   * Name for home team; used as identifier for other classes.
   */
  public String HomeTeamShortName;

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

  public MatchSummary() {

    this.AwayScore = 0;
    this.AwayTeamShortName = "";
    this.HomeScore = 0;
    this.HomeTeamShortName = "";
    this.IsFinal = false;
    this.MatchId = BaseActivity.DEFAULT_ID;
    this.MatchDate = 0;
  }

  @Exclude
  public Map<String, Object> toMap() {

    HashMap<String, Object> result = new HashMap<>();
    result.put("AwayScore", AwayScore);
    result.put("AwayTeamShortName", AwayTeamShortName);
    result.put("HomeScore", HomeScore);
    result.put("HomeTeamShortName", HomeTeamShortName);
    result.put("IsFinal", IsFinal);
    result.put("MatchDate", MatchDate);

    return result;
  }
}
