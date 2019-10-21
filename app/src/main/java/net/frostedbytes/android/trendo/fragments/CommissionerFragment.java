package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.FirebaseDatabase;

import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.MatchSummary;
import net.frostedbytes.android.trendo.models.Team;
import net.frostedbytes.android.trendo.models.Trend;
import net.frostedbytes.android.trendo.utils.LogUtils;
import net.frostedbytes.android.trendo.utils.PathUtils;
import net.frostedbytes.android.trendo.utils.SortUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static net.frostedbytes.android.trendo.BaseActivity.BASE_TAG;

public class CommissionerFragment extends Fragment {

    private static final String TAG = BASE_TAG + CommissionerFragment.class.getSimpleName();

    public interface OnCommissionerListener {

        void onReviewMatchSummaryData();
        void onReviewTeamData();
    }

    private OnCommissionerListener mCallback;

    private Button mReviewMatchSummaryDataButton;
    private Button mReviewTeamDataButton;

    private int mSeason;
    private ArrayList<MatchSummary> mRemoteMatchSummaries;
    private ArrayList<Team> mRemoteTeams;

    public static CommissionerFragment newInstance(int season, ArrayList<Team> teams, ArrayList<MatchSummary> matchSummaries) {

        LogUtils.debug(TAG, "++newInstance(%d)", season);
        CommissionerFragment fragment = new CommissionerFragment();
        Bundle args = new Bundle();
        args.putInt(BaseActivity.ARG_SEASON, season);
        args.putParcelableArrayList(BaseActivity.ARG_TEAMS, teams);
        args.putParcelableArrayList(BaseActivity.ARG_MATCH_SUMMARIES, matchSummaries);
        fragment.setArguments(args);
        return fragment;
    }

    /*
        Fragment Override(s)
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        LogUtils.debug(TAG, "++onAttach(Context)");
        try {
            mCallback = (OnCommissionerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
              String.format(Locale.ENGLISH, "Missing interface implementations for %s", context.toString()));
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            mSeason = arguments.getInt(BaseActivity.ARG_SEASON);
            mRemoteTeams = arguments.getParcelableArrayList(BaseActivity.ARG_TEAMS);
            mRemoteMatchSummaries = arguments.getParcelableArrayList(BaseActivity.ARG_MATCH_SUMMARIES);
        } else {
            LogUtils.warn(TAG, "Arguments were null.");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LogUtils.debug(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
        View view = inflater.inflate(R.layout.fragment_commissioner, container, false);
        mReviewMatchSummaryDataButton = view.findViewById(R.id.commissioner_button_match);
        mReviewMatchSummaryDataButton.setOnClickListener(v -> mCallback.onReviewMatchSummaryData());
        mReviewTeamDataButton = view.findViewById(R.id.commissioner_button_team);
        mReviewTeamDataButton.setOnClickListener(v -> mCallback.onReviewTeamData());

        checkTeamData();
        checkMatchSummaryData();
//        populateCommissionerData();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LogUtils.debug(TAG, "++onDestroy()");
        mRemoteMatchSummaries = null;
        mRemoteTeams = null;
    }

    /*
        Private Method(s)
     */
    private Team getTeam(String teamId) {

        for (Team team : mRemoteTeams) {
            if (team.Id.equals(teamId)) {
                return team;
            }
        }

        return new Team();
    }

