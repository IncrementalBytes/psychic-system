package net.frostedbytes.android.trendo;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import net.frostedbytes.android.trendo.models.Conference;
import net.frostedbytes.android.trendo.models.Event;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.MatchEvent;
import net.frostedbytes.android.trendo.models.Player;
import net.frostedbytes.android.trendo.models.Team;

public class MatchCenter {

  private static final String TAG = "MatchCenter";

  private static MatchCenter sMatchCenter;

  private List<Conference> mConferenceList;
  private List<Event> mEventList;
  private List<Match> mMatchList;
  private List<MatchEvent> mMatchEventList;
  private List<Player> mPlayerList;
  private List<Team> mTeamList;

  private String mQueriedTeam;
  private String mQueriedMatchEvents;

  private List<String> mTeamNames;
  private List<String> mEventNames;
  private List<MatchEvent> mMatchEvents;
  private List<String> mPlayerNames;

  public static MatchCenter get() {

    if (sMatchCenter == null) {
      Log.d(TAG, "Creating MatchCenter context.");
      sMatchCenter = new MatchCenter();
    }

    return sMatchCenter;
  }

  private MatchCenter() {

    Log.d(TAG, "++MatchCenter(Context)");
    mConferenceList = new ArrayList<>();
    mEventList = new ArrayList<>();
    mMatchList = new ArrayList<>();
    mMatchEventList = new ArrayList<>();
    mPlayerList = new ArrayList<>();
    mTeamList = new ArrayList<>();

    mQueriedMatchEvents = BaseActivity.DEFAULT_ID;
    mQueriedTeam = BaseActivity.DEFAULT_ID;

    mEventNames = new ArrayList<>();
    mMatchEvents = new ArrayList<>();
    mPlayerNames = new ArrayList<>();
    mTeamNames = new ArrayList<>();
  }

  public Event getEvent(String eventId) {

    Log.d(TAG, "++getEvent(String)");
    for (Event event : mEventList) {
      if (event.Id.equals(eventId))
        return event;
    }

    return null;
  }

  public List<String> getEventNames() {

    if (!mEventNames.isEmpty()) {
      return mEventNames;
    }

    Log.d(TAG, "++getEventNames()");
    for (Event event : mEventList) {
      if (!mEventNames.contains(event.Name)) {
        mEventNames.add(event.Name);
      }
    }

    return mEventNames;
  }

  public List<Event> getEvents() {

    Log.d(TAG, "++getEvents()");
    return mEventList;
  }

  public Match getMatch(String matchId) {

    Log.d(TAG, "++getMatch(String)");
    for (Match match : mMatchList) {
      if (match.Id.equals(matchId)) {
        return match;
      }
    }

    return null;
  }

  public List<Match> getMatches() {

    Log.d(TAG, "++getMatches()");
    return mMatchList;
  }

  public List<MatchEvent> getMatchEvents(String matchId) {

    if (mQueriedMatchEvents.equals(matchId) && !mMatchEvents.isEmpty()) {
      return mMatchEvents;
    }

    Log.d(TAG, "++getMatchEvents(String)");
    mMatchEvents = new ArrayList<>();
    mQueriedMatchEvents = matchId;
    for (MatchEvent matchEvent : mMatchEventList) {
      if (matchEvent.MatchId.equals(matchId)) {
        if (!mMatchEvents.contains(matchEvent)) {
          mMatchEvents.add(matchEvent);
        }
      }
    }

    return mMatchEvents;
  }

  public Player getPlayer(String playerName) {

    Log.d(TAG, "++getPlayer(String)");
    String firstName = playerName;
    String lastName = "";
    if (playerName.contains(" ")) {
      firstName = playerName.substring(0, playerName.indexOf(" ")).trim();
      lastName = playerName.substring(playerName.indexOf(" "), (playerName.length())).trim();
    }

    for (Player player : mPlayerList) {
      if (player.FirstName.equals(firstName) && player.LastName.equals(lastName)) {
        return player;
      }
    }

    return null;
  }

  public List<String> getPlayerNames(String teamId) {

    if (mQueriedTeam.equals(teamId) && !mPlayerNames.isEmpty()) {
      return mPlayerNames;
    }

    Log.d(TAG, "++getPlayerNames(String)");
    mQueriedTeam = teamId;
    for (Player player : mPlayerList) {
      if (player.TeamId.equals(teamId) && !player.IsDefunct) {
        mPlayerNames.add(player.FirstName + " " + player.LastName);
      }
    }

    return mPlayerNames;
  }

  public Team getTeam(String teamId) {

    Log.d(TAG, "++getTeam(String)");
    for (Team team : mTeamList) {
      if (team.Id.equals(teamId) || team.FullName.equals(teamId)) {
        return team;
      }
    }

    return null;
  }

  public List<String> getTeamNames() {

    if (!mTeamNames.isEmpty()) {
      return mTeamNames;
    }

    Log.d(TAG, "++getTeamNames()");
    for (Team team : mTeamList) {
      if (!mTeamNames.contains(team.FullName)) {
        mTeamNames.add(team.FullName);
      }
    }

    return mTeamNames;
  }

  public List<Team> getTeams() {

    Log.d(TAG, "++getTeams()");
    return mTeamList;
  }

  void setConferences(List<Conference> conferences) {

    Log.d(TAG, "++setConferences(List<Conference>)");
    mConferenceList = conferences;
  }

  void setEvents(List<Event> events) {

    Log.d(TAG, "++setEvents(List<Event>");
    mEventList = events;
    mEventNames = new ArrayList<>();
  }

  void setMatches(List<Match> matches) {

    Log.d(TAG, "++setMatches(List<Match>)");
    mMatchList = matches;
  }

  void setMatchEvents(List<MatchEvent> matchEvents) {

    Log.d(TAG, "++setMatchEvents(List<MatchEvent>)");
    mMatchEventList = matchEvents;
    mMatchEvents = new ArrayList<>();
  }

  void setPlayers(List<Player> players) {

    Log.d(TAG, "++setPlayers(List<Player>)");
    mPlayerList = players;
    mPlayerNames = new ArrayList<>();
  }

  void setTeams(List<Team> teams) {

    Log.d(TAG, "++setTeams(List<Team>");
    mTeamList = teams;
    mTeamNames = new ArrayList<>();
  }
}
