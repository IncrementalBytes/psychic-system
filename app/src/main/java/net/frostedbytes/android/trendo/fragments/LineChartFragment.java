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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Trend;
import net.frostedbytes.android.trendo.utils.LogUtils;
import net.frostedbytes.android.trendo.views.CustomMarkerView;

public class LineChartFragment extends Fragment {

  private static final String TAG = LineChartFragment.class.getSimpleName();

  private Switch mCenterSwitch;
  private Switch mLeftSwitch;
  private Switch mRightSwitch;
  private LineChart mLineChart;

  private LineDataSet mCenterDataSet;
  private List<Entry> mCenterEntries;
  private Trend mCompare;
  private LineDataSet mCompareCenterDataSet;
  private List<Entry> mCompareCenterEntries;
  private LineDataSet mCompareLeftDataSet;
  private List<Entry> mCompareLeftEntries;
  private LineDataSet mCompareRightDataSet;
  private List<Entry> mCompareRightEntries;
  private LineDataSet mLeftDataSet;
  private List<Entry> mLeftEntries;
  private LineDataSet mRightDataSet;
  private List<Entry> mRightEntries;
  private Trend mTrend;

  public static LineChartFragment newInstance(Trend trend, Trend compare) {

    LogUtils.debug(TAG, "++newInstance(Trend, Trend)");
    LineChartFragment fragment = new LineChartFragment();
    Bundle args = new Bundle();
    args.putSerializable(BaseActivity.ARG_TREND, trend);
    if (compare != null) {
      args.putSerializable(BaseActivity.ARG_COMPARE, compare);
    }

    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    LogUtils.debug(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_line_chart, container, false);
    mLineChart = view.findViewById(R.id.line_chart);
    mCenterSwitch = view.findViewById(R.id.line_chart_switch_left);
    mLeftSwitch = view.findViewById(R.id.line_chart_switch_center);
    mRightSwitch = view.findViewById(R.id.line_chart_switch_right);

    String centerLabel = "";
    String leftLabel = "";
    String rightLabel = "";
    if (!mTrend.GoalsFor.isEmpty()) {
      centerLabel = getString(R.string.goals_against);
      leftLabel = getString(R.string.goals_for);
      rightLabel = getString(R.string.goal_differential);
    } else if (!mTrend.TotalPoints.isEmpty()) {
      leftLabel = getString(R.string.total_points);
      rightLabel = getString(R.string.points_per_game);
    } else if (!mTrend.MaxPointsPossible.isEmpty()) {
      leftLabel = getString(R.string.max_points);
      rightLabel = getString(R.string.points_by_average);
    }

    mCenterSwitch.setText(centerLabel);
    mRightSwitch.setText(rightLabel);
    mLeftSwitch.setText(leftLabel);

    mLeftSwitch.setChecked(true);
    mCenterSwitch.setVisibility(mTrend.GoalsFor.isEmpty() ? View.INVISIBLE : View.VISIBLE);

    mCenterSwitch.setOnCheckedChangeListener((compoundButton, checked) -> updateUI());
    mLeftSwitch.setOnCheckedChangeListener((compoundButton, checked) -> updateUI());
    mRightSwitch.setOnCheckedChangeListener((compoundButton, checked) -> updateUI());

    if (mCompareLeftEntries.size() > 0) {
      mCompareLeftDataSet = new LineDataSet(
        mCompareLeftEntries,
        String.format(Locale.ENGLISH, "%s - %d", leftLabel, mCompare.Year));
      mCompareLeftDataSet.setAxisDependency(AxisDependency.LEFT);
      if (getContext() != null) {
        mCompareLeftDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorLeftCompare));
      } else {
        mCompareLeftDataSet.setColor(Color.argb(255,102, 187, 106));
      }

