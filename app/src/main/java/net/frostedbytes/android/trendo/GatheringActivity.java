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
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.Team;

public class GatheringActivity extends BaseActivity {

  private static final String TAG = "GatheringActivity";

  private boolean mMatchesDone;
  private boolean mTeamsDone;

  public List<Match> Matches;
  public List<Team> Teams;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_gathering);

    showProgressDialog();
    Matches = new ArrayList<>();
    Teams = new ArrayList<>();
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    Query matchesQuery = database.child("matches").orderByChild("matchdate");
    matchesQuery.addListenerForSingleValueEvent(new ValueEventListener() {

      private static final String TAG = "matchesQuery";

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot data : dataSnapshot.getChildren()) {
          Match match = data.getValue(Match.class);
          if (match != null) {
            match.Id = data.getKey();
            Matches.add(match);
          }
        }

        onGatheringMatchesComplete();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++onCancelled(DatabaseError)");
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
            Teams.add(team);
          }
        }

        onGatheringTeamsComplete();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++onCancelled(DatabaseError)");
        hideProgressDialog();
      }
    });
  }

  void onGatheringMatchesComplete() {

    Log.d(TAG, "++onGatheringMatchesComplete()");
    mMatchesDone = true;
    gatheringCheck();
  }

  void onGatheringTeamsComplete() {

    Log.d(TAG, "++onGatheringTeamsComplete()");
    mTeamsDone = true;
    gatheringCheck();
  }

  private void gatheringCheck() {

    Log.d(TAG, "++gatheringCheck()");
    if (mMatchesDone && mTeamsDone) {
      MatchCenter.get(this).setMatches(Matches);
      MatchCenter.get(this).setTeams(Teams);

      startActivity(new Intent(GatheringActivity.this, MainActivity.class));
      hideProgressDialog();
      finish();
    }
  }
}
