package net.frostedbytes.android.trendo.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import java.util.UUID;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.MatchCenter;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.Team;

public class MatchCreationFragment extends DialogFragment implements DatePickerFragment.OnMatchDateSetListener {

  private static final String TAG = "MatchCreationFragment";

  private static final String DIALOG_DATE = "Match Date dialog";

  private OnMatchCreatedListener mCallback;

  public interface OnMatchCreatedListener {

    void onMatchCreated(String matchId);
  }

  private Match mMatch;

  private Button mDateButton;
  private Spinner mHomeSpinner;
  private Spinner mAwaySpinner;

  public static MatchCreationFragment newInstance() {

    Log.d(TAG, "++newInstance()");
    Bundle args = new Bundle();
    MatchCreationFragment fragment = new MatchCreationFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    try {
      mCallback = (OnMatchCreatedListener) getTargetFragment();
    } catch (ClassCastException e) {
      throw new ClassCastException("Calling Fragment must implement OnMatchCreatedListener.");
    }
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    super.onCreateDialog(savedInstanceState);

    Log.d(TAG, "++onCreateDialog(Bundle");
    final View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_match_creation, null);
    mDateButton = v.findViewById(R.id.match_button_date);
    mHomeSpinner = v.findViewById(R.id.match_spinner_home_team);
    mAwaySpinner = v.findViewById(R.id.match_spinner_away_team);

    mMatch = new Match();

    updateDate();
    mDateButton.setOnClickListener(new View.OnClickListener() {

      private static final String TAG = "mDateButton";

      @Override
      public void onClick(View v) {

        Log.d(TAG, "++onClick(View)");
        FragmentManager manager = getFragmentManager();
        DatePickerFragment dialog = DatePickerFragment.newInstance(mMatch.MatchDate);
        dialog.setTargetFragment(MatchCreationFragment.this, MatchDetailFragment.REQUEST_DATE);
        dialog.show(manager, DIALOG_DATE);
      }
    });

    // get a list of teams for the object adapter used by the spinner controls
    List<String> teamNames = MatchCenter.get().getTeamNames();
    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        getActivity(),
        android.R.layout.simple_list_item_1,
        teamNames);

    // specify the layout to use when the list of choices appears
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    // apply the adapter to the spinner
    mHomeSpinner.setAdapter(adapter);
    mHomeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

      private static final String TAG = "mHomeSpinner";

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Log.d(TAG, "++onItemSelected(AdapterView<?>, View, int, long)");
        Team home = MatchCenter.get().getTeam(mHomeSpinner.getSelectedItem().toString());
        if (home != null) {
          mMatch.HomeId = home.Id;
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

        Log.d(TAG, "++onNothingSelected(AdapterView<?>)");
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

      private static final String TAG = "mAwaySpinner";

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Log.d(TAG, "++onItemSelected(AdapterView<?>, View, int, long)");
        Team away = MatchCenter.get().getTeam(mAwaySpinner.getSelectedItem().toString());
        if (away != null) {
          mMatch.AwayId = away.Id;
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

        Log.d(TAG, "++onNothingSelected(AdapterView<?>)");
      }
    });

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setView(v)
        .setPositiveButton(android.R.string.ok,
            new DialogInterface.OnClickListener() {

              private static final String TAG = "setPositiveButton";

              @Override
              public void onClick(DialogInterface dialog, int which) {

                Log.d(TAG, "++onClick(DialogInterface, int");
                List<Match> matches = MatchCenter.get().getMatches();
                boolean found = false;
                for (Match match : matches) {
                  if (mMatch.AwayId.equals(match.AwayId) &&
                      mMatch.HomeId.equals(match.HomeId) &&
                      mMatch.MatchDate == match.MatchDate) {
                    sendResult(match.Id);
                    found = true;
                    break;
                  }
                }

                if (!found) {
                  // match isn't local, run a check against database
                  DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                  Query matchesQuery = database.child("matches").orderByChild("HomeTeam").equalTo(mMatch.HomeId);
                  matchesQuery.addListenerForSingleValueEvent(new ValueEventListener() {

                    private static final String TAG = "matchesQuery";

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                      Log.d(TAG, "++onDataChange(DataSnapshot)");
                      for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Match match = data.getValue(Match.class);
                        if (match != null) {
                          match.Id = data.getKey();
                          if (match.AwayId.equals(mMatch.AwayId) && match.MatchDate == mMatch.MatchDate) {
                            onQueryingMatchesComplete(match);
                          }
                        }
                      }

                      onQueryingMatchesComplete(mMatch);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                      Log.d(TAG, "++onCancelled(DatabaseError)");
                      Log.e(TAG, databaseError.getMessage());
                    }
                  });
                }
              }
            })
        .setNegativeButton(android.R.string.cancel,
            new DialogInterface.OnClickListener() {

              private static final String TAG = "setNegativeButton";

              @Override
              public void onClick(DialogInterface dialog, int which) {

                Log.d(TAG, "User cancelled match creation");
                sendResult(BaseActivity.DEFAULT_ID);
              }
            });

    return builder.create();
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

  void onQueryingMatchesComplete(Match match) {

    Log.d(TAG, "++onQueryingMatchesComplete(Match)");
    if (match.Id.equals(BaseActivity.DEFAULT_ID)) {
      match.Id = UUID.randomUUID().toString();
      try {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("matches").child(match.Id).setValue(match);
      } catch (DatabaseException dex) {
        Log.d(TAG, dex.getLocalizedMessage());
        Toast.makeText(getActivity(), "Could not create match. Permissions?", Toast.LENGTH_SHORT).show();
      }
    } else {
      Log.d(TAG, "Found existing match in database");
    }

    sendResult(match.Id);
  }

  private void sendResult(String matchId) {

    Log.d(TAG, "++sendResult(String)");
    mCallback.onMatchCreated(matchId);
    dismiss();
  }

  private void updateDate() {

    Log.d(TAG, "++updateDate()");
    mDateButton.setText(Match.formatDateForDisplay(mMatch.MatchDate));
  }
}
