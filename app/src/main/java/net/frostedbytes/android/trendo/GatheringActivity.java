package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import net.frostedbytes.android.trendo.models.Conference;
import net.frostedbytes.android.trendo.models.Event;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.MatchEvent;
import net.frostedbytes.android.trendo.models.Team;

public class GatheringActivity extends BaseActivity {

  private static final String TAG = "GatheringActivity";

  private boolean mConferencesDone;
  private boolean mEventsDone;
  private boolean mMatchesDone;
  private boolean mMatchEventsDone;
  private boolean mTeamsDone;

  private List<Conference> mConferences;
  private List<Event> mEvents;
  private List<Match> mMatches;
  private List<MatchEvent> mMatchEvents;
  private List<Team> mTeams;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_gathering);

    showProgressDialog();
    mConferences = new ArrayList<>();
    mEvents = new ArrayList<>();
    mMatches = new ArrayList<>();
    mMatchEvents = new ArrayList<>();
    mTeams = new ArrayList<>();
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    Query conferencesQuery = database.child("conferences");
    conferencesQuery.addListenerForSingleValueEvent(new ValueEventListener() {

      private static final String TAG = "matchesQuery";

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot data : dataSnapshot.getChildren()) {
          Conference conference = data.getValue(Conference.class);
          if (conference != null) {
            conference.Id = data.getKey();
            mConferences.add(conference);
          }
        }

        onGatheringConferencesComplete();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++onCancelled(DatabaseError)");
        hideProgressDialog();
      }
    });

    Query eventsQuery = database.child("events");
    eventsQuery.addListenerForSingleValueEvent(new ValueEventListener() {

      private static final String TAG = "eventsQuery";

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot data : dataSnapshot.getChildren()) {
          Event event = data.getValue(Event.class);
          if (event != null) {
            event.Id = data.getKey();
            mEvents.add(event);
          }
        }

        onGatheringEventsComplete();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++onCancelled(DatabaseError)");
        hideProgressDialog();
      }
    });

    Query matchesQuery = database.child("matches").orderByChild("matchdate");
    matchesQuery.addListenerForSingleValueEvent(new ValueEventListener() {

      private static final String TAG = "matchesQuery";

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot data : dataSnapshot.getChildren()) {
          Match match = data.getValue(Match.class);
          if (match != null) {
            match.Id = data.getKey();
            mMatches.add(match);
          }
        }

        onGatheringMatchesComplete();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++onCancelled(DatabaseError)");
        Log.e(TAG, databaseError.getMessage());
        hideProgressDialog();
      }
    });

    Query matchEventsQuery = database.child("matchevents");
    matchEventsQuery.addListenerForSingleValueEvent(new ValueEventListener() {

      private static final String TAG = "matchEventsQuery";

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot data : dataSnapshot.getChildren()) {
          MatchEvent matchEvent = data.getValue(MatchEvent.class);
          if (matchEvent != null) {
            matchEvent.Id = data.getKey();
            mMatchEvents.add(matchEvent);
          }
        }

        onGatheringMatchEventsComplete();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++onCancelled(DatabaseError)");
        Log.e(TAG, databaseError.getMessage());
        hideProgressDialog();
      }
    });

    Query teamsQuery = database.child("teams").orderByChild("shortname");
    teamsQuery.addListenerForSingleValueEvent(new ValueEventListener() {

      private static final String TAG = "teamsQuery";

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot data : dataSnapshot.getChildren()) {
          Team team = data.getValue(Team.class);
          if (team != null) {
            team.Id = data.getKey();
            mTeams.add(team);
          }
        }

        onGatheringTeamsComplete();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++onCancelled(DatabaseError)");
        Log.e(TAG, databaseError.getMessage());
        hideProgressDialog();
      }
    });
  }

  void onGatheringConferencesComplete() {

    Log.d(TAG, "++onGatheringConferencesComplete()");
    mConferencesDone = true;
    gatheringCheck();
  }

  void onGatheringEventsComplete() {

    Log.d(TAG, "++onGatheringEventsComplete()");
    mEventsDone = true;
    gatheringCheck();
  }

  void onGatheringMatchesComplete() {

    Log.d(TAG, "++onGatheringMatchesComplete()");
    mMatchesDone = true;
    gatheringCheck();
  }

  void onGatheringMatchEventsComplete() {

    Log.d(TAG, "++onGatheringMatchEventsComplete()");
    mMatchEventsDone = true;
    gatheringCheck();
  }

  void onGatheringTeamsComplete() {

    Log.d(TAG, "++onGatheringTeamsComplete()");
    mTeamsDone = true;
    gatheringCheck();
  }

  private void gatheringCheck() {

    Log.d(TAG, "++gatheringCheck()");
    if (mMatchesDone && mTeamsDone && mEventsDone && mConferencesDone && mMatchEventsDone) {
      MatchCenter.get(this).setConferences(mConferences);
      MatchCenter.get(this).setEvents(mEvents);
      MatchCenter.get(this).setMatches(mMatches);
      MatchCenter.get(this).setMatchEvents(mMatchEvents);
      MatchCenter.get(this).setTeams(mTeams);

      startActivity(new Intent(GatheringActivity.this, MainActivity.class));
      hideProgressDialog();
      finish();
    }
  }
}
