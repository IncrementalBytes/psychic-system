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
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.utils.LogUtils;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.UserSetting;

public class UserSettingFragment extends Fragment {

  private static final String TAG = UserSettingFragment.class.getSimpleName();

  public interface OnUserSettingListener {

    void onUserSettingSaved(UserSetting userSettings);
  }

  private OnUserSettingListener mCallback;

  private Spinner mTeamSpinner;
  private Spinner mYearSpinner;
  private CheckBox mCompareCheck;
  private Spinner mCompareSpinner;
  private TextView mErrorMessageText;

  private Map<String, String> mTeamMappings;
  private UserSetting mUserSetting;

  public static UserSettingFragment newInstance(UserSetting userSetting) {

    LogUtils.debug(TAG, "++newInstance(%s)", userSetting.UserId);
    UserSettingFragment fragment = new UserSettingFragment();
    Bundle args = new Bundle();
    args.putSerializable(BaseActivity.ARG_USER_SETTINGS, userSetting);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    LogUtils.debug(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_settings, container, false);

    Bundle arguments = getArguments();
    if (arguments != null) {
      mUserSetting = (UserSetting)getArguments().getSerializable(BaseActivity.ARG_USER_SETTINGS);
    } else {
      mUserSetting = new UserSetting();
    }

    mTeamSpinner = view.findViewById(R.id.settings_spinner_team);
    mYearSpinner = view.findViewById(R.id.settings_spinner_year);
    mCompareCheck = view.findViewById(R.id.settings_check_compare);
    mCompareSpinner = view.findViewById(R.id.settings_spinner_compare);
    mErrorMessageText = view.findViewById(R.id.settings_text_error_message);
    Button saveButton = view.findViewById(R.id.settings_button_save);

    mTeamMappings = new HashMap<>();
    String[] teamItems = getResources().getStringArray(R.array.teams);
    for (String teamNameResource : teamItems) {
      String[] teamSegments = teamNameResource.split(",");
      if (teamSegments.length == 2) {
        mTeamMappings.put(teamSegments[0], teamSegments[1]);
      } else {
        LogUtils.debug(TAG, "Skipping unexpected entry: " + teamNameResource);
      }
    }

    List<String> teams = new ArrayList<>(mTeamMappings.keySet());
    Collections.sort(teams);
    String[] yearItems = getResources().getStringArray(R.array.years);
    List<String> years = Arrays.asList(yearItems);

    // get a list of teams for the object adapter used by the spinner controls
    if (getActivity() != null) {
      ArrayAdapter<String> teamsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<>(teams));
      teamsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      mTeamSpinner.setAdapter(teamsAdapter);
      if (!mUserSetting.TeamFullName.isEmpty()) {
        mTeamSpinner.setSelection(teams.indexOf(mUserSetting.TeamFullName));
      }

      // get a list of teams for the object adapter used by the spinner controls
      ArrayAdapter<String> yearsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, yearItems);
      yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      mYearSpinner.setAdapter(yearsAdapter);
      if (mUserSetting.Year > 0) {
        mYearSpinner.setSelection(years.indexOf(String.valueOf(mUserSetting.Year)));
      }

      mCompareSpinner.setAdapter(yearsAdapter);
      if (mUserSetting.CompareTo > 0) {
        mCompareSpinner.setEnabled(true);
        mCompareCheck.setChecked(true);
        mCompareSpinner.setSelection(years.indexOf(String.valueOf(mUserSetting.CompareTo)));
      } else {
        mCompareSpinner.setEnabled(false);
        mCompareCheck.setChecked(false);
      }
    }

    mCompareCheck.setOnCheckedChangeListener((compoundButton, isChecked) -> {

      if (isChecked) {
        mCompareSpinner.setEnabled(true);
      } else {
        mCompareSpinner.setEnabled(false);
        mCompareSpinner.setSelection(0); // reset
      }
    });

    saveButton.setOnClickListener(view1 -> {
      LogUtils.debug(TAG, "++mSaveButton::onClick(View)");

      if (mTeamSpinner.getSelectedItem().toString().isEmpty()) {
        mErrorMessageText.setText(R.string.err_missing_team_selection);
      } else if (mYearSpinner.getSelectedItem().toString().isEmpty()) {
        mErrorMessageText.setText(R.string.err_missing_year_selection);
      } else if(mCompareCheck.isChecked() && mCompareSpinner.getSelectedItem().toString().isEmpty()) {
        mErrorMessageText.setText(R.string.err_missing_compare_selection);
      } else {
        try {
          UserSetting setting = new UserSetting();
          setting.UserId = mUserSetting.UserId;
          if (!mCompareCheck.isChecked()) {
            setting.CompareTo = 0;
          } else {
            setting.CompareTo = Integer.parseInt(mCompareSpinner.getSelectedItem().toString());
          }

          if (mYearSpinner.getSelectedItem().toString().isEmpty()) {
            setting.Year = 0;
          } else {
            setting.Year = Integer.parseInt(mYearSpinner.getSelectedItem().toString());
          }

          setting.TeamFullName = mTeamSpinner.getSelectedItem().toString();
          setting.TeamShortName = mTeamMappings.get(mTeamSpinner.getSelectedItem().toString());

          Map<String, Object> childUpdates = new HashMap<>();
          String path = UserSetting.ROOT + "/" + mUserSetting.UserId;
          LogUtils.debug(TAG, "Updating user settings: %s", path);
          childUpdates.put(path, setting.toMap());
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
      throw new ClassCastException(context.toString() + " must implement onUserSettingSaved(UserSetting).");
    }
  }
}
