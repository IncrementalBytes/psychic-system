package net.frostedbytes.android.trendo.fragments;

import static net.frostedbytes.android.trendo.BaseActivity.BASE_TAG;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Trend;
import net.frostedbytes.android.trendo.utils.LogUtils;

public class LineChartFragment extends Fragment {

    private static final String TAG = BASE_TAG + LineChartFragment.class.getSimpleName();

    private LineChart mLineChart;

    private LineDataSet mAheadDataSet;
    private List<Entry> mAheadEntries;
    private LineDataSet mBehindDataSet;
    private List<Entry> mBehindEntries;
    private LineDataSet mMainDataSet;
    private List<Entry> mMainEntries;

    private Trend mAheadTrend;
    private Trend mBehindTrend;
    private Trend mTrend;

    public static LineChartFragment newInstance(Trend trend, Trend ahead, Trend behind) {

        LogUtils.debug(TAG, "++newInstance(Trend, Trend, Trend)");
        LineChartFragment fragment = new LineChartFragment();
        Bundle args = new Bundle();
        args.putSerializable(BaseActivity.ARG_TREND, trend);
        args.putSerializable(BaseActivity.ARG_AHEAD, ahead);
        args.putSerializable(BaseActivity.ARG_BEHIND, behind);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        LogUtils.debug(TAG, "++onAttach(Context)");
        mAheadEntries = new ArrayList<>();
        mBehindEntries = new ArrayList<>();
        mMainEntries = new ArrayList<>();

        Bundle arguments = getArguments();
        if (arguments == null) {
            Toast.makeText(context, "Did not receive details about match.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (arguments.containsKey(BaseActivity.ARG_AHEAD)) {
            mAheadTrend = new Trend();
            try {
                if (arguments.getSerializable(BaseActivity.ARG_AHEAD) != null) {
                    mAheadTrend = (Trend) arguments.getSerializable(BaseActivity.ARG_AHEAD);
                }
            } catch (ClassCastException cce) {
                LogUtils.debug(TAG, "%s", cce.getMessage());
            }

            if (mAheadTrend == null) {
                LogUtils.warn(TAG, "Ahead Trend data unexpected.");
            }
        }

        if (arguments.containsKey(BaseActivity.ARG_BEHIND)) {
            mBehindTrend = new Trend();
            try {
                if (arguments.getSerializable(BaseActivity.ARG_BEHIND) != null) {
                    mBehindTrend = (Trend) arguments.getSerializable(BaseActivity.ARG_BEHIND);
                }
            } catch (ClassCastException cce) {
                LogUtils.debug(TAG, "%s", cce.getMessage());
            }

            if (mBehindTrend == null) {
                LogUtils.warn(TAG, "Trend data unexpected.");
                return;
            }
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
            LogUtils.debug(TAG, "%s", cce.getMessage());
        }

        if (mTrend == null) {
            Toast.makeText(context, "Trend data unexpected.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> trendKeys;
        if (!mTrend.GoalDifferential.isEmpty()) { // order the keys ascending (keys are match dates in milliseconds)
            trendKeys = new ArrayList<>(mTrend.GoalDifferential.keySet());
        } else if (!mTrend.TotalPoints.isEmpty()) {
            trendKeys = new ArrayList<>(mTrend.TotalPoints.keySet());
        } else if (!mTrend.PointsPerGame.isEmpty()) {
            trendKeys = new ArrayList<>(mTrend.PointsPerGame.keySet());
        } else if (!mTrend.MaxPointsPossible.isEmpty()) {
            trendKeys = new ArrayList<>(mTrend.MaxPointsPossible.keySet());
        } else if (!mTrend.GoalsAgainst.isEmpty()) {
            trendKeys = new ArrayList<>(mTrend.GoalsAgainst.keySet());
        } else if (!mTrend.PointsByAverage.isEmpty()) {
            trendKeys = new ArrayList<>(mTrend.PointsByAverage.keySet());
        } else {
            trendKeys = new ArrayList<>(mTrend.GoalsFor.keySet());
        }

        Collections.sort(trendKeys);

        for (String trendKey : trendKeys) { // build the entries based on trend keys
            String trimmedKey = trendKey.substring(3);
            float sortedFloat = Float.parseFloat(trimmedKey);
            Long longItem = null;
            Float floatItem = null;
            if (!mTrend.GoalsFor.isEmpty()) {
                longItem = mTrend.GoalsFor.get(trendKey);
            } else if (!mTrend.GoalsAgainst.isEmpty()) {
                longItem = mTrend.GoalsAgainst.get(trendKey);
            } else if (!mTrend.GoalDifferential.isEmpty()) {
                longItem = mTrend.GoalDifferential.get(trendKey);
            } else if (!mTrend.TotalPoints.isEmpty()) {
                longItem = mTrend.TotalPoints.get(trendKey);
            } else if (!mTrend.PointsByAverage.isEmpty()) {
                longItem = mTrend.PointsByAverage.get(trendKey);
            } else if (!mTrend.PointsPerGame.isEmpty()) {
                Double temp = mTrend.PointsPerGame.get(trendKey);
                if (temp != null) {
                    floatItem = temp.floatValue();
                }
            } else if (!mTrend.MaxPointsPossible.isEmpty()) {
                longItem = mTrend.MaxPointsPossible.get(trendKey);
            }

            if (longItem != null) {
                mMainEntries.add(new Entry(sortedFloat, longItem));
            } else if (floatItem != null) {
                mMainEntries.add(new Entry(sortedFloat, floatItem));
            }
        }

        if (mAheadTrend != null) { // populate the data sets with data from the team ahead
            List<String> compareKeys;
            if (!mTrend.GoalDifferential.isEmpty()) {
                compareKeys = new ArrayList<>(mTrend.GoalDifferential.keySet());
            } else if (!mTrend.TotalPoints.isEmpty()) {
                compareKeys = new ArrayList<>(mTrend.TotalPoints.keySet());
            } else if (!mTrend.PointsByAverage.isEmpty()) {
                compareKeys = new ArrayList<>(mTrend.PointsByAverage.keySet());
            } else if (!mTrend.PointsPerGame.isEmpty()) {
                compareKeys = new ArrayList<>(mTrend.PointsPerGame.keySet());
            } else if (!mTrend.MaxPointsPossible.isEmpty()) {
                compareKeys = new ArrayList<>(mTrend.MaxPointsPossible.keySet());
            } else if (!mTrend.GoalsAgainst.isEmpty()) {
                compareKeys = new ArrayList<>(mTrend.GoalsAgainst.keySet());
            } else {
                compareKeys = new ArrayList<>(mTrend.GoalsFor.keySet());
            }

            Collections.sort(compareKeys);

            for (String compareKey : compareKeys) {
                String trimmedKey = compareKey.substring(3);
                float sortedFloat = Float.parseFloat(trimmedKey);
                Long longItem = null;
                Float floatItem = null;
                if (!mAheadTrend.GoalsFor.isEmpty()) {
                    longItem = mAheadTrend.GoalsFor.get(compareKey);
                } else if (!mAheadTrend.GoalsAgainst.isEmpty()) {
                    longItem = mAheadTrend.GoalsAgainst.get(compareKey);
                } else if (!mAheadTrend.GoalDifferential.isEmpty()) {
                    longItem = mAheadTrend.GoalDifferential.get(compareKey);
                } else if (!mAheadTrend.TotalPoints.isEmpty()) {
                    longItem = mAheadTrend.TotalPoints.get(compareKey);
                } else if (!mAheadTrend.PointsByAverage.isEmpty()) {
                    longItem = mAheadTrend.PointsByAverage.get(compareKey);
                } else if (!mAheadTrend.PointsPerGame.isEmpty()) {
                    Double temp = mAheadTrend.PointsPerGame.get(compareKey);
                    if (temp != null) {
                        floatItem = temp.floatValue();
                    }
                } else if (!mAheadTrend.MaxPointsPossible.isEmpty()) {
                    longItem = mAheadTrend.MaxPointsPossible.get(compareKey);
                }

                if (longItem != null) {
                    mAheadEntries.add(new Entry(sortedFloat, longItem));
                } else if (floatItem != null) {
                    mAheadEntries.add(new Entry(sortedFloat, floatItem));
                }
            }
        }

        if (mBehindTrend != null) { // populate the data sets with data from the team behind
            List<String> compareKeys;
            if (!mTrend.GoalDifferential.isEmpty()) {
                compareKeys = new ArrayList<>(mTrend.GoalDifferential.keySet());
            } else if (!mTrend.TotalPoints.isEmpty()) {
                compareKeys = new ArrayList<>(mTrend.TotalPoints.keySet());
            } else if (!mTrend.PointsByAverage.isEmpty()) {
                compareKeys = new ArrayList<>(mTrend.PointsByAverage.keySet());
            } else if (!mTrend.PointsPerGame.isEmpty()) {
                compareKeys = new ArrayList<>(mTrend.PointsPerGame.keySet());
            } else if (!mTrend.MaxPointsPossible.isEmpty()) {
                compareKeys = new ArrayList<>(mTrend.MaxPointsPossible.keySet());
            } else if (!mTrend.GoalsAgainst.isEmpty()) {
                compareKeys = new ArrayList<>(mTrend.GoalsAgainst.keySet());
            } else {
                compareKeys = new ArrayList<>(mTrend.GoalsFor.keySet());
            }

            Collections.sort(compareKeys);

            for (String compareKey : compareKeys) {
                String trimmedKey = compareKey.substring(3);
                float sortedFloat = Float.parseFloat(trimmedKey);
                Long longItem = null;
                Float floatItem = null;
                if (!mBehindTrend.GoalsFor.isEmpty()) {
                    longItem = mBehindTrend.GoalsFor.get(compareKey);
                } else if (!mBehindTrend.GoalsAgainst.isEmpty()) {
                    longItem = mBehindTrend.GoalsAgainst.get(compareKey);
                } else if (!mBehindTrend.GoalDifferential.isEmpty()) {
                    longItem = mBehindTrend.GoalDifferential.get(compareKey);
                } else if (!mBehindTrend.TotalPoints.isEmpty()) {
                    longItem = mBehindTrend.TotalPoints.get(compareKey);
                } else if (!mBehindTrend.PointsByAverage.isEmpty()) {
                    longItem = mBehindTrend.PointsByAverage.get(compareKey);
                } else if (!mBehindTrend.PointsPerGame.isEmpty()) {
                    Double temp = mBehindTrend.PointsPerGame.get(compareKey);
                    if (temp != null) {
                        floatItem = temp.floatValue();
                    }
                } else if (!mBehindTrend.MaxPointsPossible.isEmpty()) {
                    longItem = mBehindTrend.MaxPointsPossible.get(compareKey);
                }

                if (longItem != null) {
                    mBehindEntries.add(new Entry(sortedFloat, longItem));
                } else if (floatItem != null) {
                    mBehindEntries.add(new Entry(sortedFloat, floatItem));
                }
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LogUtils.debug(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
        View view = inflater.inflate(R.layout.fragment_line_chart, container, false);
        mLineChart = view.findViewById(R.id.line_chart);

        if (mAheadEntries.size() > 0) { // add data and customize line with team ahead of target
            mAheadDataSet = new LineDataSet(mAheadEntries, mAheadTrend.TeamShortName);
            mAheadDataSet.setAxisDependency(AxisDependency.LEFT);
            if (getContext() != null) {
                mAheadDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorAhead));
            } else {
                mAheadDataSet.setColor(Color.GREEN);
            }

            mAheadDataSet.setLineWidth(2.0f);
            mAheadDataSet.disableDashedLine();
            mAheadDataSet.setDrawCircles(false);
            mAheadDataSet.setDrawValues(false);
            mAheadDataSet.setHighlightEnabled(true);
            mAheadDataSet.setDrawHighlightIndicators(true);
            mAheadDataSet.setDrawHorizontalHighlightIndicator(false);
        }

        if (mBehindEntries.size() > 0) { // add data and customize line with team behind target
            mBehindDataSet = new LineDataSet(mBehindEntries, mBehindTrend.TeamShortName);
            mBehindDataSet.setAxisDependency(AxisDependency.LEFT);
            if (getContext() != null) {
                mBehindDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorBehind));
            } else {
                mBehindDataSet.setColor(Color.YELLOW);
            }

            mBehindDataSet.setLineWidth(2.0f);
            mBehindDataSet.disableDashedLine();
            mBehindDataSet.setDrawCircles(false);
            mBehindDataSet.setDrawValues(false);
            mBehindDataSet.setHighlightEnabled(true);
            mBehindDataSet.setDrawHighlightIndicators(true);
            mBehindDataSet.setDrawHorizontalHighlightIndicator(false);
        }

        mMainDataSet = new LineDataSet(mMainEntries, mTrend.TeamShortName);
        mMainDataSet.setAxisDependency(AxisDependency.LEFT);
        if (getContext() != null) {
            mMainDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorFavorite));
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
            legend.setDrawInside(false);
            legend.setEnabled(true);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            legend.setMaxSizePercent(.80f);
            legend.setOrientation(Legend.LegendOrientation.VERTICAL);
            legend.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextPrimary));
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
            legend.setWordWrapEnabled(true);
            legend.setXEntrySpace(10f);
            legend.setYEntrySpace(10f);

            // TODO: what should we do when multiple values are drawn? reset marker to upper line?
//      CustomMarkerView customMaker = new CustomMarkerView(getContext(), R.layout.custom_marker);
//      customMaker.setChartView(mLineChart);
//      mLineChart.setMarker(customMaker);

            updateUI();
        } else {
            LogUtils.error(TAG, "Could not set chart resources; context is null.");
        }

        return view;
    }

    private void updateUI() {

        LogUtils.debug(TAG, "++updateUI()");
        List<ILineDataSet> dataSets = new ArrayList<>();
        if (mAheadEntries.size() > 0) {
            dataSets.add(mAheadDataSet);
        }

        if (mMainEntries.size() > 0) {
            dataSets.add(mMainDataSet);
        }

        if (mBehindEntries.size() > 0) {
            dataSets.add(mBehindDataSet);
        }

        LineData lineData = new LineData(dataSets);
        mLineChart.setData(lineData);
        mLineChart.invalidate(); // refresh
    }
}
