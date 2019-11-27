/*
 * Copyright 2019 Ryan Ward
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
package net.frostedbytes.android.trendo.ui.fragments;

import static net.frostedbytes.android.trendo.ui.BaseActivity.BASE_TAG;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
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

import net.frostedbytes.android.trendo.common.Trend;
import net.frostedbytes.android.trendo.db.entity.TrendEntity;
import net.frostedbytes.android.trendo.ui.BaseActivity;
import net.frostedbytes.android.trendo.R;

public class LineChartFragment extends Fragment {

  private static final String TAG = BASE_TAG + LineChartFragment.class.getSimpleName();

  public interface OnLineChartListener {

    void onLineChartInit(boolean isSuccessful);
  }

  private OnLineChartListener mCallback;

  private LineChart mLineChart;

  //    private LineDataSet mAheadDataSet;
//    private List<Entry> mAheadEntries;
//    private LineDataSet mBehindDataSet;
//    private List<Entry> mBehindEntries;
  private LineDataSet mMainDataSet;
  private List<Entry> mMainEntries;

  //    private TrendEntity mAheadTrend;
//    private TrendEntity mBehindTrend;
  private ArrayList<TrendEntity> mTrends;
  private Trend mSelectedTrend;

  public static LineChartFragment newInstance(ArrayList<TrendEntity> trends, Trend selectedTrend) {

    Log.d(TAG, "++newInstance(Trend, Trend, Trend)");
    LineChartFragment fragment = new LineChartFragment();
    Bundle args = new Bundle();
    args.putSerializable(BaseActivity.ARG_TRENDS, trends);
    args.putSerializable(BaseActivity.ARG_TREND, selectedTrend);
//        args.putSerializable(BaseActivity.ARG_AHEAD, ahead);
//        args.putSerializable(BaseActivity.ARG_BEHIND, behind);
    fragment.setArguments(args);
    return fragment;
  }

  /*
      Fragment Override(s)
   */
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    Log.d(TAG, "++onAttach(Context)");
//        mAheadEntries = new ArrayList<>();
//        mBehindEntries = new ArrayList<>();
    mMainEntries = new ArrayList<>();

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
    if (arguments == null) {
      Log.w(TAG, "Did not receive details about match.");
      mCallback.onLineChartInit(false);
      return;
    }

//        if (arguments.containsKey(BaseActivity.ARG_AHEAD)) {
//            mAheadTrend = new TrendEntity();
//            try {
//                if (arguments.getSerializable(BaseActivity.ARG_AHEAD) != null) {
//                    mAheadTrend = (TrendEntity) arguments.getSerializable(BaseActivity.ARG_AHEAD);
//                }
//            } catch (ClassCastException cce) {
//                Log.d(TAG, cce.getMessage());
//            }
//
//            if (mAheadTrend == null) {
//                Log.w(TAG, "Ahead Trend data unexpected.");
//            }
//        }
//
//        if (arguments.containsKey(BaseActivity.ARG_BEHIND)) {
//            mBehindTrend = new TrendEntity();
//            try {
//                if (arguments.getSerializable(BaseActivity.ARG_BEHIND) != null) {
//                    mBehindTrend = (TrendEntity) arguments.getSerializable(BaseActivity.ARG_BEHIND);
//                }
//            } catch (ClassCastException cce) {
//                Log.d(TAG, cce.getMessage());
//            }
//
//            if (mBehindTrend == null) {
//                Log.w(TAG, "Trend data unexpected.");
//                return;
//            }
//        }

    if (!arguments.containsKey(BaseActivity.ARG_TRENDS)) {
      Log.e(TAG, "Did not receive trend data.");
      return;
    }

    mTrends = new ArrayList<>();
    try {
      if (arguments.getSerializable(BaseActivity.ARG_TREND) != null) {
        mTrends = (ArrayList<TrendEntity>) arguments.getSerializable(BaseActivity.ARG_TRENDS);
        mSelectedTrend = (Trend) arguments.getSerializable(BaseActivity.ARG_TREND);
      }
    } catch (ClassCastException cce) {
      Log.d(TAG, cce.getMessage());
    }

    if (mTrends == null || mSelectedTrend == null) {
      Log.e(TAG, "Trend data unexpected.");
      return;
    }

    // build the entries based on trend keys
    for (TrendEntity trend : mTrends) {
      switch (mSelectedTrend) {
        case GoalDifferential:
          mMainEntries.add(new Entry(trend.Match, trend.GoalDifferential));
          break;
        case GoalsAgainst:
          mMainEntries.add(new Entry(trend.Match, trend.GoalsAgainst));
          break;
        case GoalsFor:
          mMainEntries.add(new Entry(trend.Match, trend.GoalsFor));
          break;
        case MaxPointsPossible:
          mMainEntries.add(new Entry(trend.Match, trend.MaxPointsPossible));
          break;
        case PointsByAverage:
          mMainEntries.add(new Entry(trend.Match, trend.PointsByAverage));
          break;
        case PointsPerGame:
          double temp = trend.PointsPerGame;
          mMainEntries.add(new Entry(trend.Match, (float) temp));
          break;
        case TotalPoints:
          mMainEntries.add(new Entry(trend.Match, trend.TotalPoints));
          break;
      }
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_line_chart, container, false);
    mLineChart = view.findViewById(R.id.line_chart);

