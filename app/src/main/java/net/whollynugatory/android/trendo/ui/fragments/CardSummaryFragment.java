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
package net.whollynugatory.android.trendo.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.whollynugatory.android.trendo.R;
import net.whollynugatory.android.trendo.common.Trend;
import net.whollynugatory.android.trendo.db.views.TrendDetails;
import net.whollynugatory.android.trendo.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class CardSummaryFragment extends Fragment {

  private static final String TAG = BaseActivity.BASE_TAG + "CardSummaryFragment";

  public interface OnCardSummaryListener {

    void onCardSummaryMatchListClicked();
    void onCardSummaryTrendClicked(Trend selectedTrend);
    void onCardSummaryLoaded();
  }

  private OnCardSummaryListener mCallback;

  private CardView mGoalDifferentialCard;
  private TextView mGoalDifferentialText;
  private CardView mGoalsAgainstCard;
  private TextView mGoalsAgainstText;
  private CardView mGoalsForCard;
  private TextView mGoalsForText;
  private CardView mMatchesCard;
  private TextView mMatchesText;
  private CardView mMaxPointsCard;
  private TextView mMaxPointsText;
  private CardView mPointsByAverageCard;
  private TextView mPointsByAverageText;
  private CardView mPointsPerGameCard;
  private TextView mPointsPerGameText;
  private CardView mTotalPointsCard;
  private TextView mTotalPointsText;

  private List<TrendDetails> mTrends;

  public static CardSummaryFragment newInstance(ArrayList<TrendDetails> trends) {

    Log.d(TAG, "++newInstance(ArrayList<>)");
    CardSummaryFragment fragment = new CardSummaryFragment();
    Bundle args = new Bundle();
    args.putSerializable(BaseActivity.ARG_TRENDS, trends);
    fragment.setArguments(args);
    return fragment;
  }

  /*
      Fragment Override(s)
   */
  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);

    Log.d(TAG, "++onAttach(Context)");
    try {
      mCallback = (OnCardSummaryListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(
        String.format(Locale.ENGLISH, "Missing interface implementations for %s", context.toString()));
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    Bundle arguments = getArguments();
    if (arguments != null) {
      mTrends = (ArrayList<TrendDetails>)arguments.getSerializable(BaseActivity.ARG_TRENDS);
    } else {
      Log.e(TAG, "Arguments were null.");
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    return inflater.inflate(R.layout.fragment_card_summary, container, false);
  }

  @Override
  public void onDetach() {
    super.onDetach();

    Log.d(TAG, "++onDetach()");
    mCallback = null;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.d(TAG, "++onDestroy()");
    mTrends = null;
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    Log.d(TAG, "++onViewCreated(View, Bundle)");
    mGoalDifferentialCard = view.findViewById(R.id.summary_card_goal_differential);
    mGoalDifferentialText = view.findViewById(R.id.summary_text_goals_differential_value);
    mGoalsAgainstCard = view.findViewById(R.id.summary_card_goals_against);
    mGoalsAgainstText = view.findViewById(R.id.summary_text_goals_against_value);
    mGoalsForCard = view.findViewById(R.id.summary_card_goals_for);
    mGoalsForText = view.findViewById(R.id.summary_text_goals_for_value);
    mMatchesCard = view.findViewById(R.id.summary_card_matches);
    mMatchesText = view.findViewById(R.id.summary_text_matches_value);
    mMaxPointsCard = view.findViewById(R.id.summary_card_max_points);
    mMaxPointsText = view.findViewById(R.id.summary_text_max_points_value);
    mPointsByAverageCard = view.findViewById(R.id.summary_card_points_by_average);
    mPointsByAverageText = view.findViewById(R.id.summary_text_points_by_average_value);
    mPointsPerGameCard = view.findViewById(R.id.summary_card_points_per_game);
    mPointsPerGameText = view.findViewById(R.id.summary_text_points_per_game_value);
    mTotalPointsCard = view.findViewById(R.id.summary_card_total_points);
    mTotalPointsText = view.findViewById(R.id.summary_text_total_points_value);

    updateUI();
  }

  /*
      Private Method(s)
   */
  private void updateUI() {

    if (mTrends != null && mTrends.size() > 0) {
      Log.d(TAG, "++updateUI()");
      TrendDetails valueTrend = mTrends.get(mTrends.size() - 1);

      mGoalDifferentialCard.setOnClickListener(v -> mCallback.onCardSummaryTrendClicked(Trend.GoalDifferential));
      mGoalDifferentialText.setText(String.valueOf(valueTrend.GoalDifferential));
      mGoalsAgainstCard.setOnClickListener(v -> mCallback.onCardSummaryTrendClicked(Trend.GoalsAgainst));
      mGoalsAgainstText.setText(String.valueOf(valueTrend.GoalsAgainst));
      mGoalsForCard.setOnClickListener(v -> mCallback.onCardSummaryTrendClicked(Trend.GoalsFor));
      mGoalsForText.setText(String.valueOf(valueTrend.GoalsFor));
      mMaxPointsCard.setOnClickListener(v -> mCallback.onCardSummaryTrendClicked(Trend.MaxPointsPossible));
      mMaxPointsText.setText(String.valueOf(valueTrend.MaxPointsPossible));
      mPointsByAverageCard.setOnClickListener(v -> mCallback.onCardSummaryTrendClicked(Trend.PointsByAverage));
      mPointsByAverageText.setText(String.valueOf(valueTrend.PointsByAverage));
      mPointsPerGameCard.setOnClickListener(v -> mCallback.onCardSummaryTrendClicked(Trend.PointsPerGame));
      mPointsPerGameText.setText(String.format(Locale.US, "%.03f", valueTrend.PointsPerGame));
      mMatchesCard.setOnClickListener(v -> mCallback.onCardSummaryMatchListClicked());
      mMatchesText.setText(String.valueOf(mTrends.size()));
      mTotalPointsCard.setOnClickListener(v -> mCallback.onCardSummaryTrendClicked(Trend.TotalPoints));
      mTotalPointsText.setText(String.valueOf(valueTrend.TotalPoints));
      mCallback.onCardSummaryLoaded();
    } else {
      Log.d(TAG, "matchSummaries is empty.");
      mGoalDifferentialCard.setEnabled(false);
      mGoalsAgainstCard.setEnabled(false);
      mGoalsForCard.setEnabled(false);
      mMatchesCard.setEnabled(false);
      mPointsByAverageCard.setEnabled(false);
      mPointsPerGameCard.setEnabled(false);
      mTotalPointsCard.setEnabled(false);
    }
  }
}
