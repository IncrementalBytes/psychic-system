package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
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
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Event;
import net.frostedbytes.android.trendo.models.MatchEvent;
import net.frostedbytes.android.trendo.models.Player;
import net.frostedbytes.android.trendo.models.Team;

public class EventDetailFragment extends Fragment {

  private static final String TAG = "EventDetailFragment";

  private EventDetailListener mCallback;

  public interface EventDetailListener {

    void onMatchEventCreated(String matchId);
  }

  private boolean mMatchEventsDone;
  private MatchEvent mMatchEvent;
  private List<MatchEvent> mMatchEvents;
  private String mMatchId;
  private Team mTeam;

  private TextView mTeamNameText;
  private Spinner mPlayerNameSpinner;
  private Spinner mEventNameSpinner;
  private NumberPicker mFirstDigitPicker;
  private NumberPicker mSecondDigitPicker;
  private CheckBox mStoppageCheckBox;
  private CheckBox mAETCheckBox;
  private TextView mErrorMessageText;
  private Button mCancel;
  private Button mCreate;

  private Query mEventsQuery;
  private Query mMatchEventsQuery;
  private Query mPlayersQuery;
  private ValueEventListener mEventsValueListener;
  private ValueEventListener mMatchEventsValueListner;
  private ValueEventListener mPlayersValueListener;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    Log.d(TAG, "++onAttach(Context)");
    try {
      mCallback = (EventDetailListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException("Calling activity/fragment must implement EventDetailListener.");
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    Bundle arguments = getArguments();
    mMatchId = BaseActivity.DEFAULT_ID;
    mTeam = new Team();
    if (arguments != null) {
      mMatchId = arguments.getString(BaseActivity.ARG_MATCH_ID);
      mTeam = (Team)arguments.getSerializable(BaseActivity.ARG_TEAM);
    }

    mMatchEvent = new MatchEvent();
    mMatchEvent.Id = UUID.randomUUID().toString();
    mMatchEvent.TeamShortName = mTeam.ShortName;

    mMatchEvents = new ArrayList<>();
    mMatchEventsQuery = FirebaseDatabase.getInstance().getReference().child("matches/" + mMatchId + "/MatchEvents").orderByChild("MinuteOfEvent");
    mMatchEventsValueListner = new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        for (DataSnapshot data : dataSnapshot.getChildren()) {
          MatchEvent matchEvent = data.getValue(MatchEvent.class);
          if (matchEvent != null) {
            mMatchEvents.add(matchEvent);
          }
        }

        onGatheringMatchEventsComplete();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++onCancelled(DatabaseError)");
        Log.e(TAG, databaseError.getMessage());
      }
    };
    mMatchEventsQuery.addValueEventListener(mMatchEventsValueListner);

