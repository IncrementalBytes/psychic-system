package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.Team;

public class MatchCreationFragment extends Fragment implements DatePickerFragment.OnMatchDateSetListener {

  private static final String TAG = "MatchCreationFragment";

  private static final String DIALOG_DATE = "Match Date dialog";

  private MatchCreationListener mCallback;

  public interface MatchCreationListener {

    void onMatchCreated(String matchId);
  }

  private Match mMatch;

  private Button mDateButton;
  private Spinner mHomeSpinner;
  private Spinner mAwaySpinner;
  private Button mCancel;
  private Button mCreate;

  private Query mTeamsQuery;
  private ValueEventListener mTeamsValueListener;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    Log.d(TAG, "++onAttach(Context)");
    try {
      mCallback = (MatchCreationListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException("Calling activity/fragment must implement MatchCreationListener.");
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateDialog(Bundle)");
    View view = inflater.inflate(R.layout.fragment_match_creation, container, false);
    mDateButton = view.findViewById(R.id.match_button_date);
    mHomeSpinner = view.findViewById(R.id.match_spinner_home_team);
    mAwaySpinner = view.findViewById(R.id.match_spinner_away_team);
    mCancel = view.findViewById(R.id.match_button_cancel);
    mCreate = view.findViewById(R.id.match_button_create);

    mMatch = new Match();

    updateDate();
    mDateButton.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        Log.d(TAG, "++mDateButton::onClick(View)");
        if (getActivity() != null) {
          FragmentManager manager = getActivity().getSupportFragmentManager();
          DatePickerFragment dialog = DatePickerFragment.newInstance(mMatch.MatchDate);
          dialog.setTargetFragment(MatchCreationFragment.this, MatchDetailFragment.REQUEST_DATE);
          dialog.show(manager, DIALOG_DATE);
        } else {
          Log.e(TAG, "GetActivity() was null");
        }
      }
    });

    final List<Team> teams = new ArrayList<>();
    mTeamsQuery = FirebaseDatabase.getInstance().getReference().child("teams").orderByChild("ShortName");
    mTeamsValueListener = new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        for (DataSnapshot data : dataSnapshot.getChildren()) {
          Team team = data.getValue(Team.class);
          if (team != null) {
            team.Id = data.getKey();
            teams.add(team);
          }
        }

        onGatheringTeamsComplete(teams);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++onCancelled(DatabaseError)");
        Log.e(TAG, databaseError.getMessage());
      }
    };

    mTeamsQuery.addValueEventListener(mTeamsValueListener);
    mCancel.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {

        Log.d(TAG, "++mCancel::onClick(View");
        Log.d(TAG, "User cancelled match creation");
        sendResult(BaseActivity.DEFAULT_ID);

      }
    });

    mCreate.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        Log.d(TAG, "++mCreate::onClick(View");

        if (mMatch.Id.equals(BaseActivity.DEFAULT_ID)) {
          mMatch.Id = UUID.randomUUID().toString();
          try {
            Map<String, Object> postValues = mMatch.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/matches/" + mMatch.Id, postValues);
            FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
          } catch (DatabaseException dex) {
            Log.d(TAG, dex.getLocalizedMessage());
            Toast.makeText(getActivity(), "Could not create match. Permissions?", Toast.LENGTH_SHORT).show();
          }
        } else {
          Log.d(TAG, "Found existing match in database");
        }

        sendResult(mMatch.Id);
      }
    });

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
    if (mTeamsQuery != null && mTeamsValueListener != null) {
      mTeamsQuery.removeEventListener(mTeamsValueListener);
    }
  }

  @Override
  public void onMatchDateSet(long dateInMilliseconds) {

    Log.d(TAG, "++onMatchDateSet(long)");
    if (mMatch != null) {
      mMatch.MatchDate = dateInMilliseconds;
      updateDate();
    } else {
      Log.e(TAG, "Match was not set.");
    }
  }

  void onGatheringTeamsComplete(final List<Team> teams) {

    Log.d(TAG, "++onGatheringTeamsComplete(List<Team>)");
    List<String> teamNames = new ArrayList<>();
    for (Team team : teams) {
      teamNames.add(team.FullName);
    }

    // get a list of teams for the object adapter used by the spinner controls
    if (getActivity() != null) {
      ArrayAdapter<String> adapter = new ArrayAdapter<>(
        getActivity(),
        android.R.layout.simple_list_item_1,
        teamNames);

      // specify the layout to use when the list of choices appears
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

      // apply the adapter to the spinner
      mHomeSpinner.setAdapter(adapter);
      mHomeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

          Log.d(TAG, "++mHomeSpinner::onItemSelected(AdapterView<?>, View, int, long)");
          for (Team team : teams) {
            if (team.FullName.equals(mHomeSpinner.getSelectedItem().toString())) {
              mMatch.HomeTeam = team;
              break;
            }
          }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

          Log.d(TAG, "++mHomeSpinner::onNothingSelected(AdapterView<?>)");
        }
      });

      adapter = new ArrayAdapter<>(
        getActivity(),
        android.R.layout.simple_list_item_1,
        teamNames);

      // specify the layout to use when the list of choices appears
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

      // apply the adapter to the spinner
      mAwaySpinner.setAdapter(adapter);
      mAwaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

          Log.d(TAG, "++mAwaySpinner::onItemSelected(AdapterView<?>, View, int, long)");
          for (Team team : teams) {
            if (team.FullName.equals(mAwaySpinner.getSelectedItem().toString())) {
              mMatch.AwayTeam = team;
              break;
            }
          }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

          Log.d(TAG, "++mAwaySpinner::onNothingSelected(AdapterView<?>)");
        }
      });
    } else {
      Log.e(TAG, "GetActivity() was null.");
    }
  }

  private void sendResult(String matchId) {

    Log.d(TAG, "++sendResult(String)");
    if (mCallback != null) {
      mCallback.onMatchCreated(matchId);
    } else {
      Log.e(TAG, "Unable to process callback.");
    }
  }

  private void updateDate() {

    Log.d(TAG, "++updateDate()");
    mDateButton.setText(Match.formatDateForDisplay(mMatch.MatchDate));
  }
}
