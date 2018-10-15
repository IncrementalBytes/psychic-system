package net.frostedbytes.android.trendo.fragments;

import static net.frostedbytes.android.trendo.BaseActivity.BASE_TAG;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Locale;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.models.Team;
import net.frostedbytes.android.trendo.models.UserPreference;
import net.frostedbytes.android.trendo.utils.LogUtils;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Trend;
import net.frostedbytes.android.trendo.utils.PathUtils;
import net.frostedbytes.android.trendo.utils.SortUtils;

public class TrendFragment extends Fragment {

  private static final String TAG = BASE_TAG + TrendFragment.class.getSimpleName();

  public interface OnTrendListener {

    void onTrendQueryFailure();
  }

  private OnTrendListener mCallback;

  private ArrayList<Team> mTeams;

  private static Trend mAhead;
  private static Trend mBehind;
  private static Trend mTrend;

  private ViewPager mViewPager;

  private UserPreference mUserPreference;

  private Query mAheadQuery;
  private Query mBehindQuery;
  private Query mTrendQuery;

  public static TrendFragment newInstance(UserPreference userPreference, ArrayList<Team> teams) {

    LogUtils.debug(TAG, "++newInstance(UserPreference)");
    TrendFragment fragment = new TrendFragment();
    Bundle args = new Bundle();
    args.putSerializable(BaseActivity.ARG_USER_PREFERENCE, userPreference);
    args.putParcelableArrayList(BaseActivity.ARG_TEAMS, teams);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    LogUtils.debug(TAG, "++onAttach(Context)");
    try {
      mCallback = (OnTrendListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(
        String.format(Locale.ENGLISH, "%s must implement onTrendQueryFailure().", context.toString()));
    }

    Bundle arguments = getArguments();
    if (arguments != null) {
      mUserPreference = (UserPreference) arguments.getSerializable(BaseActivity.ARG_USER_PREFERENCE);
      mTeams = arguments.getParcelableArrayList(BaseActivity.ARG_TEAMS);
      mTeams.sort(new SortUtils.ByTeamName());
    } else {
      LogUtils.debug(TAG, "Arguments were null.");
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    LogUtils.debug(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_trend, container, false);
    mViewPager = view.findViewById(R.id.trend_view_pager);
    PagerTabStrip pagerTabStrip = view.findViewById(R.id.trend_view_pager_header);

    pagerTabStrip.getChildAt(1).setPadding(30, 15, 30, 15);
    pagerTabStrip.setDrawFullUnderline(false);

    if (mUserPreference != null && mUserPreference.Season > 0 && !mUserPreference.TeamId.isEmpty()) {
      String queryPath = PathUtils.combine(Trend.ROOT, mUserPreference.Season, mUserPreference.TeamId);
      LogUtils.debug(TAG, "Trend Query: %s",  queryPath);
      mTrendQuery = FirebaseDatabase.getInstance().getReference().child(queryPath);
      mTrendQuery.addListenerForSingleValueEvent(new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

          mTrend = dataSnapshot.getValue(Trend.class);
          if (mTrend != null) {
            mTrend.TeamShortName = getTeamShortName(mUserPreference.TeamId);
            mTrend.Year = mUserPreference.Season;
            queryAheadTrend();
          } else {
            LogUtils.warn(TAG, "Could not get trend data.");
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

          LogUtils.debug(TAG, "++onCancelled(DatabaseError)");
          LogUtils.error(TAG, "%s", databaseError.getDetails());
        }
      });
    }
    else {
      LogUtils.error(TAG, "Failed to get user preferences from arguments.");
      mCallback.onTrendQueryFailure();
    }

    return view;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    LogUtils.debug(TAG, "++onDestroy()");
    mAheadQuery = null;
    mBehindQuery = null;
    mTrendQuery = null;
    mAhead = null;
    mBehind = null;
    mTrend = null;
  }

  private String getTeamShortName(String teamId) {
    for (Team team : mTeams) {
      if (team.Id.equals(teamId)) {
        return team.ShortName;
      }
    }
    return getString(R.string.not_available);
  }

  private void populateTrendData() {

    LogUtils.debug(TAG, "++populateTrendData()");
    if (mTrend == null || mTrend.GoalsFor.isEmpty()) {
      LogUtils.warn(TAG, "Failed when querying trend data.");
      mCallback.onTrendQueryFailure();
    } else {
      mViewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {

        @Override
        public Fragment getItem(int position) {

          Trend trend = new Trend();
          Trend ahead = new Trend();
          Trend behind = new Trend();
          switch (position) {
            case 0:
              trend.TeamShortName = mTrend.TeamShortName;
              trend.TotalPoints = mTrend.TotalPoints;
              trend.Year = mTrend.Year;

              ahead.TeamShortName = mAhead.TeamShortName;
              ahead.TotalPoints = mAhead.TotalPoints;
              ahead.Year = mAhead.Year;

              behind.TeamShortName = mBehind.TeamShortName;
              behind.TotalPoints = mBehind.TotalPoints;
              behind.Year = mBehind.Year;

              return LineChartFragment.newInstance(trend, ahead, behind);
            case 1:
              trend.TeamShortName = mTrend.TeamShortName;
              trend.PointsPerGame= mTrend.PointsPerGame;
              trend.Year = mTrend.Year;

              ahead.TeamShortName = mAhead.TeamShortName;
              ahead.PointsPerGame= mAhead.PointsPerGame;
              ahead.Year = mAhead.Year;

              behind.TeamShortName = mBehind.TeamShortName;
              behind.PointsPerGame= mBehind.PointsPerGame;
              behind.Year = mBehind.Year;

              return LineChartFragment.newInstance(trend, ahead, behind);
            case 2:
              trend.TeamShortName = mTrend.TeamShortName;
              trend.GoalsFor = mTrend.GoalsFor;
              trend.Year = mTrend.Year;

              ahead.TeamShortName = mAhead.TeamShortName;
              ahead.GoalsFor = mAhead.GoalsFor;
              ahead.Year = mAhead.Year;

              behind.TeamShortName = mBehind.TeamShortName;
              behind.GoalsFor = mBehind.GoalsFor;
              behind.Year = mBehind.Year;

              return LineChartFragment.newInstance(trend, ahead, behind);
            case 3:
              trend.TeamShortName = mTrend.TeamShortName;
              trend.GoalsAgainst = mTrend.GoalsAgainst;
              trend.Year = mTrend.Year;

              ahead.TeamShortName = mAhead.TeamShortName;
              ahead.GoalsAgainst = mAhead.GoalsAgainst;
              ahead.Year = mAhead.Year;

              behind.TeamShortName = mBehind.TeamShortName;
              behind.GoalsAgainst = mBehind.GoalsAgainst;
              behind.Year = mBehind.Year;

              return LineChartFragment.newInstance(trend, ahead, behind);
            case 4:
              trend.TeamShortName = mTrend.TeamShortName;
              trend.GoalDifferential = mTrend.GoalDifferential;
              trend.Year = mTrend.Year;

              ahead.TeamShortName = mAhead.TeamShortName;
              ahead.GoalDifferential = mAhead.GoalDifferential;
              ahead.Year = mAhead.Year;

              behind.TeamShortName = mBehind.TeamShortName;
              behind.GoalDifferential = mBehind.GoalDifferential;
              behind.Year = mBehind.Year;

              return LineChartFragment.newInstance(trend, ahead, behind);
            case 5:
              trend.TeamShortName = mTrend.TeamShortName;
              trend.PointsByAverage = mTrend.PointsByAverage;
              trend.Year = mTrend.Year;

              ahead.TeamShortName = mAhead.TeamShortName;
              ahead.PointsByAverage = mAhead.PointsByAverage;
              ahead.Year = mAhead.Year;

              behind.TeamShortName = mBehind.TeamShortName;
              behind.PointsByAverage = mBehind.PointsByAverage;
              behind.Year = mBehind.Year;

              return LineChartFragment.newInstance(trend, ahead, behind);
            case 6:
              trend.TeamShortName = mTrend.TeamShortName;
              trend.MaxPointsPossible = mTrend.MaxPointsPossible;
              trend.Year = mTrend.Year;

              ahead.TeamShortName = mAhead.TeamShortName;
              ahead.MaxPointsPossible = mAhead.MaxPointsPossible;
              ahead.Year = mAhead.Year;

              behind.TeamShortName = mBehind.TeamShortName;
              behind.MaxPointsPossible = mBehind.MaxPointsPossible;
              behind.Year = mBehind.Year;

              return LineChartFragment.newInstance(trend, ahead, behind);
            default:
              mCallback.onTrendQueryFailure();
              return null;
          }
        }

        @Override
        public int getCount() {

          return BaseActivity.NUM_TRENDS;
        }

        @Override
        public CharSequence getPageTitle(int position) {

          switch (position) {
            case 0:
              return getString(R.string.total_points);
            case 1:
              return getString(R.string.points_per_game);
            case 2:
              return getString(R.string.goals_for);
            case 3:
              return getString(R.string.goals_against);
            case 4:
              return getString(R.string.goal_differential);
            case 5:
              return getString(R.string.total_points_by_ppg);
            case 6:
              return getString(R.string.max_points_possible);
            default:
              mCallback.onTrendQueryFailure();
              return null;
          }
        }
      });
    }
  }

