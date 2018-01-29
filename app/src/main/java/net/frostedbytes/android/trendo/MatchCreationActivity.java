package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
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
import net.frostedbytes.android.trendo.fragments.DatePickerFragment;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.Team;
import net.frostedbytes.android.trendo.views.TouchableImageView;

public class MatchCreationActivity extends BaseActivity implements DatePickerFragment.OnMatchDateSetListener {

  private static final String TAG = "MatchCreationActivity";

  private static final String DIALOG_DATE = "Match Date dialog";

  private Match mMatch;

  private Button mDateButton;
  private Spinner mHomeSpinner;
  private Spinner mAwaySpinner;
  private TextView mErrorMessageText;

  private Query mTeamsQuery;
  private ValueEventListener mTeamsValueListener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_match_creation);

    TouchableImageView cancelImageView = findViewById(R.id.create_match_imageview_cancel);
    mDateButton = findViewById(R.id.create_match_button_date);
    mHomeSpinner = findViewById(R.id.create_match_spinner_home_team);
    mAwaySpinner = findViewById(R.id.create_match_spinner_away_team);
    mErrorMessageText = findViewById(R.id.create_match_text_error_message);
    Button createButton = findViewById(R.id.create_match_button_create);

    mMatch = new Match();

    updateDate();
    mDateButton.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        Log.d(TAG, "++mDateButton::onClick(View)");
        FragmentManager manager = getSupportFragmentManager();
        long defaultDate = mMatch.MatchDate;
        if (defaultDate == 0) {

        }

        DatePickerFragment dialog = DatePickerFragment.newInstance(defaultDate);
        dialog.show(manager, DIALOG_DATE);
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
            team.ShortName = data.getKey();
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

    cancelImageView.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
          case MotionEvent.ACTION_DOWN:
            Log.d(TAG, "++mCancelImageView::setOnTouchListener(");
            Log.d(TAG, "User cancelled match creation");
            setResult(RESULT_CANCELED);
            finish();
            return true;
          case MotionEvent.ACTION_UP:
            view.performClick();
            return true;
        }

        return false;
      }
    });

    createButton.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        Log.d(TAG, "++mCreateButton::onClick(View");

        if (validateForm()) {
          showProgressDialog("Validating...");
          // make sure match does not exist under either team (it shouldn't)

          Query matchesQuery = FirebaseDatabase.getInstance().getReference().child("matches").orderByChild("MatchDate");
          matchesQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

              boolean matchExists = false;
              for (DataSnapshot data : dataSnapshot.getChildren()) {
                Match match = data.getValue(Match.class);
                if (match != null &&
                  match.HomeTeamShortName.equals(mMatch.HomeTeamShortName) &&
                  match.AwayTeamShortName.equals(mMatch.AwayTeamShortName) &&
                  match.MatchDate == mMatch.MatchDate) {
                  matchExists = true;
                  break;
                }
              }

              onQueryMatchesComplete(matchExists);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

              Log.d(TAG, "++onCancelled(DatabaseError)");
              Log.e(TAG, databaseError.getMessage());
            }
          });
        }
      }
    });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.d(TAG, "++onDestroy()");
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

  private void onGatheringTeamsComplete(final List<Team> teams) {

    Log.d(TAG, "++onGatheringTeamsComplete(List<Team>)");
    List<String> teamNames = new ArrayList<>();
    for (Team team : teams) {
      teamNames.add(team.FullName);
    }

    // get a list of teams for the object adapter used by the spinner controls
    ArrayAdapter<String> adapter = new ArrayAdapter<>(
      this,
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
            mMatch.HomeTeamShortName = team.ShortName;
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
      this,
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
            mMatch.AwayTeamShortName = team.ShortName;
            break;
          }
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

        Log.d(TAG, "++mAwaySpinner::onNothingSelected(AdapterView<?>)");
      }
    });
  }

  private void onQueryMatchesComplete(boolean matchExists) {

    Log.d(TAG, "++onQueryMatchesComplete(boolean)");
    if (!matchExists) {
      if (mMatch.Id.equals(BaseActivity.DEFAULT_ID)) {
        mMatch.Id = UUID.randomUUID().toString();
        try {
          Map<String, Object> postValues = mMatch.toMap();
          Map<String, Object> childUpdates = new HashMap<>();
          childUpdates.put("/matches/" + mMatch.Id, postValues);
          FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);

          Intent intent = new Intent();
          intent.putExtra(ARG_MATCH_ID, mMatch.Id);
          setResult(RESULT_OK, intent);
          finish();
        } catch (DatabaseException dex) {
          Log.d(TAG, dex.getLocalizedMessage());
          mErrorMessageText.setText(R.string.err_event_permissions);
        }
      } else {
        Log.d(TAG, "Found existing match in database");
      }
    } else {
      mErrorMessageText.setText(mMatch.toString() + "; Match already exists.");
    }

    hideProgressDialog();
  }

  private void updateDate() {

    Log.d(TAG, "++updateDate()");
    if (mMatch != null) {
      mDateButton.setText(Match.formatDateForDisplay(mMatch.MatchDate));
    }
  }

  private boolean validateForm() {

    Log.d(TAG, "++validateForm()");
    mErrorMessageText.setText("");
    if (mMatch.HomeTeamShortName.equals(mMatch.AwayTeamShortName)) {
      mErrorMessageText.setText(mMatch.HomeTeamShortName + " cannot play itself.");
      return false;
    }

    return true;
  }
}
