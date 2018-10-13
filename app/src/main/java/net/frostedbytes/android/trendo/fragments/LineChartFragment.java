package net.frostedbytes.android.trendo.fragments;

import static net.frostedbytes.android.trendo.BaseActivity.BASE_TAG;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Trend;
import net.frostedbytes.android.trendo.utils.LogUtils;

public class LineChartFragment extends Fragment {

  private static final String TAG = BASE_TAG + LineChartFragment.class.getSimpleName();

  private LineChart mLineChart;

  private LineDataSet mAheadDataSet;
  private List<Entry> mAheadEntries;
  private LineDataSet mBehindDataSet;
  private List<Entry> mBehindEntries;
  private LineDataSet mMainDataSet;
  private List<Entry> mMainEntries;

  private Trend mAheadTrend;
  private Trend mBehindTrend;
  private Trend mTrend;

  public static LineChartFragment newInstance(Trend trend, Trend ahead, Trend behind) {

    LogUtils.debug(TAG, "++newInstance(Trend, Trend, Trend)");
    LineChartFragment fragment = new LineChartFragment();
    Bundle args = new Bundle();
    args.putSerializable(BaseActivity.ARG_TREND, trend);
    args.putSerializable(BaseActivity.ARG_AHEAD, ahead);
    args.putSerializable(BaseActivity.ARG_BEHIND, behind);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    LogUtils.debug(TAG, "++onAttach(Context)");
    mAheadEntries = new ArrayList<>();
    mBehindEntries = new ArrayList<>();
    mMainEntries = new ArrayList<>();

    Bundle arguments = getArguments();
    if (arguments == null) {
      Toast.makeText(context, "Did not receive details about match.", Toast.LENGTH_SHORT).show();
      return;
    }

    if (arguments.containsKey(BaseActivity.ARG_AHEAD)) {
      mAheadTrend = new Trend();
      try {
        if (arguments.getSerializable(BaseActivity.ARG_AHEAD) != null) {
          mAheadTrend = (Trend) arguments.getSerializable(BaseActivity.ARG_AHEAD);
        }
      } catch (ClassCastException cce) {
        LogUtils.debug(TAG, "%s", cce.getMessage());
      }

      if (mAheadTrend == null) {
        LogUtils.warn(TAG, "Ahead Trend data unexpected.");
      }
    }

    if (arguments.containsKey(BaseActivity.ARG_BEHIND)) {
      mBehindTrend = new Trend();
      try {
        if (arguments.getSerializable(BaseActivity.ARG_BEHIND) != null) {
          mBehindTrend = (Trend) arguments.getSerializable(BaseActivity.ARG_BEHIND);
        }
      } catch (ClassCastException cce) {
        LogUtils.debug(TAG, "%s", cce.getMessage());
      }

      if (mBehindTrend == null) {
        LogUtils.warn(TAG, "Trend data unexpected.");
        return;
      }
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

    List<String> trendKeys;
    if (!mTrend.GoalDifferential.isEmpty()) { // order the keys ascending (keys are match dates in milliseconds)
      trendKeys = new ArrayList<>(mTrend.GoalDifferential.keySet());
    } else if (!mTrend.TotalPoints.isEmpty()) {
      trendKeys = new ArrayList<>(mTrend.TotalPoints.keySet());
    } else if (!mTrend.PointsPerGame.isEmpty()) {
      trendKeys = new ArrayList<>(mTrend.PointsPerGame.keySet());
    } else if (!mTrend.MaxPointsPossible.isEmpty()) {
      trendKeys = new ArrayList<>(mTrend.MaxPointsPossible.keySet());
    } else if (!mTrend.GoalsAgainst.isEmpty()) {
      trendKeys = new ArrayList<>(mTrend.GoalsAgainst.keySet());
    } else if (!mTrend.PointsByAverage.isEmpty()) {
      trendKeys = new ArrayList<>(mTrend.PointsByAverage.keySet());
    } else {
      trendKeys = new ArrayList<>(mTrend.GoalsFor.keySet());
    }

    Collections.sort(trendKeys);

    for (String trendKey : trendKeys) { // build the entries based on trend keys
      String trimmedKey = trendKey.substring(3, trendKey.length());
      float sortedFloat = Float.parseFloat(trimmedKey);
      if (!mTrend.GoalsFor.isEmpty()) {
        mMainEntries.add(new Entry(sortedFloat, mTrend.GoalsFor.get(trendKey)));
      } else if (!mTrend.GoalsAgainst.isEmpty()) {
        mMainEntries.add(new Entry(sortedFloat, mTrend.GoalsAgainst.get(trendKey)));
      } else if (!mTrend.GoalDifferential.isEmpty()) {
        mMainEntries.add(new Entry(sortedFloat, mTrend.GoalDifferential.get(trendKey)));
      } else if (!mTrend.TotalPoints.isEmpty()) {
        mMainEntries.add(new Entry(sortedFloat, mTrend.TotalPoints.get(trendKey)));
      } else if (!mTrend.PointsByAverage.isEmpty()) {
        mMainEntries.add(new Entry(sortedFloat, mTrend.PointsByAverage.get(trendKey)));
      } else if (!mTrend.PointsPerGame.isEmpty()) {
        mMainEntries.add(new Entry(sortedFloat, mTrend.PointsPerGame.get(trendKey).floatValue()));
      } else if (!mTrend.MaxPointsPossible.isEmpty()) {
        mMainEntries.add(new Entry(sortedFloat, mTrend.MaxPointsPossible.get(trendKey)));
      }
    }

    if (mAheadTrend != null) { // if we have a comparison going, populate the data sets with data from the team ahead
      List<String> compareKeys;
      if (!mTrend.GoalDifferential.isEmpty()) {
        compareKeys = new ArrayList<>(mTrend.GoalDifferential.keySet());
      } else if (!mTrend.TotalPoints.isEmpty()) {
        compareKeys = new ArrayList<>(mTrend.TotalPoints.keySet());
      } else if (!mTrend.PointsByAverage.isEmpty()) {
        compareKeys = new ArrayList<>(mTrend.PointsByAverage.keySet());
      } else if (!mTrend.PointsPerGame.isEmpty()) {
        compareKeys = new ArrayList<>(mTrend.PointsPerGame.keySet());
      } else if (!mTrend.MaxPointsPossible.isEmpty()) {
        compareKeys = new ArrayList<>(mTrend.MaxPointsPossible.keySet());
      } else if (!mTrend.GoalsAgainst.isEmpty()) {
        compareKeys = new ArrayList<>(mTrend.GoalsAgainst.keySet());
      } else {
        compareKeys = new ArrayList<>(mTrend.GoalsFor.keySet());
      }

      Collections.sort(compareKeys);

      for (String compareKey : compareKeys) {
        String trimmedKey = compareKey.substring(3, compareKey.length());
        float sortedFloat = Float.parseFloat(trimmedKey);
        if (!mAheadTrend.GoalsFor.isEmpty()) {
          mAheadEntries.add(new Entry(sortedFloat, mAheadTrend.GoalsFor.get(compareKey)));
        } else if (!mAheadTrend.GoalsAgainst.isEmpty()) {
          mAheadEntries.add(new Entry(sortedFloat, mAheadTrend.GoalsAgainst.get(compareKey)));
        } else if (!mAheadTrend.GoalDifferential.isEmpty()) {
          mAheadEntries.add(new Entry(sortedFloat, mAheadTrend.GoalDifferential.get(compareKey)));
        } else if (!mAheadTrend.TotalPoints.isEmpty()) {
          mAheadEntries.add(new Entry(sortedFloat, mAheadTrend.TotalPoints.get(compareKey)));
        } else if (!mAheadTrend.PointsByAverage.isEmpty()) {
          mAheadEntries.add(new Entry(sortedFloat, mAheadTrend.PointsByAverage.get(compareKey)));
        } else if (!mAheadTrend.PointsPerGame.isEmpty()) {
          mAheadEntries.add(new Entry(sortedFloat, mAheadTrend.PointsPerGame.get(compareKey).floatValue()));
        } else if (!mAheadTrend.MaxPointsPossible.isEmpty()) {
          mAheadEntries.add(new Entry(sortedFloat, mAheadTrend.MaxPointsPossible.get(compareKey)));
        }
      }
    }

    if (mBehindTrend != null) { // if we have a comparison going, populate the data sets with data from the team behind
      List<String> compareKeys;
      if (!mTrend.GoalDifferential.isEmpty()) {
        compareKeys = new ArrayList<>(mTrend.GoalDifferential.keySet());
      } else if (!mTrend.TotalPoints.isEmpty()) {
        compareKeys = new ArrayList<>(mTrend.TotalPoints.keySet());
      } else if (!mTrend.PointsByAverage.isEmpty()) {
        compareKeys = new ArrayList<>(mTrend.PointsByAverage.keySet());
      } else if (!mTrend.PointsPerGame.isEmpty()) {
        compareKeys = new ArrayList<>(mTrend.PointsPerGame.keySet());
      } else if (!mTrend.MaxPointsPossible.isEmpty()) {
        compareKeys = new ArrayList<>(mTrend.MaxPointsPossible.keySet());
      } else if (!mTrend.GoalsAgainst.isEmpty()) {
        compareKeys = new ArrayList<>(mTrend.GoalsAgainst.keySet());
      } else {
        compareKeys = new ArrayList<>(mTrend.GoalsFor.keySet());
      }

      Collections.sort(compareKeys);

      for (String compareKey : compareKeys) {
        String trimmedKey = compareKey.substring(3, compareKey.length());
        float sortedFloat = Float.parseFloat(trimmedKey);
        if (!mBehindTrend.GoalsFor.isEmpty()) {
          mBehindEntries.add(new Entry(sortedFloat, mBehindTrend.GoalsFor.get(compareKey)));
        } else if (!mBehindTrend.GoalsAgainst.isEmpty()) {
          mBehindEntries.add(new Entry(sortedFloat, mBehindTrend.GoalsAgainst.get(compareKey)));
        } else if (!mBehindTrend.GoalDifferential.isEmpty()) {
          mBehindEntries.add(new Entry(sortedFloat, mBehindTrend.GoalDifferential.get(compareKey)));
        } else if (!mBehindTrend.TotalPoints.isEmpty()) {
          mBehindEntries.add(new Entry(sortedFloat, mBehindTrend.TotalPoints.get(compareKey)));
        } else if (!mBehindTrend.PointsByAverage.isEmpty()) {
          mBehindEntries.add(new Entry(sortedFloat, mBehindTrend.PointsByAverage.get(compareKey)));
        } else if (!mBehindTrend.PointsPerGame.isEmpty()) {
          mBehindEntries.add(new Entry(sortedFloat, mBehindTrend.PointsPerGame.get(compareKey).floatValue()));
        } else if (!mBehindTrend.MaxPointsPossible.isEmpty()) {
          mBehindEntries.add(new Entry(sortedFloat, mBehindTrend.MaxPointsPossible.get(compareKey)));
        }
      }
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    LogUtils.debug(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_line_chart, container, false);
    mLineChart = view.findViewById(R.id.line_chart);

    if (mAheadEntries.size() > 0) {
      mAheadDataSet = new LineDataSet(mAheadEntries, mAheadTrend.TeamShortName);
      mAheadDataSet.setAxisDependency(AxisDependency.LEFT);
      if (getContext() != null) {
        mAheadDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorAhead));
      } else {
        mAheadDataSet.setColor(Color.GREEN);
      }

      mAheadDataSet.setLineWidth(2.0f);
      mAheadDataSet.disableDashedLine();
      mAheadDataSet.setDrawCircles(false);
      mAheadDataSet.setDrawValues(false);
      mAheadDataSet.setHighlightEnabled(true);
      mAheadDataSet.setDrawHighlightIndicators(true);
      mAheadDataSet.setDrawHorizontalHighlightIndicator(false);
    }

    if (mBehindEntries.size() > 0) {
      mBehindDataSet = new LineDataSet(mBehindEntries, mBehindTrend.TeamShortName);
      mBehindDataSet.setAxisDependency(AxisDependency.LEFT);
      if (getContext() != null) {
        mBehindDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorBehind));
      } else {
        mBehindDataSet.setColor(Color.YELLOW);
      }

