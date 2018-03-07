package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import net.frostedbytes.android.trendo.fragments.MatchListFragment;
import net.frostedbytes.android.trendo.fragments.TrendFragment;
import net.frostedbytes.android.trendo.fragments.UserSettingFragment;
import net.frostedbytes.android.trendo.models.MatchSummary;
import net.frostedbytes.android.trendo.models.UserSetting;
import net.frostedbytes.android.trendo.utils.LogUtils;

public class MainActivity extends BaseActivity implements UserSettingFragment.OnUserSettingListener, MatchListFragment.OnMatchListListener {

  private static final String TAG = MainActivity.class.getSimpleName();

  private ActionBar mActionBar;
  private MenuItem mSettingsMenuItem;

  private FragmentManager mFragmentManager;

  private UserSetting mUserSettings;

  private Query mSettingsQuery;
  private ValueEventListener mSettingsValueListener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LogUtils.debug(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_match_list);

    showProgressDialog(getString(R.string.status_initializing));

    Toolbar toolbar = findViewById(R.id.main_toolbar);
    setSupportActionBar(toolbar);
    mActionBar = getSupportActionBar();
    if (mActionBar != null) {
      mActionBar.setDisplayShowHomeEnabled(true);
    }

    mFragmentManager = getSupportFragmentManager();

    mUserSettings = new UserSetting();
    mUserSettings.UserId = getIntent().getStringExtra(BaseActivity.ARG_USER);
    mSettingsQuery = FirebaseDatabase.getInstance().getReference().child(UserSetting.ROOT).child(mUserSettings.UserId);
    mSettingsValueListener = new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        UserSetting userSettings = dataSnapshot.getValue(UserSetting.class);
        if (userSettings != null) {
          mUserSettings = userSettings;
          mUserSettings.UserId = dataSnapshot.getKey();
        }

        onGatheringSettingsComplete();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        LogUtils.debug(TAG, "++onCancelled(DatabaseError)");
        LogUtils.error(TAG, databaseError.getMessage());
      }
    };
    mSettingsQuery.addValueEventListener(mSettingsValueListener);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    LogUtils.debug(TAG, "++onDestroy()");
    if (mSettingsQuery != null && mSettingsValueListener != null) {
      mSettingsQuery.removeEventListener(mSettingsValueListener);
    }

    mUserSettings = null;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    LogUtils.debug(TAG, "++onCreateOptionsMenu(Menu)");
    getMenuInflater().inflate(R.menu.menu_main, menu);
    mSettingsMenuItem = menu.findItem(R.id.menu_item_settings);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    LogUtils.debug(TAG, String.format("++onOptionsItemSelected(%1s)", item.getTitle()));
    switch (item.getItemId()) {
      case android.R.id.home:
        if (mFragmentManager.getBackStackEntryCount() > 0) {
          mFragmentManager.popBackStack();
        } else {
          mActionBar.setDisplayHomeAsUpEnabled(false);
          onGatheringSettingsComplete();
        }

        if (mSettingsMenuItem != null) {
          mSettingsMenuItem.setVisible(true);
        }

        return true;
      case R.id.menu_item_settings:
        showSettingsFragment();
        return true;
      case R.id.menu_item_logout:
        AlertDialog dialog = new AlertDialog.Builder(this)
          .setMessage(R.string.logout_message)
          .setPositiveButton(android.R.string.yes, (dialog1, which) -> {

              // sign out of firebase
              FirebaseAuth.getInstance().signOut();

              // sign out of google, if necessary
              GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
              GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
              googleSignInClient.signOut().addOnCompleteListener(this, task -> {

                  // return to sign-in activity
                  startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                  finish();
                });
            })
          .setNegativeButton(android.R.string.no, null)
          .create();
        dialog.show();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onUserSettingSaved(UserSetting userSettings) {

    LogUtils.debug(TAG, "++onSettingsSaved(%1s, %2d)", userSettings.TeamShortName, userSettings.Year);
    if (mActionBar != null) {
      mActionBar.setDisplayHomeAsUpEnabled(false);
    }

    if (mSettingsMenuItem != null) {
      mSettingsMenuItem.setVisible(true);
    }

    // remove settings fragment from stack; returning to match list fragment
    if (mFragmentManager.getBackStackEntryCount() > 0) {
      mFragmentManager.popBackStack();
    }

    if (!userSettings.equals(mUserSettings)) {
      // settings have changed, signal that to match list fragment
      mUserSettings = userSettings;
      onGatheringSettingsComplete();
    }
  }

  @Override
  public void onPopulated(int size) {

    LogUtils.debug(TAG, "++onPopulated(%1d)", size);
    if (mActionBar != null) {
      mActionBar.setDisplayHomeAsUpEnabled(false);
      mActionBar.setSubtitle(getResources().getQuantityString(R.plurals.subtitle,size, mUserSettings.TeamShortName, size));
    }

    hideProgressDialog();
  }

  @Override
  public void onSelected(MatchSummary matchSummary) {

    LogUtils.debug(TAG, "++onSelected(MatchSummary)");
    if (mActionBar != null) {
      mActionBar.setDisplayHomeAsUpEnabled(true);
      mActionBar.setSubtitle("");
    }

    if (mSettingsMenuItem != null) {
      mSettingsMenuItem.setVisible(false);
    }

    Fragment fragment = TrendFragment.newInstance(mUserSettings, matchSummary);
    FragmentTransaction transaction = mFragmentManager.beginTransaction();
    transaction.replace(R.id.fragment_container, fragment, "TREND_FRAGMENT");
    transaction.addToBackStack(null);
    transaction.commit();
  }

  private void onGatheringSettingsComplete() {

    LogUtils.debug(TAG, "++onGatheringSettingsComplete()");
    hideProgressDialog();
    if (mUserSettings == null || mUserSettings.TeamShortName.isEmpty()) {
      LogUtils.debug(TAG, "No team settings information found; starting settings fragment.");
      showSettingsFragment();
    } else {
      LogUtils.debug(TAG, "User settings found; starting match list fragment.");
      if (mActionBar != null) {
        mActionBar.setDisplayHomeAsUpEnabled(false);
      }

      showProgressDialog(getString(R.string.status_querying));
      Fragment fragment = MatchListFragment.newInstance(mUserSettings);
      FragmentTransaction transaction = mFragmentManager.beginTransaction();
      transaction.replace(R.id.fragment_container, fragment, "MATCH_LIST_FRAGMENT");
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }

  private void showSettingsFragment() {

    LogUtils.debug(TAG, "++showSettingsFragment()");
    if (mActionBar != null) {
      mActionBar.setDisplayHomeAsUpEnabled(true);
      mActionBar.setSubtitle("Settings");
    }

    if (mSettingsMenuItem != null) {
      mSettingsMenuItem.setVisible(false);
    }

    Fragment fragment = UserSettingFragment.newInstance(mUserSettings);
    FragmentTransaction transaction = mFragmentManager.beginTransaction();
    transaction.replace(R.id.fragment_container, fragment, "SETTINGS_FRAGMENT");
    transaction.addToBackStack(null);
    transaction.commit();
  }
}
