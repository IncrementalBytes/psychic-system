package net.frostedbytes.android.trendo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import java.util.UUID;

public class MatchFragment extends Fragment {

  private static final String TAG = "MatchFragment";

  private static final String ARG_MATCH_ID = "match_id";

  public static final int REQUEST_MATCH = 0;

  private TextView mHomeScoreText;
  private TextView mAwayScoreText;

  private TableLayout mTrendTable;
  private TableLayout mRecordAgainstTable;

  private Match mMatch;
  private Team mAway;
  private Team mHome;

  public static MatchFragment newInstance(UUID matchId) {

    System.out.println("++" + TAG + "::newInstance(UUID)");
    Bundle args = new Bundle();
    args.putSerializable(ARG_MATCH_ID, matchId);

    MatchFragment fragment = new MatchFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    System.out.println("++" + TAG + "::onCreate(Bundle)");
    if (getArguments() != null) {
      UUID matchId = (UUID) getArguments().getSerializable(ARG_MATCH_ID);
      mMatch = MatchCenter.get(getActivity()).getMatch(matchId);
      mHome = MatchCenter.get(getActivity()).getTeam(mMatch.getHomeId());
      mAway = MatchCenter.get(getActivity()).getTeam(mMatch.getAwayId());
    } else {
      System.out.println("getArguments() is null.");
    }
  }

  @Override
  public void onPause() {
    super.onPause();

    mMatch = MatchCenter.get(getActivity()).getMatch(mMatch.getId());
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    System.out.println("++" + TAG + "::onCreateView(LayoutInflater,ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.activity_match, container, false);

    TouchableTextView homeText = view.findViewById(R.id.scoring_text_home_team);
    homeText.setText(mHome.getShortName());
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
    TouchableTextView awayText = view.findViewById(R.id.scoring_text_away_team);
    awayText.setText(mAway.getShortName());
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
    TouchableImageView increaseHomeImageView = view.findViewById(R.id.event_button_increase_home);
    if (!mMatch.getIsMatchFinal()) {
      increaseHomeImageView.setOnTouchListener(new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              addEvent(mMatch.getHomeId());
              return true;
            case MotionEvent.ACTION_UP:
              view.performClick();
              return true;
          }

          return false;
        }
      });
    }

    TouchableImageView decreaseHomeImageView = view.findViewById(R.id.event_button_decrease_home);
    if (!mMatch.getIsMatchFinal()) {
      decreaseHomeImageView.setOnTouchListener(new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              editEvent(mMatch.getHomeId());
              return true;
            case MotionEvent.ACTION_UP:
              view.performClick();
              return true;
          }

          return false;
        }
      });
    }

    TouchableImageView increaseAwayImageView = view.findViewById(R.id.event_button_increase_away);
    if (!mMatch.getIsMatchFinal()) {
      increaseAwayImageView.setOnTouchListener(new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              addEvent(mMatch.getAwayId());
              return true;
            case MotionEvent.ACTION_UP:
              view.performClick();
              return true;
          }

          return false;
        }
      });
    }

    TouchableImageView decreaseAwayImageView = view.findViewById(R.id.event_button_decrease_away);
    if (!mMatch.getIsMatchFinal()) {
      decreaseAwayImageView.setOnTouchListener(new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              editEvent(mMatch.getAwayId());
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
    if (mMatch.getIsMatchFinal()) {
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

  private void addEvent(UUID teamId) {

    System.out.println("++" + TAG + "::addEvent(UUID)");
    // TODO: present user with event submission form to send to server
  }

  private void editEvent(UUID teamId) {

    System.out.println("++" + TAG + "::editEvent(UUID)");
    // TODO: list current events for team/match for user to remove/edit

    updateScores();
  }

  private void finalizeMatch() {

    System.out.println("++" + TAG + "::finalizeMatch()");
    // TODO: present user with event submission form to send to server
    populateTrend(mHome, mAway);
  }

  private void populateTrend(Team targetTeam, Team opponentTeam) {

    System.out.println("++" + TAG + "::populateTrend(Team, Team)");

    // clear the tables before proceeding
    mTrendTable.removeAllViews();
    mRecordAgainstTable.removeAllViews();
  }

  private void updateScores() {

    System.out.println("++" + TAG + "::updateScores()");
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
        System.out.println("In doInBackground");
      } catch (Exception e) {
        System.out.println("Failed in doInBackground: " + e.toString());
      }

      return null;
    }
  }
}
