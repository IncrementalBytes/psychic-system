package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Locale;
import net.frostedbytes.android.trendo.fragments.MatchListFragment;
import net.frostedbytes.android.trendo.fragments.TrendFragment;
import net.frostedbytes.android.trendo.fragments.UserPreferencesFragment;
import net.frostedbytes.android.trendo.models.MatchSummary;
import net.frostedbytes.android.trendo.models.UserPreference;
import net.frostedbytes.android.trendo.utils.LogUtils;

public class MainActivity extends BaseActivity implements
  NavigationView.OnNavigationItemSelectedListener,
  UserPreferencesFragment.OnPreferencesListener,
  MatchListFragment.OnMatchListListener {

  private static final String TAG = MainActivity.class.getSimpleName();

  private DrawerLayout mDrawerLayout;

  private UserPreference mUserPreference;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LogUtils.debug(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_match_list);

    showProgressDialog(getString(R.string.status_initializing));

    mDrawerLayout = findViewById(R.id.main_drawer_layout);
    Toolbar toolbar = findViewById(R.id.main_toolbar);

    setSupportActionBar(toolbar);
    getSupportFragmentManager().addOnBackStackChangedListener(() -> {
      Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
      if (fragment != null) {
        updateTitleAndDrawer(fragment);
      }
    });

    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
      this,
      mDrawerLayout,
      toolbar,
      R.string.navigation_drawer_open,
      R.string.navigation_drawer_close);
    mDrawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = findViewById(R.id.main_navigation_view);
    navigationView.setNavigationItemSelectedListener(this);

    // get parameters from previous activity
    String userName = getIntent().getStringExtra(BaseActivity.ARG_USER_NAME);
    String email = getIntent().getStringExtra(BaseActivity.ARG_EMAIL);

    // update the navigation header
    View navigationHeaderView = navigationView.inflateHeaderView(R.layout.main_navigation_header);
    TextView navigationFullName = navigationHeaderView.findViewById(R.id.navigation_text_full_name);
    navigationFullName.setText(userName);
    TextView navigationEmail = navigationHeaderView.findViewById(R.id.navigation_text_email);
    navigationEmail.setText(email);

    // make sure this user object has the preferences
    mUserPreference = new UserPreference();
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    if (sharedPreferences.contains(UserPreferencesFragment.KEY_TEAM_PREFERENCE)) {
      String preference = sharedPreferences.getString(UserPreferencesFragment.KEY_TEAM_PREFERENCE, getString(R.string.none));
      if (preference.equals(getString(R.string.none))) {
        mUserPreference.TeamFullName = "";
        mUserPreference.TeamShortName = "";
      } else {
        mUserPreference.TeamFullName = preference.substring(0, preference.indexOf(','));
        mUserPreference.TeamShortName = preference.substring(preference.indexOf(',') + 1, preference.length());
      }
    }

    if (sharedPreferences.contains(UserPreferencesFragment.KEY_SEASON_PREFERENCE)) {
      String preference = sharedPreferences.getString(UserPreferencesFragment.KEY_SEASON_PREFERENCE, getString(R.string.none));
      if (preference.equals(getString(R.string.none))) {
        mUserPreference.Season = 0;
      } else {
        mUserPreference.Season = Integer.parseInt(preference);
      }
    }

    if (sharedPreferences.contains(UserPreferencesFragment.KEY_COMPARE_PREFERENCE)) {
      String preference = sharedPreferences.getString(UserPreferencesFragment.KEY_COMPARE_PREFERENCE, getString(R.string.none));
      if (preference.equals(getString(R.string.none))) {
        mUserPreference.Compare = 0;
      } else {
        mUserPreference.Compare = Integer.parseInt(preference);
      }
    } else {
      mUserPreference.Compare = 0;
    }

    replaceFragment(MatchListFragment.newInstance(mUserPreference));
  }

  @Override
  public void onBackPressed() {

    LogUtils.debug(TAG, "++onBackPressed()");
    if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
      mDrawerLayout.closeDrawer(GravityCompat.START);
    } else {
      if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
        finish();
      } else {
        super.onBackPressed();
      }
    }
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {

    LogUtils.debug(TAG, "++onNavigationItemSelected(%s)", item.getTitle());
    switch (item.getItemId()) {
      case R.id.navigation_menu_home:
        replaceFragment(MatchListFragment.newInstance(mUserPreference));
        break;
      case R.id.navigation_menu_preferences:
        replaceFragment(new UserPreferencesFragment());
        break;
      case R.id.navigation_menu_logout:
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
        break;
    }

    mDrawerLayout.closeDrawer(GravityCompat.START);
    return true;
  }

  @Override
  public void onPopulated(int size) {

    LogUtils.debug(TAG, "++onPopulated(%1d)", size);
    hideProgressDialog();
    if (mUserPreference != null && !mUserPreference.TeamShortName.isEmpty()) {
      setTitle(getResources().getQuantityString(R.plurals.subtitle, size, mUserPreference.TeamShortName, size));
    } else {
      setTitle("Match Summaries");
      Snackbar.make(findViewById(R.id.main_drawer_layout), getString(R.string.no_matches), Snackbar.LENGTH_LONG).show();
    }
  }

  @Override
  public void onPreferenceChanged() {

    LogUtils.debug(TAG, "++onPreferenceChanged()");
    if (mUserPreference != null) {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
      if (sharedPreferences.contains(UserPreferencesFragment.KEY_TEAM_PREFERENCE)) {
        String preference = sharedPreferences.getString(UserPreferencesFragment.KEY_TEAM_PREFERENCE, getString(R.string.none));
        if (preference.equals(getString(R.string.none))) {
          mUserPreference.TeamFullName = "";
          mUserPreference.TeamShortName = "";
        } else {
          mUserPreference.TeamFullName = preference.substring(0, preference.indexOf(','));
          mUserPreference.TeamShortName = preference.substring(preference.indexOf(',') + 1, preference.length());
        }
      }

      if (sharedPreferences.contains(UserPreferencesFragment.KEY_SEASON_PREFERENCE)) {
        String preference = sharedPreferences.getString(UserPreferencesFragment.KEY_SEASON_PREFERENCE, getString(R.string.none));
        if (preference.equals(getString(R.string.none))) {
          mUserPreference.Season = 0;
        } else {
          mUserPreference.Season = Integer.parseInt(preference);
        }
      }

      if (sharedPreferences.contains(UserPreferencesFragment.KEY_COMPARE_PREFERENCE)) {
        String preference = sharedPreferences.getString(UserPreferencesFragment.KEY_COMPARE_PREFERENCE, getString(R.string.none));
        if (preference.equals(getString(R.string.none))) {
          mUserPreference.Compare = 0;
        } else {
          mUserPreference.Compare = Integer.parseInt(preference);
        }
      } else {
        mUserPreference.Compare = 0;
      }
    }
  }

  @Override
  public void onSelected(MatchSummary matchSummary) {

    LogUtils.debug(TAG, "++onSelected(MatchSummary)");
    replaceFragment(TrendFragment.newInstance(mUserPreference, matchSummary));
  }

  private void replaceFragment(Fragment fragment){

    LogUtils.debug(TAG, "++replaceFragment()");
    LogUtils.debug(TAG, "%s (%s): %d (%d)", mUserPreference.TeamFullName, mUserPreference.TeamShortName, mUserPreference.Season, mUserPreference.Compare);
    String backStateName = fragment.getClass().getName();
    if (mUserPreference.toString().length() > 0) {
      backStateName = String.format(
        Locale.ENGLISH,
        "%s-%s",
        fragment.getClass().getName(),
        mUserPreference.toString());
    }

    FragmentManager fragmentManager = getSupportFragmentManager();
    boolean fragmentPopped = fragmentManager.popBackStackImmediate (backStateName, 0);
    if (!fragmentPopped){ //fragment not in back stack, create it.
      FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
      fragmentTransaction.replace(R.id.main_fragment_container, fragment);
      fragmentTransaction.addToBackStack(backStateName);
      fragmentTransaction.commit();
    }
  }

  private void updateTitleAndDrawer(Fragment fragment) {

    String fragmentClassName = fragment.getClass().getName();
    if (fragmentClassName.equals(MatchListFragment.class.getName())) {
      setTitle("Match Summaries");
    } else if (fragmentClassName.equals(UserPreferencesFragment.class.getName())){
      setTitle("Preferences");
    }
  }
}
