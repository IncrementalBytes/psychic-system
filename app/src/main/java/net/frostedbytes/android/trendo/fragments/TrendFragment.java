package net.frostedbytes.android.trendo.fragments;

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
import java.util.Calendar;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Trend;

public class TrendFragment extends Fragment {

  private static final String TAG = "TrendFragment";

  static final String ARG_MATCH_ID = "match_id";

  private static int NUM_TRENDS = 5;

  ViewPager mViewPager;
  PagerTabStrip mPagerTabStrip;

  private static Trend mTrend;
  private String mMatchId;

  private Query mTrendQuery;
  private ValueEventListener mTrendValueListener;

  public static TrendFragment newInstance(String matchId) {

    Log.d(TAG, "++newInstance(String)");
    TrendFragment fragment = new TrendFragment();
    Bundle args = new Bundle();
    args.putString(ARG_MATCH_ID, matchId);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_trend, container, false);
    mViewPager = view.findViewById(R.id.trend_view_pager);
    mPagerTabStrip = view.findViewById(R.id.trend_view_pager_header);

    // get arguments
    Bundle arguments = getArguments();
    if (arguments != null) {
      mMatchId = getArguments().getString(ARG_MATCH_ID);
    } else {
      mMatchId = BaseActivity.DEFAULT_ID;
    }

    // grab trend data for match
    String queryPath = Trend.ROOT + "/" + mMatchId;
    Log.d(TAG, "Query: " + queryPath);
    mTrendQuery = FirebaseDatabase.getInstance().getReference().child(queryPath);
    mTrendValueListener = new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        mTrend = dataSnapshot.getValue(Trend.class);
        if (mTrend != null) {
          Calendar calendar = Calendar.getInstance();
          calendar.setTimeInMillis(mTrend.MatchDate);
          mTrend.MatchId = dataSnapshot.getKey();
          populateTrendData();
        } else {
          Log.w(TAG, "Could not get trend data for " + mMatchId);
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.e(TAG, databaseError.getDetails());
      }
    };
    mTrendQuery.addValueEventListener(mTrendValueListener);

    // finish setting up view
    mPagerTabStrip.setBackgroundColor(Color.rgb(240, 240, 240));
    mPagerTabStrip.getChildAt(1).setPadding(30, 15, 30, 15);
    mPagerTabStrip.setDrawFullUnderline(false);
    mPagerTabStrip.setTabIndicatorColor(Color.rgb(240,240,240));

    return view;
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
            return LineChartFragment.newInstance(mTrend.TotalPointsMap);
          case 1: // fragment # 1 - this will show points per game
            //return LineChartFragment.newInstance(mTrend.PointsPerGameMap);
          case 2: // fragment # 2 - this will show goals against
            return LineChartFragment.newInstance(mTrend.GoalsAgainstMap);
          case 3: // fragment # 3 - this will show goals for
            return LineChartFragment.newInstance(mTrend.GoalsForMap);
          case 4: // fragment # 4 - this will show goal differential
            return LineChartFragment.newInstance(mTrend.GoalDifferentialMap);
          default:
            return null;
        }
      }

      @Override
      public int getCount() {

        return NUM_TRENDS;
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
