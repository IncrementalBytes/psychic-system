package net.frostedbytes.android.trendo.fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.Team;

public class MatchDetailFragment extends Fragment {

  private static final String TAG = "MatchDetailFragment";

  public static final String ARG_MATCH_ID = "match_id";

  public static final int REQUEST_MATCH = 0;

  private TextView mHomeScoreText;
  private TextView mAwayScoreText;

  private TableLayout mTrendTable;
  private TableLayout mRecordAgainstTable;

  private String mMatchId;
  private Match mMatch;
  private Team mAway;
  private Team mHome;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater,ViewGroup, Bundle)");
    Bundle arguments = getArguments();
    if (arguments == null) {
      Log.d(TAG, "matchId has not been set.");
    } else {
      mMatchId = getArguments().getString(ARG_MATCH_ID);
      Log.d(TAG, "matchId: " + mMatchId);
      // TODO: get match
      // TODO: get home team
      // TODO: get away team
    }

    View view = inflater.inflate(R.layout.fragment_match_details, container, false);
    TextView homeText = view.findViewById(R.id.scoring_text_home_team);
    homeText.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
          case MotionEvent.ACTION_DOWN:
            populateTrend(mHome, mAway);
            return true;
          case MotionEvent.ACTION_UP:
            view.performClick();
            return true;
        }

        return false;
      }
    });

    mHomeScoreText = view.findViewById(R.id.scoring_text_home_team_score);
    TextView awayText = view.findViewById(R.id.scoring_text_away_team);
    awayText.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
          case MotionEvent.ACTION_DOWN:
            populateTrend(mAway, mHome);
            return true;
          case MotionEvent.ACTION_UP:
            view.performClick();
            return true;
        }

        return false;
      }
    });

    mAwayScoreText = view.findViewById(R.id.scoring_text_away_team_score);
    mTrendTable = view.findViewById(R.id.trend_table_past_matches);
    mRecordAgainstTable = view.findViewById(R.id.record_table_opponent);

    updateScores();

    // setup event increase/decrease buttons
    ImageView increaseHomeImageView = view.findViewById(R.id.event_button_increase_home);
    if (mMatch != null && !mMatch.IsFinal) {
      increaseHomeImageView.setOnTouchListener(new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              addEvent(mMatch.HomeId);
//              addEvent(UUID.fromString(mMatch.getHomeId()));
              return true;
            case MotionEvent.ACTION_UP:
              view.performClick();
              return true;
          }

          return false;
        }
      });
    }

    ImageView decreaseHomeImageView = view.findViewById(R.id.event_button_decrease_home);
    if (mMatch != null && !mMatch.IsFinal) {
      decreaseHomeImageView.setOnTouchListener(new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              addEvent(mMatch.HomeId);
//              editEvent(UUID.fromString(mMatch.getHomeId()));
              return true;
            case MotionEvent.ACTION_UP:
              view.performClick();
              return true;
          }

          return false;
        }
      });
    }

    ImageView increaseAwayImageView = view.findViewById(R.id.event_button_increase_away);
    if (mMatch != null && !mMatch.IsFinal) {
      increaseAwayImageView.setOnTouchListener(new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              addEvent(mMatch.AwayId);
//              addEvent(UUID.fromString(mMatch.getAwayId()));
              return true;
            case MotionEvent.ACTION_UP:
              view.performClick();
              return true;
          }

          return false;
        }
      });
    }

    ImageView decreaseAwayImageView = view.findViewById(R.id.event_button_decrease_away);
    if (mMatch != null && !mMatch.IsFinal) {
      decreaseAwayImageView.setOnTouchListener(new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              addEvent(mMatch.AwayId);
//              editEvent(UUID.fromString(mMatch.getAwayId()));
              return true;
            case MotionEvent.ACTION_UP:
              view.performClick();
              return true;
          }

          return false;
        }
      });
    }

    // query information for trending section (using home team as default; user can toggle to away)
    populateTrend(mHome, mAway);
    new PopulateTrendTask().execute();

    // initialize the finalize button
    Button finalizeMatch = view.findViewById(R.id.match_button_finalize);
    if (mMatch != null && !mMatch.IsFinal) {
      finalizeMatch.setEnabled(false);
    } else {
      finalizeMatch.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
          finalizeMatch();
        }
      });
    }

    return view;
  }

  private void addEvent(String teamId) {

    Log.d(TAG, "++addEvent(String)");
    // TODO: present user with event submission form to send to server
  }

  private void editEvent(String teamId) {

    Log.d(TAG, "++editEvent(String)");
    // TODO: list current events for team/match for user to remove/edit

    updateScores();
  }

  private void finalizeMatch() {

    Log.d(TAG, "++finalizeMatch()");
    // TODO: present user with event submission form to send to server
    populateTrend(mHome, mAway);
  }

  private void populateTrend(Team targetTeam, Team opponentTeam) {

    Log.d(TAG, "++populateTrend(Team, Team)");

    // clear the tables before proceeding
    mTrendTable.removeAllViews();
    mRecordAgainstTable.removeAllViews();
  }

  private void updateScores() {

    Log.d(TAG, "++updateScores()");
    // TODO: get events for this match and calculate goals for each team
    int homeScore = 0;
    int awayScore = 0;
    mHomeScoreText.setText(String.valueOf(homeScore));
    mAwayScoreText.setText(String.valueOf(awayScore));
  }

  private static class PopulateTrendTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {

      try {
        Log.d(TAG, "In doInBackground");
      } catch (Exception e) {
        Log.d(TAG, "Failed in doInBackground: " + e.toString());
      }

      return null;
    }
  }
}
