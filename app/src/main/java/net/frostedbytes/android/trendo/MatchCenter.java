package net.frostedbytes.android.trendo;

import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.Team;

public class MatchCenter {

  private static final String TAG = "MatchCenter";

  private static MatchCenter sMatchCenter;

  private List<Match> mMatches;
  private List<Team> mTeams;

  public static MatchCenter get() {

    if (sMatchCenter == null) {
      Log.d(TAG, "Creating MatchCenter context.");
      sMatchCenter = new MatchCenter();
    }

    return sMatchCenter;
  }

  private MatchCenter() {

    mMatches = new ArrayList<>();
    mTeams = new ArrayList<>();
    // TODO: add asset import/initialization
  }

  public Match getMatch(String matchId) {

    Log.d(TAG, "++getMatch(String)");
    if (mMatches == null || mMatches.size() == 0) {
      getMatches();
    }

    for (Match match : mMatches) {
      if (match.Id.equals(matchId)) {
        return match;
      }
    }

    return null;
  }

  public List<Match> getMatches() {

    Log.d(TAG, "++getMatches()");
    if (mMatches != null && mMatches.size() != 0) {
      return mMatches;
    }

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("matches");
    reference.addListenerForSingleValueEvent(new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        for (DataSnapshot matchData : dataSnapshot.getChildren()) {
          Match temp = matchData.getValue(Match.class);
          if (temp != null) {
            // assign the id which is the key
            temp.Id = matchData.getKey();
            if (mMatches.size() == 0) {
              mMatches.add(temp);
            } else {
              boolean found = false;
              if (mMatches.contains(temp)) {
                found = true;
              }

              if (!found) {
                mMatches.add(temp);
              }
            }
          }
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d("ValueEventListener", "++onCancelled(DatabaseError)");
      }
    });

    return mMatches;
  }

  public Team getTeam(String teamId) {

    Log.d(TAG, "++getTeam(String)");
    if (mTeams == null || mTeams.size() == 0) {
      getTeams();
    }

    for (Team team : mTeams) {
      if (team.Id.equals(teamId)) {
        return team;
      }
    }

    return null;
  }

  public List<Team> getTeams() {

    Log.d(TAG, "++getTeams()");
    if (mTeams != null && mTeams.size() != 0) {
      return mTeams;
    }

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("teams");
    reference.addListenerForSingleValueEvent(new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        for (DataSnapshot teamData : dataSnapshot.getChildren()) {
          Team temp = teamData.getValue(Team.class);
          if (temp != null) {
            // assign the id which is the key
            temp.Id = teamData.getKey();
            if (mTeams.size() == 0) {
              mTeams.add(temp);
            } else {
              boolean found = false;
              if (mTeams.contains(temp)) {
                found = true;
              }

              if (!found) {
                mTeams.add(temp);
              }
            }
          }
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d("ValueEventListener", "++onCancelled(DatabaseError)");
      }
    });

    return mTeams;
  }
}
