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

public class PointsPerGameFragment extends Fragment {

  private static final String TAG = "PointsPerGameFragment";

  static final String ARG_TREND_ID = "trend_id";

  public static final String DisplayName = "Points Per Game";

  public static PointsPerGameFragment newInstance(List<Double> dataPoints) {

    Log.d(TAG, "++newInstance(List<Double>)");
    PointsPerGameFragment fragment = new PointsPerGameFragment();
    Bundle args = new Bundle();
    float[] floatArray = new float[dataPoints.size()];
    int index = 0;
    for (Double dataPoint : dataPoints) {
      floatArray[index] = dataPoint.floatValue();
      index++;
    }

    args.putFloatArray(ARG_TREND_ID, floatArray);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_points_per_game, container, false);
    LineChart chart = view.findViewById(R.id.trend_chart_points_per_game);

    Bundle arguments = getArguments();
    float[] dataPoints;
    if (arguments != null) {
      dataPoints = getArguments().getFloatArray(ARG_TREND_ID);
    } else {
      dataPoints = new float[0];
    }

    List<Entry> entries = new ArrayList<>();
    int matchDays = 1;
    for (float dataPoint : dataPoints) {
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