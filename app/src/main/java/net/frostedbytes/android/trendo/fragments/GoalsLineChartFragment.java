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
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.utils.LogUtils;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Trend;
import net.frostedbytes.android.trendo.views.CustomMarkerView;

public class GoalsLineChartFragment extends Fragment {

  private static final String TAG = "GoalsLineChartFragment";

  private LineChart mLineChart;
  private Switch mAgainstSwitch;
  private Switch mDifferentialSwitch;
  private Switch mForSwitch;

  private List<Entry> mAgainstEntries;
  private List<Entry> mForEntries;
  private List<Entry> mDifferentialEntries;
  private String mHighlightDate;

  private LineDataSet mForDataSet;
  private LineDataSet mAgainstDataSet;
  private LineDataSet mDifferentialDataSet;

  public static GoalsLineChartFragment newInstance(Trend trend, String matchDate) {

    LogUtils.debug(TAG, "++newInstance(Trend, long");
    GoalsLineChartFragment fragment = new GoalsLineChartFragment();
    Bundle args = new Bundle();
    args.putSerializable(BaseActivity.ARG_TREND, trend);
    args.putString(BaseActivity.ARG_MATCH_DATE, matchDate);
    fragment.setArguments(args);
    return fragment;
  }

  // TODO: add newInstance with previous years trend for comparison

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

    // setting up data sets
    mForDataSet = new LineDataSet(mForEntries, getResources().getString(R.string.goals_for));
    mForDataSet.setAxisDependency(AxisDependency.LEFT);
    mForDataSet.setColor(Color.GREEN);
    mForDataSet.setLineWidth(2.0f);
    mForDataSet.disableDashedLine();
    mForDataSet.setDrawCircles(false);
    mForDataSet.setDrawValues(false);
    mForDataSet.setHighlightEnabled(true);
    mForDataSet.setDrawHighlightIndicators(true);
    mForDataSet.setDrawHorizontalHighlightIndicator(false);

    mAgainstDataSet = new LineDataSet(mAgainstEntries, getResources().getString(R.string.goals_against));
    mAgainstDataSet.setAxisDependency(AxisDependency.LEFT);
    mAgainstDataSet.setColor(Color.RED);
    mAgainstDataSet.setLineWidth(2.0f);
    mAgainstDataSet.disableDashedLine();
    mAgainstDataSet.setDrawCircles(false);
    mAgainstDataSet.setDrawValues(false);
    mAgainstDataSet.setHighlightEnabled(true);
    mAgainstDataSet.setDrawHighlightIndicators(true);
    mAgainstDataSet.setDrawHorizontalHighlightIndicator(false);

    mDifferentialDataSet = new LineDataSet(mDifferentialEntries, getResources().getString(R.string.goal_differential));
    mDifferentialDataSet.setAxisDependency(AxisDependency.RIGHT);
    mDifferentialDataSet.setColor(Color.YELLOW);
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
      legend.setEnabled(true);

      // TODO: what should we do when multiple values are drawn? reset marker to upper line?
      CustomMarkerView customMaker = new CustomMarkerView(getContext(), R.layout.custom_marker);
      customMaker.setChartView(mLineChart);
      mLineChart.setMarker(customMaker);

      updateUI();
    } else {
      Toast.makeText(getContext(), "Problem setting chart properties.", Toast.LENGTH_SHORT).show();
    }

    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    LogUtils.debug(TAG, "onAttach(Context)");
    mAgainstEntries = new ArrayList<>();
    mForEntries = new ArrayList<>();
    mDifferentialEntries = new ArrayList<>();
    mHighlightDate = BaseActivity.DEFAULT_DATE;
    mForEntries = new ArrayList<>();

    Bundle arguments = getArguments();
    if (arguments == null) {
      Toast.makeText(context, "Did not receive details about match.", Toast.LENGTH_SHORT).show();
      return;
    }

    mHighlightDate = arguments.getString(BaseActivity.ARG_MATCH_DATE);
    if (!arguments.containsKey(BaseActivity.ARG_TREND)) {
      Toast.makeText(context, "Did not receive trend data.", Toast.LENGTH_SHORT).show();
      return;
    }

    Trend trend = new Trend();
    try {
      if (arguments.getSerializable(BaseActivity.ARG_TREND) != null) {
        trend = (Trend) arguments.getSerializable(BaseActivity.ARG_TREND);
      }
    } catch (ClassCastException cce) {
      LogUtils.debug(TAG, cce.getMessage());
    }

    if (trend == null) {
      Toast.makeText(context, "Trend data unexpected.", Toast.LENGTH_SHORT).show();
      return;
    }

    // order the keys ascending (keys are match dates in milliseconds)
    List<String> sortedKeys = new ArrayList<>();
    sortedKeys.addAll(trend.GoalsFor.keySet());
    Collections.sort(sortedKeys);
    for (String sortedKey : sortedKeys) {
      String trimmedKey = sortedKey.substring(4, 8);
      float sortedFloat = Float.parseFloat(trimmedKey);
      mAgainstEntries.add(new Entry(sortedFloat, trend.GoalsAgainst.get(sortedKey)));
      mForEntries.add(new Entry(sortedFloat, trend.GoalsFor.get(sortedKey)));
      mDifferentialEntries.add(new Entry(sortedFloat, trend.GoalDifferential.get(sortedKey)));
    }
  }

  private void updateUI() {

    LogUtils.debug(TAG, "++updateUI()");
    List<ILineDataSet> dataSets = new ArrayList<>();
    if (mForSwitch.isChecked()) {
      dataSets.add(mForDataSet);
    }

    if (mAgainstSwitch.isChecked()) {
      dataSets.add(mAgainstDataSet);
    }

    if (mDifferentialSwitch.isChecked()) {
      dataSets.add(mDifferentialDataSet);
    }

    LineData lineData = new LineData(dataSets);
    mLineChart.setData(lineData);


    Highlight highlight = new Highlight(Float.parseFloat(mHighlightDate.substring(4, 8)), 0, 0);
    mLineChart.highlightValue(highlight, false);

    mLineChart.invalidate(); // refresh
  }
}
