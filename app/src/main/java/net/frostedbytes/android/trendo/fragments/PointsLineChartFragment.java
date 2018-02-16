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

public class PointsLineChartFragment extends Fragment {

  private static final String TAG = "PointsLineChartFragment";

  LineChart mLineChart;
  Switch mTotalSwitch;
  Switch mAverageSwitch;

  List<Entry> mTotalEntries;
  List<Entry> mAverageEntries;
  long mHighlightDate;
  Entry mHighlightedEntry;

  LineDataSet mTotalDataSet;
  LineDataSet mAverageDataSet;
  List<ILineDataSet> mDataSets;

  public static PointsLineChartFragment newInstance(Trend trend, long matchDate) {

    Log.d(TAG, "++newInstance(Trend, long");
    PointsLineChartFragment fragment = new PointsLineChartFragment();
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
    mTotalSwitch = view.findViewById(R.id.line_chart_switch_left);
    mAverageSwitch = view.findViewById(R.id.line_chart_switch_right);
    Switch middleSwitch = view.findViewById(R.id.line_chart_switch_center);
    middleSwitch.setVisibility(View.INVISIBLE);

    mTotalSwitch.setText(R.string.total_points);
    mAverageSwitch.setText(R.string.points_per_game);
    mTotalSwitch.setChecked(true);
    mTotalSwitch.setOnCheckedChangeListener((compoundButton, checked) -> updateUI());
    mAverageSwitch.setOnCheckedChangeListener((compoundButton, checked) -> updateUI());

    // setting up data sets
    mTotalDataSet = new LineDataSet(mTotalEntries, "Total"); // add entries to data set
    mTotalDataSet .setAxisDependency(AxisDependency.LEFT);
    mTotalDataSet .setColor(Color.GREEN);
    mTotalDataSet .setLineWidth(2.0f);
    mTotalDataSet .disableDashedLine();
    mTotalDataSet .setDrawCircles(false);
    mTotalDataSet .setDrawValues(false);
    mTotalDataSet .setHighlightEnabled(true);
    mTotalDataSet .setDrawHighlightIndicators(true);

    mAverageDataSet = new LineDataSet(mAverageEntries, "Per Game"); // add entries to data set
    mAverageDataSet.setAxisDependency(AxisDependency.RIGHT);
    mAverageDataSet.setColor(Color.RED);
    mAverageDataSet.setLineWidth(1.5f);
    mAverageDataSet.disableDashedLine();
    mAverageDataSet.setDrawCircles(false);
    mAverageDataSet.setDrawValues(false);
    mAverageDataSet.setHighlightEnabled(true);
    mAverageDataSet.setDrawHighlightIndicators(true);

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
    mTotalEntries = new ArrayList<>();
    mAverageEntries = new ArrayList<>();
    mHighlightDate = 0;

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

        List<String> sortedKeys = new ArrayList(trend != null ? trend.TotalPoints.keySet() : null);
        Collections.sort(sortedKeys);

        for (String sortedKey : sortedKeys) {
          float sortedFloat = Float.parseFloat(sortedKey);
          mTotalEntries.add(new Entry(sortedFloat, trend.TotalPoints.get(sortedKey)));
          mAverageEntries.add(new Entry(sortedFloat, trend.PointsPerGame.get(sortedKey).floatValue()));

          if (mHighlightDate == sortedFloat) {
            mHighlightedEntry = new Entry(sortedFloat, trend.TotalPoints.get(sortedKey));
          }
        }
      }
    }
  }

  private void updateUI() {

    Log.d(TAG, "++updateUI()");
    mDataSets = new ArrayList<>();
    if (mTotalSwitch.isChecked()) {
      mDataSets.add(mTotalDataSet);
    }

    if (mAverageSwitch.isChecked()) {
      mDataSets.add(mAverageDataSet);
    }

    LineData lineData = new LineData(mDataSets);
    mLineChart.setData(lineData);

    // TODO: can we only show vertical line?
    Highlight highlight = new Highlight((float)mHighlightDate, 0, 0);
    mLineChart.highlightValue(highlight, false);

    mLineChart.invalidate(); // refresh
  }
}
