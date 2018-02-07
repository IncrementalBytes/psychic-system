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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.UserSetting;

public class UserSettingFragment extends Fragment {

  private static final String TAG = "UserSettingFragment";

  static final String ARG_USER_ID = "user_id";

  public interface OnUserSettingListener {

    void onUserSettingSaved(UserSetting userSettings);
  }

  private OnUserSettingListener mCallback;

  private Spinner mTeamSpinner;
  private Spinner mYearSpinner;
  private TextView mErrorMessageText;
  private Button mSaveButton;

  private Map<String, String> mTeamMappings;
  private String mUserId;

  public static UserSettingFragment newInstance(String userId) {

    Log.d(TAG, String.format("++newInstance(%1s)", userId));
    UserSettingFragment fragment = new UserSettingFragment();
    Bundle args = new Bundle();
    args.putString(ARG_USER_ID, userId);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_settings, container, false);

    Bundle arguments = getArguments();
    if (arguments != null) {
      mUserId = getArguments().getString(ARG_USER_ID);
    } else {
      mUserId = BaseActivity.DEFAULT_ID;
    }

    mTeamSpinner = view.findViewById(R.id.settings_spinner_team);
    mYearSpinner = view.findViewById(R.id.settings_spinner_year);
    mErrorMessageText = view.findViewById(R.id.settings_text_error_message);
    mSaveButton = view.findViewById(R.id.settings_button_save);

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
    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<>(teams));
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mTeamSpinner.setAdapter(adapter);
    mTeamSpinner.setOnItemSelectedListener(spinnerListener);

    // get a list of teams for the object adapter used by the spinner controls
    resourceItems = getResources().getStringArray(R.array.years);
    adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, resourceItems);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mYearSpinner.setAdapter(adapter);
    mYearSpinner.setOnItemSelectedListener(spinnerListener);

    mSaveButton.setEnabled(false);
    mSaveButton.setOnClickListener(view1 -> {
      Log.d(TAG, "++mSaveButton::onClick(View");

      try {
        UserSetting setting = new UserSetting();
        setting.Id = mUserId;
        setting.Year = Integer.parseInt(mYearSpinner.getSelectedItem().toString());
        setting.TeamShortName = mTeamMappings.get(mTeamSpinner.getSelectedItem().toString());
        Map<String, Object> postValues = setting.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(UserSetting.ROOT + "/" + mUserId, postValues);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);

        mCallback.onUserSettingSaved(setting);
      } catch (DatabaseException dex) {
        mErrorMessageText.setText(R.string.err_settings_failed);
      }
    });

    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      mCallback = (OnUserSettingListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString() + " must implement onUserSettingSaved(UserSetting)");
    }
  }

  private void validateForm() {

    Log.d(TAG, "++validateForm()");
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
