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
import net.frostedbytes.android.trendo.models.MatchSummary;
import net.frostedbytes.android.trendo.utils.LogUtils;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Trend;
import net.frostedbytes.android.trendo.models.UserSetting;

public class TrendFragment extends Fragment {

  private static final String TAG = TrendFragment.class.getSimpleName();

  private ViewPager mViewPager;

  private MatchSummary mMatchSummary;
  private UserSetting mSettings;
  private static Trend mCompare;
  private static Trend mTrend;

  private Query mCompareQuery;
  private Query mTrendQuery;
  private ValueEventListener mCompareValueListener;
  private ValueEventListener mTrendValueListener;

  public static TrendFragment newInstance(UserSetting userSettings, MatchSummary matchSummary) {

    LogUtils.debug(TAG, "++newInstance(String)");
    TrendFragment fragment = new TrendFragment();
    Bundle args = new Bundle();
    args.putSerializable(BaseActivity.ARG_USER_SETTINGS, userSettings);
    args.putSerializable(BaseActivity.ARG_MATCH_SUMMARY, matchSummary);
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
      mMatchSummary = (MatchSummary)arguments.getSerializable(BaseActivity.ARG_MATCH_SUMMARY);
      mSettings = (UserSetting) arguments.getSerializable(BaseActivity.ARG_USER_SETTINGS);
    } else {
      LogUtils.debug(TAG, "Arguments were null.");
    }

    if (mSettings != null) {
      String queryPath = Trend.ROOT + "/" + String.valueOf(mSettings.Year) + "/" + mSettings.TeamShortName;
      LogUtils.debug(TAG, "Trend Query: " + queryPath);
      mTrendQuery = FirebaseDatabase.getInstance().getReference().child(queryPath);
      mTrendValueListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

          mTrend = dataSnapshot.getValue(Trend.class);
          if (mTrend != null) {
            mTrend.Year = mSettings.Year;
            populateTrendData();
          } else {
            LogUtils.warn(TAG, "Could not get trend data.");
          }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

          LogUtils.error(TAG, databaseError.getDetails());
        }
      };
      mTrendQuery.addValueEventListener(mTrendValueListener);

      if (mSettings.CompareTo != 0 || mSettings.CompareTo != mSettings.Year) {
        LogUtils.debug(TAG, "Adding %d trend data along side %d", mSettings.CompareTo, mSettings.Year);
        String compareQueryPath = Trend.ROOT + "/" + String.valueOf(mSettings.CompareTo) + "/" + mSettings.TeamShortName;
        LogUtils.debug(TAG, "CompareTo Query: " + compareQueryPath);
        mCompareQuery = FirebaseDatabase.getInstance().getReference().child(compareQueryPath);
        mCompareValueListener = new ValueEventListener() {

          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {

            mCompare = dataSnapshot.getValue(Trend.class);
            if (mCompare != null) {
              mCompare.Year = mSettings.CompareTo;
              populateTrendData();
            } else {
              LogUtils.warn(TAG, "Could not get trend compare data.");
            }
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

            LogUtils.error(TAG, databaseError.getDetails());
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

    if (mSettings.CompareTo > 0 && (mCompare == null || mCompare.GoalsFor.isEmpty())) {
      return;
    }

    if (mTrend == null || mTrend.GoalsFor.isEmpty()) {
      return;
    }

    LogUtils.debug(TAG, "++populateTrendData()");
    mViewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {

      @Override
      public Fragment getItem(int position) {

        switch (position) {
          case 0:
            return GoalsLineChartFragment.newInstance(mTrend, mCompare, mMatchSummary);
          case 1:
            return PointsLineChartFragment.newInstance(mTrend, mCompare, mMatchSummary);
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
          default:
            return null;
        }
      }
    });
  }
}