    View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_event_detail, null);
    mTeamNameText = view.findViewById(R.id.event_text_team_name);
    mPlayerNameSpinner = view.findViewById(R.id.event_spinner_player_name);
    mEventNameSpinner = view.findViewById(R.id.event_spinner_event_name);
    mFirstDigitPicker = view.findViewById(R.id.event_picker_first_digit);
    mSecondDigitPicker = view.findViewById(R.id.event_picker_second_digit);
    mStoppageCheckBox = view.findViewById(R.id.event_check_stoppage_time);
    mAETCheckBox = view.findViewById(R.id.event_check_add_extra_time);
    mErrorMessageText = view.findViewById(R.id.event_text_error_message);
    mCancel = view.findViewById(R.id.event_button_cancel);
    mCreate = view.findViewById(R.id.event_button_create);

    mTeamNameText.setText(mTeam.ShortName);

    final List<String> players = new ArrayList<>();
    players.add("");
    // TODO: remove hard-coded 2017 and replace with user setting (default to current year)
    mPlayersQuery = FirebaseDatabase.getInstance().getReference().child("teams/" + mTeam.Id + "/Rosters/2017").orderByChild("Name");
    mPlayersValueListener = new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        for (DataSnapshot data : dataSnapshot.getChildren()) {
          Player player = data.getValue(Player.class);
          if (player != null) {
            players.add(player.Name);
          }
        }

        onGatheringPlayersComplete(players);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++onCancelled(DatabaseError)");
        Log.e(TAG, databaseError.getMessage());
      }
    };
    mPlayersQuery.addValueEventListener(mPlayersValueListener);

    final List<String> events = new ArrayList<>();
    events.add("");
    mEventsQuery = FirebaseDatabase.getInstance().getReference().child("events").orderByChild("Name");
    mEventsValueListener = new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        for (DataSnapshot data : dataSnapshot.getChildren()) {
          Event event = data.getValue(Event.class);
          if (event != null) {
            events.add(event.Name);
          }
        }

        onGatheringEventsComplete(events);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++onCancelled(DatabaseError)");
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

    mStoppageCheckBox.setOnCheckedChangeListener(
      new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

          mMatchEvent.IsStoppageTime = isChecked;
        }
      }
    );

    mAETCheckBox.setOnCheckedChangeListener(
      new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

          mMatchEvent.IsAdditionalExtraTime = isChecked;
        }
      }
    );

    mCancel.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {

        Log.d(TAG, "++mCancel::onClick(View");
        Log.d(TAG, "User cancelled match creation");
        sendResult();

      }
    });

    mCreate.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        Log.d(TAG, "++mCreate::onClick(View");

        if (validateForm()) {
          try {
            Map<String, Object> postValues = mMatchEvent.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/matches/" + mMatchId + "/MatchEvents/" + mMatchEvent.Id, postValues);
            FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
            sendResult();
          } catch (DatabaseException dex) {
            Log.d(TAG, dex.getLocalizedMessage());
            mErrorMessageText.setText(R.string.err_event_permissions);
          }
        }
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
    if (mPlayersQuery != null && mPlayersValueListener != null) {
      mPlayersQuery.removeEventListener(mPlayersValueListener);
    }

    if (mEventsQuery != null && mEventsValueListener != null) {
      mEventsQuery.removeEventListener(mEventsValueListener);
    }

    if (mMatchEventsQuery != null && mMatchEventsValueListner != null) {
      mMatchEventsQuery.removeEventListener(mMatchEventsValueListner);
    }
  }

  void onGatheringEventsComplete(List<String> eventNames) {

    Log.d(TAG, "++onGatheringEventsComplete(List<String>)");
    if (getActivity() != null) {
      ArrayAdapter<String> adapter = new ArrayAdapter<>(
        getActivity(),
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
    } else {
      Log.e(TAG, "GetActivity() was null");
    }
  }

  void onGatheringMatchEventsComplete() {

    Log.d(TAG, "++onGatheringMatchEventsComplete()");
    mMatchEventsDone = true;
  }

  void onGatheringPlayersComplete(List<String> playerNames) {

    Log.d(TAG, "++onGatheringPlayersComplete(List<String>)");
    if (getActivity() != null) {
      ArrayAdapter<String> adapter = new ArrayAdapter<>(
        getActivity(),
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
      Log.e(TAG, "GetActivity() was null");
    }
  }

  void sendResult() {

    Log.d(TAG, "++sendResult()");
    if (mCallback != null) {
      mCallback.onMatchEventCreated(mMatchId);
    } else {
      Log.e(TAG, "Unable to process callback.");
    }
  }

  boolean validateForm() {

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

    while (!mMatchEventsDone) {
      try {
        wait(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    for (MatchEvent matchEvent : mMatchEvents) {
      if (matchEvent.EventName.equals(mEventNameSpinner.getSelectedItem().toString()) &&
        matchEvent.MinuteOfEvent == ((mFirstDigitPicker.getValue() * 10) + mSecondDigitPicker.getValue())){
        mErrorMessageText.setText(matchEvent.toString() + " already exists.");
        return false;
      }
    }

    return true;
  }
}
