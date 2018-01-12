package net.frostedbytes.android.trendo;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import net.frostedbytes.android.trendo.models.Conference;
import net.frostedbytes.android.trendo.models.Event;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.MatchEvent;
import net.frostedbytes.android.trendo.models.Team;

public class MatchCenter {

  private static final String TAG = "MatchCenter";

  private static MatchCenter sMatchCenter;

  private List<Conference> mConferenceList;
  private List<Event> mEventList;
  private List<Match> mMatchList;
  private List<MatchEvent> mMatchEventList;
  private List<Team> mTeamList;

  public static MatchCenter get(Context context) {

    if (sMatchCenter == null) {
      Log.d(TAG, "Creating MatchCenter context.");
      sMatchCenter = new MatchCenter(context);
    }

    return sMatchCenter;
  }

  private MatchCenter(Context context) {

    Log.d(TAG, "++MatchCenter(Context)");
    mConferenceList = new ArrayList<>();
    mEventList = new ArrayList<>();
    mMatchList = new ArrayList<>();
    mMatchEventList = new ArrayList<>();
    mTeamList = new ArrayList<>();
  }

  public Event getEvent(String eventId) {

    for (Event event : mEventList) {
      if (event.Id.equals(eventId))
        return event;
    }

    return null;
  }

  public List<String> getEventNames() {

    List<String> eventNames = new ArrayList<>();
    for (Event event : mEventList) {
      if (!eventNames.contains(event.Name)) {
        eventNames.add(event.Name);
      }
    }

    return eventNames;
  }

  public List<Event> getEvents() {

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

    List<MatchEvent> matchEvents = new ArrayList<>();
    for (MatchEvent matchEvent : mMatchEventList) {
      if (matchEvent.MatchId.equals(matchId)) {
        if (!matchEvents.contains(matchEvent)) {
          matchEvents.add(matchEvent);
        }
      }
    }

    return matchEvents;
  }

  public Team getTeam(String teamId) {

    Log.d(TAG, "++getTeam(String)");
    for (Team team : mTeamList) {
      if (team.Id.equals(teamId)) {
        return team;
      }
    }

    return null;
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
  }

  void setMatches(List<Match> matches) {

    Log.d(TAG, "++setMatches(List<Match>)");
    mMatchList = matches;
  }

  void setMatchEvents(List<MatchEvent> matchEvents) {

    Log.d(TAG, "++setMatchEvents(List<MatchEvent>)");
    mMatchEventList = matchEvents;
  }

  void setTeams(List<Team> teams) {

    Log.d(TAG, "++setTeams(List<Team>");
    mTeamList = teams;
  }
}