//    if (mAheadEntries.size() > 0) { // add data and customize line with team ahead of target
//      mAheadDataSet = new LineDataSet(
//        mAheadEntries,
//        String.format(
//          Locale.ENGLISH,
//          "%d. %s",
//          mAheadTrend.TeamObj.TablePosition,
//          mAheadTrend.TeamObj.ShortName));
//      mAheadDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
//      if (getContext() != null) {
//        mAheadDataSet.setColor(ContextCompat.getColor(getContext(), R.color.ahead));
//      } else {
//        mAheadDataSet.setColor(Color.GREEN);
//      }
//
//      mAheadDataSet.setLineWidth(2.0f);
//      mAheadDataSet.disableDashedLine();
//      mAheadDataSet.setDrawCircles(false);
//      mAheadDataSet.setDrawValues(false);
//      mAheadDataSet.setHighlightEnabled(true);
//      mAheadDataSet.setDrawHighlightIndicators(true);
//      mAheadDataSet.setDrawHorizontalHighlightIndicator(false);
//    }
//
//    if (mBehindEntries.size() > 0) { // add data and customize line with team behind target
//      mBehindDataSet = new LineDataSet(
//        mBehindEntries,
//        String.format(
//          Locale.ENGLISH,
//          "%d. %s",
//          mBehindTrend.TeamObj.TablePosition,
//          mBehindTrend.TeamObj.ShortName));
//      mBehindDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
//      if (getContext() != null) {
//        mBehindDataSet.setColor(ContextCompat.getColor(getContext(), R.color.behind));
//      } else {
//        mBehindDataSet.setColor(Color.YELLOW);
//      }
//
//      mBehindDataSet.setLineWidth(2.0f);
//      mBehindDataSet.disableDashedLine();
//      mBehindDataSet.setDrawCircles(false);
//      mBehindDataSet.setDrawValues(false);
//      mBehindDataSet.setHighlightEnabled(true);
//      mBehindDataSet.setDrawHighlightIndicators(true);
//      mBehindDataSet.setDrawHorizontalHighlightIndicator(false);
//    }

    mMainDataSet = new LineDataSet(
      mMainEntries,
      String.format(
        Locale.ENGLISH,
        "%s",
        mTrends.get(0).TeamId));
    mMainDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
    if (getContext() != null) {
      mMainDataSet.setColor(ContextCompat.getColor(getContext(), R.color.favorite));
    } else {
      mMainDataSet.setColor(Color.YELLOW);
    }

    mMainDataSet = new LineDataSet(
        mMainEntries,
        String.format(
            Locale.ENGLISH,
            "%d. %s",
0,
//            mTrends.TablePosition,
            mTrends.get(0).TeamId));
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
      updateUI();
    } else {
      Log.e(TAG, "Could not set chart resources; context is null.");
      mCallback.onLineChartInit(false);
    }

    return view;
  }

    @Override
    public void onDestroy() {
      super.onDestroy();

      Log.d(TAG, "++onDestroy()");
//      mAheadDataSet = null;
//      mAheadEntries = null;
//      mAheadTrend = null;
      mMainEntries = null;
      mMainDataSet = null;
      mTrends = null;
//      mBehindDataSet = null;
//      mBehindEntries = null;
//      mBehindTrend = null;
    }

    /*
        Private Method(s)
     */
    private void updateUI() {

      Log.d(TAG, "++updateUI()");
      List<ILineDataSet> dataSets = new ArrayList<>();
//      if (mAheadEntries.size() > 0) {
//        dataSets.add(mAheadDataSet);
//      }

      if (mMainEntries.size() > 0) {
        dataSets.add(mMainDataSet);
      }

//      if (mBehindEntries.size() > 0) {
//        dataSets.add(mBehindDataSet);
//      }

      LineData lineData = new LineData(dataSets);
      mLineChart.setData(lineData);
      mLineChart.invalidate(); // refresh
    }
  }
