package net.frostedbytes.android.trendo.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import net.frostedbytes.android.trendo.MatchCenter;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Event;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.MatchEvent;
import net.frostedbytes.android.trendo.models.Team;
import net.frostedbytes.android.trendo.views.TouchableImageView;
import net.frostedbytes.android.trendo.views.TouchableTextView;

public class MatchDetailFragment extends Fragment {

  private static final String TAG = "MatchDetailFragment";

  public static final String ARG_MATCH_ID = "match_id";

  private static final String DIALOG_EVENT = "DialogEvent";

  public static final int REQUEST_MATCH = 0;
  public static final int REQUEST_DATE = 1;
  public static final int REQUEST_MATCH_EVENT = 2;

  private TextView mHomeScoreText;
  private TextView mAwayScoreText;

  private TableLayout mTrendTable;
  private TableLayout mRecordAgainstTable;

  private String mMatchId;
  private Match mMatch;
  private Team mAway;
  private Team mHome;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    if (getArguments() == null) {
      Log.d(TAG, "matchId has not been set.");
    } else {
      mMatchId = getArguments().getString(ARG_MATCH_ID);
      Log.d(TAG, "matchId: " + mMatchId);
      mMatch = MatchCenter.get(getActivity()).getMatch(mMatchId);
      mHome = MatchCenter.get(getActivity()).getTeam(mMatch.HomeId);
      mAway = MatchCenter.get(getActivity()).getTeam(mMatch.AwayId);
    }
  }

  @Override
  public void onPause() {
    super.onPause();

    Log.d(TAG, "++onPause()");
    mMatch = MatchCenter.get(getActivity()).getMatch(mMatch.Id);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_match_details, container, false);
    TouchableTextView homeText = view.findViewById(R.id.scoring_text_home_team);
    homeText.setText(mHome.ShortName);
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
    TouchableImageView increaseHomeImageView = view.findViewById(R.id.event_button_increase_home);
    if (mMatch != null && !mMatch.IsFinal) {
      increaseHomeImageView.setOnTouchListener(new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              addEvent(mMatch.HomeId);
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
    if (mMatch != null && !mMatch.IsFinal) {
      decreaseHomeImageView.setOnTouchListener(new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              addEvent(mMatch.HomeId);
              return true;
            case MotionEvent.ACTION_UP:
              view.performClick();
              return true;
          }

          return false;
        }
      });
    }

    TouchableTextView awayText = view.findViewById(R.id.scoring_text_away_team);
    awayText.setText(mAway.ShortName);
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
    TouchableImageView increaseAwayImageView = view.findViewById(R.id.event_button_increase_away);
    if (mMatch != null && !mMatch.IsFinal) {
      increaseAwayImageView.setOnTouchListener(new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              addEvent(mMatch.AwayId);
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
    if (mMatch != null && !mMatch.IsFinal) {
      decreaseAwayImageView.setOnTouchListener(new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              addEvent(mMatch.AwayId);
              return true;
            case MotionEvent.ACTION_UP:
              view.performClick();
              return true;
          }

          return false;
        }
      });
    }

    updateScores();

    // query information for trending section (using home team as default; user can toggle to away)
    mTrendTable = view.findViewById(R.id.trend_table_past_matches);
    mRecordAgainstTable = view.findViewById(R.id.record_table_opponent);
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
    FragmentManager manager = getFragmentManager();
    EventDetailFragment dialog = EventDetailFragment.newInstance(mMatch.Id, teamId);
    dialog.setTargetFragment(MatchDetailFragment.this, REQUEST_MATCH_EVENT);
    dialog.show(manager, DIALOG_EVENT);
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
    String ownGoalId = null;
    List<String> goalEventIds = new ArrayList<>();
    for (Event event : MatchCenter.get(getActivity()).getEvents()) {
      switch(event.Name) {
        case "Own Goal":
          ownGoalId = event.Id;
          goalEventIds.add(event.Id);
          break;
        case "Goal":
        case "Goal (Penalty)":
          goalEventIds.add(event.Id);
          break;
      }
    }

    int homeScore = 0;
    int awayScore = 0;
    for (MatchEvent matchEvent : MatchCenter.get(getActivity()).getMatchEvents(mMatchId)){
      if (goalEventIds.contains(matchEvent.EventId)) {
        if ((matchEvent.TeamId.equals(mHome.Id) && !matchEvent.EventId.equals(ownGoalId)) ||
            (matchEvent.TeamId.equals(mAway.Id) && matchEvent.EventId.equals(ownGoalId))) {
          homeScore++;
        } else if ((matchEvent.TeamId.equals(mAway.Id) && !matchEvent.EventId.equals(ownGoalId)) ||
            (matchEvent.TeamId.equals(mAway.Id) && matchEvent.EventId.equals(ownGoalId))) {
          awayScore++;
        }
      }
    }

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
