package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.R;

public class LineChartFragment extends Fragment {

  private static final String TAG = "LineChartFragment";

  LineChart mLineChart;

  List<Entry> mEntries;
  long mMinimum;
  long mHighlightDate;

  public static LineChartFragment newLongInstance(HashMap<String, Long> dataPoints, long matchDate) {

    Log.d(TAG, "++newLongInstance(HashMap<String, Long>), long");
    LineChartFragment fragment = new LineChartFragment();
    Bundle args = new Bundle();
    args.putSerializable(BaseActivity.ARG_VALUES_LONG, dataPoints);
    args.putLong(BaseActivity.ARG_MATCH_DATE, matchDate);
    fragment.setArguments(args);
    return fragment;
  }

  public static LineChartFragment newDoubleInstance(HashMap<String, Double> dataPoints, long matchDate) {

    Log.d(TAG, "++newDoubleInstance(HashMap<String, Long>), long");
    LineChartFragment fragment = new LineChartFragment();
    Bundle args = new Bundle();
    args.putSerializable(BaseActivity.ARG_VALUES_DOUBLE, dataPoints);
    args.putLong(BaseActivity.ARG_MATCH_DATE, matchDate);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_line_chart, container, false);
    mLineChart = view.findViewById(R.id.trend_line_chart);

    LineDataSet dataSet = new LineDataSet(mEntries, ""); // add entries to data set
    dataSet.setColor(Color.RED);
    dataSet.setLineWidth(2.0f);
    dataSet.disableDashedLine();
    dataSet.setDrawCircles(false);
    dataSet.setDrawValues(false);

    LineData lineData = new LineData(dataSet);
    mLineChart.setData(lineData);

    mLineChart.getDescription().setEnabled(false);

    YAxis yAxis = mLineChart.getAxisLeft();
    yAxis.setAxisMinimum(mMinimum < 0 ? (mMinimum - .5f) : mMinimum);
    yAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
    yAxis.setDrawGridLines(true);

    mLineChart.getXAxis().setEnabled(false);
    mLineChart.getAxisRight().setEnabled(false);
    mLineChart.getLegend().setEnabled(false);

    Highlight highlight = new Highlight((float)mHighlightDate, 0, 0);
    mLineChart.highlightValue(highlight, false);

    mLineChart.invalidate(); // refresh

    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    Log.d(TAG, "onAttach(Context)");
    mMinimum = 0;
    mHighlightDate = 0;
    mEntries = new ArrayList<>();

    Bundle arguments = getArguments();
    if (arguments != null) {
      mHighlightDate = arguments.getLong(BaseActivity.ARG_MATCH_DATE);
      if (arguments.containsKey(BaseActivity.ARG_VALUES_LONG)) {
        HashMap<String, Long> longValues = new HashMap<>();
        try {
          longValues = (HashMap<String, Long>) arguments.getSerializable(BaseActivity.ARG_VALUES_LONG);
        } catch (ClassCastException cce) {
          Log.d(TAG, cce.getMessage());
        }

        List<String> sortedKeys = new ArrayList(longValues.keySet());
        Collections.sort(sortedKeys);
        for (String sortedKey : sortedKeys) {
          long sortedLong = Long.parseLong(sortedKey);
          if (longValues.get(sortedKey) < mMinimum) {
            mMinimum = longValues.get(sortedKey);
          }

          mEntries.add(new Entry(sortedLong, longValues.get(sortedKey)));
        }
      } else if (arguments.containsKey(BaseActivity.ARG_VALUES_DOUBLE)) {
        HashMap<String, Double> doubleValues = new HashMap<>();
        try {
          doubleValues = (HashMap<String, Double>) arguments.getSerializable(BaseActivity.ARG_VALUES_DOUBLE);
        } catch (ClassCastException cce) {
          Log.d(TAG, cce.getMessage());
        }

        List<String> sortedKeys = new ArrayList(doubleValues.keySet());
        Collections.sort(sortedKeys);
        for (String sortedKey : sortedKeys) {
          long sortedLong = Long.parseLong(sortedKey);
          Double temp  = doubleValues.get(sortedKey);
          if (doubleValues.get(sortedKey) < mMinimum) {
            mMinimum = temp.longValue();
          }

          mEntries.add(new Entry(sortedLong, temp.longValue()));
        }
      }
    }
  }
}