      mCompareLeftDataSet.setLineWidth(1.0f);
      mCompareLeftDataSet.enableDashedLine(10, 10, 0);
      mCompareLeftDataSet.setDrawCircles(false);
      mCompareLeftDataSet.setDrawValues(false);
      mCompareLeftDataSet.setHighlightEnabled(false);
      mCompareLeftDataSet.setDrawHighlightIndicators(false);
      mCompareLeftDataSet.setDrawHorizontalHighlightIndicator(false);
      mLeftDataSet = new LineDataSet(
        mLeftEntries,
        String.format(Locale.ENGLISH, "%s - %d", leftLabel, mTrend.Year));
    } else {
      mLeftDataSet = new LineDataSet(mLeftEntries, leftLabel);
    }

    mLeftDataSet.setAxisDependency(AxisDependency.LEFT);
    if (getContext() != null) {
      mLeftDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorLeft));
    } else {
      mLeftDataSet.setColor(Color.GREEN);
    }

    mLeftDataSet.setLineWidth(2.0f);
    mLeftDataSet.disableDashedLine();
    mLeftDataSet.setDrawCircles(false);
    mLeftDataSet.setDrawValues(false);
    mLeftDataSet.setHighlightEnabled(true);
    mLeftDataSet.setDrawHighlightIndicators(true);
    mLeftDataSet.setDrawHorizontalHighlightIndicator(false);

    if (mCompareCenterEntries.size() > 0) {
      mCompareCenterDataSet = new LineDataSet(
        mCompareCenterEntries,
        String.format(Locale.ENGLISH, "%s - %d", centerLabel, mCompare.Year));
      mCompareCenterDataSet.setAxisDependency(AxisDependency.LEFT);
      if (getContext() != null) {
        mCompareCenterDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorCenterCompare));
      } else {
        mCompareCenterDataSet.setColor(Color.argb(255, 255, 152, 0));
      }

      mCompareCenterDataSet.setLineWidth(1.0f);
      mCompareCenterDataSet.enableDashedLine(10, 10, 0);
      mCompareCenterDataSet.setDrawCircles(false);
      mCompareCenterDataSet.setDrawValues(false);
      mCompareCenterDataSet.setHighlightEnabled(false);
      mCompareCenterDataSet.setDrawHighlightIndicators(false);
      mCompareCenterDataSet.setDrawHorizontalHighlightIndicator(false);
      mCenterDataSet = new LineDataSet(
        mCenterEntries,
        String.format(Locale.ENGLISH, "%s - %d", centerLabel, mTrend.Year));
    } else {
      mCenterDataSet = new LineDataSet(mCenterEntries, centerLabel);
    }

    mCenterDataSet.setAxisDependency(AxisDependency.LEFT);
    if (getContext() != null) {
      mCenterDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorCenter));
    } else {
      mCenterDataSet.setColor(Color.YELLOW);
    }

    mCenterDataSet.setLineWidth(2.0f);
    mCenterDataSet.disableDashedLine();
    mCenterDataSet.setDrawCircles(false);
    mCenterDataSet.setDrawValues(false);
    mCenterDataSet.setHighlightEnabled(true);
    mCenterDataSet.setDrawHighlightIndicators(true);
    mCenterDataSet.setDrawHorizontalHighlightIndicator(false);

    if (mCompareRightEntries.size() > 0) {
      mCompareRightDataSet = new LineDataSet(
        mCompareRightEntries,
        String.format(Locale.ENGLISH, "%s - %d", rightLabel, mCompare.Year));
      mCompareRightDataSet.setAxisDependency(AxisDependency.RIGHT);
      if (getContext() != null) {
        mCompareRightDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorRightCompare));
      } else {
        mCompareRightDataSet.setColor(Color.argb(255, 239, 83, 80));
      }

      mCompareRightDataSet.setLineWidth(1.0f);
      mCompareRightDataSet.enableDashedLine(10, 10, 0);
      mCompareRightDataSet.setDrawCircles(false);
      mCompareRightDataSet.setDrawValues(false);
      mCompareRightDataSet.setHighlightEnabled(false);
      mCompareRightDataSet.setDrawHighlightIndicators(false);
      mCompareRightDataSet.setDrawHorizontalHighlightIndicator(false);
      mRightDataSet = new LineDataSet(
        mRightEntries,
        String.format(Locale.ENGLISH, "%s - %d", rightLabel, mTrend.Year));
    } else {
      mRightDataSet = new LineDataSet(mRightEntries, rightLabel);
    }

    mRightDataSet.setAxisDependency(AxisDependency.RIGHT);
    if (getContext() != null) {
      mRightDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorRight));
    } else {
      mRightDataSet.setColor(Color.RED);
    }

    mRightDataSet.setLineWidth(1.0f);
    mRightDataSet.disableDashedLine();
    mRightDataSet.setDrawCircles(false);
    mRightDataSet.setDrawValues(false);
    mRightDataSet.setHighlightEnabled(true);
    mRightDataSet.setDrawHighlightIndicators(true);
    mRightDataSet.setDrawHorizontalHighlightIndicator(false);

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
    mCenterEntries = new ArrayList<>();
    mCompareCenterEntries = new ArrayList<>();
    mCompareLeftEntries = new ArrayList<>();
    mCompareRightEntries = new ArrayList<>();
    mLeftEntries = new ArrayList<>();
    mRightEntries = new ArrayList<>();

    Bundle arguments = getArguments();
    if (arguments == null) {
      Toast.makeText(context, "Did not receive details about match.", Toast.LENGTH_SHORT).show();
      return;
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
    if (mTrend.GoalsFor.isEmpty()) {
      if (!mTrend.TotalPoints.isEmpty()) {
        trendKeys = new ArrayList<>(mTrend.TotalPoints.keySet());
      } else {
        trendKeys = new ArrayList<>(mTrend.MaxPointsPossible.keySet());
      }
    }

    Collections.sort(trendKeys);

    for (String trendKey : trendKeys) {
      String trimmedKey = trendKey.substring(3, trendKey.length());
      float sortedFloat = Float.parseFloat(trimmedKey);
      if (!mTrend.GoalsFor.isEmpty()) {
        mLeftEntries.add(new Entry(sortedFloat, mTrend.GoalsFor.get(trendKey)));
        mCenterEntries.add(new Entry(sortedFloat, mTrend.GoalsAgainst.get(trendKey)));
        mRightEntries.add(new Entry(sortedFloat, mTrend.GoalDifferential.get(trendKey)));
      } else if (!mTrend.TotalPoints.isEmpty()) {
        mLeftEntries.add(new Entry(sortedFloat, mTrend.TotalPoints.get(trendKey)));
        mRightEntries.add(new Entry(sortedFloat, mTrend.PointsPerGame.get(trendKey).floatValue()));
      } else if (!mTrend.MaxPointsPossible.isEmpty()) {
        mLeftEntries.add(new Entry(sortedFloat, mTrend.MaxPointsPossible.get(trendKey)));
        mRightEntries.add(new Entry(sortedFloat, mTrend.PointsByAverage.get(trendKey)));
      }
    }

    if (mCompare != null) { // if we have a comparison going, populate the data sets with compare data
      List<String> compareKeys = new ArrayList<>(mCompare.GoalsFor.keySet());
      if (mTrend.GoalsFor.isEmpty()) {
        if (!mTrend.TotalPoints.isEmpty()) {
          compareKeys = new ArrayList<>(mCompare.TotalPoints.keySet());
        } else {
          compareKeys = new ArrayList<>(mCompare.MaxPointsPossible.keySet());
        }
      }

      Collections.sort(compareKeys);

      for (String compareKey : compareKeys) {
        String trimmedKey = compareKey.substring(3, compareKey.length());
        float sortedFloat = Float.parseFloat(trimmedKey);
        if (!mCompare.GoalsFor.isEmpty()) {
          mCompareLeftEntries.add(new Entry(sortedFloat, mCompare.GoalsFor.get(compareKey)));
          mCompareCenterEntries.add(new Entry(sortedFloat, mCompare.GoalsAgainst.get(compareKey)));
          mCompareRightEntries.add(new Entry(sortedFloat, mCompare.GoalDifferential.get(compareKey)));
        } else if (!mCompare.TotalPoints.isEmpty()) {
          mCompareLeftEntries.add(new Entry(sortedFloat, mCompare.TotalPoints.get(compareKey)));
          mCompareRightEntries.add(new Entry(sortedFloat, mCompare.PointsPerGame.get(compareKey).floatValue()));
        } else if (!mCompare.MaxPointsPossible.isEmpty()) {
          mCompareLeftEntries.add(new Entry(sortedFloat, mCompare.MaxPointsPossible.get(compareKey)));
          mCompareRightEntries.add(new Entry(sortedFloat, mCompare.PointsByAverage.get(compareKey)));
        }
      }
    }
  }

  private void updateUI() {

    LogUtils.debug(TAG, "++updateUI()");
    List<ILineDataSet> dataSets = new ArrayList<>();
    if (mLeftSwitch.isChecked()) {
      dataSets.add(mLeftDataSet);
      if (mCompareLeftDataSet != null && mCompareLeftDataSet.getEntryCount() > 0) {
        dataSets.add(mCompareLeftDataSet);
      }
    }

    if (mCenterSwitch.isChecked()) {
      dataSets.add(mCenterDataSet);
      if (mCompareCenterDataSet != null && mCompareCenterDataSet.getEntryCount() > 0) {
        dataSets.add(mCompareCenterDataSet);
      }
    }

    if (mRightSwitch.isChecked()) {
      dataSets.add(mRightDataSet);
      if (mCompareRightDataSet != null && mCompareRightDataSet.getEntryCount() > 0) {
        dataSets.add(mCompareRightDataSet);
      }
    }

    LineData lineData = new LineData(dataSets);
    mLineChart.setData(lineData);
    mLineChart.invalidate(); // refresh
  }
}
