package net.frostedbytes.android.trendo;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import net.frostedbytes.android.trendo.models.MatchSummary;
import net.frostedbytes.android.trendo.models.Trend;
import org.junit.Test;

public class GenerateUnitTest {

  @Test
  public void generateResults() throws IOException {

    // create match summary objects
    Map<String, Map<String, Map<String, Object>>> yearlyTrends = new HashMap<>();
    Map<String, Map<String, Map<String, Object>>> yearlySummaries = new HashMap<>();
    Map<String, Map<String, Map<String, Map<String, Object>>>> finalSummaries = new HashMap<>();
    Map<String, Map<String, Map<String, Map<String, Object>>>> finalTrends = new HashMap<>();

    for (int i = 2017; i <= 2018; i++) {
      List<MatchSummary> matchSummaries = generateMatchSummaries(i);

      // convert match summary objects into mapped objects
      Map<String, Map<String, Object>> mappedSummaries = new HashMap<>();
      for (MatchSummary matchSummary : matchSummaries) {
        mappedSummaries.put(matchSummary.MatchId, matchSummary.toMap());
      }

      // add year node to match summary parent node
      yearlySummaries.put(String.valueOf(i), mappedSummaries);

      // add root node
      finalSummaries.put(MatchSummary.ROOT, yearlySummaries);

      // add mapped match summary objects to team node
      Map<String, Map<String, Object>> teamTrends = generateTrends(matchSummaries);

      // add team node (with mapped match summary objects) to year node
      yearlyTrends.put(String.valueOf(i), teamTrends);

      // add year node to match summary parent node
      finalTrends.put(Trend.ROOT, yearlyTrends);
    }

    // print pretty json
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gson.toJson(finalSummaries));
    gson = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gson.toJson(finalTrends));
    assertEquals(1, 1);
  }

  private List<MatchSummary> generateMatchSummaries(int year) throws IOException {

    List<MatchSummary> matchSummaries = new ArrayList<>();
    String parsableString;
    String resourcePath = String.format(Locale.ENGLISH, "%d.txt", year);
    try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath)) {
      try (Scanner s = new Scanner(inputStream)) {
        parsableString = s.useDelimiter("\\A").hasNext() ? s.next() : "";
      }
    }

    String[] lineSegments = parsableString.split("\r\n");
    for (String line : lineSegments) {
      if (line.startsWith("--")) { // comment line; ignore
        continue;
      }

      // [DATE, DD.MM.YYYY];[HOMEID];[AWAYID];[HOMESCORE] : [AWAYSCORE]
      System.out.println(String.format(Locale.ENGLISH, "Processing: %s", line));
      List<String> elements = new ArrayList<>(Arrays.asList(line.split(";")));
      String dateString = elements.remove(0);
      List<String> dateElements = new ArrayList<>(Arrays.asList(dateString.split("\\.")));
      String dayElement = dateElements.remove(0);
      String monthElement = dateElements.remove(0);
      String yearElement = dateElements.remove(0);
      MatchSummary currentSummary = new MatchSummary();
      currentSummary.MatchDate = String.format(Locale.ENGLISH, "%s%s%s", yearElement, monthElement, dayElement);

      currentSummary.MatchId = UUID.randomUUID().toString();
      currentSummary.HomeId = elements.remove(0);
      currentSummary.AwayId = elements.remove(0);

      String scoreString = elements.remove(0);
      List<String> scoreElements = new ArrayList<>(Arrays.asList(scoreString.split(":")));
      currentSummary.HomeScore = Integer.parseInt(scoreElements.remove(0).trim());
      currentSummary.AwayScore = Integer.parseInt(scoreElements.remove(0).trim());

      // put last summary into collection
      currentSummary.IsFinal = true;
      matchSummaries.add(currentSummary);
    }

    // arrange this list by match date
    matchSummaries.sort((summary1, summary2) -> Integer.compare(summary1.MatchDate.compareTo(summary2.MatchDate), 0));

    return matchSummaries;
  }

  private Map<String, Map<String, Object>> generateTrends(List<MatchSummary> matchSummaries) throws IOException {

    Map<String, Map<String, Object>> mappedTrends = new HashMap<>();
    String parsableString;
    try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("Teams.txt")) {
      try (Scanner s = new Scanner(inputStream)) {
        parsableString = s.useDelimiter("\\A").hasNext() ? s.next() : "";
      }
    }

    String[] lineSegments = parsableString.split("\r\n");
    for (String line : lineSegments) {
      if (line.startsWith("--")) { // comment line; ignore
        continue;
      }

      // [TEAMID],[TEAMNAME],[TEAMSHORTNAME]
      System.out.println(String.format(Locale.ENGLISH, "Processing: %s", line));
      List<String> elements = new ArrayList<>(Arrays.asList(line.split(",")));
      String currentTeamId = elements.remove(0);

      // search for all games this teamId participated in
      List<MatchSummary> teamMatches = new ArrayList<>();
      for (MatchSummary summary : matchSummaries) {
        if (summary.AwayId.equals(currentTeamId) || summary.HomeId.equals(currentTeamId)) {
          teamMatches.add(summary);
        }
      }

      // process trends for these matches
      teamMatches.sort((summary1, summary2) -> Integer.compare(summary1.MatchDate.compareTo(summary2.MatchDate), 0));

      Map<String, Object> teamTrends = new HashMap<>();
      Map<String, Long> goalsAgainstMap = new HashMap<>();
      Map<String, Long> goalsForMap = new HashMap<>();
      Map<String, Long> goalDifferentialMap = new HashMap<>();
      Map<String, Long> totalPointsMap = new HashMap<>();
      Map<String, Double> pointsPerGameMap = new HashMap<>();
      Map<String, Long> maxPointsPossibleMap = new HashMap<>();
      Map<String, Long> pointsByAverageMap = new HashMap<>();

      long goalsAgainst;
      long goalDifferential;
      long goalsFor;
      long totalPoints;
      long prevGoalAgainst = 0;
      long prevGoalDifferential = 0;
      long prevGoalFor = 0;
      long prevTotalPoints = 0;
      long totalMatches = 34;
      long matchesRemaining = totalMatches;

      int matchDay = 0;
      for (MatchSummary summary : teamMatches) {
        System.out.println(
          String .format(
            Locale.ENGLISH,
            "Processing %s (%d) vs. %s (%d) on %s",
            summary.HomeId,
            summary.HomeScore,
            summary.AwayId,
            summary.AwayScore,
            summary.MatchDate));
        if (summary.HomeId.equals(currentTeamId)) { // targetTeam is the home team
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
        } else if (summary.AwayId.equals(currentTeamId)) { // targetTeam is the away team
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
        } else {
          System.out.println(String.format(Locale.ENGLISH, "%s is neither Home or Away; skipping.", currentTeamId));
          continue;
        }

        matchDay += 1;
        String key = String.format(Locale.ENGLISH, "ID_%02d", matchDay);
        goalsAgainstMap.put(key, goalsAgainst + prevGoalAgainst);
        goalDifferentialMap.put(key, goalDifferential + prevGoalDifferential);
        goalsForMap.put(key, goalsFor + prevGoalFor);
        totalPointsMap.put(key, totalPoints + prevTotalPoints);
        maxPointsPossibleMap.put(key, (totalPoints + prevTotalPoints) + (--matchesRemaining * 3));

        double result = (double) totalPoints + prevTotalPoints;
        if (result > 0) {
          result = (totalPoints + prevTotalPoints) / (double) (totalPointsMap.size());
        }

        pointsPerGameMap.put(key, result);
        pointsByAverageMap.put(key, (long) (result * totalMatches));

        // update previous values for next pass
        prevGoalAgainst = goalsAgainst + prevGoalAgainst;
        prevGoalDifferential = goalDifferential + prevGoalDifferential;
        prevGoalFor = goalsFor + prevGoalFor;
        prevTotalPoints = totalPoints + prevTotalPoints;
      }

      if (goalsAgainstMap.size() > 0) {
        teamTrends.put("GoalsAgainst", goalsAgainstMap);
      }

      if (goalDifferentialMap.size() > 0) {
        teamTrends.put("GoalDifferential", goalDifferentialMap);
      }

      if (goalsForMap.size() > 0) {
        teamTrends.put("GoalsFor", goalsForMap);
      }

      if (totalPointsMap.size() > 0) {
        teamTrends.put("TotalPoints", totalPointsMap);
      }

      if (pointsPerGameMap.size() > 0) {
        teamTrends.put("PointsPerGame", pointsPerGameMap);
      }

      if (maxPointsPossibleMap.size() > 0) {
        teamTrends.put("MaxPointsPossible", maxPointsPossibleMap);
      }

      if (pointsByAverageMap.size() > 0) {
        teamTrends.put("PointsByAverage", pointsByAverageMap);
      }

      if (teamTrends.size() > 0) {
        mappedTrends.put(currentTeamId, teamTrends);
      }
    }

    return mappedTrends;
  }
}
