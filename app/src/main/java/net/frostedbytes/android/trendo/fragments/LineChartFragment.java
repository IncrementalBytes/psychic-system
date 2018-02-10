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
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.frostedbytes.android.trendo.R;

public class LineChartFragment extends Fragment {

  private static final String TAG = "LineChartFragment";

  static final String ARG_DATA_VALUES = "data_values";

  public static LineChartFragment newInstance(HashMap<String, Long> dataPoints) {

    Log.d(TAG, "++newInstance(HashMap<String, Long>)");
    LineChartFragment fragment = new LineChartFragment();
    Bundle args = new Bundle();
    args.putSerializable(ARG_DATA_VALUES, dataPoints);
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
    HashMap<String, Long> dataValues = new HashMap<>();
    Bundle arguments = getArguments();
    if (arguments != null) {
      try {
        dataValues = (HashMap<String, Long>) arguments.getSerializable(ARG_DATA_VALUES);

        List<String> sortedKeys = new ArrayList(dataValues.keySet());
        Collections.sort(sortedKeys);

        // key is the match date, get the month
        Calendar calendar = Calendar.getInstance();
        for (String sortedKey : sortedKeys) {
          calendar.setTimeInMillis(Long.parseLong(sortedKey));

          if (dataValues.get(sortedKey).longValue() < minimum) {
            minimum = dataValues.get(sortedKey).longValue();
          }

          entries.add(new Entry(matchDays++, dataValues.get(sortedKey).longValue()));
        }
      } catch (ClassCastException cce) {
        Log.d(TAG, cce.getMessage());
      }
    }

    LineDataSet dataSet = new LineDataSet(entries, ""); // add entries to data set
    dataSet.setColor(Color.RED);
    dataSet.setLineWidth(2.0f);
    dataSet.disableDashedLine();
    dataSet.setDrawCircles(false);
    dataSet.setDrawValues(false);

    LineData lineData = new LineData(dataSet);
    chart.setData(lineData);

    chart.getDescription().setEnabled(false);

    YAxis yAxis = chart.getAxisLeft();
    yAxis.setAxisMinimum(minimum < 0 ? (minimum - .5f) : minimum);
    yAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
    yAxis.setDrawGridLines(true);

    chart.getXAxis().setEnabled(false);
    chart.getAxisRight().setEnabled(false);
    chart.getLegend().setEnabled(false);

    // TODO: chart contains all matches to date, highlight the specific match the user clicked

    chart.invalidate(); // refresh

    return view;
  }
}
