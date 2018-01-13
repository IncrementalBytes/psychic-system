package net.frostedbytes.android.trendo.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.Serializable;
import java.util.List;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.MatchCenter;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Event;
import net.frostedbytes.android.trendo.models.MatchEvent;
import net.frostedbytes.android.trendo.models.Player;
import net.frostedbytes.android.trendo.models.Team;

public class EventDetailFragment extends DialogFragment {

  private static final String TAG = "EventDetailFragment";

  public static final String EXTRA_MATCH_EVENT = "matchevent";

  private static final String ARG_MATCH = "match";
  private static final String ARG_TEAM = "team";

  private MatchEvent mMatchEvent;

  TextView mTeamNameText;
  Spinner mPlayerNameSpinner;
  Spinner mEventNameSpinner;
  private int progress;
  SeekBar mMinuteSeekBar;
  TextView mMinuteOfEventText;
  CheckBox mAETCheckBox;
  private int progressAET;
  SeekBar mMinuteAETSeekBar;
  TextView mMinuteOfEventAETText;

  public static EventDetailFragment newInstance(String matchId, String teamId) {

    Bundle args = new Bundle();
    args.putSerializable(ARG_MATCH, matchId);
    args.putSerializable(ARG_TEAM, teamId);

    EventDetailFragment fragment = new EventDetailFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull
  public Dialog onCreateDialog(Bundle savedInstanceState) throws NullPointerException {

    String matchId = BaseActivity.DEFAULT_ID;
    String teamId = BaseActivity.DEFAULT_ID;
    Bundle arguments = getArguments();
    if (arguments != null) {
      Serializable value = arguments.getSerializable(ARG_MATCH);
      if (value != null) {
        matchId = (String)value;
      }
      value = arguments.getSerializable(ARG_TEAM);
      if (value != null) {
        teamId = (String)value;
      }
    }

    mMatchEvent = new MatchEvent();
    mMatchEvent.MatchId = matchId;
    mMatchEvent.TeamId = teamId;

    final View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_event_detail, null);

    Team team = MatchCenter.get().getTeam(teamId);

    // update UI components
    mTeamNameText = v.findViewById(R.id.event_text_team_name);
    mTeamNameText.setText(team.ShortName);
    mPlayerNameSpinner = v.findViewById(R.id.event_spinner_player_name);
    mEventNameSpinner = v.findViewById(R.id.event_spinner_event_name);
    mMinuteSeekBar = v.findViewById(R.id.event_seek_minute);
    mMinuteOfEventText = v.findViewById(R.id.event_text_time_of_event);

    // get a list of players (specific to passed team) for the object adapter used by the spinner controls
    List<String> playerNames = MatchCenter.get().getPlayerNames(teamId);
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

        Player player = MatchCenter.get().getPlayer(mPlayerNameSpinner.getSelectedItem().toString());
        mMatchEvent.PlayerId = player.Id;
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    // get a list of events for the object adapter used by the spinner controls
    List<String> eventNames = MatchCenter.get().getEventNames();
    adapter = new ArrayAdapter<>(
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

        Event event = MatchCenter.get().getEvent(mEventNameSpinner.getSelectedItem().toString());
        mMatchEvent.EventId = event.Id;
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    mMinuteSeekBar.setOnSeekBarChangeListener(
        new OnSeekBarChangeListener() {

          @Override
          public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {

            progress = progressValue;
            mMinuteOfEventText.setText(String.valueOf(progress));
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {
          }

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {
          }
        });

    mAETCheckBox = v.findViewById(R.id.event_check_aet);
    mAETCheckBox.setOnCheckedChangeListener(
        new OnCheckedChangeListener() {

          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (isChecked) {
              mMinuteAETSeekBar.setEnabled(true);
            } else {
              mMinuteAETSeekBar.setEnabled(false);
              mMinuteAETSeekBar.setProgress(0);
              mMinuteOfEventAETText.setText("");
            }
          }
        }
    );

    mMinuteAETSeekBar = v.findViewById(R.id.event_seek_minute_aet);
    mMinuteAETSeekBar.setProgress(0);
    mMinuteAETSeekBar.setEnabled(false);
    mMinuteOfEventAETText = v.findViewById(R.id.event_text_time_of_event_aet);
    mMinuteOfEventAETText.setText("");
    mMinuteOfEventAETText.setEnabled(false);
    mMinuteAETSeekBar.setOnSeekBarChangeListener(
        new OnSeekBarChangeListener() {

          @Override
          public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {

            progressAET = progressValue;
            mMinuteOfEventAETText.setText(String.valueOf(progressAET));
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {
          }

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {
          }
        });

    return new AlertDialog.Builder(getActivity())
        .setView(v)
        .setNegativeButton(android.R.string.cancel,
            new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int which) {
                //MatchCenter.get().deleteEvent(mMatchEvent.getId());
                //sendResult(MatchFragment.REQUEST_MATCH_EVENT, new UUID(0,0));
              }
            })
        .setPositiveButton(android.R.string.ok,
            new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int which) {
                SeekBar seekBar = v.findViewById(R.id.event_seek_minute);
                if (seekBar != null) {
                  mMatchEvent.MinuteOfEvent = seekBar.getProgress();
                }

                CheckBox checkBox = v.findViewById(R.id.event_check_aet);
                if (checkBox != null && checkBox.isChecked()) {
                  mMatchEvent.IsAdditionalExtraTime = checkBox.isChecked();
                }

                checkBox = v.findViewById(R.id.event_check_stoppage_time);
                if (checkBox != null) {
                  mMatchEvent.IsStoppageTime = checkBox.isChecked();
                }

                // TODO: add additional extra time value

//                MatchCenter.get().addMatchEvent(mMatchEvent);
//                sendResult(MatchFragment.REQUEST_MATCH_EVENT, mMatchEvent.getId());
              }
            })
        .create();
  }

  private void sendResult(int resultCode, String matchEventId) {

    if (getTargetFragment() == null) {
      return;
    }

    Intent intent = new Intent();
    intent.putExtra(EXTRA_MATCH_EVENT, matchEventId);

    getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
  }
}
