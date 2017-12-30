package net.frostedbytes.android.trendo;

import java.util.Locale;
import java.util.UUID;

class Team {

  private UUID mConferenceId;
  private boolean mDefunct;
  private String mFullName;
  private UUID mId;
  private UUID mParentId;
  private String mShortName;

  Team(UUID teamId) {

    mConferenceId = UUID.fromString("000000000-0000-0000-0000-000000000000");
    mDefunct = false;
    mFullName = "";
    mId = teamId;
    mParentId = UUID.fromString("000000000-0000-0000-0000-000000000000");
    mShortName = "";
  }

  UUID getConferenceId() {
    return mConferenceId;
  }

  void setConferenceId(UUID conferenceId) {
    mConferenceId = conferenceId;
  }

  boolean getIsDefunct() {
    return mDefunct;
  }

  void setIsDefunct(boolean defunct) {
    mDefunct = defunct;
  }

  public void setIsDefunct(int defunct) {
    mDefunct = defunct != 0;
  }

  String getFullName() {
    return mFullName;
  }

  void setFullName(String fullName) {
    mFullName = fullName;
  }

  public UUID getId() {
    return mId;
  }

  public void setId(UUID teamId) {
    mId = teamId;
  }

  String getShortName() {
    return mShortName;
  }

  void setShortName(String shortName) {
    mShortName = shortName;
  }

  UUID getParentId() {
    return mParentId;
  }

  void setParentId(UUID parentId) {
    mParentId = parentId;
  }

  @Override
  public boolean equals(Object compareTo) throws ClassCastException {

    //check for self-comparison
    if (this == compareTo) {
      return true;
    }

    if ((compareTo instanceof Team)) {
      //cast to native object is now safe
      try {
        Team compareToTeam = (Team) compareTo;
        return this.mConferenceId.equals(compareToTeam.getConferenceId()) &&
            this.mDefunct == compareToTeam.getIsDefunct() &&
            this.mFullName.equals(compareToTeam.getFullName()) &&
            this.mShortName.equals(compareToTeam.getShortName()) &&
            this.mParentId.equals(compareToTeam.getParentId()) &&
            this.mId.equals(compareToTeam.getId());
      } catch (ClassCastException cce) {
        System.out.println("ERR: Could not cast object to Match class.");
        return false;
      }
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return String.format(
        Locale.getDefault(),
        "%s (%s)%s",
        mFullName,
        mShortName,
        getIsDefunct() ? " (DEFUNCT)" : "");
  }
}
