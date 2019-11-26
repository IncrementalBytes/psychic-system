/*
 * Copyright 2019 Ryan Ward
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.frostedbytes.android.trendo.ui.fragments;

import static net.frostedbytes.android.trendo.ui.BaseActivity.BASE_TAG;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Locale;

import net.frostedbytes.android.trendo.db.entity.TrendEntity;
import net.frostedbytes.android.trendo.ui.BaseActivity;
import net.frostedbytes.android.trendo.db.entity.TeamEntity;
import net.frostedbytes.android.trendo.db.entity.User;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.utils.SortUtils;

public class TrendFragment extends Fragment {

    private static final String TAG = BASE_TAG + TrendFragment.class.getSimpleName();

    public interface OnTrendListener {

        void onShowByConference(boolean showBy);
        void onTrendInit(boolean isSuccessful);
    }

    private OnTrendListener mCallback;

    private ArrayList<TeamEntity> mTeams;

    private static TrendEntity mAhead;
    private static TrendEntity mBehind;
    private static TrendEntity mTrend;

    private ViewPager mViewPager;

    private boolean mOrderByConference;
    private User mUser;

    public static TrendFragment newInstance(User user, ArrayList<TeamEntity> teams) {

        return newInstance(user, teams, false);
    }

    public static TrendFragment newInstance(User user, ArrayList<TeamEntity> teams, boolean orderByConference) {

        Log.d(TAG, "++newInstance(UserPreference)");
        TrendFragment fragment = new TrendFragment();
        Bundle args = new Bundle();
        args.putBoolean(BaseActivity.ARG_ORDER_BY, orderByConference);
        args.putSerializable(BaseActivity.ARG_TEAMS, teams);
        args.putSerializable(BaseActivity.ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    /*
        Fragment Override(s)
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.d(TAG, "++onAttach(Context)");
        try {
            mCallback = (OnTrendListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                String.format(Locale.ENGLISH, "Missing interface implementations for %s", context.toString()));
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            mOrderByConference = arguments.getBoolean(BaseActivity.ARG_ORDER_BY);
            mUser = (User) arguments.getSerializable(BaseActivity.ARG_USER);
            mTeams = (ArrayList<TeamEntity>)arguments.getSerializable(BaseActivity.ARG_TEAMS);
            mTeams.sort(new SortUtils.ByTeamName());
        } else {
            Log.d(TAG, "Arguments were null.");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
        View view = inflater.inflate(R.layout.fragment_trend, container, false);
        mViewPager = view.findViewById(R.id.trend_view_pager);

        PagerTabStrip pagerTabStrip = view.findViewById(R.id.trend_view_pager_header);
        pagerTabStrip.getChildAt(1).setPadding(30, 15, 30, 15);
        pagerTabStrip.setDrawFullUnderline(false);

        ToggleButton orderingToggle = view.findViewById(R.id.trend_toggle_standing);
        orderingToggle.setChecked(mOrderByConference);
        orderingToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                mCallback.onShowByConference(true);
            } else {
                mCallback.onShowByConference(false);
            }
        });

        mAhead = new TrendEntity();
        mBehind = new TrendEntity();
        mTrend = new TrendEntity();

        if (mUser != null && mUser.Year > 0 && !mUser.TeamId.isEmpty()) {
//            String queryPath = PathUtils.combine(Trend.ROOT, mUser.Year, mUser.TeamId);
//            Log.d(TAG, "Trend Query: " + queryPath);
//            FirebaseDatabase.getInstance().getReference().child(queryPath).addListenerForSingleValueEvent(new ValueEventListener() {
//
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                    mTrend = dataSnapshot.getValue(Trend.class);
//                    if (mTrend != null) {
////                        mTrend.TeamObj = getTeam(mUser.TeamId);
//                        mTrend.Year = mUser.Year;
//                        if (mUser.AheadTeamId.equals(BaseActivity.DEFAULT_ID)) {
//                            Log.d(TAG, "Team is ranked first.");
//                            queryBehindTrend();
//                        } else {
//                            queryAheadTrend();
//                        }
//                    } else {
//                        Log.w(TAG, "Could not get trend data.");
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    Log.d(TAG, "++onCancelled(DatabaseError)");
//                    Log.e(TAG, databaseError.getDetails());
//                }
//            });
        } else {
            Log.e(TAG, "Failed to get user preferences from arguments.");
            mCallback.onTrendInit(false);
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "++onDestroy()");
        mAhead = null;
        mBehind = null;
        mTrend = null;
    }

    /*
        Private Method(s)
     */
    private TeamEntity getTeam(String teamId) {

        for (TeamEntity team : mTeams) {
            if (team.Id.equals(teamId)) {
                return team;
            }
        }

        return new TeamEntity();
    }

    private void populateTrendData() {

        Log.d(TAG, "++populateTrendData()");
//        if (mTrend == null || mTrend.GoalsFor.isEmpty()) {
//            Log.w(TAG, "Failed when querying trend data.");
//            mCallback.onTrendInit(false);
//        } else {
//            mViewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
//
//                @Override
//                public Fragment getItem(int position) {
//
//                    Trend trend = new Trend();
//                    Trend ahead = new Trend();
//                    Trend behind = new Trend();
//                    switch (position) {
//                        case 0:
//                            trend.TeamObj = mTrend.TeamObj;
//                            trend.TotalPoints = mTrend.TotalPoints;
//                            trend.Year = mTrend.Year;
//
//                            ahead.TeamObj = mAhead.TeamObj;
//                            ahead.TotalPoints = mAhead.TotalPoints;
//                            ahead.Year = mAhead.Year;
//
//                            behind.TeamObj = mBehind.TeamObj;
//                            behind.TotalPoints = mBehind.TotalPoints;
//                            behind.Year = mBehind.Year;
//
//                            return LineChartFragment.newInstance(trend, ahead, behind);
//                        case 1:
//                            trend.TeamObj = mTrend.TeamObj;
//                            trend.PointsPerGame = mTrend.PointsPerGame;
//                            trend.Year = mTrend.Year;
//
//                            ahead.TeamObj = mAhead.TeamObj;
//                            ahead.PointsPerGame = mAhead.PointsPerGame;
//                            ahead.Year = mAhead.Year;
//
//                            behind.TeamObj = mBehind.TeamObj;
//                            behind.PointsPerGame = mBehind.PointsPerGame;
//                            behind.Year = mBehind.Year;
//
//                            return LineChartFragment.newInstance(trend, ahead, behind);
//                        case 2:
//                            trend.TeamObj = mTrend.TeamObj;
//                            trend.GoalsFor = mTrend.GoalsFor;
//                            trend.Year = mTrend.Year;
//
//                            ahead.TeamObj = mAhead.TeamObj;
//                            ahead.GoalsFor = mAhead.GoalsFor;
//                            ahead.Year = mAhead.Year;
//
//                            behind.TeamObj = mBehind.TeamObj;
//                            behind.GoalsFor = mBehind.GoalsFor;
//                            behind.Year = mBehind.Year;
//
//                            return LineChartFragment.newInstance(trend, ahead, behind);
//                        case 3:
//                            trend.TeamObj = mTrend.TeamObj;
//                            trend.GoalsAgainst = mTrend.GoalsAgainst;
//                            trend.Year = mTrend.Year;
//
//                            ahead.TeamObj = mAhead.TeamObj;
//                            ahead.GoalsAgainst = mAhead.GoalsAgainst;
//                            ahead.Year = mAhead.Year;
//
//                            behind.TeamObj = mBehind.TeamObj;
//                            behind.GoalsAgainst = mBehind.GoalsAgainst;
//                            behind.Year = mBehind.Year;
//
//                            return LineChartFragment.newInstance(trend, ahead, behind);
//                        case 4:
//                            trend.TeamObj = mTrend.TeamObj;
//                            trend.GoalDifferential = mTrend.GoalDifferential;
//                            trend.Year = mTrend.Year;
//
//                            ahead.TeamObj = mAhead.TeamObj;
//                            ahead.GoalDifferential = mAhead.GoalDifferential;
//                            ahead.Year = mAhead.Year;
//
//                            behind.TeamObj = mBehind.TeamObj;
//                            behind.GoalDifferential = mBehind.GoalDifferential;
//                            behind.Year = mBehind.Year;
//
//                            return LineChartFragment.newInstance(trend, ahead, behind);
//                        case 5:
//                            trend.TeamObj = mTrend.TeamObj;
//                            trend.PointsByAverage = mTrend.PointsByAverage;
//                            trend.Year = mTrend.Year;
//
//                            ahead.TeamObj = mAhead.TeamObj;
//                            ahead.PointsByAverage = mAhead.PointsByAverage;
//                            ahead.Year = mAhead.Year;
//
//                            behind.TeamObj = mBehind.TeamObj;
//                            behind.PointsByAverage = mBehind.PointsByAverage;
//                            behind.Year = mBehind.Year;
//
//                            return LineChartFragment.newInstance(trend, ahead, behind);
//                        case 6:
//                            trend.TeamObj = mTrend.TeamObj;
//                            trend.MaxPointsPossible = mTrend.MaxPointsPossible;
//                            trend.Year = mTrend.Year;
//
//                            ahead.TeamObj = mAhead.TeamObj;
//                            ahead.MaxPointsPossible = mAhead.MaxPointsPossible;
//                            ahead.Year = mAhead.Year;
//
//                            behind.TeamObj = mBehind.TeamObj;
//                            behind.MaxPointsPossible = mBehind.MaxPointsPossible;
//                            behind.Year = mBehind.Year;
//
//                            return LineChartFragment.newInstance(trend, ahead, behind);
//                        default:
//                            mCallback.onTrendInit(false);
//                            return null;
//                    }
//                }
//
//                @Override
//                public int getCount() {
//
//                    return BaseActivity.NUM_TRENDS;
//                }
//
//                @Override
//                public CharSequence getPageTitle(int position) {
//
//                    switch (position) {
//                        case 0:
//                            return getString(R.string.total_points);
//                        case 1:
//                            return getString(R.string.points_per_game);
//                        case 2:
//                            return getString(R.string.goals_for);
//                        case 3:
//                            return getString(R.string.goals_against);
//                        case 4:
//                            return getString(R.string.goal_differential);
//                        case 5:
//                            return getString(R.string.total_points_by_ppg);
//                        case 6:
//                            return getString(R.string.max_points_possible);
//                        default:
//                            mCallback.onTrendInit(false);
//                            return null;
//                    }
//                }
//            });
//        }
    }

    private void queryAheadTrend() {

        Log.d(TAG, "++queryAheadTrend()");
//        String queryPath = PathUtils.combine(Trend.ROOT, mUser.Year, mUser.AheadTeamId);
//        Log.d(TAG, "Trend Query: " + queryPath);
//        FirebaseDatabase.getInstance().getReference().child(queryPath).addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                mAhead = dataSnapshot.getValue(Trend.class);
//                if (mAhead != null) {
////                    mAhead.TeamObj = getTeam(mUser.AheadTeamId);
//                    mAhead.Year = mUser.Year;
//                    if (mUser.BehindTeamId.equals(BaseActivity.DEFAULT_ID)) {
//                        Log.d(TAG, "Team is ranked last.");
//                        populateTrendData();
//                    } else {
//                        queryBehindTrend();
//                    }
//                } else {
//                    Log.w(TAG, "Could not get trend data for team ahead.");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                Log.d(TAG, "++queryAheadTrend::onCancelled(DatabaseError)");
//                Log.e(TAG, databaseError.getDetails());
//            }
//        });
    }

    private void queryBehindTrend() {

        Log.d(TAG, "++queryBehindTrend()");
//        String queryPath = PathUtils.combine(Trend.ROOT, mUser.Year, mUser.BehindTeamId);
//        Log.d(TAG, "Trend Query: " + queryPath);
//        FirebaseDatabase.getInstance().getReference().child(queryPath).addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                mBehind = dataSnapshot.getValue(Trend.class);
//                if (mBehind != null) {
////                    mBehind.TeamObj = getTeam(mUser.BehindTeamId);
//                    mBehind.Year = mUser.Year;
//                    populateTrendData();
//                } else {
//                    Log.w(TAG, "Could not get trend data for team behind.");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                Log.d(TAG, "++queryBehindTrend::onCancelled(DatabaseError)");
//                Log.e(TAG, databaseError.getDetails());
//            }
//        });
    }
}
