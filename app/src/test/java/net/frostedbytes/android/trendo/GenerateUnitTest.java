package net.frostedbytes.android.trendo;

import static org.junit.Assert.assertEquals;

import android.os.Build.VERSION_CODES;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.MatchEvent;
import net.frostedbytes.android.trendo.models.MatchSummary;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = VERSION_CODES.LOLLIPOP, packageName = "net.frostedbytes.android.trendo")
public class GenerateUnitTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void finish() throws Exception {
  }

  @Test
  public void generateResults() throws IOException {

    generateResults("SEA", 2017);
    assertEquals(1, 1);
  }

  long convertDateToMilliseconds(String matchDate) {

    int year = Integer.parseInt(matchDate.substring(0, 4));
    int month = Integer.parseInt(matchDate.substring(4, 6));
    int day = Integer.parseInt(matchDate.substring(6, 8));

    // months are 0 index based
    Date date = new GregorianCalendar(year, month - 1, day, 0, 0, 0).getTime();
    return date.getTime();
  }

  void generateResults(String teamShortName, int year) throws IOException {

    String parsableString;
    try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(year + "/" + teamShortName + ".txt")) {
      try (Scanner s = new Scanner(inputStream)) {
        parsableString = s.useDelimiter("\\A").hasNext() ? s.next() : "";
      }
    }

    String[] lineSegments = parsableString.split("\r\n");
    Map<String, Map<String, Object>> matches = new HashMap<>();
    Map<String, Map<String, Object>> matchSummaries = new HashMap<>();
    Match currentMatch = new Match();
    MatchSummary currentSummary = new MatchSummary();
    currentMatch.Id = UUID.randomUUID().toString();
    for (String line : lineSegments) {
      if (line.isEmpty() || line.startsWith("#")) {
        continue;
      }

      System.out.println("Processing: " + line);
      List<String> elements = new ArrayList<>();
      elements.addAll(Arrays.asList(line.split(",")));
      String homeTeam = elements.remove(0);
      String awayTeam = elements.remove(0);
      String dateString = elements.remove(0);
      long convertedMatchDate = convertDateToMilliseconds(dateString);
      if (elements.isEmpty()) {
        continue;
      }

      String matchEventId = UUID.randomUUID().toString();
      if (!currentMatch.MatchEvents.isEmpty()) {
        if (!currentMatch.HomeTeamShortName.equals(homeTeam) || !currentMatch.AwayTeamShortName.equals(awayTeam)) {
          // put current match into collection
          currentMatch.IsFinal = true;
          matches.put(currentMatch.Id, currentMatch.toMap());

          // put current summary into collection
          currentSummary.IsFinal = true;
          matchSummaries.put(currentMatch.Id, currentSummary.toMap());

          // update current match
          System.out.println("Creating new match and summary.");
          currentMatch = new Match();
          currentMatch.Id = UUID.randomUUID().toString();
          currentMatch.HomeTeamShortName = homeTeam;
          currentMatch.AwayTeamShortName = awayTeam;
          currentMatch.MatchDate = convertedMatchDate;
          currentMatch.MatchEvents = new HashMap<>();

          // update current summary
          currentSummary = new MatchSummary();
          currentSummary.AwayScore = 0;
          currentSummary.AwayTeamShortName = awayTeam;
          currentSummary.HomeScore = 0;
          currentSummary.HomeTeamShortName = homeTeam;
          currentSummary.MatchId = currentMatch.Id;
          currentSummary.MatchDate = convertedMatchDate;
        } else {
          System.out.println("Same match; progressing to match events.");
        }
      } else {
        System.out.println("Processing first line?");
        currentMatch.HomeTeamShortName = homeTeam;
        currentMatch.AwayTeamShortName = awayTeam;
        currentMatch.MatchDate = convertedMatchDate;

        currentSummary.AwayTeamShortName = awayTeam;
        currentSummary.HomeTeamShortName = homeTeam;
        currentSummary.MatchDate = convertedMatchDate;
      }

      MatchEvent matchEvent = new MatchEvent();
      matchEvent.Id = matchEventId;
      String event = elements.remove(0);
      int goalCount = 0;
      switch (event) {
        case "assist":
          matchEvent.EventName = "Assist";
          break;
        case "goal":
          matchEvent.EventName = "Goal";
          goalCount = 1;
          break;
        case "goalpenalty":
          matchEvent.EventName = "Goal (Penalty)";
          goalCount = 1;
          break;
        case "owngoal":
          matchEvent.EventName = "Own Goal";
          goalCount = -1;
          break;
        case "yellow":
          matchEvent.EventName = "Yellow Card";
          break;
        case "red":
          matchEvent.EventName = "Red Card";
          break;
        case "sub":
          matchEvent.EventName = "";
          break;
        case "penaltymissed":
          matchEvent.EventName = "Penalty Missed";
          break;
        default:
          matchEvent.EventName = "UNKNOWN";
      }

      matchEvent.TeamShortName = elements.remove(0);
      if (goalCount < 0) {
        if (matchEvent.TeamShortName.equals(homeTeam)) {
          currentSummary.AwayScore++;
        } else {
          currentSummary.HomeScore++;
        }
      } else if (goalCount > 0) {
        if (matchEvent.TeamShortName.equals(homeTeam)) {
          currentSummary.HomeScore++;
        } else {
          currentSummary.AwayScore++;
        }
      }

      matchEvent.MinuteOfEvent = Integer.parseInt(elements.remove(0));
      if (event.equals("sub") && matchEvent.EventName.isEmpty()) {
        System.out.println("Processing substitution.");
        // first player name is sub off, second player name is sub on
        String subOff = elements.remove(0);
        String subOn = elements.remove(0);

        // check for stoppage/extra-time
        boolean isStoppageTime = false;
        if (!elements.isEmpty()) {
          isStoppageTime = Boolean.parseBoolean(elements.remove(0));
        }

        boolean isAdditionalExtraTime = false;
        if (!elements.isEmpty()) {
          isAdditionalExtraTime = Boolean.parseBoolean(elements.remove(0));
        }

        matchEvent.EventName = "Substitution (off)";
        matchEvent.PlayerName = subOff;
        matchEvent.IsStoppageTime = isStoppageTime;
        matchEvent.IsAdditionalExtraTime = isAdditionalExtraTime;
        currentMatch.MatchEvents.put(matchEventId, matchEvent.toMap());

        matchEventId = UUID.randomUUID().toString();
        matchEvent.Id = matchEventId;
        matchEvent.EventName = "Substitution (on)";
        matchEvent.PlayerName = subOn;
        currentMatch.MatchEvents.put(matchEventId, matchEvent.toMap());
      } else if (!elements.isEmpty()) {
        System.out.println("Processing additional details on match event");
        // check for stoppage/extra-time
        boolean isStoppageTime = Boolean.parseBoolean(elements.get(0));
        if (!isStoppageTime) {
          // see if this is a players name
          matchEvent.PlayerName = elements.remove(0);
          if (!elements.isEmpty()) {
            isStoppageTime = Boolean.parseBoolean(elements.remove(0));
          }
        }

        boolean isAdditionalExtraTime = false;
        if (!elements.isEmpty()) {
          isAdditionalExtraTime = Boolean.parseBoolean(elements.remove(0));
        }

        matchEvent.IsAdditionalExtraTime = isAdditionalExtraTime;
        matchEvent.IsStoppageTime = isStoppageTime;
        currentMatch.MatchEvents.put(matchEventId, matchEvent.toMap());
      } else {
        currentMatch.MatchEvents.put(matchEventId, matchEvent.toMap());
      }
    }

    // add last match and summary
    currentMatch.IsFinal = true;
    matches.put(currentMatch.Id, currentMatch.toMap());
    currentSummary.IsFinal = true;
    matchSummaries.put(currentMatch.Id, currentSummary.toMap());

    Map<String, Map<String, Map<String, Object>>> yearlyMatches = new HashMap<>();
    yearlyMatches.put("2017", matches);
    Map<String, Map<String, Map<String, Map<String, Object>>>> finalMatches = new HashMap<>();
    finalMatches.put("matches", yearlyMatches);

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gson.toJson(finalMatches));

    Map<String, Map<String, Map<String, Object>>> yearlySummaries = new HashMap<>();
    yearlySummaries.put("2017", matchSummaries);
    Map<String, Map<String, Map<String, Map<String, Object>>>> finalSummaries = new HashMap<>();
    finalSummaries.put("MatchSummaries", yearlySummaries);

    gson = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gson.toJson(finalSummaries));
  }
}
