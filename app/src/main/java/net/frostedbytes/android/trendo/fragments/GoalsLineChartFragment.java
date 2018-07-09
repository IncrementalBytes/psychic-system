package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.models.MatchSummary;
import net.frostedbytes.android.trendo.utils.LogUtils;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Trend;
import net.frostedbytes.android.trendo.views.CustomMarkerView;

public class GoalsLineChartFragment extends Fragment {

  private static final String TAG = GoalsLineChartFragment.class.getSimpleName();

  private Switch mAgainstSwitch;
  private Switch mDifferentialSwitch;
  private Switch mForSwitch;
  private LineChart mLineChart;

  private LineDataSet mAgainstDataSet;
  private List<Entry> mAgainstEntries;
  private Trend mCompare;
  private LineDataSet mCompareAgainstDataSet;
  private List<Entry> mCompareAgainstEntries;
  private LineDataSet mCompareDifferentialDataSet;
  private List<Entry> mCompareDifferentialEntries;
  private LineDataSet mCompareForDataSet;
  private List<Entry> mCompareForEntries;
  private LineDataSet mDifferentialDataSet;
  private List<Entry> mDifferentialEntries;
  private LineDataSet mForDataSet;
  private List<Entry> mForEntries;
  private MatchSummary mHighlightSummary;
  private Trend mTrend;

  public static GoalsLineChartFragment newInstance(Trend trend, Trend compare, MatchSummary matchSummary) {

    LogUtils.debug(TAG, "++newInstance(Trend, Trend, MatchSummary)");
    GoalsLineChartFragment fragment = new GoalsLineChartFragment();
    Bundle args = new Bundle();
    args.putSerializable(BaseActivity.ARG_TREND, trend);
    if (compare != null) {
      args.putSerializable(BaseActivity.ARG_COMPARE, compare);
    }

    args.putSerializable(BaseActivity.ARG_MATCH_SUMMARY, matchSummary);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    LogUtils.debug(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_line_chart, container, false);
    mLineChart = view.findViewById(R.id.line_chart);
    mAgainstSwitch = view.findViewById(R.id.line_chart_switch_left);
    mDifferentialSwitch = view.findViewById(R.id.line_chart_switch_right);
    mForSwitch = view.findViewById(R.id.line_chart_switch_center);

    mAgainstSwitch.setText(R.string.goals_against);
    mDifferentialSwitch.setText(R.string.goal_differential);
    mForSwitch.setText(R.string.goals_for);
    mForSwitch.setVisibility(View.VISIBLE);
    mForSwitch.setChecked(true);
    mForSwitch.setOnCheckedChangeListener((compoundButton, checked) -> updateUI());
    mAgainstSwitch.setOnCheckedChangeListener((compoundButton, checked) -> updateUI());
    mDifferentialSwitch.setOnCheckedChangeListener((compoundButton, checked) -> updateUI());

    if (mCompareForEntries.size() > 0) {
      mCompareForDataSet = new LineDataSet(
        mCompareForEntries,
        String.format(Locale.ENGLISH, "%s - %d", getResources().getString(R.string.goals_for), mCompare.Year));
      mCompareForDataSet.setAxisDependency(AxisDependency.LEFT);
      if (getContext() != null) {
        mCompareForDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorForCompare));
      } else {
        mCompareForDataSet.setColor(Color.GREEN);
      }

