package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.MatchSummary;
import net.frostedbytes.android.trendo.models.Team;
import net.frostedbytes.android.trendo.models.Trend;
import net.frostedbytes.android.trendo.utils.LogUtils;
import net.frostedbytes.android.trendo.utils.PathUtils;
import net.frostedbytes.android.trendo.utils.SortUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static net.frostedbytes.android.trendo.BaseActivity.BASE_TAG;

public class MatchSummaryDataFragment extends Fragment {

    private static final String TAG = BASE_TAG + MatchSummaryDataFragment.class.getSimpleName();

    public interface OnMatchSummaryDataListener {

        void onMatchSummaryDataSynchronized(boolean needsRefreshing);
    }

    private OnMatchSummaryDataListener mCallback;

    private RecyclerView mRecyclerView;

    private ArrayList<MatchSummary> mMatchSummaries;
    private int mSeason;
    private ArrayList<Team> mTeams;

    public static MatchSummaryDataFragment newInstance(int season, ArrayList<MatchSummary> matchSummaries, ArrayList<Team> teams) {

        LogUtils.debug(TAG, "++newInstance(MatchSummaries: %d)", matchSummaries.size());
        MatchSummaryDataFragment fragment = new MatchSummaryDataFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(BaseActivity.ARG_MATCH_SUMMARIES, matchSummaries);
        args.putInt(BaseActivity.ARG_SEASON, season);
        args.putParcelableArrayList(BaseActivity.ARG_TEAMS, teams);
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
            mCallback = (OnMatchSummaryDataListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                String.format(Locale.ENGLISH, "Missing interface implementations for %s", context.toString()));
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMatchSummaries = arguments.getParcelableArrayList(BaseActivity.ARG_MATCH_SUMMARIES);
            mSeason = arguments.getInt(BaseActivity.ARG_SEASON);
            mTeams = arguments.getParcelableArrayList(BaseActivity.ARG_TEAMS);
        } else {
            LogUtils.warn(TAG, "Arguments were null.");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LogUtils.debug(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
        View view = inflater.inflate(R.layout.fragment_default, container, false);
        if (mMatchSummaries.size() > 0) {
            view = inflater.inflate(R.layout.fragment_data_list, container, false);
            mRecyclerView = view.findViewById(R.id.data_list_view);
            final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(manager);
            Button synchronizeButton = view.findViewById(R.id.data_button_synchronize);
            synchronizeButton.setEnabled(true);
            synchronizeButton.setOnClickListener(v -> synchronizeMatchSummaries());
            updateUI();
        } else {
            TextView defaultMessage = view.findViewById(R.id.default_text_message);
            defaultMessage.setText(getString(R.string.data_synchronized));
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LogUtils.debug(TAG, "++onDestroy()");
        mMatchSummaries = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        LogUtils.debug(TAG, "++onResume()");
        updateUI();
    }

    /*
        Private Method(s)
     */
    private void generateTrends() {

        LogUtils.debug(TAG, "++generateTrends()");
        Map<String, Integer> aggregateMap = new HashMap<>();
        for (Team team : mTeams) {
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

            ArrayList<MatchSummary> matchSummaries = new ArrayList<>(mMatchSummaries);
            Collections.reverse(matchSummaries);
            for (MatchSummary summary : matchSummaries) {
                if (!summary.IsFinal) {
                    continue;
                }

                if (summary.HomeId.equals(team.Id)) { // targetTeam is the home team
                    goalsAgainst = summary.AwayScore;
                    goalDifferential = summary.HomeScore - summary.AwayScore;
                    goalsFor = summary.HomeScore;
                    if (summary.HomeScore > summary.AwayScore) {
                        totalPoints = (long) 3;
                        team.TotalWins++;
                    } else if (summary.HomeScore < summary.AwayScore) {
                        totalPoints = (long) 0;
                    } else {
                        totalPoints = (long) 1;
                    }
                } else if (summary.AwayId.equals(team.Id)) { // targetTeam is the away team
                    goalsAgainst = summary.HomeScore;
                    goalDifferential = summary.AwayScore - summary.HomeScore;
                    goalsFor = summary.AwayScore;
                    if (summary.AwayScore > summary.HomeScore) {
                        totalPoints = (long) 3;
                        team.TotalWins++;
                    } else if (summary.AwayScore < summary.HomeScore) {
                        totalPoints = (long) 0;
                    } else {
                        totalPoints = (long) 1;
                    }
                } else { // not a match where team.Id played
                    continue;
                }

                String key = String.format(Locale.ENGLISH, "ID_%02d", ++matchDay);
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

        mTeams.sort(new SortUtils.ByTotalPoints());
        int tablePosition = mTeams.size() + 1;
        for (Team team : mTeams) {
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

    private void synchronizeMatchSummaries() {

        LogUtils.debug(TAG, "++synchronizeMatchSummaries()");
        boolean needsRefreshing = false;
        for (MatchSummary matchSummary : mMatchSummaries) {
            LogUtils.debug(TAG, "Checking %s", matchSummary.toString());
            if (matchSummary.IsLocal && !matchSummary.IsRemote) {
                LogUtils.debug(TAG, "Missing %s from server data", matchSummary.toString());
                String queryPath = PathUtils.combine(MatchSummary.ROOT, mSeason, matchSummary.Id);
                FirebaseDatabase.getInstance().getReference().child(queryPath).setValue(
                    matchSummary,
                    (databaseError, databaseReference) -> {

                        if (databaseError != null) {
                            if (databaseError.getCode() == 0) {
                                LogUtils.debug(TAG, "Successfully added/edited match summary.");
                            } else if (databaseError.getCode() < 0) {
                                LogUtils.error(
                                    TAG,
                                    "Could not create/edit match summary: %s",
                                    databaseError.getMessage());
                            }
                        }
                    });

                needsRefreshing = true;
            } else if (!matchSummary.IsLocal && matchSummary.IsRemote) {
                LogUtils.warn(TAG, "Missing %s from local data", matchSummary.toString());
            }
        }

        if (needsRefreshing) {
            generateTrends();
        }

        mCallback.onMatchSummaryDataSynchronized(needsRefreshing);
    }

    private void updateUI() {

        if (mMatchSummaries != null && mMatchSummaries.size() > 0) {
            LogUtils.debug(TAG, "++updateUI()");
            MatchSummaryDataAdapter matchSummaryDataAdapter = new MatchSummaryDataAdapter(mMatchSummaries);
            mRecyclerView.setAdapter(matchSummaryDataAdapter);
        }
    }

    /**
     * Adapter class for MatchSummary objects
     */
    private class MatchSummaryDataAdapter extends RecyclerView.Adapter<MatchSummaryDataHolder> {

        private final List<MatchSummary> mMatchSummaries;

        MatchSummaryDataAdapter(List<MatchSummary> matchSummaries) {

            mMatchSummaries = matchSummaries;
        }

        @NonNull
        @Override
        public MatchSummaryDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new MatchSummaryDataHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull MatchSummaryDataHolder holder, int position) {

            MatchSummary matchSummary = mMatchSummaries.get(position);
            holder.bind(matchSummary);
        }

        @Override
        public int getItemCount() {

            return mMatchSummaries.size();
        }
    }

    /**
     * Holder class for MatchSummary objects
     */
    private class MatchSummaryDataHolder extends RecyclerView.ViewHolder {

        private final TextView mTitleTextView;
        private final TextView mDetailsTextView;
        private final ImageView mLocalImageView;
        private final ImageView mRemoteImageView;

        private MatchSummary mMatchSummary;

        MatchSummaryDataHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.data_item, parent, false));

            mTitleTextView = itemView.findViewById(R.id.data_text_title);
            mDetailsTextView = itemView.findViewById(R.id.data_text_details);
            mLocalImageView = itemView.findViewById(R.id.data_image_local);
            mRemoteImageView = itemView.findViewById(R.id.data_image_remote);
        }

        void bind(MatchSummary matchSummary) {

            mMatchSummary = matchSummary;
            mTitleTextView.setText(
                String.format(
                    Locale.ENGLISH,
                    "%s vs. %s",
                    mMatchSummary.HomeFullName,
                    mMatchSummary.AwayFullName));
            mDetailsTextView.setText(
                String.format(
                    Locale.getDefault(),
                    "%d - %d",
                    mMatchSummary.HomeScore,
                    mMatchSummary.AwayScore));
            if (getContext() != null) {
                if (mMatchSummary.IsLocal) {
                    mLocalImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_success_dark, getContext().getTheme()));
                } else {
                    mLocalImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_failure_dark, getContext().getTheme()));
                }

                if (mMatchSummary.IsRemote) {
                    mRemoteImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_success_dark, getContext().getTheme()));
                } else {
                    mRemoteImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_failure_dark, getContext().getTheme()));
                }
            } else {
                LogUtils.warn(TAG, "Failed to get theme from context.");
            }
        }
    }
}
