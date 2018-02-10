package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import net.frostedbytes.android.trendo.fragments.MatchListFragment;
import net.frostedbytes.android.trendo.fragments.TrendFragment;
import net.frostedbytes.android.trendo.fragments.UserSettingFragment;
import net.frostedbytes.android.trendo.models.UserSetting;

public class MatchListActivity extends BaseActivity implements UserSettingFragment.OnUserSettingListener, MatchListFragment.OnMatchListListener {

  private static final String TAG = "MatchListActivity";

  private ActionBar mActionBar;

  FragmentManager mFragmentManager;

  private UserSetting mSettings;
  private String mUserId;

  private Query mSettingsQuery;
  private ValueEventListener mSettingsValueListener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_match_list);

    showProgressDialog("Initializing...");
    mActionBar = getSupportActionBar();
    if (mActionBar != null) {
      mActionBar.setDisplayShowHomeEnabled(true);
    }

    mFragmentManager = getSupportFragmentManager();

    mSettings = new UserSetting();
    mUserId = getIntent().getStringExtra(BaseActivity.ARG_USER);
    mSettingsQuery = FirebaseDatabase.getInstance().getReference().child(UserSetting.ROOT).child(mUserId);
    mSettingsValueListener = new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        mSettings = dataSnapshot.getValue(UserSetting.class);
        onGatheringSettingsComplete();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++onCancelled(DatabaseError)");
        Log.e(TAG, databaseError.getMessage());
      }
    };
    mSettingsQuery.addValueEventListener(mSettingsValueListener);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.d(TAG, "++onDestroy()");
    if (mSettingsQuery != null && mSettingsValueListener != null) {
      mSettingsQuery.removeEventListener(mSettingsValueListener);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    Log.d(TAG, "++onCreateOptionsMenu(Menu)");
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    Log.d(TAG, String.format("++onOptionsItemSelected(%1s)", item.getTitle()));
    switch (item.getItemId()) {
      case android.R.id.home:
        if (mFragmentManager.getBackStackEntryCount() > 0) {
          mFragmentManager.popBackStack();
        } else {
          mActionBar.setDisplayHomeAsUpEnabled(false);
          onGatheringSettingsComplete();
        }

        return true;
      case R.id.action_logout:
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignInActivity.class));
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onUserSettingSaved(UserSetting userSettings) {

    Log.d(TAG, String.format("++onSettingsSaved(%1s, %2d)", userSettings.TeamShortName, userSettings.Year));
    if (mActionBar != null) {
      mActionBar.setDisplayHomeAsUpEnabled(false);
    }

    // remove settings fragment from stack; returning to match list fragment
    if (mFragmentManager.getBackStackEntryCount() > 0) {
      mFragmentManager.popBackStack();
    }

    if (!userSettings.equals(mSettings)) {
      // settings have changed, signal that to match list fragment
      mSettings = userSettings;
      onGatheringSettingsComplete();
    }
  }

  @Override
  public void onPopulated(int size) {

    Log.d(TAG, String.format("++onPopulated(%1d)", size));
    if (mActionBar != null) {
      mActionBar.setDisplayHomeAsUpEnabled(false);
      mActionBar.setSubtitle(getResources().getQuantityString(R.plurals.subtitle,size, mSettings.TeamShortName, size));
    }

    hideProgressDialog();
  }

  @Override
  public void onSelected(long matchDate) {

    Log.d(TAG, String.format("++onSelected(%1d)", matchDate));
    if (mActionBar != null) {
      mActionBar.setDisplayHomeAsUpEnabled(true);
      mActionBar.setSubtitle("");
    }

    Fragment fragment = TrendFragment.newInstance(mSettings, matchDate);
    FragmentTransaction transaction = mFragmentManager.beginTransaction();
    transaction.replace(R.id.fragment_container, fragment, "TREND_FRAGMENT");
    transaction.addToBackStack(null);
    transaction.commit();
  }

  @Override
  public void onSettingsClicked() {

    Log.d(TAG, "++onSettingsClicked()");
    if (mActionBar != null) {
      mActionBar.setDisplayHomeAsUpEnabled(true);
      mActionBar.setSubtitle("Settings");
    }

    Fragment fragment = UserSettingFragment.newInstance(mUserId);
    FragmentTransaction transaction = mFragmentManager.beginTransaction();
    transaction.replace(R.id.fragment_container, fragment, "SETTINGS_FRAGMENT");
    transaction.addToBackStack(null);
    transaction.commit();
  }

  private void onGatheringSettingsComplete() {

    Log.d(TAG, "++onGatheringSettingsComplete()");
    hideProgressDialog();
    if (mSettings == null || mSettings.TeamShortName.isEmpty()) {
      Log.d(TAG, "No team settings information found; starting settings fragment.");
      if (mActionBar != null) {
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setSubtitle("");
      }

      Fragment fragment = UserSettingFragment.newInstance(mUserId);
      FragmentTransaction transaction = mFragmentManager.beginTransaction();
      transaction.replace(R.id.fragment_container, fragment, "SETTINGS_FRAGMENT");
      transaction.addToBackStack(null);
      transaction.commit();
    } else {
      Log.d(TAG, "User settings found; starting match list fragment.");
      if (mActionBar != null) {
        mActionBar.setDisplayHomeAsUpEnabled(false);
      }

      showProgressDialog("Querying...");
      Fragment fragment = MatchListFragment.newInstance(mSettings);
      FragmentTransaction transaction = mFragmentManager.beginTransaction();
      transaction.replace(R.id.fragment_container, fragment, "MATCH_LIST_FRAGMENT");
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }
}
