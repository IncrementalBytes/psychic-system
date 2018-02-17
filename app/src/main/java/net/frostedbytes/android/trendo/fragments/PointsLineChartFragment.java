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

public class PointsLineChartFragment extends Fragment {

  private static final String TAG = "PointsLineChartFragment";

  private LineChart mLineChart;
  private Switch mTotalSwitch;
  private Switch mAverageSwitch;

  private List<Entry> mTotalEntries;
  private List<Entry> mAverageEntries;
  private String mHighlightDate;

  private LineDataSet mTotalDataSet;
  private LineDataSet mAverageDataSet;

  public static PointsLineChartFragment newInstance(Trend trend, String matchDate) {

    LogUtils.debug(TAG, "++newInstance(Trend, long");
    PointsLineChartFragment fragment = new PointsLineChartFragment();
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
    mTotalDataSet.setDrawHorizontalHighlightIndicator(false);

    mAverageDataSet = new LineDataSet(mAverageEntries, "Per Game"); // add entries to data set
    mAverageDataSet.setAxisDependency(AxisDependency.RIGHT);
    mAverageDataSet.setColor(Color.RED);
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
      legend.setEnabled(true);

      // TODO: what should we do when multiple values are drawn? reset marker to upper line?
      CustomMarkerView customMaker = new CustomMarkerView(getContext(), R.layout.custom_marker);
      customMaker.setChartView(mLineChart);
      mLineChart.setMarker(customMaker);

      updateUI();
    } else {
      Toast.makeText(getContext(), "Could not get resource value.", Toast.LENGTH_SHORT).show();
    }

    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    LogUtils.debug(TAG, "onAttach(Context)");
    mTotalEntries = new ArrayList<>();
    mAverageEntries = new ArrayList<>();
    mHighlightDate = BaseActivity.DEFAULT_DATE;

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

    List<String> sortedKeys = new ArrayList(trend != null ? trend.TotalPoints.keySet() : null);
    Collections.sort(sortedKeys);

    for (String sortedKey : sortedKeys) {
      String trimmedKey = sortedKey.substring(4, 8);
      float sortedFloat = Float.parseFloat(trimmedKey);
      mTotalEntries.add(new Entry(sortedFloat, trend.TotalPoints.get(sortedKey)));
      mAverageEntries.add(new Entry(sortedFloat, trend.PointsPerGame.get(sortedKey).floatValue()));
    }
  }

  private void updateUI() {

    LogUtils.debug(TAG, "++updateUI()");
    List<ILineDataSet> dataSets = new ArrayList<>();
    if (mTotalSwitch.isChecked()) {
      dataSets.add(mTotalDataSet);
    }

    if (mAverageSwitch.isChecked()) {
      dataSets.add(mAverageDataSet);
    }

    LineData lineData = new LineData(dataSets);
    mLineChart.setData(lineData);

    Highlight highlight = new Highlight(Float.parseFloat(mHighlightDate.substring(4, 8)), 0, 0);
    mLineChart.highlightValue(highlight, false);

    mLineChart.invalidate(); // refresh
  }
}