  private void queryAheadTrend() {

    LogUtils.debug(TAG, "++queryAheadTrend()");
    String queryPath = PathUtils.combine(Trend.ROOT, mUserPreference.Season, mUserPreference.AheadTeamId);
    LogUtils.debug(TAG, "Trend Query: %s",  queryPath);
    mAheadQuery = FirebaseDatabase.getInstance().getReference().child(queryPath);
    mAheadQuery.addListenerForSingleValueEvent(new ValueEventListener() {

      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        mAhead = dataSnapshot.getValue(Trend.class);
        if (mAhead != null) {
          mAhead.TeamShortName = getTeamShortName(mUserPreference.AheadTeamId);
          mAhead.Year = mUserPreference.Season;
          queryBehindTrend();
        } else {
          LogUtils.warn(TAG, "Could not get trend data for team ahead.");
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

        LogUtils.debug(TAG, "++queryAheadTrend::onCancelled(DatabaseError)");
        LogUtils.error(TAG, "%s", databaseError.getDetails());
      }
    });
  }

  private void queryBehindTrend() {

    LogUtils.debug(TAG, "++queryBehindTrend()");
    String queryPath = PathUtils.combine(Trend.ROOT, mUserPreference.Season, mUserPreference.BehindTeamId);
    LogUtils.debug(TAG, "Trend Query: %s",  queryPath);
    mBehindQuery = FirebaseDatabase.getInstance().getReference().child(queryPath);
    mBehindQuery.addListenerForSingleValueEvent(new ValueEventListener() {

      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        mBehind = dataSnapshot.getValue(Trend.class);
        if (mBehind != null) {
          mBehind.TeamShortName = getTeamShortName(mUserPreference.BehindTeamId);
          mBehind.Year = mUserPreference.Season;
          populateTrendData();
        } else {
          LogUtils.warn(TAG, "Could not get trend data for team behind.");
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

        LogUtils.debug(TAG, "++queryBehindTrend::onCancelled(DatabaseError)");
        LogUtils.error(TAG, "%s", databaseError.getDetails());
      }
    });
  }
}
