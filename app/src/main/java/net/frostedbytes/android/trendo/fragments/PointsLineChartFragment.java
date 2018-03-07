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
import com.google.firebase.crash.FirebaseCrash;
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

public class PointsLineChartFragment extends Fragment {

  private static final String TAG = PointsLineChartFragment.class.getSimpleName();

  private Switch mAverageSwitch;
  private LineChart mLineChart;
  private Switch mTotalSwitch;

  private LineDataSet mAverageDataSet;
  private List<Entry> mAverageEntries;
  private Trend mCompare;
  private LineDataSet mCompareAverageDataSet;
  private List<Entry> mCompareAverageEntries;
  private LineDataSet mCompareTotalDataSet;
  private List<Entry> mCompareTotalEntries;
  private MatchSummary mHighlightSummary;
  private LineDataSet mTotalDataSet;
  private List<Entry> mTotalEntries;
  private Trend mTrend;

  public static PointsLineChartFragment newInstance(Trend trend, Trend compare, MatchSummary matchSummary) {

    LogUtils.debug(TAG, "++newInstance(Trend, Trend, MatchSummary)");
    PointsLineChartFragment fragment = new PointsLineChartFragment();
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
    mTotalSwitch = view.findViewById(R.id.line_chart_switch_left);
    mAverageSwitch = view.findViewById(R.id.line_chart_switch_right);
    Switch middleSwitch = view.findViewById(R.id.line_chart_switch_center);
    middleSwitch.setVisibility(View.INVISIBLE);

    mTotalSwitch.setText(R.string.total_points);
    mAverageSwitch.setText(R.string.points_per_game);
    mTotalSwitch.setChecked(true);
    mTotalSwitch.setOnCheckedChangeListener((compoundButton, checked) -> updateUI());
    mAverageSwitch.setOnCheckedChangeListener((compoundButton, checked) -> updateUI());

    if (mCompareTotalEntries.size() > 0) {
      mCompareTotalDataSet = new LineDataSet(
        mCompareTotalEntries,
        String.format(Locale.ENGLISH, "%s - %d", getResources().getString(R.string.total_points), mCompare.Year));
      mCompareTotalDataSet.setAxisDependency(AxisDependency.LEFT);
      if (getContext() != null) {
        mCompareTotalDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorTotalPointsCompare));
      } else {
        mCompareTotalDataSet.setColor(Color.BLUE);
      }

