package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import net.frostedbytes.android.trendo.utils.LogUtils;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.UserSetting;

public class UserSettingFragment extends Fragment {

  private static final String TAG = "UserSettingFragment";

  public interface OnUserSettingListener {

    void onUserSettingSaved(UserSetting userSettings);
  }

  private OnUserSettingListener mCallback;

  private Spinner mTeamSpinner;
  private Spinner mYearSpinner;
  private TextView mErrorMessageText;

  private Map<String, String> mTeamMappings;
  private String mUserId;

  public static UserSettingFragment newInstance(String userId) {

    LogUtils.debug(TAG, String.format("++newInstance(%1s)", userId));
    UserSettingFragment fragment = new UserSettingFragment();
    Bundle args = new Bundle();
    args.putString(BaseActivity.ARG_USER_ID, userId);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    LogUtils.debug(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_settings, container, false);

    Bundle arguments = getArguments();
    if (arguments != null) {
      mUserId = getArguments().getString(BaseActivity.ARG_USER_ID);
    } else {
      mUserId = BaseActivity.DEFAULT_ID;
    }

    mTeamSpinner = view.findViewById(R.id.settings_spinner_team);
    mYearSpinner = view.findViewById(R.id.settings_spinner_year);
    mErrorMessageText = view.findViewById(R.id.settings_text_error_message);
    Button saveButton = view.findViewById(R.id.settings_button_save);

    mTeamMappings = new HashMap<>();
    String[] resourceItems = getResources().getStringArray(R.array.teams);
    for (String teamNameResource : resourceItems) {
      String[] teamSegments = teamNameResource.split(",");
      if (teamSegments.length == 2) {
        mTeamMappings.put(teamSegments[0], teamSegments[1]);
      } else {
        LogUtils.debug(TAG, "Skipping unexpected entry: " + teamNameResource);
      }
    }

    List<String> teams = new ArrayList<>(mTeamMappings.keySet());
    Collections.sort(teams);

    // get a list of teams for the object adapter used by the spinner controls
    if (getActivity() != null) {
      ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<>(teams));
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      mTeamSpinner.setAdapter(adapter);

      // get a list of teams for the object adapter used by the spinner controls
      resourceItems = getResources().getStringArray(R.array.years);
      adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, resourceItems);
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      mYearSpinner.setAdapter(adapter);
    }

    saveButton.setOnClickListener(view1 -> {
      LogUtils.debug(TAG, "++mSaveButton::onClick(View");

      if (mTeamSpinner.getSelectedItem().toString().isEmpty()) {
        mErrorMessageText.setText(R.string.err_missing_team_selection);
      } else if (mYearSpinner.getSelectedItem().toString().isEmpty()) {
        mErrorMessageText.setText(R.string.err_missing_year_selection);
      } else {
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
}
