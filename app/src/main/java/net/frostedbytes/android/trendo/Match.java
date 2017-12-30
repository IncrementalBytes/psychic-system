package net.frostedbytes.android.trendo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

class Match {

  private UUID mAwayTeamId;
  private int mAwayScore;
  private UUID mHomeTeamId;
  private int mHomeScore;
  private final UUID mId;
  private boolean mIsMatchFinal;
  private Date mMatchDate;

  Match(UUID matchId) {

    mAwayTeamId = UUID.fromString("000000000-0000-0000-0000-000000000000");
    mAwayScore = 0;
    mHomeTeamId = UUID.fromString("000000000-0000-0000-0000-000000000000");
    mHomeScore = 0;

    mId = matchId;
    mMatchDate = new Date();
    mIsMatchFinal = false;
  }

  UUID getAwayId() {
    return mAwayTeamId;
  }

  void setAwayId(UUID awayId) {
    mAwayTeamId = awayId;
  }

  public int getAwayScore() {
    return mAwayScore;
  }

  public void setAwayScore(int awayScore) {
    mAwayScore = awayScore;
  }

  UUID getId() {
    return mId;
  }

  boolean getIsMatchFinal() {
    return mIsMatchFinal;
  }

  void setIsMatchFinal(boolean matchFinal) {
    mIsMatchFinal = matchFinal;
  }

  UUID getHomeId() {
    return mHomeTeamId;
  }

  void setHomeId(UUID homeId) {
    mHomeTeamId = homeId;
  }

  public int getHomeScore() {
    return mHomeScore;
  }

  public void setHomeScore(int homeScore) {
    mHomeScore = homeScore;
  }

  Date getMatchDate() {
    return mMatchDate;
  }

  void setMatchDate(Date date) {
    mMatchDate = date;
  }

  @Override
  public boolean equals(Object compareTo) throws ClassCastException {

    //check for self-comparison
    if (this == compareTo) {
      return true;
    }

    if ((compareTo instanceof Match)) {
      //cast to native object is now safe
      try {
        Match compareToMatch = (Match) compareTo;
        return this.mAwayTeamId.equals(compareToMatch.getAwayId()) &&
            this.mAwayScore == compareToMatch.getAwayScore() &&
            this.mHomeTeamId.equals(compareToMatch.getHomeId()) &&
            this.mHomeScore == compareToMatch.getHomeScore() &&
            formatDateForDisplay(this.mMatchDate).equals(formatDateForDisplay(compareToMatch.getMatchDate())) &&
            this.mIsMatchFinal == compareToMatch.getIsMatchFinal() &&
            this.mId.equals(compareToMatch.getId());
      } catch (ClassCastException cce) {
        System.out.println("ERR: Could not cast object to Match class.");
        return false;
      }
    } else {
      return false;
    }
  }

  static String formatDateForDisplay(Date date) {
    DateFormat dateFormat = SimpleDateFormat.getDateInstance();
    return dateFormat.format(date);
  }
}
