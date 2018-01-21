package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.Map;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.MatchEvent;
import net.frostedbytes.android.trendo.models.Team;
import net.frostedbytes.android.trendo.views.TouchableImageView;
import net.frostedbytes.android.trendo.views.TouchableTextView;

public class MatchDetailActivity extends BaseActivity {

  private static final String TAG = "MatchDetailActivity";

  private static final int EVENT_CREATE_RESULT = 0;

  private TouchableTextView mHomeText;
  private TextView mHomeScoreText;
  private TouchableTextView mAwayText;
  private TextView mAwayScoreText;
  private Button mFinalizeButton;

  private TableLayout mTrendTable;
  private TableLayout mRecordAgainstTable;

  private Match mMatch;

  private Query mMatchQuery;
  private ValueEventListener mMatchesValueListener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_match_detail);

    mMatchQuery = FirebaseDatabase.getInstance().getReference().child("matches");
    final String finalMatchId = getIntent().getStringExtra(BaseActivity.ARG_MATCH_ID);
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

    mHomeText = findViewById(R.id.details_text_home_team);
    mHomeScoreText = findViewById(R.id.details_text_home_team_score);
    TouchableImageView addHomeEventImageView = findViewById(R.id.details_imageview_add_home_event);
    TouchableImageView editHomeEventImageView = findViewById(R.id.details_imageview_edit_home_event);
    mAwayText = findViewById(R.id.details_text_away_team);
    mAwayScoreText = findViewById(R.id.details_text_away_team_score);
    TouchableImageView addAwayEditImageView = findViewById(R.id.details_imageview_add_away_event);
    TouchableImageView editAwayEditImageView = findViewById(R.id.details_imageview_edit_away_event);
    mTrendTable = findViewById(R.id.details_table_past_matches);
    mRecordAgainstTable = findViewById(R.id.details_table_opponent);
    mFinalizeButton = findViewById(R.id.details_button_finalize);

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
              editEvent(mMatch.HomeTeam);
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
              editEvent(mMatch.AwayTeam);
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
  }

  @Override
  public void onStop() {
    super.onStop();

    Log.d(TAG, "++onStop()");
    if (mMatchQuery != null && mMatchesValueListener != null) {
      mMatchQuery.removeEventListener(mMatchesValueListener);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {

    if (resultCode != RESULT_OK) {
      Log.d(TAG, "Child activity returned cancelled.");
      return;
    }

    if (requestCode == EVENT_CREATE_RESULT) {
      if (data == null) {
        Log.d(TAG, "Result data is null.");
      }

      // TODO: refresh to pick up new event
    }
  }

  private void addEvent(Team team) {

    Log.d(TAG, "++addEvent(Team)");
    Intent intent = new Intent(getApplicationContext(), EventDetailActivity.class);
    intent.putExtra(BaseActivity.ARG_MATCH_ID, mMatch.Id);
    intent.putExtra(BaseActivity.ARG_TEAM, team);
    startActivityForResult(intent, EVENT_CREATE_RESULT);
  }

  private void editEvent(Team team) {

    Log.d(TAG, "++editEvent(String)");
    // TODO: figure out editing of match events
  }

  private void finalizeMatch() {

    Log.d(TAG, "++finalizeMatch()");
    // TODO: add as an event instead of a button
    mMatch.IsFinal = true;
    mFinalizeButton.setEnabled(false);
    FirebaseDatabase.getInstance().getReference().child("matches/" + mMatch.Id + "/IsFinal").setValue(mMatch.IsFinal);
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

      // initialize the finalize button
      if (mMatch != null && mMatch.IsFinal) {
        mFinalizeButton.setEnabled(false);
      } else {
        mFinalizeButton.setEnabled(true);
        mFinalizeButton.setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View v) {

            finalizeMatch();
          }
        });
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
}