      mCompareForDataSet.setLineWidth(1.0f);
      mCompareForDataSet.enableDashedLine(10, 10, 0);
      mCompareForDataSet.setDrawCircles(false);
      mCompareForDataSet.setDrawValues(false);
      mCompareForDataSet.setHighlightEnabled(false);
      mCompareForDataSet.setDrawHighlightIndicators(false);
      mCompareForDataSet.setDrawHorizontalHighlightIndicator(false);
      mForDataSet = new LineDataSet(
        mForEntries,
        String.format(Locale.ENGLISH, "%s - %d", getResources().getString(R.string.goals_for), mTrend.Year));
    } else {
      mForDataSet = new LineDataSet(mForEntries, getResources().getString(R.string.goals_for));
    }

    mForDataSet.setAxisDependency(AxisDependency.LEFT);
    if (getContext() != null) {
      mForDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorFor));
    } else {
      mForDataSet.setColor(Color.GREEN);
    }

    mForDataSet.setLineWidth(2.0f);
    mForDataSet.disableDashedLine();
    mForDataSet.setDrawCircles(false);
    mForDataSet.setDrawValues(false);
    mForDataSet.setHighlightEnabled(true);
    mForDataSet.setDrawHighlightIndicators(true);
    mForDataSet.setDrawHorizontalHighlightIndicator(false);

    if (mCompareAgainstEntries.size() > 0) {
      mCompareAgainstDataSet = new LineDataSet(
        mCompareAgainstEntries,
        String.format(Locale.ENGLISH, "%s - %d", getResources().getString(R.string.goals_against), mCompare.Year));
      mCompareAgainstDataSet.setAxisDependency(AxisDependency.LEFT);
      if (getContext() != null) {
        mCompareAgainstDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorAgainstCompare));
      } else {
        mCompareAgainstDataSet.setColor(Color.YELLOW);
      }

      mCompareAgainstDataSet.setLineWidth(1.0f);
      mCompareAgainstDataSet.enableDashedLine(10, 10, 0);
      mCompareAgainstDataSet.setDrawCircles(false);
      mCompareAgainstDataSet.setDrawValues(false);
      mCompareAgainstDataSet.setHighlightEnabled(false);
      mCompareAgainstDataSet.setDrawHighlightIndicators(false);
      mCompareAgainstDataSet.setDrawHorizontalHighlightIndicator(false);
      mAgainstDataSet = new LineDataSet(
        mAgainstEntries,
        String.format(Locale.ENGLISH, "%s - %d", getResources().getString(R.string.goals_against), mTrend.Year));
    } else {
      mAgainstDataSet = new LineDataSet(mAgainstEntries, getResources().getString(R.string.goals_against));
    }

    mAgainstDataSet.setAxisDependency(AxisDependency.LEFT);
    if (getContext() != null) {
      mAgainstDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorAgainst));
    } else {
      mAgainstDataSet.setColor(Color.YELLOW);
    }

    mAgainstDataSet.setLineWidth(2.0f);
    mAgainstDataSet.disableDashedLine();
    mAgainstDataSet.setDrawCircles(false);
    mAgainstDataSet.setDrawValues(false);
    mAgainstDataSet.setHighlightEnabled(true);
    mAgainstDataSet.setDrawHighlightIndicators(true);
    mAgainstDataSet.setDrawHorizontalHighlightIndicator(false);

    if (mCompareDifferentialEntries.size() > 0) {
      mCompareDifferentialDataSet = new LineDataSet(
        mCompareDifferentialEntries,
        String.format(Locale.ENGLISH, "%s - %d", getResources().getString(R.string.goal_differential), mCompare.Year));
      mCompareDifferentialDataSet.setAxisDependency(AxisDependency.RIGHT);
      if (getContext() != null) {
        mCompareDifferentialDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorDifferentialCompare));
      } else {
        mCompareDifferentialDataSet.setColor(Color.RED);
      }

      mCompareDifferentialDataSet.setLineWidth(1.0f);
      mCompareDifferentialDataSet.enableDashedLine(10, 10, 0);
      mCompareDifferentialDataSet.setDrawCircles(false);
      mCompareDifferentialDataSet.setDrawValues(false);
      mCompareDifferentialDataSet.setHighlightEnabled(false);
      mCompareDifferentialDataSet.setDrawHighlightIndicators(false);
      mCompareDifferentialDataSet.setDrawHorizontalHighlightIndicator(false);
      mDifferentialDataSet = new LineDataSet(
        mDifferentialEntries,
        String.format(Locale.ENGLISH, "%s - %d", getResources().getString(R.string.goal_differential), mTrend.Year));
    } else {
      mDifferentialDataSet = new LineDataSet(mDifferentialEntries, getResources().getString(R.string.goal_differential));
    }

    mDifferentialDataSet.setAxisDependency(AxisDependency.RIGHT);
    if (getContext() != null) {
      mDifferentialDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorDifferential));
    } else {
      mDifferentialDataSet.setColor(Color.RED);
    }

    mDifferentialDataSet.setLineWidth(1.0f);
    mDifferentialDataSet.disableDashedLine();
    mDifferentialDataSet.setDrawCircles(false);
    mDifferentialDataSet.setDrawValues(false);
    mDifferentialDataSet.setHighlightEnabled(true);
    mDifferentialDataSet.setDrawHighlightIndicators(true);
    mDifferentialDataSet.setDrawHorizontalHighlightIndicator(false);

    mLineChart.getDescription().setEnabled(false);

    YAxis leftAxis = mLineChart.getAxisLeft();
    if (getContext() != null) {
      leftAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextPrimary));
      leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
      leftAxis.setDrawGridLines(false);
      leftAxis.setDrawZeroLine(true);

      YAxis rightAxis = mLineChart.getAxisRight();
      rightAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextPrimary));
      rightAxis.setEnabled(true);

      XAxis bottomAxis = mLineChart.getXAxis();
      bottomAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextPrimary));
      bottomAxis.setDrawGridLines(false);
      bottomAxis.setPosition(XAxisPosition.BOTTOM);
      bottomAxis.setEnabled(false);

      Legend legend = mLineChart.getLegend();
      legend.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextPrimary));
      legend.setXEntrySpace(10f);
      legend.setYEntrySpace(10f);
      legend.setMaxSizePercent(.80f);
      legend.setWordWrapEnabled(true);
      legend.setEnabled(true);

      // TODO: what should we do when multiple values are drawn? reset marker to upper line?
      CustomMarkerView customMaker = new CustomMarkerView(getContext(), R.layout.custom_marker);
      customMaker.setChartView(mLineChart);
      mLineChart.setMarker(customMaker);

      updateUI();
    } else {
      LogUtils.error(TAG, "Could not set chart resources; context is null.");
    }

    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    LogUtils.debug(TAG, "++onAttach(Context)");
    mAgainstEntries = new ArrayList<>();
    mCompareAgainstEntries = new ArrayList<>();
    mCompareDifferentialEntries = new ArrayList<>();
    mCompareForEntries = new ArrayList<>();
    mForEntries = new ArrayList<>();
    mDifferentialEntries = new ArrayList<>();
    mForEntries = new ArrayList<>();

    Bundle arguments = getArguments();
    if (arguments == null) {
      Toast.makeText(context, "Did not receive details about match.", Toast.LENGTH_SHORT).show();
      return;
    }

    mHighlightSummary = new MatchSummary();
    try {
      if (arguments.getSerializable(BaseActivity.ARG_MATCH_SUMMARY) != null) {
        mHighlightSummary = (MatchSummary) arguments.getSerializable(BaseActivity.ARG_MATCH_SUMMARY);
      }
    } catch (ClassCastException cce) {
      LogUtils.debug(TAG, "%s", cce.getMessage());
    }

    if (!arguments.containsKey(BaseActivity.ARG_TREND)) {
      Toast.makeText(context, "Did not receive trend data.", Toast.LENGTH_SHORT).show();
      return;
    }

    mTrend = new Trend();
    try {
      if (arguments.getSerializable(BaseActivity.ARG_TREND) != null) {
        mTrend = (Trend) arguments.getSerializable(BaseActivity.ARG_TREND);
      }
    } catch (ClassCastException cce) {
      LogUtils.debug(TAG, "%s", cce.getMessage());
    }

    if (mTrend == null) {
      Toast.makeText(context, "Trend data unexpected.", Toast.LENGTH_SHORT).show();
      return;
    }

    mCompare = null;
    if (arguments.containsKey(BaseActivity.ARG_COMPARE)) {
      mCompare = new Trend();
      try {
        if (arguments.getSerializable(BaseActivity.ARG_COMPARE) != null) {
          mCompare = (Trend) arguments.getSerializable(BaseActivity.ARG_COMPARE);
        }
      } catch (ClassCastException cce) {
        LogUtils.debug(TAG, "%s", cce.getMessage());
      }

      if (mCompare == null) {
        Toast.makeText(context, "Compare trend data unexpected.", Toast.LENGTH_SHORT).show();
      }
    }

    // order the keys ascending (keys are match dates in milliseconds)
    List<String> trendKeys = new ArrayList<>(mTrend.GoalsFor.keySet());
    Collections.sort(trendKeys);

    for (String trendKey : trendKeys) {
      String trimmedKey = trendKey.substring(3, trendKey.length());
      float sortedFloat = Float.parseFloat(trimmedKey);
      mAgainstEntries.add(new Entry(sortedFloat, mTrend.GoalsAgainst.get(trendKey)));
      mForEntries.add(new Entry(sortedFloat, mTrend.GoalsFor.get(trendKey)));
      mDifferentialEntries.add(new Entry(sortedFloat, mTrend.GoalDifferential.get(trendKey)));
    }

    if (mCompare != null) {
      List<String> compareKeys = new ArrayList<>(mCompare.GoalsFor.keySet());
      Collections.sort(compareKeys);
      for (String compareKey : compareKeys) {
        String trimmedKey = compareKey.substring(3, compareKey.length());
        float sortedFloat = Float.parseFloat(trimmedKey);
        // trend key is the match day; both sets of trends should have entries for corresponding match days
        if (mCompare.GoalsAgainst.containsKey(compareKey)) {
          mCompareAgainstEntries.add(new Entry(sortedFloat, mCompare.GoalsAgainst.get(compareKey)));
        }

        if (mCompare.GoalsFor.containsKey(compareKey)) {
          mCompareForEntries.add(new Entry(sortedFloat, mCompare.GoalsFor.get(compareKey)));
        }

        if (mCompare.GoalDifferential.containsKey(compareKey)) {
          mCompareDifferentialEntries.add(new Entry(sortedFloat, mCompare.GoalDifferential.get(compareKey)));
        }
      }
    }
  }

  private void updateUI() {

    LogUtils.debug(TAG, "++updateUI()");
    List<ILineDataSet> dataSets = new ArrayList<>();
    if (mForSwitch.isChecked()) {
      dataSets.add(mForDataSet);
      if (mCompareForDataSet != null && mCompareForDataSet.getEntryCount() > 0) {
        dataSets.add(mCompareForDataSet);
      }
    }

    if (mAgainstSwitch.isChecked()) {
      dataSets.add(mAgainstDataSet);
      if (mCompareAgainstDataSet != null && mCompareAgainstDataSet.getEntryCount() > 0) {
        dataSets.add(mCompareAgainstDataSet);
      }
    }

    if (mDifferentialSwitch.isChecked()) {
      dataSets.add(mDifferentialDataSet);
      if (mCompareDifferentialDataSet != null && mCompareDifferentialDataSet.getEntryCount() > 0) {
        dataSets.add(mCompareDifferentialDataSet);
      }
    }

    LineData lineData = new LineData(dataSets);
    mLineChart.setData(lineData);


    Highlight highlight = new Highlight(mHighlightSummary.MatchDay, 0, 0);
    mLineChart.highlightValue(highlight, false);

    mLineChart.invalidate(); // refresh
  }
}
