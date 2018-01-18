package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
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
import android.widget.TableLayout;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.Map;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.MatchEvent;
import net.frostedbytes.android.trendo.models.Team;
import net.frostedbytes.android.trendo.views.TouchableImageView;
import net.frostedbytes.android.trendo.views.TouchableTextView;

public class MatchDetailFragment extends Fragment {

  private static final String TAG = "MatchDetailFragment";

  public static final String ARG_MATCH_ID = "match_id";
  public static final String ARG_TEAM = "team";

  public static final int REQUEST_DATE = 0;

  private MatchDetailListener mCallback;

  public interface MatchDetailListener {

    void onCreateMatchEventRequest(String matchId, Team team);
    void onEditMatchEventRequest(String matchId, String teamShortName);
  }

  private TouchableTextView mHomeText;
  private TextView mHomeScoreText;
  private TouchableTextView mAwayText;
  private TextView mAwayScoreText;

  private TableLayout mTrendTable;
  private TableLayout mRecordAgainstTable;

  private Match mMatch;

  private Query mMatchQuery;
  private ValueEventListener mMatchesValueListener;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    Log.d(TAG, "++onAttach(Context)");
    try {
      mCallback = (MatchDetailListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException("Calling activity/fragment must implement MatchDetailListener.");
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    Bundle arguments = getArguments();
    String matchId = BaseActivity.DEFAULT_ID;
    if (arguments != null) {
      matchId = getArguments().getString(ARG_MATCH_ID);
    }

    mMatchQuery = FirebaseDatabase.getInstance().getReference().child("matches");
    final String finalMatchId = matchId;
    mMatchesValueListener = new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        mMatch = new Match();
        for (DataSnapshot data : dataSnapshot.getChildren()) {
          if (data.getKey().equals(finalMatchId)) {
            Match match = data.getValue(Match.class);
            if (match != null) {
              mMatch = match;
              mMatch.Id = data.getKey();
            }
          }
        }

        onGatheringMatchComplete(mMatch);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++onCancelled(DatabaseError)");
        Log.e(TAG, databaseError.getMessage());
      }
    };

    mMatchQuery.addValueEventListener(mMatchesValueListener);

    View view = inflater.inflate(R.layout.fragment_match_details, container, false);
    mHomeText = view.findViewById(R.id.scoring_text_home_team);
    mHomeScoreText = view.findViewById(R.id.scoring_text_home_team_score);
    TouchableImageView addHomeEventImageView = view.findViewById(R.id.button_add_home_event);
    TouchableImageView editHomeEventImageView = view.findViewById(R.id.button_edit_home_event);
    mAwayText = view.findViewById(R.id.scoring_text_away_team);
    mAwayScoreText = view.findViewById(R.id.scoring_text_away_team_score);
    TouchableImageView addAwayEditImageView = view.findViewById(R.id.button_add_away_event);
    TouchableImageView editAwayEditImageView = view.findViewById(R.id.button_edit_away_event);
    mTrendTable = view.findViewById(R.id.trend_table_past_matches);
    mRecordAgainstTable = view.findViewById(R.id.record_table_opponent);

