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

public class TotalPointsFragment extends Fragment {

  private static final String TAG = "TotalPointsFragment";

  static final String ARG_TREND_ID = "trend_id";

  public static final String DisplayName = "Total Points";

  public static TotalPointsFragment newInstance(List<Long> dataPoints) {

    Log.d(TAG, "++newInstance(List<Long>)");
    TotalPointsFragment fragment = new TotalPointsFragment();
    Bundle args = new Bundle();
    long[] longArray = new long[dataPoints.size()];
    int index = 0;
    for (Long dataPoint : dataPoints) {
      longArray[index] = dataPoint;
      index++;
    }

    args.putLongArray(ARG_TREND_ID, longArray);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_total_points, container, false);
    LineChart chart = view.findViewById(R.id.trend_chart_total_points);

    Bundle arguments = getArguments();
    long[] dataPoints;
    if (arguments != null) {
      dataPoints = getArguments().getLongArray(ARG_TREND_ID);
    } else {
      dataPoints = new long[0];
    }

    List<Entry> entries = new ArrayList<>();
    int matchDays = 1;
    for (Long dataPoint : dataPoints) {
      entries.add(new Entry(matchDays, dataPoint));
      matchDays++;
    }

    LineDataSet dataSet = new LineDataSet(entries, ""); // add entries to data set
    dataSet.setColor(Color.RED);
    dataSet.disableDashedLine();
    dataSet.setDrawCircles(false);
    dataSet.setDrawValues(false);

    LineData lineData = new LineData(dataSet);
    chart.setData(lineData);

    chart.getDescription().setEnabled(false);

    chart.getAxisLeft().setAxisMinimum(0);
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
