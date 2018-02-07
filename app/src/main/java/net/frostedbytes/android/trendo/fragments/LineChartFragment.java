package net.frostedbytes.android.trendo.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;
import java.util.List;
import net.frostedbytes.android.trendo.R;

public class LineChartFragment extends Fragment {

  private static final String TAG = "LineChartFragment";

  static final String ARG_DOUBLE_ARRAY = "double_array";
  static final String ARG_LONG_ARRAY = "long_array";

  public static LineChartFragment newInstance(long[] dataPoints) {

    Log.d(TAG, "++newInstance(List<Long>)");
    LineChartFragment fragment = new LineChartFragment();
    Bundle args = new Bundle();
    args.putLongArray(ARG_LONG_ARRAY, dataPoints);
    fragment.setArguments(args);
    return fragment;
  }

  public static LineChartFragment newInstance(double[] dataPoints) {

    Log.d(TAG, "++newInstance(List<Long>)");
    LineChartFragment fragment = new LineChartFragment();
    Bundle args = new Bundle();
    args.putDoubleArray(ARG_DOUBLE_ARRAY, dataPoints);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_line_chart, container, false);
    LineChart chart = view.findViewById(R.id.trend_line_chart);

    List<Entry> entries = new ArrayList<>();
    long minimum = 0;
    int matchDays = 1;
    Bundle arguments = getArguments();
    if (arguments != null) {
      if (arguments.containsKey(ARG_DOUBLE_ARRAY)) {
        double[] dataPoints = getArguments().getDoubleArray(ARG_DOUBLE_ARRAY);
        for (Double dataPoint : dataPoints) {
          if (dataPoint < minimum) {
            minimum = dataPoint.longValue();
          }

          entries.add(new Entry(matchDays, dataPoint.floatValue()));
          matchDays++;
        }
      } else if (arguments.containsKey(ARG_LONG_ARRAY)) {
        long[] dataPoints = getArguments().getLongArray(ARG_LONG_ARRAY);
        for (Long dataPoint : dataPoints) {
          if (dataPoint < minimum) {
            minimum = dataPoint;
          }

          entries.add(new Entry(matchDays, dataPoint));
          matchDays++;
        }
      }
    }


    LineDataSet dataSet = new LineDataSet(entries, ""); // add entries to data set
    dataSet.setColor(Color.RED);
    dataSet.disableDashedLine();
    dataSet.setDrawCircles(false);
    dataSet.setDrawValues(false);

    LineData lineData = new LineData(dataSet);
    chart.setData(lineData);

    chart.getDescription().setEnabled(false);

    chart.getAxisLeft().setAxisMinimum(minimum); // TODO: make minimum just a little less (but not too much)
    chart.getAxisLeft().setPosition(YAxisLabelPosition.OUTSIDE_CHART);
    chart.getAxisLeft().setDrawGridLines(false);

    chart.getXAxis().setDrawGridLines(false);
    chart.getXAxis().setPosition(XAxisPosition.BOTTOM);

    chart.getAxisRight().setEnabled(false);
    chart.getLegend().setEnabled(false);

    chart.invalidate(); // refresh

    return view;
  }
}