    mHomeText.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
          case MotionEvent.ACTION_DOWN:
            populateTrend(mMatch.HomeTeam, mMatch.AwayTeam);
            return true;
          case MotionEvent.ACTION_UP:
            view.performClick();
            return true;
        }

        return false;
      }
    });

    addHomeEventImageView.setOnTouchListener(new OnTouchListener() {

      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {

        if (mMatch != null && !mMatch.IsFinal) {
          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              //addEvent(mMatch.HomeTeam.Id);
              addEvent(mMatch.HomeTeam);
              return true;
            case MotionEvent.ACTION_UP:
              view.performClick();
              return true;
          }
        }
        return false;
      }
    });

    editHomeEventImageView.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {

        if (mMatch != null && !mMatch.IsFinal) {
          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              editEvent(mMatch.HomeTeam.Id);
              return true;
            case MotionEvent.ACTION_UP:
              view.performClick();
              return true;
          }
        }
        return false;
      }
    });

    mAwayText.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
          case MotionEvent.ACTION_DOWN:
            populateTrend(mMatch.AwayTeam, mMatch.HomeTeam);
            return true;
          case MotionEvent.ACTION_UP:
            view.performClick();
            return true;
        }

        return false;
      }
    });

    addAwayEditImageView.setOnTouchListener(new OnTouchListener() {

      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {

        if (mMatch != null && !mMatch.IsFinal) {
          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              //addEvent(mMatch.AwayTeam.Id);
              addEvent(mMatch.AwayTeam);
              return true;
            case MotionEvent.ACTION_UP:
              view.performClick();
              return true;
          }
        }
        return false;
      }
    });

    editAwayEditImageView.setOnTouchListener(new OnTouchListener() {

      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {

        if (mMatch != null && !mMatch.IsFinal) {
          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              editEvent(mMatch.AwayTeam.Id);
              return true;
            case MotionEvent.ACTION_UP:
              view.performClick();
              return true;
          }
        }
        return false;
      }
    });

    updateScores();

    // query information for trending section (using home team as default; user can toggle to away)
    if (mMatch != null && mMatch.HomeTeam != null && mMatch.AwayTeam != null) {
      populateTrend(mMatch.HomeTeam, mMatch.AwayTeam);
      new PopulateTrendTask().execute();
    }

    // initialize the finalize button
    Button finalizeMatch = view.findViewById(R.id.match_button_finalize);
    if (mMatch != null && mMatch.IsFinal) {
      finalizeMatch.setEnabled(false);
    } else {
      finalizeMatch.setEnabled(true);
      finalizeMatch.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
          finalizeMatch();
        }
      });
    }

    return view;
  }

  @Override
  public void onStart() {
    super.onStart();

    Log.d(TAG, "++onStart()");
  }

  @Override
  public void onStop() {
    super.onStop();

    Log.d(TAG, "++onStop()");
    if (mMatchQuery != null && mMatchesValueListener != null) {
      mMatchQuery.removeEventListener(mMatchesValueListener);
    }
  }

  private void addEvent(Team team) {

    Log.d(TAG, "++addEvent(Team)");
    if (mCallback != null) {
      mCallback.onCreateMatchEventRequest(mMatch.Id, team);
    } else {
      Log.e(TAG, "Callback was null.");
    }
  }

  private void editEvent(String teamId) {

    Log.d(TAG, "++editEvent(String)");
    if (mCallback != null) {
      mCallback.onEditMatchEventRequest(mMatch.Id, teamId);
    } else {
      Log.e(TAG, "Callback was null.");
    }
  }

  private void finalizeMatch() {

    Log.d(TAG, "++finalizeMatch()");
    // TODO: present user with event submission form to send to server
    populateTrend(mMatch.HomeTeam, mMatch.AwayTeam);
  }

  void onGatheringMatchComplete(Match match) {

    Log.d(TAG, "++onGatheringMatchComplete(Match)");
    if (match != null) {
      if (match.HomeTeam != null) {
        mHomeText.setText(match.HomeTeam.ShortName);
      }

      if (match.AwayTeam != null) {
        mAwayText.setText(match.AwayTeam.ShortName);
      }

      updateScores();
    }
  }

  private void populateTrend(Team targetTeam, Team opponentTeam) {

    Log.d(TAG, "++populateTrend(Team, Team)");
    if (targetTeam != null && opponentTeam != null) {
      // clear the tables before proceeding
      mTrendTable.removeAllViews();
      mRecordAgainstTable.removeAllViews();
    } else {
      Log.e(TAG, "Cannot populate trend; teams are not known.");
    }
  }

  private void updateScores() {

    Log.d(TAG, "++updateScores()");
    int homeScore = 0;
    int awayScore = 0;
    if (mMatch != null && mMatch.MatchEvents != null) {
      for (Map.Entry<String, MatchEvent> matchEvent : mMatch.MatchEvents.entrySet()) {
        switch (matchEvent.getValue().EventName) {
          case "Own Goal":
            if (matchEvent.getValue().TeamShortName.equals(mMatch.HomeTeam.ShortName)) {
              awayScore++;
            } else if (matchEvent.getValue().TeamShortName.equals(mMatch.AwayTeam.ShortName)) {
              homeScore++;
            }
            break;
          case "Goal":
          case "Goal (Penalty)":
            if (matchEvent.getValue().TeamShortName.equals(mMatch.HomeTeam.ShortName)) {
              homeScore++;
            } else if (matchEvent.getValue().TeamShortName.equals(mMatch.AwayTeam.ShortName)) {
              awayScore++;
            }
            break;
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