      mBehindDataSet.setLineWidth(2.0f);
      mBehindDataSet.disableDashedLine();
      mBehindDataSet.setDrawCircles(false);
      mBehindDataSet.setDrawValues(false);
      mBehindDataSet.setHighlightEnabled(true);
      mBehindDataSet.setDrawHighlightIndicators(true);
      mBehindDataSet.setDrawHorizontalHighlightIndicator(false);
    }

    mMainDataSet = new LineDataSet(mMainEntries, mTrend.TeamShortName);
    mMainDataSet.setAxisDependency(AxisDependency.LEFT);
    if (getContext() != null) {
      mMainDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorFavorite));
    } else {
      mMainDataSet.setColor(Color.YELLOW);
    }

    mMainDataSet.setLineWidth(2.0f);
    mMainDataSet.disableDashedLine();
    mMainDataSet.setDrawCircles(false);
    mMainDataSet.setDrawValues(false);
    mMainDataSet.setHighlightEnabled(true);
    mMainDataSet.setDrawHighlightIndicators(true);
    mMainDataSet.setDrawHorizontalHighlightIndicator(false);

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
//      CustomMarkerView customMaker = new CustomMarkerView(getContext(), R.layout.custom_marker);
//      customMaker.setChartView(mLineChart);
//      mLineChart.setMarker(customMaker);

      updateUI();
    } else {
      LogUtils.error(TAG, "Could not set chart resources; context is null.");
    }

    return view;
  }

  private void updateUI() {

    LogUtils.debug(TAG, "++updateUI()");
    List<ILineDataSet> dataSets = new ArrayList<>();
    if (mAheadEntries.size() > 0) {
      dataSets.add(mAheadDataSet);
    }

    if (mBehindEntries.size() > 0) {
      dataSets.add(mBehindDataSet);
    }

    if (mMainEntries.size() > 0) {
      dataSets.add(mMainDataSet);
    }

    LineData lineData = new LineData(dataSets);
    mLineChart.setData(lineData);
    mLineChart.invalidate(); // refresh
  }
}
