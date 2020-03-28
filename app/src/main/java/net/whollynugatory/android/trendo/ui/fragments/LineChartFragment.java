/*
 * Copyright 2020 Ryan Ward
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.whollynugatory.android.trendo.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.whollynugatory.android.trendo.R;
import net.whollynugatory.android.trendo.common.Trend;
import net.whollynugatory.android.trendo.db.viewmodel.TrendoViewModel;
import net.whollynugatory.android.trendo.db.views.TrendDetails;
import net.whollynugatory.android.trendo.ui.BaseActivity;
import net.whollynugatory.android.trendo.utils.PreferenceUtils;

public class LineChartFragment extends Fragment {

  private static final String TAG = BaseActivity.BASE_TAG + "LineChartFragment";

  public interface OnLineChartListener {

    void onLineChartInit(boolean isSuccessful);
  }

  private OnLineChartListener mCallback;

  private TrendoViewModel mTrendoViewModel;

  private LineChart mLineChart;

  private LineDataSet mMainDataSet;
  private List<Entry> mMainEntries;

  private Trend mSelectedTrend;

  public static LineChartFragment newInstance(Trend selectedTrend) {

    Log.d(TAG, "++newInstance(Trend)");
    LineChartFragment fragment = new LineChartFragment();
    Bundle args = new Bundle();
    args.putSerializable(BaseActivity.ARG_TREND, selectedTrend);
    fragment.setArguments(args);
    return fragment;
  }

  /*
      Fragment Override(s)
   */
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    Log.d(TAG, "++onActivityCreated()");
    updateUI();
  }

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);

    Log.d(TAG, "++onAttach(Context)");
    try {
      mCallback = (OnLineChartListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(
        String.format(Locale.ENGLISH, "Missing interface implementations for %s", context.toString()));
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    Bundle arguments = getArguments();
    if (arguments != null) {
      if (arguments.containsKey(BaseActivity.ARG_TREND)) {
        mSelectedTrend = (Trend) arguments.getSerializable(BaseActivity.ARG_TREND);
      }
    }

    mTrendoViewModel = new ViewModelProvider(this).get(TrendoViewModel.class);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_line_chart, container, false);
    mLineChart = view.findViewById(R.id.line_chart);
    return view;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.d(TAG, "++onDestroy()");
    mMainEntries = null;
    mMainDataSet = null;
  }

  /*
    Private Method(s)
   */
  private void updateUI() {

    Log.d(TAG, "++updateUI()");
    mTrendoViewModel.getAllTrends(PreferenceUtils.getTeam(getContext()), PreferenceUtils.getSeason(getContext())).observe(
      getViewLifecycleOwner(),
      trendDetailsList -> {
        mMainEntries = new ArrayList<>();

        // build the entries based on trend keys
        for (TrendDetails trend : trendDetailsList) {
          switch (mSelectedTrend) {
            case GoalDifferential:
              mMainEntries.add(new Entry(trend.MatchNumber, trend.GoalDifferential));
              break;
            case GoalsAgainst:
              mMainEntries.add(new Entry(trend.MatchNumber, trend.GoalsAgainst));
              break;
            case GoalsFor:
              mMainEntries.add(new Entry(trend.MatchNumber, trend.GoalsFor));
              break;
            case MaxPointsPossible:
              mMainEntries.add(new Entry(trend.MatchNumber, trend.MaxPointsPossible));
              break;
            case PointsByAverage:
              mMainEntries.add(new Entry(trend.MatchNumber, trend.PointsByAverage));
              break;
            case PointsPerGame:
              double temp = trend.PointsPerGame;
              mMainEntries.add(new Entry(trend.MatchNumber, (float) temp));
              break;
            case TotalPoints:
              mMainEntries.add(new Entry(trend.MatchNumber, trend.TotalPoints));
              break;
          }
        }

        mMainDataSet = new LineDataSet(mMainEntries, trendDetailsList.get(0).ShortName);
        mMainDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        if (getContext() != null) {
          mMainDataSet.setColor(ContextCompat.getColor(getContext(), R.color.favorite));
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
          leftAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryText));
          leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
          leftAxis.setDrawGridLines(false);
          leftAxis.setDrawZeroLine(true);

          YAxis rightAxis = mLineChart.getAxisRight();
          rightAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryText));
          rightAxis.setEnabled(true);

          XAxis bottomAxis = mLineChart.getXAxis();
          bottomAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryText));
          bottomAxis.setDrawGridLines(false);
          bottomAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
          bottomAxis.setEnabled(false);

          Legend legend = mLineChart.getLegend();
          legend.setDrawInside(false);
          legend.setEnabled(true);
          legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
          legend.setMaxSizePercent(.80f);
          legend.setOrientation(Legend.LegendOrientation.VERTICAL);
          legend.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryText));
          legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
          legend.setWordWrapEnabled(true);
          legend.setXEntrySpace(10f);
          legend.setYEntrySpace(10f);

          mCallback.onLineChartInit(true);
        } else {
          Log.e(TAG, "Could not set chart resources; context is null.");
          mCallback.onLineChartInit(false);
        }

        List<ILineDataSet> dataSets = new ArrayList<>();

        if (mMainEntries.size() > 0) {
          dataSets.add(mMainDataSet);
        }

        LineData lineData = new LineData(dataSets);
        mLineChart.setData(lineData);
        mLineChart.invalidate(); // refresh
      });
  }
}
