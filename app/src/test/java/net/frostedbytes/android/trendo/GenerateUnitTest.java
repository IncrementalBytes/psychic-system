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

    List<MatchSummary> matchSummaries = generateMatchSummaries();
    List<Trend> trends = generateTrends(matchSummaries);

    Map<String, Map<String, Object>> mappedSummaries = new HashMap<>();
    for (MatchSummary matchSummary : matchSummaries) {
      mappedSummaries.put(matchSummary.MatchId, matchSummary.toMap());
    }

    Map<String, Map<String, Map<String, Object>>> teamSummaries = new HashMap<>();
    teamSummaries.put(mShortName, mappedSummaries);

    Map<String, Map<String, Map<String, Map<String, Object>>>> yearlySummaries = new HashMap<>();
    yearlySummaries.put(String.valueOf(mYear), teamSummaries);
    Map<String, Map<String, Map<String, Map<String, Map<String, Object>>>>> finalSummaries = new HashMap<>();
    finalSummaries.put("MatchSummaries", yearlySummaries);

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gson.toJson(finalSummaries));

    Map<String, Map<String, Object>> mappedTrends = new HashMap<>();
    for (Trend trend : trends) {
      mappedTrends.put(trend.MatchId, trend.toMap());
    }

    Map<String, Map<String, Map<String, Object>>> finalTrends = new HashMap<>();
    finalTrends.put("Trends", mappedTrends);
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
    matchSummaries.sort(new Comparator<MatchSummary>() {
      @Override
      public int compare(MatchSummary summary1, MatchSummary summary2) {

        if (summary1.MatchDate < summary2.MatchDate) {
          return -1;
        } else if (summary1.MatchDate > summary2.MatchDate) {
          return 1;
        } else {
          return 0;
        }
      }
    });

    return matchSummaries;
  }

  private List<Trend> generateTrends(List<MatchSummary> matchSummaries) {

    List<Trend> trends = new ArrayList<>();
    List<Long> goalsAgainstList = new ArrayList<>();
    List<Long> goalsForList = new ArrayList<>();
    List<Long> goalDifferentialsList = new ArrayList<>();
    List<Long> totalPointsList = new ArrayList<>();
    List<Double> pointsPerGameList = new ArrayList<>();
    for (MatchSummary summary : matchSummaries) {
      Trend newTrend = new Trend();
      long goalsAgainst;
      long goalDifferential;
      long goalsFor;
      long totalPoints;

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

      long prevGoalAgainst = 0;
      long prevGoalDifferential = 0;
      long prevGoalFor = 0;
      long prevTotalPoints = 0;
      if (!trends.isEmpty()) {
        prevGoalAgainst = goalsAgainstList.get(goalsAgainstList.size() - 1);
        prevGoalDifferential = goalDifferentialsList.get(goalDifferentialsList.size() - 1);
        prevGoalFor = goalsForList.get(goalsForList.size() - 1);
        prevTotalPoints = totalPointsList.get(totalPointsList.size() - 1);
      }

      goalsAgainstList.add(goalsAgainst + prevGoalAgainst);
      newTrend.GoalsAgainst = new ArrayList<>(goalsAgainstList);

      goalDifferentialsList.add(goalDifferential + prevGoalDifferential);
      newTrend.GoalDifferential = new ArrayList<>(goalDifferentialsList);

      goalsForList.add(goalsFor + prevGoalFor);
      newTrend.GoalsFor = new ArrayList<>(goalsForList);

      totalPointsList.add(totalPoints + prevTotalPoints);
      newTrend.TotalPoints = new ArrayList<>(totalPointsList);

      double result = (double) totalPoints;
      if (!trends.isEmpty()) {
        // already added this match to the total points
        result = (totalPointsList.get(totalPointsList.size() - 1)) / (double) (totalPointsList.size());
      }

      pointsPerGameList.add(result);
      newTrend.PointsPerGame = new ArrayList<>(pointsPerGameList);

      newTrend.MatchDate = summary.MatchDate;
      newTrend.MatchId = summary.MatchId;
      newTrend.TeamName = mTeamName;
      trends.add(newTrend);
    }

    return trends;
  }
}
