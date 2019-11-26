package net.frostedbytes.android.trendo.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.frostedbytes.android.trendo.ui.BaseActivity;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.db.entity.TrendEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class CardSummaryFragment extends Fragment {

  private static final String TAG = BaseActivity.BASE_TAG + "CardSummaryFragment";

  public interface OnCardSummaryListener {

    void onCardSummaryItemClicked();
    void onCardSummaryLoaded();
  }

  private OnCardSummaryListener mCallback;

  private CardView mCardMatches;
  private TextView mGoalDifferentialText;
  private TextView mGoalsAgainstText;
  private TextView mGoalsForText;
  private TextView mMaxPointsText;
  private TextView mPointsByAverageText;
  private TextView mPointsPerGameText;
  private TextView mTotalMatchesText;
  private TextView mTotalPointsText;

  private List<TrendEntity> mTrends;

  public static CardSummaryFragment newInstance(ArrayList<TrendEntity> trends) {

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
  public void onAttach(Context context) {
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
      mTrends = (ArrayList<TrendEntity>)arguments.getSerializable(BaseActivity.ARG_TRENDS);
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
    mCardMatches = view.findViewById(R.id.summary_card_matches);
    mGoalDifferentialText = view.findViewById(R.id.summary_text_goals_differential_value);
    mGoalsAgainstText = view.findViewById(R.id.summary_text_goals_against_value);
    mGoalsForText = view.findViewById(R.id.summary_text_goals_for_value);
    mMaxPointsText = view.findViewById(R.id.summary_text_max_points_value);
    mPointsByAverageText = view.findViewById(R.id.summary_text_points_by_average_value);
    mPointsPerGameText = view.findViewById(R.id.summary_text_points_per_game_value);
    mTotalMatchesText = view.findViewById(R.id.summary_text_matches_value);
    mTotalPointsText = view.findViewById(R.id.summary_text_total_points_value);

    updateUI();
  }

  /*
      Private Method(s)
   */
  private void updateUI() {

    if (mTrends != null && mTrends.size() > 0) {
      Log.d(TAG, "++updateUI()");
      mCardMatches.setEnabled(true);
      TrendEntity valueTrend = mTrends.get(mTrends.size() - 1);
      mGoalDifferentialText.setText(String.valueOf(valueTrend.GoalDifferential));
      mGoalsAgainstText.setText(String.valueOf(valueTrend.GoalsAgainst));
      mGoalsForText.setText(String.valueOf(valueTrend.GoalsFor));
      mMaxPointsText.setText(String.valueOf(valueTrend.MaxPointsPossible));
      mPointsByAverageText.setText(String.valueOf(valueTrend.PointsByAverage));
      mPointsPerGameText.setText(String.format(Locale.US, "%.03f", valueTrend.PointsPerGame));
      mTotalMatchesText.setText(String.valueOf(mTrends.size()));
      mTotalPointsText.setText(String.valueOf(valueTrend.TotalPoints));
      mCallback.onCardSummaryLoaded();
    } else {
      Log.d(TAG, "matchSummaries is empty.");
      mCardMatches.setEnabled(false);
    }
  }
}
