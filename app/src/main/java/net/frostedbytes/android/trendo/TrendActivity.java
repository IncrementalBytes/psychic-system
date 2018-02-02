package net.frostedbytes.android.trendo;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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

    int current = mTrend.TotalPoints.size() - 1;
    int previous = mTrend.TotalPoints.size() - 2;
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

    hideProgressDialog();
  }
}
