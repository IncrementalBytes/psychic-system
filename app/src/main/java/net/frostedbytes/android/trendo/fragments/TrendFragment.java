package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Trend;
import net.frostedbytes.android.trendo.models.UserSetting;

public class TrendFragment extends Fragment {

  private static final String TAG = "TrendFragment";

  ViewPager mViewPager;
  PagerTabStrip mPagerTabStrip;

  private long mMatchDate;
  private UserSetting mSettings;
  private static Trend mTrend;

  private Query mTrendQuery;
  private ValueEventListener mTrendValueListener;

  public static TrendFragment newInstance(UserSetting userSettings, long matchDate) {

    Log.d(TAG, "++newInstance(String)");
    TrendFragment fragment = new TrendFragment();
    Bundle args = new Bundle();
    args.putSerializable(BaseActivity.ARG_USER_SETTINGS, userSettings);
    args.putLong(BaseActivity.ARG_MATCH_DATE, matchDate);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_trend, container, false);
    mViewPager = view.findViewById(R.id.trend_view_pager);
    mPagerTabStrip = view.findViewById(R.id.trend_view_pager_header);

    mPagerTabStrip.setBackgroundColor(Color.rgb(240, 240, 240));
    mPagerTabStrip.getChildAt(1).setPadding(30, 15, 30, 15);
    mPagerTabStrip.setDrawFullUnderline(false);
    mPagerTabStrip.setTabIndicatorColor(Color.rgb(240,240,240));

    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    Log.d(TAG, "++onAttach(Context)");
    Bundle arguments = getArguments();
    if (arguments != null) {
      mMatchDate = arguments.getLong(BaseActivity.ARG_MATCH_DATE);
      mSettings = (UserSetting) arguments.getSerializable(BaseActivity.ARG_USER_SETTINGS);
    } else {
      Log.d(TAG, "Arguments were null.");
    }

    if (mSettings != null) {
      String queryPath = Trend.ROOT + "/" + String.valueOf(mSettings.Year) + "/" + mSettings.TeamShortName;
      Log.d(TAG, "Query: " + queryPath);
      mTrendQuery = FirebaseDatabase.getInstance().getReference().child(queryPath);
      mTrendValueListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

          mTrend = dataSnapshot.getValue(Trend.class);
          if (mTrend != null) {
            populateTrendData();
          } else {
            Log.w(TAG, "Could not get trend data.");
          }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

          Log.e(TAG, databaseError.getDetails());
        }
      };
      mTrendQuery.addValueEventListener(mTrendValueListener);
    }
    else {
      Log.e(TAG, "Failed to get user settings from arguments.");
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.d(TAG, "++onDestroy()");
    if (mTrendQuery != null && mTrendValueListener != null) {
      mTrendQuery.removeEventListener(mTrendValueListener);
    }
  }

  private void populateTrendData() {

    Log.d(TAG, "++populateTrendData()");
    mViewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {

      @Override
      public Fragment getItem(int position) {

        switch (position) {
          case 0: // fragment # 0 - this will show total points
            return LineChartFragment.newLongInstance(mTrend.TotalPoints, mMatchDate);
          case 1: // fragment # 1 - this will show points per game
            return LineChartFragment.newDoubleInstance(mTrend.PointsPerGame, mMatchDate);
          case 2: // fragment # 2 - this will show goals against
            return LineChartFragment.newLongInstance(mTrend.GoalsAgainst, mMatchDate);
          case 3: // fragment # 3 - this will show goals for
            return LineChartFragment.newLongInstance(mTrend.GoalsFor, mMatchDate);
          case 4: // fragment # 4 - this will show goal differential
            return LineChartFragment.newLongInstance(mTrend.GoalDifferential, mMatchDate);
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
            return "Total Points";
          case 1:
            return "Points per Game";
          case 2:
            return "Goals Against";
          case 3:
            return "Goals For";
          case 4:
            return "Goal Differential";
          default:
            return null;
        }
      }
    });
  }
}
