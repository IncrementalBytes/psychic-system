package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.MatchSummary;
import net.frostedbytes.android.trendo.models.Team;
import net.frostedbytes.android.trendo.utils.LogUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import static net.frostedbytes.android.trendo.BaseActivity.BASE_TAG;

public class CommissionerFragment extends Fragment {

    private static final String TAG = BASE_TAG + CommissionerFragment.class.getSimpleName();

    public interface OnCommissionerListener {

        void onCommissionerInit(boolean isSuccessful);
    }

    private OnCommissionerListener mCallback;

    private ArrayList<MatchSummary> mRemoteMatchSummaries;
    private ArrayList<MatchSummary> mReviewMatchSummaries;
    private int mSeason;
    private ArrayList<Team> mRemoteTeams;
    private ArrayList<Team> mReviewTeams;

    private ViewPager mViewPager;

    public static CommissionerFragment newInstance(int season, ArrayList<Team> remoteTeams, ArrayList<MatchSummary> remoteMatchSummaries) {

        LogUtils.debug(TAG, "++newInstance(%d)", season);
        CommissionerFragment fragment = new CommissionerFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(BaseActivity.ARG_MATCH_SUMMARIES, remoteMatchSummaries);
        args.putInt(BaseActivity.ARG_SEASON, season);
        args.putParcelableArrayList(BaseActivity.ARG_TEAMS, remoteTeams);
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
            mRemoteMatchSummaries = arguments.getParcelableArrayList(BaseActivity.ARG_MATCH_SUMMARIES);
            mRemoteTeams = arguments.getParcelableArrayList(BaseActivity.ARG_TEAMS);
            mSeason = arguments.getInt(BaseActivity.ARG_SEASON);
        } else {
            LogUtils.warn(TAG, "Arguments were null.");
            mCallback.onCommissionerInit(false);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LogUtils.debug(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
        View view = inflater.inflate(R.layout.fragment_commissioner, container, false);
        mViewPager = view.findViewById(R.id.commissioner_view_pager);
        loadTeamData();
        loadMatchSummaryData();
        populateCommissionerData();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LogUtils.debug(TAG, "++onDestroy()");
        mReviewMatchSummaries = null;
        mReviewTeams = null;
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

    private void loadMatchSummaryData() {

        LogUtils.debug(TAG, "++loadMatchSummaryData()");
        String parsableString;
        String resourcePath = String.format(Locale.ENGLISH, "%d.txt", mSeason);
        LogUtils.debug(TAG, "Loading %s", resourcePath);
        BufferedReader reader = null;
        if (mReviewMatchSummaries == null) {
            mReviewMatchSummaries = new ArrayList<>();
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
                    mReviewMatchSummaries.add(currentSummary);
                    LogUtils.debug(TAG, "Adding %s to match summary collection.", currentSummary.toString());
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

    private void loadTeamData() {

        LogUtils.debug(TAG, "++loadTeamData()");
        String parsableString;
        String resourcePath = "Teams.txt";
        LogUtils.debug(TAG, "Loading %s", resourcePath);
        BufferedReader reader = null;
        if (mReviewTeams == null) {
            mReviewTeams = new ArrayList<>();
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
                for (Team team : mRemoteTeams) {
                    if (team.equals(currentTeam)) {
                        teamFound = true;
                        team.IsLocal = true;
                        break;
                    }
                }

                if (!teamFound) {
                    mReviewTeams.add(currentTeam);
                    LogUtils.debug(TAG, "Adding %s to team collection.", currentTeam.toString());
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

    private void populateCommissionerData() {

        LogUtils.debug(TAG, "++populateCommissionerData()");
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {

            @Override
            public Fragment getItem(int position) {

                switch (position) {
                    case 0:
                        return TeamDataFragment.newInstance(mReviewTeams);
                    case 1:
                        return MatchSummaryDataFragment.newInstance(mSeason, mReviewMatchSummaries, mRemoteTeams);
                    default:
                        mCallback.onCommissionerInit(false);
                        return null;
                }
            }

            @Override
            public int getCount() {

                return BaseActivity.NUM_COMMISSIONER_DATA;
            }

            @Override
            public CharSequence getPageTitle(int position) {

                switch (position) {
                    case 0:
                        return getString(R.string.teams);
                    case 1:
                        return getString(R.string.matches);
                    default:
                        mCallback.onCommissionerInit(false);
                        return null;
                }
            }
        });

        mCallback.onCommissionerInit(true);
    }
}
