package net.frostedbytes.android.trendo;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import net.frostedbytes.android.trendo.models.Trend;

public class TrendActivity extends BaseActivity {

  private static final String TAG = "TrendActivity";

  private Trend mTrend;

  private Query mTrendQuery;
  private ValueEventListener mTrendValueListener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_trend);

    ImageView cancelImageView = findViewById(R.id.trend_image_cancel);

    final String matchId = getIntent().getStringExtra(BaseActivity.ARG_MATCH_ID);
    mTrendQuery = FirebaseDatabase.getInstance().getReference().child("Trends").child(matchId);
    mTrendValueListener = new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        showProgressDialog("Processing...");
        mTrend = dataSnapshot.getValue(Trend.class);
        if (mTrend != null) {
          mTrend.MatchId = dataSnapshot.getKey();
          populateTrendData();
        } else {
          Log.w(TAG, "Could not get trend data for " + matchId);
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.e(TAG, databaseError.getDetails());
      }
    };
    mTrendQuery.addValueEventListener(mTrendValueListener);

    cancelImageView.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {

        Log.d(TAG, "++cancelImageView::onClick(View");
        Log.d(TAG, "User cancelled match creation");
        setResult(RESULT_CANCELED);
        finish();
      }
    });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.d(TAG, "++onDestroy()");
    if (mTrendQuery != null && mTrendValueListener != null) {
      mTrendQuery.removeEventListener(mTrendValueListener);
    }
  }

  private void populateTrendData() {

    final Button trendTotalPointsButton = findViewById(R.id.trend_button_points_header);
    final Button trendPointsPerGameButton = findViewById(R.id.trend_button_ppg_header);
    final Button trendGoalsForButton = findViewById(R.id.trend_button_goals_for_header);
    final Button trendGoalsAgainstButton = findViewById(R.id.trend_button_goals_against_header);
    final Button trendGoalDifferentialButton = findViewById(R.id.trend_button_goal_diff_header);

    int current = mTrend.TotalPoints.size() - 1;
    int previous = mTrend.TotalPoints.size() - 2;

    trendTotalPointsButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        trendTotalPointsButton.setBackgroundColor(getResources().getColor(R.color.colorAccentTransparent));
        trendPointsPerGameButton.setBackgroundColor(Color.WHITE);
        trendGoalsForButton.setBackgroundColor(Color.WHITE);
        trendGoalsAgainstButton.setBackgroundColor(Color.WHITE);
        trendGoalDifferentialButton.setBackgroundColor(Color.WHITE);

        drawLineChart(mTrend.TotalPoints);
      }
    });

    TextView trendValue = findViewById(R.id.trend_text_points_value);
    trendValue.setText(String.format(getString(R.string.trend_round_value), mTrend.TotalPoints.get(current)));
    trendValue = findViewById(R.id.trend_text_points_diff);
    long diff = mTrend.TotalPoints.get(current) - mTrend.TotalPoints.get(previous);
    if (diff < 0) {
      trendValue.setTextColor(Color.RED);
      trendValue.setText(String.format(getString(R.string.trend_negative_round), diff));
    } else if (diff > 0) {
      trendValue.setTextColor(Color.GREEN);
      trendValue.setText(String.format(getString(R.string.trend_positive_round), diff));
    } else {
      trendValue.setTextColor(Color.BLACK);
      trendValue.setText("-");
    }

    trendPointsPerGameButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        trendTotalPointsButton.setBackgroundColor(Color.WHITE);
        trendPointsPerGameButton.setBackgroundColor(getResources().getColor(R.color.colorAccentTransparent));
        trendGoalsForButton.setBackgroundColor(Color.WHITE);
        trendGoalsAgainstButton.setBackgroundColor(Color.WHITE);
        trendGoalDifferentialButton.setBackgroundColor(Color.WHITE);

        //drawLineChart(mTrend.PointsPerGame);
      }
    });

    trendValue = findViewById(R.id.trend_text_ppg_value);
    trendValue.setText(String.format(getString(R.string.trend_decimal_value), mTrend.PointsPerGame.get(current)));
    trendValue = findViewById(R.id.trend_text_ppg_diff);
    double ppgDiff = mTrend.PointsPerGame.get(current) - mTrend.PointsPerGame.get(previous);
    if (ppgDiff < 0) {
      trendValue.setTextColor(Color.RED);
      trendValue.setText(String.format(getString(R.string.trend_negative_decimal), ppgDiff));
    } else if (ppgDiff > 0) {
      trendValue.setTextColor(Color.GREEN);
      trendValue.setText(String.format(getString(R.string.trend_positive_decimal), ppgDiff));
    } else {
      trendValue.setTextColor(Color.BLACK);
      trendValue.setText("-");
    }

    trendGoalsForButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        trendTotalPointsButton.setBackgroundColor(Color.WHITE);
        trendPointsPerGameButton.setBackgroundColor(Color.WHITE);
        trendGoalsForButton.setBackgroundColor(getResources().getColor(R.color.colorAccentTransparent));
        trendGoalsAgainstButton.setBackgroundColor(Color.WHITE);
        trendGoalDifferentialButton.setBackgroundColor(Color.WHITE);

        drawLineChart(mTrend.GoalsFor);
      }
    });

    trendValue = findViewById(R.id.trend_text_goals_for_value);
    trendValue.setText(String.format(getString(R.string.trend_round_value), mTrend.GoalsFor.get(current)));
    trendValue = findViewById(R.id.trend_text_goals_for_diff);
    diff = mTrend.GoalsFor.get(current) - mTrend.GoalsFor.get(previous);
    if (diff < 0) {
      trendValue.setTextColor(Color.RED);
      trendValue.setText(String.format(getString(R.string.trend_negative_round), diff));
    } else if (diff > 0) {
      trendValue.setTextColor(Color.GREEN);
      trendValue.setText(String.format(getString(R.string.trend_positive_round), diff));
    } else {
      trendValue.setTextColor(Color.BLACK);
      trendValue.setText("-");
    }

    trendGoalsAgainstButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        trendTotalPointsButton.setBackgroundColor(Color.WHITE);
        trendPointsPerGameButton.setBackgroundColor(Color.WHITE);
        trendGoalsForButton.setBackgroundColor(Color.WHITE);
        trendGoalsAgainstButton.setBackgroundColor(getResources().getColor(R.color.colorAccentTransparent));
        trendGoalDifferentialButton.setBackgroundColor(Color.WHITE);

        drawLineChart(mTrend.GoalsAgainst);
      }
    });

    trendValue = findViewById(R.id.trend_text_goals_against_value);
    trendValue.setText(String.format(getString(R.string.trend_round_value), mTrend.GoalsAgainst.get(current)));
    trendValue = findViewById(R.id.trend_text_goals_against_diff);
    diff = mTrend.GoalsAgainst.get(current) - mTrend.GoalsAgainst.get(previous);
    if (diff < 0) {
      trendValue.setTextColor(Color.RED);
      trendValue.setText(String.format(getString(R.string.trend_negative_round), diff));
    } else if (diff > 0) {
      trendValue.setTextColor(Color.GREEN);
      trendValue.setText(String.format(getString(R.string.trend_positive_round), diff));
    } else {
      trendValue.setTextColor(Color.BLACK);
      trendValue.setText("-");
    }

    trendGoalDifferentialButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        trendTotalPointsButton.setBackgroundColor(Color.WHITE);
        trendPointsPerGameButton.setBackgroundColor(Color.WHITE);
        trendGoalsForButton.setBackgroundColor(Color.WHITE);
        trendGoalsAgainstButton.setBackgroundColor(Color.WHITE);
        trendGoalDifferentialButton.setBackgroundColor(getResources().getColor(R.color.colorAccentTransparent));

        drawLineChart(mTrend.GoalDifferential);
      }
    });

    trendValue = findViewById(R.id.trend_text_goal_diff_value);
    trendValue.setText(String.format(getString(R.string.trend_round_value), mTrend.GoalDifferential.get(current)));
    trendValue = findViewById(R.id.trend_text_goal_diff);
    diff = mTrend.GoalDifferential.get(current) - mTrend.GoalDifferential.get(previous);
    if (diff < 0) {
      trendValue.setTextColor(Color.RED);
      trendValue.setText(String.format(getString(R.string.trend_negative_round), diff));
    } else if (diff > 0) {
      trendValue.setTextColor(Color.GREEN);
      trendValue.setText(String.format(getString(R.string.trend_positive_round), diff));
    } else {
      trendValue.setTextColor(Color.BLACK);
      trendValue.setText("-");
    }

    trendTotalPointsButton.callOnClick(); // default
    hideProgressDialog();
  }

  private void drawLineChart(List<Long> data) {

    // draw the selected trend table
    LineChart chart = findViewById(R.id.trend_chart);

    List<Entry> entries = new ArrayList<>();
    int matchDays = 1;
    for (Long dataPoint : data) {
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
  }
}