    private void checkMatchSummaryData() {

        LogUtils.debug(TAG, "++checkMatchSummaryData()");
        String parsableString;
        String resourcePath = String.format(Locale.ENGLISH, "%d.txt", mSeason);
        LogUtils.debug(TAG, "Loading %s", resourcePath);
        BufferedReader reader = null;
        if (mRemoteMatchSummaries == null) {
            LogUtils.warn(TAG, "Match Summary data was null.");
            mReviewMatchSummaryDataButton.setVisibility(View.VISIBLE);
            return;
        }

        try {
            reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getContext()).getAssets().open(resourcePath)));
            while ((parsableString = reader.readLine()) != null) { //process line
                if (parsableString.startsWith("--")) { // comment line; ignore
                    continue;
                }

                // [DATE, DD.MM.YYYY];[HOMEID];[AWAYID];[HOMESCORE] : [AWAYSCORE]
                List<String> elements = new ArrayList<>(Arrays.asList(parsableString.split(";")));
                String dateString = elements.remove(0);
                List<String> dateElements = new ArrayList<>(Arrays.asList(dateString.split("\\.")));
                String dayElement = dateElements.remove(0);
                String monthElement = dateElements.remove(0);
                String yearElement = dateElements.remove(0);
                MatchSummary currentSummary = new MatchSummary();
                currentSummary.MatchDate = String.format(Locale.ENGLISH, "%s%s%s", yearElement, monthElement, dayElement);

                currentSummary.Id = UUID.randomUUID().toString();
                currentSummary.HomeId = elements.remove(0);
                currentSummary.HomeFullName = getTeam(currentSummary.HomeId).FullName;
                currentSummary.AwayId = elements.remove(0);
                currentSummary.AwayFullName = getTeam(currentSummary.AwayId).FullName;

                String scoreString = elements.remove(0);
                List<String> scoreElements = new ArrayList<>(Arrays.asList(scoreString.split(":")));
                currentSummary.HomeScore = Integer.parseInt(scoreElements.remove(0).trim());
                currentSummary.AwayScore = Integer.parseInt(scoreElements.remove(0).trim());

                // put last summary into collection
                currentSummary.IsFinal = true;
                currentSummary.IsLocal = true;

                // attempt to locate this match in existing list
                boolean matchFound = false;
                for (MatchSummary matchSummary : mRemoteMatchSummaries) {
                    if (matchSummary.equals(currentSummary)) {
                        matchFound = true;
                        matchSummary.IsLocal = true;
                        break;
                    }
                }

                if (!matchFound) {
                    LogUtils.debug(TAG, "Missing match summary; need review.");
                    mReviewMatchSummaryDataButton.setVisibility(View.VISIBLE);
                }
            }
        } catch (IOException e) {
            String errorMessage = getString(R.string.err_match_summary_data_load_failed);
            LogUtils.error(TAG, errorMessage);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    String errorMessage = getString(R.string.err_match_summary_data_cleanup_failed);
                    LogUtils.error(TAG, errorMessage);
                }
            }
        }
    }

    private void checkTeamData() {

        LogUtils.debug(TAG, "++checkTeamData()");
        String parsableString;
        String resourcePath = "Teams.txt";
        LogUtils.debug(TAG, "Loading %s", resourcePath);
        BufferedReader reader = null;
        if (mRemoteTeams == null) {
            LogUtils.warn(TAG, "Team data was null.");
            mReviewTeamDataButton.setVisibility(View.VISIBLE);
            return;
        }

        try {
            reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getContext()).getAssets().open(resourcePath)));
            while ((parsableString = reader.readLine()) != null) { //process line
                if (parsableString.startsWith("--")) { // comment line; ignore
                    continue;
                }

                // [UUID],[CONFERENCE_ID],[IS_DEFUNCT],[FRIENDLY_NAME],[ABBREVIATION]
                List<String> elements = new ArrayList<>(Arrays.asList(parsableString.split(";")));
                Team currentTeam = new Team();
                currentTeam.Id = elements.remove(0);
                currentTeam.ConferenceId = Integer.parseInt(elements.remove(0));
                currentTeam.Established = Integer.parseInt(elements.remove(0));
                currentTeam.Defunct = Integer.parseInt(elements.remove(0));
                currentTeam.FullName = elements.remove(0);
                currentTeam.ShortName = elements.remove(0);
                currentTeam.IsLocal = true;

                // attempt to locate this team in existing list
                boolean teamFound = false;
                for (Team remoteTeam : mRemoteTeams) {
                    if (remoteTeam.equals(currentTeam)) {
                        teamFound = true;
                        remoteTeam.IsLocal = true;
                        break;
                    }
                }

                if (!teamFound) {
                    LogUtils.debug(TAG, "Missing team; need review.");
                    mReviewTeamDataButton.setVisibility(View.VISIBLE);
                    break;
                }
            }
        } catch (IOException e) {
            String errorMessage = getString(R.string.err_team_data_load_failed);
            LogUtils.error(TAG, errorMessage);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    String errorMessage = getString(R.string.err_team_data_cleanup_failed);
                    LogUtils.error(TAG, errorMessage);
                }
            }
        }
    }

    private void generateTrends() {

        LogUtils.debug(TAG, "++generateTrends()");
        Map<String, Integer> aggregateMap = new HashMap<>();
        for (Team team : mRemoteTeams) {
            LogUtils.debug(TAG, "Generating trends for %s (%s)", team.FullName, team.Id);
            Map<String, Object> mappedTrends = new HashMap<>();
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

            ArrayList<MatchSummary> matchSummaries = new ArrayList<>(mRemoteMatchSummaries);
            Collections.reverse(matchSummaries);
            for (MatchSummary summary : matchSummaries) {
                if (!summary.IsFinal) {
                    continue;
                }

                if (summary.HomeId.equals(team.Id)) { // targetTeam is the home team
                    LogUtils.debug(TAG, "Processing match for %s on %s", team.ShortName, summary.MatchDate);
                    goalsAgainst = summary.AwayScore;
                    goalDifferential = summary.HomeScore - summary.AwayScore;
                    goalsFor = summary.HomeScore;
                    if (summary.HomeScore > summary.AwayScore) {
                        totalPoints = (long) 3;
                        team.TotalWins++;
                        LogUtils.debug(TAG, "%s won %d time(s): %d", team.ShortName, team.TotalWins, totalPoints);
                    } else if (summary.HomeScore < summary.AwayScore) {
                        totalPoints = (long) 0;
                        LogUtils.debug(TAG, "%s lost: %d", team.ShortName, totalPoints);
                    } else {
                        totalPoints = (long) 1;
                        LogUtils.debug(TAG, "%s tied: %d", team.ShortName, totalPoints);
                    }
                } else if (summary.AwayId.equals(team.Id)) { // targetTeam is the away team
                    LogUtils.debug(TAG, "Processing match for %s on %s", team.ShortName, summary.MatchDate);
                    goalsAgainst = summary.HomeScore;
                    goalDifferential = summary.AwayScore - summary.HomeScore;
                    goalsFor = summary.AwayScore;
                    if (summary.AwayScore > summary.HomeScore) {
                        totalPoints = (long) 3;
                        team.TotalWins++;
                        LogUtils.debug(TAG, "%s won %d time(s): %d", team.ShortName, team.TotalWins, totalPoints);
                    } else if (summary.AwayScore < summary.HomeScore) {
                        totalPoints = (long) 0;
                        LogUtils.debug(TAG, "%s lost: %d", team.ShortName, totalPoints);
                    } else {
                        totalPoints = (long) 1;
                        LogUtils.debug(TAG, "%s tied: %d", team.ShortName, totalPoints);
                    }
                } else { // not a match where team.Id played
                    continue;
                }

                String key = String.format(Locale.ENGLISH, "ID_%02d", ++matchDay);
                LogUtils.debug(
                  TAG,
                  "Calculating Goals Against, was %d, now %d",
                  prevGoalAgainst,
                  goalsAgainst + prevGoalAgainst);
                goalsAgainstMap.put(key, goalsAgainst + prevGoalAgainst);
                LogUtils.debug(
                  TAG,
                  "Calculating Goal Differential, was %d, now %d",
                  prevGoalDifferential,
                  goalDifferential + prevGoalDifferential);
                goalDifferentialMap.put(key, goalDifferential + prevGoalDifferential);
                LogUtils.debug(
                  TAG,
                  "Calculating Goals For, was %d, now %d",
                  prevGoalFor,
                  goalsFor + prevGoalFor);
                goalsForMap.put(key, goalsFor + prevGoalFor);
                LogUtils.debug(
                  TAG,
                  "Calculating Total Points, was %d, now %d",
                  prevTotalPoints,
                  totalPoints + prevTotalPoints);
                totalPointsMap.put(key, totalPoints + prevTotalPoints);
                long remainingMatches = --matchesRemaining;
                LogUtils.debug(
                  TAG,
                  "Calculating Max Possible Points, was %d, now %d",
                  prevTotalPoints,
                  (totalPoints + prevTotalPoints) + (remainingMatches * 3));
                maxPointsPossibleMap.put(key, (totalPoints + prevTotalPoints) + (remainingMatches * 3));

                double result = (double) totalPoints + prevTotalPoints;
                if (result > 0) {
                    result = (totalPoints + prevTotalPoints) / (double) (totalPointsMap.size());
                }

                pointsPerGameMap.put(key, result);
                pointsByAverageMap.put(key, (long) (result * totalMatches));

                // update previous values for next pass
                LogUtils.debug(
                  TAG,
                  "Setting previous Goals Against, was %d, now %d",
                  prevGoalAgainst,
                  goalsAgainst + prevGoalAgainst);
                prevGoalAgainst = goalsAgainst + prevGoalAgainst;
                LogUtils.debug(
                  TAG,
                  "Setting previous Goal Differential, was %d, now %d",
                  prevGoalDifferential,
                  goalDifferential + prevGoalDifferential);
                prevGoalDifferential = goalDifferential + prevGoalDifferential;
                LogUtils.debug(
                  TAG,
                  "Setting previous Goals For, was %d, now %d",
                  prevGoalFor,
                  goalsFor + prevGoalFor);
                prevGoalFor = goalsFor + prevGoalFor;
                LogUtils.debug(
                  TAG,
                  "Setting previous Total Points, was %d, now %d",
                  prevTotalPoints,
                  totalPoints + prevTotalPoints);
                prevTotalPoints = totalPoints + prevTotalPoints;
            }

            mappedTrends.put("GoalsAgainst", goalsAgainstMap);
            mappedTrends.put("GoalDifferential", goalDifferentialMap);
            mappedTrends.put("GoalsFor", goalsForMap);
            mappedTrends.put("TotalPoints", totalPointsMap);
            mappedTrends.put("PointsPerGame", pointsPerGameMap);
            mappedTrends.put("MaxPointsPossible", maxPointsPossibleMap);
            mappedTrends.put("PointsByAverage", pointsByAverageMap);

            String queryPath = PathUtils.combine(Trend.ROOT, mSeason, team.Id);
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(queryPath, mappedTrends);
            FirebaseDatabase.getInstance().getReference().updateChildren(
              childUpdates,
              (databaseError, databaseReference) -> {

                  if (databaseError != null && databaseError.getCode() < 0) {
                      LogUtils.error(TAG, "Could not generate trends: %s", databaseError.getMessage());
                  }
              });

            team.TotalPoints = prevTotalPoints;
            team.GoalDifferential = prevGoalDifferential;
            team.GoalsScored = prevGoalFor;
        }

        mRemoteTeams.sort(new SortUtils.ByTotalPoints());
        int tablePosition = mRemoteTeams.size() + 1;
        for (Team team : mRemoteTeams) {
            aggregateMap.put(team.Id, --tablePosition);
        }

        // now we need to sort the teams based on points and tiebreakers
        String queryPath = PathUtils.combine(Trend.AGGREGATE_ROOT, mSeason);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(queryPath, aggregateMap);
        FirebaseDatabase.getInstance().getReference().updateChildren(
          childUpdates,
          (databaseError, databaseReference) -> {

              if (databaseError != null && databaseError.getCode() < 0) {
                  String error = getString(R.string.err_aggregate_not_created);
                  LogUtils.error(TAG, "%s: %s", error, databaseError.getMessage());
              }
          });
    }
}
