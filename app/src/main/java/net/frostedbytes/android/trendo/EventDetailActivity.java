package net.frostedbytes.android.trendo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.MatchEvent;

public class EventDetailActivity extends BaseActivity {

  private static final String TAG = "EventDetailActivity";

  private MatchEvent mMatchEvent;
  private Match mMatch;

  private Spinner mPlayerNameSpinner;
  private Spinner mEventNameSpinner;
  private NumberPicker mFirstDigitPicker;
  private NumberPicker mSecondDigitPicker;
  private TextView mErrorMessageText;

  private Query mEventsQuery;
  private Query mTeamQuery;
  private ValueEventListener mEventsValueListener;
  private ValueEventListener mTeamValueListener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_event_detail);
    mMatch = (Match)getIntent().getSerializableExtra(ARG_MATCH);
    String teamShortName = getIntent().getStringExtra(ARG_TEAM_NAME);

    mMatchEvent = new MatchEvent();
    mMatchEvent.Id = UUID.randomUUID().toString();
    mMatchEvent.TeamShortName = teamShortName;

    TextView teamShortNameText = findViewById(R.id.event_text_team_short_name);
    mPlayerNameSpinner = findViewById(R.id.event_spinner_player_name);
    mEventNameSpinner = findViewById(R.id.event_spinner_event_name);
    mFirstDigitPicker = findViewById(R.id.event_picker_first_digit);
    mSecondDigitPicker = findViewById(R.id.event_picker_second_digit);
    CheckBox stoppageCheckBox = findViewById(R.id.event_check_stoppage_time);
    CheckBox AETCheckBox = findViewById(R.id.event_check_add_extra_time);
    mErrorMessageText = findViewById(R.id.event_text_error_message);
    ImageView cancelImageView = findViewById(R.id.event_imageview_cancel);
    Button create = findViewById(R.id.event_button_create);

    teamShortNameText.setText(teamShortName);

    final List<String> playerNames = new ArrayList<>();
    playerNames.add("");
    // TODO: remove hard-coded year and replace with user setting
    mTeamQuery = FirebaseDatabase.getInstance().getReference().child("teams/" + teamShortName + "/Rosters/2017");
    mTeamValueListener = new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        Log.d(TAG, "++mTeamValueListener::onDataChange(DataSnapshot)");
        for (DataSnapshot data : dataSnapshot.getChildren()) {
          String playerName = data.getValue(String.class);
          if (playerName!= null) {
            playerNames.add(playerName);
          }
        }

        onGatheringPlayerNamesComplete(playerNames);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++mTeamValueListener::onCancelled(DatabaseError)");
        Log.e(TAG, databaseError.getMessage());
      }
    };
    mTeamQuery.addValueEventListener(mTeamValueListener);

    final List<String> events = new ArrayList<>();
    mEventsQuery = FirebaseDatabase.getInstance().getReference().child("events");
    mEventsValueListener = new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        Log.d(TAG, "++mEventsValueListener::onDataChange(DataSnapshot)");
        for (DataSnapshot data : dataSnapshot.getChildren()) {
          String eventName = data.getValue(String.class);
          if (eventName!= null) {
            events.add(eventName);
          }
        }

        onGatheringEventsComplete(events);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++mEventsValueListener::onCancelled(DatabaseError)");
        Log.e(TAG, databaseError.getMessage());
      }
    };
    mEventsQuery.addValueEventListener(mEventsValueListener);

    mFirstDigitPicker.setMinValue(0);
    mFirstDigitPicker.setMaxValue(12);
    mFirstDigitPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

      @Override
      public void onValueChange(NumberPicker picker, int oldVal, int newVal){

        mMatchEvent.MinuteOfEvent = (newVal * 10) + mSecondDigitPicker.getValue();
      }
    });

    mSecondDigitPicker.setMinValue(0);
    mSecondDigitPicker.setMaxValue(9);
    mSecondDigitPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

      @Override
      public void onValueChange(NumberPicker picker, int oldVal, int newVal){

        mMatchEvent.MinuteOfEvent = (mFirstDigitPicker.getValue() * 10) + newVal;
      }
    });

    stoppageCheckBox.setOnCheckedChangeListener(
      new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

          mMatchEvent.IsStoppageTime = isChecked;
        }
      }
    );

    AETCheckBox.setOnCheckedChangeListener(
      new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

          mMatchEvent.IsAdditionalExtraTime = isChecked;
        }
      }
    );

    cancelImageView.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {

        Log.d(TAG, "++mCancel::onClick(View");
        Log.d(TAG, "User cancelled match creation");
        setResult(RESULT_CANCELED);
        finish();
      }
    });

    create.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        Log.d(TAG, "++mCreate::onClick(View");

        if (validateForm()) {
          try {
            Map<String, Object> postValues = mMatchEvent.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/matches/" + mMatch.Id + "/MatchEvents/" + mMatchEvent.Id, postValues);
            FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
            setResult(RESULT_OK);
            finish();
          } catch (DatabaseException dex) {
            Log.d(TAG, dex.getLocalizedMessage());
            mErrorMessageText.setText(R.string.err_event_permissions);
          }
        }
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();

    Log.d(TAG, "+++onResume()");
  }

  @Override
  public void onStart() {
    super.onStart();

    Log.d(TAG, "++onStart()");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.d(TAG, "++onDestroy()");
    if (mEventsQuery != null && mEventsValueListener != null) {
      mEventsQuery.removeEventListener(mEventsValueListener);
    }

    if (mTeamQuery != null && mTeamValueListener != null) {
      mTeamQuery.removeEventListener(mTeamValueListener);
    }
  }

  private void onGatheringEventsComplete(List<String> eventNames) {

    Log.d(TAG, "++onGatheringEventsComplete(List<String>)");
    java.util.Collections.sort(eventNames);
    ArrayAdapter<String> adapter = new ArrayAdapter<>(
      this,
      android.R.layout.simple_list_item_1,
      eventNames);

    // specify the layout to use when the list of choices appears
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    // apply the adapter to the spinner
    mEventNameSpinner.setAdapter(adapter);
    mEventNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        mMatchEvent.EventName = mEventNameSpinner.getSelectedItem().toString();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
  }

  private void onGatheringPlayerNamesComplete(List<String> playerNames) {

    Log.d(TAG, "++onGatheringPlayerNamesComplete(List<String>)");
    if (playerNames.size() > 1) {
      java.util.Collections.sort(playerNames);
      ArrayAdapter<String> adapter = new ArrayAdapter<>(
        this,
        android.R.layout.simple_list_item_1,
        playerNames);

      // specify the layout to use when the list of choices appears
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

      // apply the adapter to the spinner
      mPlayerNameSpinner.setAdapter(adapter);
      mPlayerNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

          mMatchEvent.PlayerName = mPlayerNameSpinner.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
      });
    } else {
      mPlayerNameSpinner.setEnabled(false);
    }
  }

  private boolean validateForm() {

    Log.d(TAG, "++validateForm()");
    mErrorMessageText.setText("");
    if (mEventNameSpinner.getSelectedItem().toString().isEmpty()) {
      mErrorMessageText.setText(R.string.err_event_missing);
      return false;
    }

    if (mFirstDigitPicker.getValue() == 0 && mSecondDigitPicker.getValue() == 0) {
      mErrorMessageText.setText(R.string.err_event_time_missing);
      return false;
    }

    for (Map.Entry<String, Object> entry : mMatch.MatchEvents.entrySet()) {
      MatchEvent matchEvent = (MatchEvent) entry;
      if (matchEvent.EventName.equals(mEventNameSpinner.getSelectedItem().toString()) &&
        matchEvent.MinuteOfEvent == ((mFirstDigitPicker.getValue() * 10) + mSecondDigitPicker.getValue())){
        mErrorMessageText.setText(matchEvent.toString() + " already exists.");
        return false;
      }
    }

    return true;
  }
}
