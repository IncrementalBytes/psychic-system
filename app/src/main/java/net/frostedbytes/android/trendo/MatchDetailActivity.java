package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import java.util.HashMap;
import java.util.Map;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.MatchEvent;
import net.frostedbytes.android.trendo.views.TouchableImageView;
import net.frostedbytes.android.trendo.views.TouchableTextView;

public class MatchDetailActivity extends BaseActivity {

  private static final String TAG = "MatchDetailActivity";

  private static final int EVENT_CREATE_RESULT = 0;

  private TextView mHomeScoreText;
  private TextView mAwayScoreText;
  private Button mFinalizeButton;

  private TableLayout mTrendTable;
  private TableLayout mRecordAgainstTable;

  private Match mMatch;
  private Map<String, MatchEvent> mMatchEvents;

  private Query mMatchEventsQuery;
  private ChildEventListener mMatchEventsListener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_match_detail);

    mMatch = (Match)getIntent().getSerializableExtra(BaseActivity.ARG_MATCH);

    mMatchEvents = new HashMap<>();
    mMatchEventsQuery = FirebaseDatabase.getInstance().getReference().child("matches/" + mMatch.Id + "/MatchEvents");
    mMatchEventsListener = new ChildEventListener() {

      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        Log.d(TAG, "++onChildAdded(DataSnapshot, String)");
        MatchEvent matchEvent = dataSnapshot.getValue(MatchEvent.class);
        if (matchEvent != null) {
          mMatchEvents.put(dataSnapshot.getKey(), matchEvent);
        }

        updateScores();
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        Log.d(TAG, "++onChildChanged(DataSnapshot, String)");
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {

        Log.d(TAG, "++onChildRemoved(DataSnapshot)");
      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        Log.d(TAG, "++onChildMoved(DataSnapshot)");
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++onCancelled(DatabaseError)");
        Log.e(TAG, databaseError.getMessage());
      }
    };
    mMatchEventsQuery.addChildEventListener(mMatchEventsListener);

    TouchableTextView homeText = findViewById(R.id.details_text_home_team);
    mHomeScoreText = findViewById(R.id.details_text_home_team_score);
    TouchableImageView addHomeEventImageView = findViewById(R.id.details_imageview_add_home_event);
    TouchableImageView editHomeEventImageView = findViewById(R.id.details_imageview_edit_home_event);
    TouchableTextView awayText = findViewById(R.id.details_text_away_team);
    mAwayScoreText = findViewById(R.id.details_text_away_team_score);
    TouchableImageView addAwayEditImageView = findViewById(R.id.details_imageview_add_away_event);
    TouchableImageView editAwayEditImageView = findViewById(R.id.details_imageview_edit_away_event);
    mTrendTable = findViewById(R.id.details_table_past_matches);
    mRecordAgainstTable = findViewById(R.id.details_table_opponent);
    mFinalizeButton = findViewById(R.id.details_button_finalize);

    homeText.setText(mMatch.HomeTeamShortName);
    homeText.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
          case MotionEvent.ACTION_DOWN:
            populateTrend(mMatch.HomeTeamShortName, mMatch.AwayTeamShortName);
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
              addEvent(mMatch.HomeTeamShortName);
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
              editEvent(mMatch.HomeTeamShortName);
              return true;
            case MotionEvent.ACTION_UP:
              view.performClick();
              return true;
          }
        }
        return false;
      }
    });

    awayText.setText(mMatch.AwayTeamShortName);
    awayText.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
          case MotionEvent.ACTION_DOWN:
            populateTrend(mMatch.AwayTeamShortName, mMatch.HomeTeamShortName);
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
              addEvent(mMatch.AwayTeamShortName);
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
              editEvent(mMatch.AwayTeamShortName);
              return true;
            case MotionEvent.ACTION_UP:
              view.performClick();
              return true;
          }
        }
        return false;
      }
    });

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

  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.d(TAG, "++onDestroy()");
    if (mMatchEventsQuery != null && mMatchEventsListener != null) {
      mMatchEventsQuery.removeEventListener(mMatchEventsListener);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    Log.d(TAG, "++onCreateOptionsMenu(Menu)");
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    Log.d(TAG, "++onOptionsItemSelected(MenuItem)");
    int i = item.getItemId();
    switch (i) {
      case R.id.action_logout:
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignInActivity.class));
        finish();
        return true;
      case R.id.action_settings:
        startActivityForResult(new Intent(this, SettingsActivity.class), BaseActivity.RESULT_SETTINGS);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {

    if (resultCode != RESULT_OK) {
      Log.d(TAG, "Child activity returned cancelled.");
      return;
    }

    if (requestCode == EVENT_CREATE_RESULT) {
      //updateScores();
    }
  }

  private void addEvent(String teamShortName) {

    Log.d(TAG, "++addEvent(String)");
    Intent intent = new Intent(getApplicationContext(), EventDetailActivity.class);
    intent.putExtra(BaseActivity.ARG_MATCH, mMatch);
    intent.putExtra(BaseActivity.ARG_TEAM_NAME, teamShortName);
    startActivityForResult(intent, EVENT_CREATE_RESULT);
  }

  private void editEvent(String teamShortName) {

    Log.d(TAG, "++editEvent(String)");
    // TODO: figure out editing of match events
  }

  private void finalizeMatch() {

    Log.d(TAG, "++finalizeMatch()");
    // TODO: add as an event instead of a button
    mMatch.IsFinal = true;
    mFinalizeButton.setEnabled(false);
    FirebaseDatabase.getInstance().getReference().child("matches/" + mMatch.Id + "/IsFinal").setValue(mMatch.IsFinal);
    populateTrend(mMatch.HomeTeamShortName, mMatch.AwayTeamShortName);
  }

  private void populateTrend(String targetTeam, String opponentTeam) {

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
    if (mMatchEvents != null) {
      for (Map.Entry<String, MatchEvent> matchEvent : mMatchEvents.entrySet()) {
        switch (matchEvent.getValue().EventName) {
          case "Own Goal":
            if (matchEvent.getValue().TeamShortName.equals(mMatch.HomeTeamShortName)) {
              awayScore++;
            } else if (matchEvent.getValue().TeamShortName.equals(mMatch.AwayTeamShortName)) {
              homeScore++;
            }
            break;
          case "Goal":
          case "Goal (Penalty)":
            if (matchEvent.getValue().TeamShortName.equals(mMatch.HomeTeamShortName)) {
              homeScore++;
            } else if (matchEvent.getValue().TeamShortName.equals(mMatch.AwayTeamShortName)) {
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
