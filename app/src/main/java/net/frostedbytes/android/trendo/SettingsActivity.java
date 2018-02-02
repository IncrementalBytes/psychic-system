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
import android.widget.Spinner;
import android.widget.TextView;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frostedbytes.android.trendo.models.Settings;

public class SettingsActivity extends BaseActivity {

  private static final String TAG = "SettingsActivity";

  private Spinner mTeamSpinner;
  private Spinner mYearSpinner;
  private TextView mErrorMessageText;
  private Button mSaveButton;

  private Map<String, String> mTeamMappings;
  private String mUserId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_settings);

    mUserId = getIntent().getStringExtra(BaseActivity.ARG_USER);

    ImageView cancelImageView = findViewById(R.id.settings_image_cancel);
    mTeamSpinner = findViewById(R.id.settings_spinner_team);
    mYearSpinner = findViewById(R.id.settings_spinner_year);
    mErrorMessageText = findViewById(R.id.settings_text_error_message);
    mSaveButton = findViewById(R.id.settings_button_save);

    cancelImageView.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {

        Log.d(TAG, "++cancelImageView::onClick(View");
        Log.d(TAG, "User cancelled match creation");
        setResult(RESULT_CANCELED);
        finish();
      }
    });

    mTeamMappings = new HashMap<>();
    String[] resourceItems = getResources().getStringArray(R.array.teams);
    for (String teamNameResource : resourceItems) {
      String[] teamSegments = teamNameResource.split(",");
      if (teamSegments.length == 2) {
        mTeamMappings.put(teamSegments[0], teamSegments[1]);
      } else {
        Log.d(TAG, "Skipping unexpected entry: " + teamNameResource);
      }
    }

    List<String> teams = new ArrayList<>(mTeamMappings.keySet());
    Collections.sort(teams);

    // get a list of teams for the object adapter used by the spinner controls
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(teams));
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mTeamSpinner.setAdapter(adapter);
    mTeamSpinner.setOnItemSelectedListener(spinnerListener);

    // get a list of teams for the object adapter used by the spinner controls
    resourceItems = getResources().getStringArray(R.array.years);
    adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, resourceItems);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mYearSpinner.setAdapter(adapter);
    mYearSpinner.setOnItemSelectedListener(spinnerListener);

    mSaveButton.setEnabled(false);
    mSaveButton.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        Log.d(TAG, "++mSaveButton::onClick(View");

        try {
          Settings setting = new Settings();
          setting.Id = mUserId;
          setting.Year = Integer.parseInt(mYearSpinner.getSelectedItem().toString());
          setting.TeamShortName = mTeamMappings.get(mTeamSpinner.getSelectedItem().toString());
          Map<String, Object> postValues = setting.toMap();
          Map<String, Object> childUpdates = new HashMap<>();
          childUpdates.put("Settings/" + mUserId, postValues);
          FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);

          Intent intent = new Intent();
          intent.putExtra(BaseActivity.ARG_SETTINGS, setting);
          setResult(RESULT_OK, intent);
          finish();
        } catch (DatabaseException dex) {
          mErrorMessageText.setText(R.string.err_settings_failed);
        }
      }
    });
  }

  private void validateForm() {

    if (!mTeamSpinner.getSelectedItem().toString().isEmpty() && !mYearSpinner.getSelectedItem().toString().isEmpty()) {
      mSaveButton.setEnabled(true);
    } else {
      mSaveButton.setEnabled(false);
    }
  }

  private final OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

      validateForm();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
  };
}