      mCompareTotalDataSet.setLineWidth(1.0f);
      mCompareTotalDataSet.enableDashedLine(10, 10, 0);
      mCompareTotalDataSet.setDrawCircles(false);
      mCompareTotalDataSet.setDrawValues(false);
      mCompareTotalDataSet.setHighlightEnabled(false);
      mCompareTotalDataSet.setDrawHighlightIndicators(false);
      mCompareTotalDataSet.setDrawHorizontalHighlightIndicator(false);
      mTotalDataSet = new LineDataSet(
        mTotalEntries,
        String.format(Locale.ENGLISH, "%s - %d", getResources().getString(R.string.total_points), mTrend.Year));
    } else {
      mTotalDataSet = new LineDataSet(mTotalEntries, getResources().getString(R.string.total_points));
    }

    mTotalDataSet.setAxisDependency(AxisDependency.LEFT);
    if (getContext() != null) {
      mTotalDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorTotalPoints));
    } else {
      mTotalDataSet.setColor(Color.BLUE);
    }

    mTotalDataSet.setLineWidth(2.0f);
    mTotalDataSet.disableDashedLine();
    mTotalDataSet.setDrawCircles(false);
    mTotalDataSet.setDrawValues(false);
    mTotalDataSet.setHighlightEnabled(true);
    mTotalDataSet.setDrawHighlightIndicators(true);
    mTotalDataSet.setDrawHorizontalHighlightIndicator(false);

    if (mCompareAverageEntries.size() > 0) {
      mCompareAverageDataSet = new LineDataSet(
        mCompareAverageEntries,
        String.format(Locale.ENGLISH, "%s - %d", getResources().getString(R.string.points_per_game), mCompare.Year));
      mCompareAverageDataSet.setAxisDependency(AxisDependency.RIGHT);
      if (getContext() != null) {
        mCompareAverageDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorPointsPerGameCompare));
      } else {
        mCompareAverageDataSet.setColor(Color.YELLOW);
      }

      mCompareAverageDataSet.setLineWidth(1.0f);
      mCompareAverageDataSet.enableDashedLine(10, 10, 0);
      mCompareAverageDataSet.setDrawCircles(false);
      mCompareAverageDataSet.setDrawValues(false);
      mCompareAverageDataSet.setHighlightEnabled(false);
      mCompareAverageDataSet.setDrawHighlightIndicators(false);
      mCompareAverageDataSet.setDrawHorizontalHighlightIndicator(false);
      mAverageDataSet = new LineDataSet(
        mAverageEntries,
        String.format(Locale.ENGLISH, "%s - %d", getResources().getString(R.string.points_per_game), mTrend.Year));
    } else {
      mAverageDataSet = new LineDataSet(mAverageEntries, getResources().getString(R.string.points_per_game));
    }

    mAverageDataSet.setAxisDependency(AxisDependency.RIGHT);
    if (getContext() != null) {
      mAverageDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorPointsPerGame));
    } else {
      mAverageDataSet.setColor(Color.YELLOW);
    }

    mAverageDataSet.setLineWidth(1.5f);
    mAverageDataSet.disableDashedLine();
    mAverageDataSet.setDrawCircles(false);
    mAverageDataSet.setDrawValues(false);
    mAverageDataSet.setHighlightEnabled(true);
    mAverageDataSet.setDrawHighlightIndicators(true);
    mAverageDataSet.setDrawHorizontalHighlightIndicator(false);

    mLineChart.getDescription().setEnabled(false);

    if (getContext() != null) {
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
      LogUtils.debug(TAG, "Could not set chart resources; context is null.");
      FirebaseCrash.log("Could not set chart resources; context is null.");
    }

    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    LogUtils.debug(TAG, "++onAttach(Context)");
    mCompareAverageEntries = new ArrayList<>();
    mCompareTotalEntries = new ArrayList<>();
    mAverageEntries = new ArrayList<>();
    mTotalEntries = new ArrayList<>();

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
      LogUtils.debug(TAG, cce.getMessage());
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
      LogUtils.debug(TAG, cce.getMessage());
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
        LogUtils.debug(TAG, cce.getMessage());
      }

      if (mCompare == null) {
        Toast.makeText(context, "Compare trend data unexpected.", Toast.LENGTH_SHORT).show();
      }
    }

    List<String> trendKeys = new ArrayList<>();
    trendKeys.addAll(mTrend.GoalsFor.keySet());
    Collections.sort(trendKeys);

    for (String trendKey : trendKeys) {
      String trimmedKey = trendKey.substring(3, trendKey.length());
      float sortedFloat = Float.parseFloat(trimmedKey);
      mTotalEntries.add(new Entry(sortedFloat, mTrend.TotalPoints.get(trendKey)));
      mAverageEntries.add(new Entry(sortedFloat, mTrend.PointsPerGame.get(trendKey).floatValue()));
    }

    if (mCompare != null) {
      List<String> compareKeys = new ArrayList<>();
      compareKeys.addAll(mCompare.GoalsFor.keySet());
      Collections.sort(compareKeys);
      for (String compareKey : compareKeys) {
        String trimmedKey = compareKey.substring(3, compareKey.length());
        float sortedFloat = Float.parseFloat(trimmedKey);
        // trend key is the match day; both sets of trends should have entries for corresponding match days
        if (mCompare.TotalPoints.containsKey(compareKey)) {
          mCompareTotalEntries.add(new Entry(sortedFloat, mCompare.TotalPoints.get(compareKey)));
        }

        if (mCompare.PointsPerGame.containsKey(compareKey)) {
          mCompareAverageEntries.add(new Entry(sortedFloat, mCompare.PointsPerGame.get(compareKey).floatValue()));
        }
      }
    }
  }

  private void updateUI() {

    LogUtils.debug(TAG, "++updateUI()");
    List<ILineDataSet> dataSets = new ArrayList<>();
    if (mTotalSwitch.isChecked()) {
      dataSets.add(mTotalDataSet);
      if (mCompareTotalDataSet != null && mCompareTotalDataSet.getEntryCount() > 0) {
        dataSets.add(mCompareTotalDataSet);
      }
    }

    if (mAverageSwitch.isChecked()) {
      dataSets.add(mAverageDataSet);
      if (mCompareAverageDataSet != null && mCompareAverageDataSet.getEntryCount() > 0) {
        dataSets.add(mCompareAverageDataSet);
      }
    }

    LineData lineData = new LineData(dataSets);
    mLineChart.setData(lineData);

    Highlight highlight = new Highlight(mHighlightSummary.MatchDay, 0, 0);
    mLineChart.highlightValue(highlight, false);

    mLineChart.invalidate(); // refresh
  }
}
