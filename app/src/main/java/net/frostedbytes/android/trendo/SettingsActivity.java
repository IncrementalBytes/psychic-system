package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frostedbytes.android.trendo.models.Settings;
import net.frostedbytes.android.trendo.models.Team;

public class SettingsActivity extends BaseActivity {

  private static final String TAG = "SettingsActivity";

  private Spinner mTeamSpinner;
  private Spinner mYearSpinner;
  private TextView mErrorMessageText;

  private Team mSelectedTeam;
  private List<Team> mTeams;
  private String mUserId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_settings);

    mUserId = getIntent().getStringExtra(BaseActivity.ARG_USER);

    ImageView cancelImageView = findViewById(R.id.settings_imageview_cancel);
    mTeamSpinner = findViewById(R.id.settings_spinner_team);
    mYearSpinner = findViewById(R.id.settings_spinner_year);
    mErrorMessageText = findViewById(R.id.settings_text_error_message);
    final Button saveButton = findViewById(R.id.settings_button_save);

    mTeams = new ArrayList<>();
    Query teamsQuery = FirebaseDatabase.getInstance().getReference().child("teams").orderByChild("FullName");
    ValueEventListener teamsValueListener = new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        for (DataSnapshot data : dataSnapshot.getChildren()) {
          Team team = data.getValue(Team.class);
          if (team != null) {
            team.ShortName = data.getKey();
            mTeams.add(team);
          } else {
            Log.d(TAG, "Unable to get team from dataSnapshot.");
          }
        }

        onGatheringTeamsComplete();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++onCancelled(DatabaseError)");
        Log.e(TAG, databaseError.getMessage());
      }
    };
    teamsQuery.addValueEventListener(teamsValueListener);

    cancelImageView.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {

        Log.d(TAG, "++cancelImageView::onClick(View");
        Log.d(TAG, "User cancelled match creation");
        setResult(RESULT_CANCELED);
        finish();
      }
    });

    mTeamSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        mSelectedTeam = mTeams.get(i);
        teamSelected();
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });

    mYearSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        saveButton.setEnabled(true);
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });

    saveButton.setEnabled(false);
    saveButton.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        Log.d(TAG, "++mSaveButton::onClick(View");

        // TODO: validate information on view before proceeding
        // update settings for user
        try {
          Settings setting = new Settings();
          setting.Year = Integer.parseInt(mYearSpinner.getSelectedItem().toString());
          setting.TeamShortName = mSelectedTeam.ShortName;
          Map<String, Object> postValues = setting.toMap();
          Map<String, Object> childUpdates = new HashMap<>();
          childUpdates.put("/settings/" + mUserId, postValues);
          FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);

          Intent intent = new Intent();
          intent.putExtra(BaseActivity.ARG_TEAM_NAME, setting.TeamShortName);
          intent.putExtra(BaseActivity.ARG_YEAR_SETTING, setting.Year);
          setResult(RESULT_OK, intent);
          finish();
        } catch (DatabaseException dex) {
          mErrorMessageText.setText(R.string.err_settings_failed);
        }
      }
    });
  }

  private void onGatheringTeamsComplete() {

    Log.d(TAG, "++onGatheringTeamsComplete(List<Team>)");
    List<String> teamNames = new ArrayList<>();
    for (Team team : mTeams) {
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
    mTeamSpinner.setAdapter(adapter);
  }

  private void teamSelected() {

    // grab the years available for this team
    List<String> availableYears = new ArrayList<>();
    if (!mSelectedTeam.MatchSummaries.isEmpty()) {
      mErrorMessageText.setText("");
      for (Map.Entry<String, Map<String, Object>> matchSummary : mSelectedTeam.MatchSummaries.entrySet()) {
        availableYears.add(matchSummary.getKey());
      }

      // get a list of teams for the object adapter used by the spinner controls
      ArrayAdapter<String> adapter = new ArrayAdapter<>(
        this,
        android.R.layout.simple_list_item_1,
        availableYears);

      // specify the layout to use when the list of choices appears
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

      // apply the adapter to the spinner
      mYearSpinner.setAdapter(adapter);
      mYearSpinner.setEnabled(true);
    } else {
      mErrorMessageText.setText(String.format(getString(R.string.err_no_results_for_team), mSelectedTeam.FullName));
      mYearSpinner.setEnabled(false);
    }
  }
}
