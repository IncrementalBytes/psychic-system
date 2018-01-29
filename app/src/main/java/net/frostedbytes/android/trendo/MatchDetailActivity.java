package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler.Value;
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
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.MatchEvent;
import net.frostedbytes.android.trendo.models.MatchSummary;
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
  private ValueEventListener mMatchListener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_match_detail);

    String matchId = getIntent().getStringExtra(BaseActivity.ARG_MATCH_ID);
    int year = getIntent().getIntExtra(BaseActivity.ARG_YEAR_SETTING, -1);

    mMatch = new Match();
    mMatchQuery = FirebaseDatabase.getInstance().getReference().child("matches/" + String.valueOf(year) + "/" + matchId);
    mMatchListener = new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        mMatch = dataSnapshot.getValue(Match.class);
        updateScores();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++onCancelled(DatabaseError)");
        Log.e(TAG, databaseError.getMessage());
      }
    };
    mMatchQuery.addValueEventListener(mMatchListener);

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

    mAwayText.setOnTouchListener(new OnTouchListener() {
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
    if (mMatchQuery != null && mMatchListener != null) {
      mMatchQuery.removeEventListener(mMatchListener);
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
    if (mMatch.MatchEvents != null) {
      mHomeText.setText(mMatch.HomeTeamShortName);
      mAwayText.setText(mMatch.AwayTeamShortName);
      for (Map.Entry<String, MatchEvent> eventObject : mMatch.MatchEvents.entrySet()) {
        MatchEvent matchEvent = eventObject.getValue();
        switch (matchEvent.EventName) {
          case "Own Goal":
            if (matchEvent.TeamShortName.equals(mMatch.HomeTeamShortName)) {
              awayScore++;
            } else if (matchEvent.TeamShortName.equals(mMatch.AwayTeamShortName)) {
              homeScore++;
            }
            break;
          case "Goal":
          case "Goal (Penalty)":
            if (matchEvent.TeamShortName.equals(mMatch.HomeTeamShortName)) {
              homeScore++;
            } else if (matchEvent.TeamShortName.equals(mMatch.AwayTeamShortName)) {
              awayScore++;
            }
            break;
        }
      }

      // now update the match summary object
      if (!mMatch.Id.equals(BaseActivity.DEFAULT_ID)) {
        MatchSummary summary = new MatchSummary();
        summary.AwayScore = awayScore;
        summary.AwayTeamShortName = mMatch.AwayTeamShortName;
        summary.HomeScore = homeScore;
        summary.HomeTeamShortName = mMatch.HomeTeamShortName;
        summary.IsFinal = mMatch.IsFinal;
        summary.MatchDate = mMatch.MatchDate;

        // grab year from match date
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mMatch.MatchDate);

        Map<String, Object> postValues = summary.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/teams/" + mMatch.AwayTeamShortName + "/MatchSummaries/" + calendar.get(Calendar.YEAR) + "/" + mMatch.Id, postValues);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
        childUpdates.put("/teams/" + mMatch.HomeTeamShortName + "/MatchSummaries/" + calendar.get(Calendar.YEAR) + "/" + mMatch.Id, postValues);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
      }
    }

    mHomeScoreText.setText(String.valueOf(homeScore));
    mAwayScoreText.setText(String.valueOf(awayScore));
  }
}
