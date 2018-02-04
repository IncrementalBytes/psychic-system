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
import net.frostedbytes.android.trendo.fragments.SettingsFragment;
import net.frostedbytes.android.trendo.fragments.TrendFragment;
import net.frostedbytes.android.trendo.models.Settings;

public class MatchListActivity extends BaseActivity implements SettingsFragment.OnSettingsSavedListener, MatchListFragment.OnMatchListListener {

  private static final String TAG = "MatchListActivity";

  private ActionBar mActionBar;

  private Settings mSettings;
  private String mUserId;

  private Query mSettingsQuery;
  private ValueEventListener mSettingsValueListener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_match_list);

    mActionBar = getSupportActionBar();
    if (mActionBar != null) {
      mActionBar.setDisplayShowHomeEnabled(true);
    }

    mSettings = new Settings();
    mUserId = getIntent().getStringExtra(BaseActivity.ARG_USER);
    mSettingsQuery = FirebaseDatabase.getInstance().getReference().child("Settings").child(mUserId);
    mSettingsValueListener = new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        mSettings = dataSnapshot.getValue(Settings.class);
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
  public void onResume() {
    super.onResume();

    Log.d(TAG, "++onResume()");
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

    Log.d(TAG, "++onOptionsItemSelected(MenuItem)");
    switch (item.getItemId()) {
      case android.R.id.home:
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
          fragmentManager.popBackStack();
        }

        return true;
      case R.id.action_logout:
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignInActivity.class));
        finish();
        return true;
      case R.id.action_settings:
        // TODO: ignore if we're already showing the settings fragment
        if (mActionBar != null) {
          mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        Fragment fragment = SettingsFragment.newInstance(mUserId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onSettingsSaved(Settings userSettings) {

    Log.d(TAG, "++onSettingsSaved(Settings)");
    if (mActionBar != null) {
      mActionBar.setDisplayHomeAsUpEnabled(false);
    }

    mSettings = userSettings;
    FragmentManager fragmentManager = getSupportFragmentManager();
    if (fragmentManager.getBackStackEntryCount() > 0) {
      fragmentManager.popBackStack();
    }
  }

  @Override
  public void onPopulated(int size) {

    Log.d(TAG, "++onPopulated(int)");
    if (mActionBar != null) {
      mActionBar.setSubtitle(getResources().getQuantityString(R.plurals.subtitle,size, mSettings.TeamShortName, size));
    }
  }

  @Override
  public void onSelected(String matchId) {

    Log.d(TAG, "++onSelected(String)");
    if (mActionBar != null) {
      mActionBar.setSubtitle("");
    }

    Fragment fragment = TrendFragment.newInstance(matchId);
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.fragment_container, fragment);
    transaction.addToBackStack(null);
    transaction.commit();
  }

  private void onGatheringSettingsComplete() {

    Log.d(TAG, "++onGatheringSettingsComplete()");
    if (mSettings == null || mSettings.TeamShortName.isEmpty()) {
      Log.d(TAG, "No team settings information found; starting settings fragment.");
      if (mActionBar != null) {
        mActionBar.setDisplayHomeAsUpEnabled(true);
      }

      Fragment fragment = SettingsFragment.newInstance(mUserId);
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.fragment_container, fragment);
      transaction.addToBackStack(null);
      transaction.commit();
    } else {
      Log.d(TAG, "User settings found; starting match list fragment.");
      if (mActionBar != null) {
        mActionBar.setDisplayHomeAsUpEnabled(true);
      }

      Fragment fragment = MatchListFragment.newInstance(mSettings);
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.fragment_container, fragment);
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }
}
