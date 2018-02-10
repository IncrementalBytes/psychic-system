package net.frostedbytes.android.trendo;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import net.frostedbytes.android.trendo.models.MatchSummary;
import net.frostedbytes.android.trendo.models.Trend;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class GenerateUnitTest {

  @Parameter()
  public String mShortName;

  @Parameter(value = 1)
  public String mTeamName;

  @Parameter(value = 2)
  public int mYear;

  @Parameters
  public static Collection<Object[]> initParameters() {
    return Arrays.asList(new Object[][]{{"SEA", "Seattle Sounders FC", 2017}});
    // return Arrays.asList(new Object[][] { { "SEA", "Seattle Sounders FC", 2017 }, { "COL", "Colorado Rapids", 2017 } });
  }

  @Test
  public void generateResults() throws IOException {

    // create match summary objects
    List<MatchSummary> matchSummaries = generateMatchSummaries();

    // convert match summary objects into mapped objects
    Map<String, Map<String, Object>> mappedSummaries = new HashMap<>();
    for (MatchSummary matchSummary : matchSummaries) {
      mappedSummaries.put(matchSummary.MatchId, matchSummary.toMap());
    }

    // add mapped match summary objects to team node
    Map<String, Map<String, Map<String, Object>>> teamSummaries = new HashMap<>();
    teamSummaries.put(mShortName, mappedSummaries);

    // add team node (with mapped match summary objects) to year node
    Map<String, Map<String, Map<String, Map<String, Object>>>> yearlySummaries = new HashMap<>();
    yearlySummaries.put(String.valueOf(mYear), teamSummaries);

    // add year node to match summary parent node
    Map<String, Map<String, Map<String, Map<String, Map<String, Object>>>>> finalSummaries = new HashMap<>();
    finalSummaries.put(MatchSummary.ROOT, yearlySummaries);

    // print pretty json
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gson.toJson(finalSummaries));

    Map<String, Object> mappedTrends = generateTrends(matchSummaries);

    // add mapped match summary objects to team node
    Map<String, Map<String, Object>> teamTrends = new HashMap<>();
    teamTrends.put(mShortName, mappedTrends);

    // add team node (with mapped match summary objects) to year node
    Map<String, Map<String, Map<String, Object>>> yearlyTrends = new HashMap<>();
    yearlyTrends.put(String.valueOf(mYear), teamTrends);

    // add year node to match summary parent node
    Map<String, Map<String, Map<String, Map<String, Object>>>> finalTrends = new HashMap<>();
    finalTrends.put(Trend.ROOT, yearlyTrends);

    gson = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gson.toJson(finalTrends));
    assertEquals(1, 1);
  }

  private long convertDateToMilliseconds(String matchDate) {

    int year = Integer.parseInt(matchDate.substring(0, 4));
    int month = Integer.parseInt(matchDate.substring(4, 6));
    int day = Integer.parseInt(matchDate.substring(6, 8));

    // months are 0 index based
    Date date = new GregorianCalendar(year, month - 1, day, 0, 0, 0).getTime();
    return date.getTime();
  }

  private List<MatchSummary> generateMatchSummaries() throws IOException {

    List<MatchSummary> matchSummaries = new ArrayList<>();
    MatchSummary currentSummary = new MatchSummary();
    String parsableString;
    try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(String.valueOf(mYear) + "/" + mShortName + ".txt")) {
      try (Scanner s = new Scanner(inputStream)) {
        parsableString = s.useDelimiter("\\A").hasNext() ? s.next() : "";
      }
    }

    String[] lineSegments = parsableString.split("\r\n");
    for (String line : lineSegments) {
      if (line.startsWith("#")) {
        // comment line; ignore
        continue;
      } else if (line.isEmpty()) {
        // put current summary into collection
        currentSummary.IsFinal = true;
        matchSummaries.add(currentSummary);

        // update current summary
        currentSummary = new MatchSummary();
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

      if (!currentSummary.HomeTeamName.equals(homeTeam) || !currentSummary.AwayTeamName.equals(awayTeam)) {
        currentSummary.AwayScore = 0;
        currentSummary.AwayTeamName = awayTeam;
        currentSummary.HomeScore = 0;
        currentSummary.HomeTeamName = homeTeam;
        currentSummary.MatchDate = convertedMatchDate;
        currentSummary.MatchId = UUID.randomUUID().toString();
      } else {
        System.out.println("Same match; progressing to match events.");
      }

      String event = elements.remove(0);
      int goalCount = 0;
      switch (event) {
        case "assist":
          break;
        case "goal":
          goalCount = 1;
          break;
        case "goalpenalty":
          goalCount = 1;
          break;
        case "owngoal":
          goalCount = -1;
          break;
        case "yellow":
          break;
        case "red":
          break;
        case "sub":
          break;
        case "penaltymissed":
          break;
        default:
          System.out.println("Unknown event in: " + line);
          break;
      }

      String teamName = elements.remove(0);
      if (goalCount < 0) {
        if (teamName.equals(homeTeam)) {
          currentSummary.AwayScore++;
        } else {
          currentSummary.HomeScore++;
        }
      } else if (goalCount > 0) {
        if (teamName.equals(homeTeam)) {
          currentSummary.HomeScore++;
        } else {
          currentSummary.AwayScore++;
        }
      }
    }

    // put last summary into collection
    currentSummary.IsFinal = true;
    matchSummaries.add(currentSummary);

    // arrange this list by match date
    matchSummaries.sort((summary1, summary2) -> {

      if (summary1.MatchDate < summary2.MatchDate) {
        return -1;
      } else if (summary1.MatchDate > summary2.MatchDate) {
        return 1;
      } else {
        return 0;
      }
    });

    return matchSummaries;
  }

  private Map<String, Object> generateTrends(List<MatchSummary> matchSummaries) {

    Map<String, Object> mappedTrends = new HashMap<>();

    Map<String, Long> goalsAgainstMap = new HashMap<>();
    Map<String, Long> goalsForMap = new HashMap<>();
    Map<String, Long> goalDifferentialMap = new HashMap<>();
    Map<String, Long> totalPointsMap = new HashMap<>();
    Map<String, Double> pointsPerGameMap = new HashMap<>();

    long goalsAgainst;
    long goalDifferential;
    long goalsFor;
    long totalPoints;
    long prevGoalAgainst = 0;
    long prevGoalDifferential = 0;
    long prevGoalFor = 0;
    long prevTotalPoints = 0;
    for (MatchSummary summary : matchSummaries) {
      if (summary.HomeTeamName.equals(mTeamName)) {
        // targetTeam is the home team
        goalsAgainst = summary.AwayScore;
        goalDifferential = summary.HomeScore - summary.AwayScore;
        goalsFor = summary.HomeScore;
        if (summary.HomeScore > summary.AwayScore) {
          totalPoints = (long) 3;
        } else if (summary.HomeScore < summary.AwayScore) {
          totalPoints = (long) 0;
        } else {
          totalPoints = (long) 1;
        }
      } else {
        // targetTeam is the away team
        goalsAgainst = summary.HomeScore;
        goalDifferential = summary.AwayScore - summary.HomeScore;
        goalsFor = summary.AwayScore;
        if (summary.AwayScore > summary.HomeScore) {
          totalPoints = (long) 3;
        } else if (summary.AwayScore < summary.HomeScore) {
          totalPoints = (long) 0;
        } else {
          totalPoints = (long) 1;
        }
      }

      goalsAgainstMap.put(String.valueOf(summary.MatchDate), goalsAgainst + prevGoalAgainst);
      goalDifferentialMap.put(String.valueOf(summary.MatchDate), goalDifferential + prevGoalDifferential);
      goalsForMap.put(String.valueOf(summary.MatchDate), goalsFor + prevGoalFor);
      totalPointsMap.put(String.valueOf(summary.MatchDate), totalPoints + prevTotalPoints);

      double result = (double) totalPoints + prevTotalPoints;
      if (result > 0) {
        result = (totalPoints + prevTotalPoints) / (double) (totalPointsMap.size());
      }

      pointsPerGameMap.put(String.valueOf(summary.MatchDate), result);

      // update previous values for next pass
      prevGoalAgainst = goalsAgainst + prevGoalAgainst;
      prevGoalDifferential = goalDifferential + prevGoalDifferential;
      prevGoalFor = goalsFor + prevGoalFor;
      prevTotalPoints = totalPoints + prevTotalPoints;
    }

    mappedTrends.put("GoalsAgainst", goalsAgainstMap);
    mappedTrends.put("GoalDifferential", goalDifferentialMap);
    mappedTrends.put("GoalsFor", goalsForMap);
    mappedTrends.put("TotalPoints", totalPointsMap);
    mappedTrends.put("PointsPerGame", pointsPerGameMap);
    return mappedTrends;
  }
}
