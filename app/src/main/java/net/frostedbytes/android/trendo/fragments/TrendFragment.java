package net.frostedbytes.android.trendo.fragments;

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
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.models.UserPreference;
import net.frostedbytes.android.trendo.utils.LogUtils;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Trend;
import net.frostedbytes.android.trendo.utils.PathUtils;

public class TrendFragment extends Fragment {

  private static final String TAG = TrendFragment.class.getSimpleName();

  private static Trend mCompare;
  private static Trend mTrend;

  private ViewPager mViewPager;

  private UserPreference mUserPreference;

  private Query mCompareQuery;
  private Query mTrendQuery;
  private ValueEventListener mCompareValueListener;
  private ValueEventListener mTrendValueListener;

  public static TrendFragment newInstance(UserPreference userPreference) {

    LogUtils.debug(TAG, "++newInstance(UserPreference)");
    TrendFragment fragment = new TrendFragment();
    Bundle args = new Bundle();
    args.putSerializable(BaseActivity.ARG_USER_PREFERENCE, userPreference);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    LogUtils.debug(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_trend, container, false);
    mViewPager = view.findViewById(R.id.trend_view_pager);
    PagerTabStrip pagerTabStrip = view.findViewById(R.id.trend_view_pager_header);

    pagerTabStrip.getChildAt(1).setPadding(30, 15, 30, 15);
    pagerTabStrip.setDrawFullUnderline(false);

    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    LogUtils.debug(TAG, "++onAttach(Context)");
    Bundle arguments = getArguments();
    if (arguments != null) {
      mUserPreference = (UserPreference) arguments.getSerializable(BaseActivity.ARG_USER_PREFERENCE);
    } else {
      LogUtils.debug(TAG, "Arguments were null.");
    }

    if (mUserPreference != null && mUserPreference.Season > 0 && !mUserPreference.TeamId.isEmpty()) {
      String queryPath = PathUtils.combine(Trend.ROOT, mUserPreference.Season, mUserPreference.TeamId);
      LogUtils.debug(TAG, "Trend Query: %s",  queryPath);
      mTrendQuery = FirebaseDatabase.getInstance().getReference().child(queryPath);
      mTrendValueListener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

          mTrend = dataSnapshot.getValue(Trend.class);
          if (mTrend != null) {
            mTrend.Year = mUserPreference.Season;
            populateTrendData();
          } else {
            LogUtils.warn(TAG, "Could not get trend data.");
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

          LogUtils.debug(TAG, "++onCancelled(DatabaseError)");
          LogUtils.error(TAG, "%s", databaseError.getDetails());
        }
      };
      mTrendQuery.addValueEventListener(mTrendValueListener);

      if (mUserPreference.Compare != 0 || mUserPreference.Compare != mUserPreference.Season) {
        LogUtils.debug(TAG, "Adding %d trend data along side %d", mUserPreference.Compare, mUserPreference.Season);
        String compareQueryPath = PathUtils.combine(Trend.ROOT, String.valueOf(mUserPreference.Compare), mUserPreference.TeamId);
        LogUtils.debug(TAG, "CompareTo Query: %s", compareQueryPath);
        mCompareQuery = FirebaseDatabase.getInstance().getReference().child(compareQueryPath);
        mCompareValueListener = new ValueEventListener() {

          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            mCompare = dataSnapshot.getValue(Trend.class);
            if (mCompare != null) {
              mCompare.Year = mUserPreference.Compare;
              populateTrendData();
            } else {
              LogUtils.warn(TAG, "Could not get trend compare data.");
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

            LogUtils.debug(TAG, "++onCancelled(DatabaseError)");
            LogUtils.error(TAG, "%s", databaseError.getDetails());
          }
        };
        mCompareQuery.addValueEventListener(mCompareValueListener);
      }
    }
    else {
      LogUtils.error(TAG, "Failed to get user settings from arguments.");
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    LogUtils.debug(TAG, "++onDestroy()");
    if (mTrendQuery != null && mTrendValueListener != null) {
      mTrendQuery.removeEventListener(mTrendValueListener);
    }

    if (mCompareQuery != null && mCompareValueListener != null) {
      mCompareQuery.removeEventListener(mCompareValueListener);
    }

    mTrend = null;
    mCompare = null;
  }

  private void populateTrendData() {

    if (mUserPreference.Compare > 0 && (mCompare == null || mCompare.GoalsFor.isEmpty())) {
      return;
    }

    if (mTrend == null || mTrend.GoalsFor.isEmpty()) {
      return;
    }

    LogUtils.debug(TAG, "++populateTrendData()");
    mViewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {

      @Override
      public Fragment getItem(int position) {

        Trend trend = new Trend();
        Trend compare = new Trend();
        switch (position) {
          case 0:
            trend.GoalsFor = mTrend.GoalsFor;
            trend.GoalsAgainst = mTrend.GoalsAgainst;
            trend.GoalDifferential= mTrend.GoalDifferential;
            trend.Year = mTrend.Year;
            if (mUserPreference.Compare > 0) {
              compare.GoalsFor = mCompare.GoalsFor;
              compare.GoalsAgainst = mCompare.GoalsAgainst;
              compare.GoalDifferential = mCompare.GoalDifferential;
              compare.Year = mCompare.Year;
            }

            return LineChartFragment.newInstance(trend, compare);
          case 1:
            trend.TotalPoints = mTrend.TotalPoints;
            trend.PointsPerGame = mTrend.PointsPerGame;
            trend.Year = mTrend.Year;
            if (mUserPreference.Compare > 0) {
              compare.TotalPoints = mCompare.TotalPoints;
              compare.PointsPerGame = mCompare.PointsPerGame;
              compare.Year = mCompare.Year;
            }

            return LineChartFragment.newInstance(trend, compare);
          case 2:
            trend.MaxPointsPossible = mTrend.MaxPointsPossible;
            trend.PointsByAverage = mTrend.PointsByAverage;
            trend.Year = mTrend.Year;
            if (mUserPreference.Compare > 0) {
              compare.MaxPointsPossible = mCompare.MaxPointsPossible;
              compare.PointsByAverage = mCompare.PointsByAverage;
              compare.Year = mCompare.Year;
            }

            return LineChartFragment.newInstance(trend, compare);
          default:
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
            return "Goals";
          case 1:
            return "Points";
          case 2:
            return "Theoretical";
          default:
            return null;
        }
      }
    });
  }
}
