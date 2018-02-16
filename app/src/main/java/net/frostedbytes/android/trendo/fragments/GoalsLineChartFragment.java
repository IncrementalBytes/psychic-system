package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
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
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Trend;
import net.frostedbytes.android.trendo.views.CustomMarkerView;

public class GoalsLineChartFragment extends Fragment {

  private static final String TAG = "GoalsLineChartFragment";

  LineChart mLineChart;
  Switch mAgainstSwitch;
  Switch mDifferentialSwitch;
  Switch mForSwitch;

  List<Entry> mAgainstEntries;
  List<Entry> mForEntries;
  List<Entry> mDifferentialEntries;
  long mMinDifferential;
  long mHighlightDate;
  Entry mHighlightedEntry;

  LineDataSet mForDataSet;
  LineDataSet mAgainstDataSet;
  LineDataSet mDifferentialDataSet;
  List<ILineDataSet> mDataSets;

  public static GoalsLineChartFragment newInstance(Trend trend, long matchDate) {

    Log.d(TAG, "++newInstance(Trend, long");
    GoalsLineChartFragment fragment = new GoalsLineChartFragment();
    Bundle args = new Bundle();
    args.putSerializable(BaseActivity.ARG_TREND, trend);
    args.putLong(BaseActivity.ARG_MATCH_DATE, matchDate);
    fragment.setArguments(args);
    return fragment;
  }

  // TODO: add newInstance with previous years trend for comparison

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
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

    // setting up data sets
    mForDataSet = new LineDataSet(mForEntries, "For"); // add entries to data set
    mForDataSet.setAxisDependency(AxisDependency.LEFT);
    mForDataSet.setColor(Color.GREEN);
    mForDataSet.setLineWidth(2.0f);
    mForDataSet.disableDashedLine();
    mForDataSet.setDrawCircles(false);
    mForDataSet.setDrawValues(false);
    mForDataSet.setHighlightEnabled(true);
    mForDataSet.setDrawHighlightIndicators(true);

    mAgainstDataSet = new LineDataSet(mAgainstEntries, "Against"); // add entries to data set
    mAgainstDataSet.setAxisDependency(AxisDependency.LEFT);
    mAgainstDataSet.setColor(Color.RED);
    mAgainstDataSet.setLineWidth(2.0f);
    mAgainstDataSet.disableDashedLine();
    mAgainstDataSet.setDrawCircles(false);
    mAgainstDataSet.setDrawValues(false);
    mAgainstDataSet.setHighlightEnabled(true);
    mAgainstDataSet.setDrawHighlightIndicators(true);

    mDifferentialDataSet = new LineDataSet(mDifferentialEntries, "Differential"); // add entries to data set
    mDifferentialDataSet.setAxisDependency(AxisDependency.RIGHT);
    mDifferentialDataSet.setColor(Color.YELLOW);
    mDifferentialDataSet.setLineWidth(1.0f);
    mDifferentialDataSet.disableDashedLine();
    mDifferentialDataSet.setDrawCircles(false);
    mDifferentialDataSet.setDrawValues(false);
    mDifferentialDataSet.setHighlightEnabled(true);
    mDifferentialDataSet.setDrawHighlightIndicators(true);

    mLineChart.getDescription().setEnabled(false);

    YAxis leftAxis = mLineChart.getAxisLeft();
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
    legend.setEnabled(true);

    // TODO: what should we do when multiple values are drawn? reset marker to upper line?
    CustomMarkerView customMaker = new CustomMarkerView(getContext(), R.layout.custom_marker);
    customMaker.setChartView(mLineChart);
    mLineChart.setMarker(customMaker);

    updateUI();

    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    Log.d(TAG, "onAttach(Context)");
    mAgainstEntries = new ArrayList<>();
    mForEntries = new ArrayList<>();
    mDifferentialEntries = new ArrayList<>();
    mMinDifferential = 0;
    mHighlightDate = 0;
    mForEntries = new ArrayList<>();

    // TODO: figure out match date mis-match between summary and sorted values
    Bundle arguments = getArguments();
    if (arguments != null) {
      mHighlightDate = arguments.getLong(BaseActivity.ARG_MATCH_DATE);
      if (arguments.containsKey(BaseActivity.ARG_TREND)) {
        Trend trend = new Trend();
        try {
          if (arguments.getSerializable(BaseActivity.ARG_TREND) != null) {
            trend = (Trend) arguments.getSerializable(BaseActivity.ARG_TREND);
          }
        } catch (ClassCastException cce) {
          Log.d(TAG, cce.getMessage());
        }

        // order the keys ascending (keys are match dates in milliseconds)
        List<String> sortedKeys = new ArrayList(trend != null ? trend.GoalsFor.keySet() : null);
        Collections.sort(sortedKeys);

        for (String sortedKey : sortedKeys) {
          float sortedFloat = Float.parseFloat(sortedKey);
          mAgainstEntries.add(new Entry(sortedFloat, trend.GoalsAgainst.get(sortedKey)));
          mForEntries.add(new Entry(sortedFloat, trend.GoalsFor.get(sortedKey)));
          mDifferentialEntries.add(new Entry(sortedFloat, trend.GoalDifferential.get(sortedKey)));

          // use the largest value to establish the highlight
          if (mHighlightDate == sortedFloat) {
            long goalsAgainst = trend.GoalsAgainst.get(sortedKey);
            long goalsFor = trend.GoalsFor.get(sortedKey);
            long goalDifferential = trend.GoalDifferential.get(sortedKey);
            if (goalsAgainst > goalsFor && goalsAgainst > goalDifferential) {
              mHighlightedEntry = new Entry(sortedFloat, goalsAgainst);
            } else if (goalsFor > goalsAgainst && goalsFor > goalDifferential) {
              mHighlightedEntry = new Entry(sortedFloat, goalsFor);
            } else {
              mHighlightedEntry = new Entry(sortedFloat, goalDifferential);
            }
          }
        }
      }
    }
  }

  private void updateUI() {

    Log.d(TAG, "++updateUI()");
    mDataSets = new ArrayList<>();
    if (mForSwitch.isChecked()) {
      mDataSets.add(mForDataSet);
    }

    if (mAgainstSwitch.isChecked()) {
      mDataSets.add(mAgainstDataSet);
    }

    if (mDifferentialSwitch.isChecked()) {
      mDataSets.add(mDifferentialDataSet);
    }

    LineData lineData = new LineData(mDataSets);
    mLineChart.setData(lineData);

    // TODO: can we only show vertical line?
    Highlight highlight = new Highlight((float)mHighlightDate, 0, 0);
    mLineChart.highlightValue(highlight, false);

    mLineChart.invalidate(); // refresh
  }
}
