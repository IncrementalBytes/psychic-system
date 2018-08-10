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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;
import net.frostedbytes.android.trendo.fragments.MatchListFragment;
import net.frostedbytes.android.trendo.fragments.TrendFragment;
import net.frostedbytes.android.trendo.fragments.UserPreferencesFragment;
import net.frostedbytes.android.trendo.models.MatchSummary;
import net.frostedbytes.android.trendo.models.Team;
import net.frostedbytes.android.trendo.models.UserPreference;
import net.frostedbytes.android.trendo.utils.LogUtils;
import net.frostedbytes.android.trendo.utils.PathUtils;
import net.frostedbytes.android.trendo.utils.SortUtils;

public class MainActivity extends BaseActivity implements
  NavigationView.OnNavigationItemSelectedListener,
  UserPreferencesFragment.OnPreferencesListener,
  MatchListFragment.OnMatchListListener {

  private static final String TAG = MainActivity.class.getSimpleName();

  private DrawerLayout mDrawerLayout;

  private ArrayList<MatchSummary> mMatchSummaries;
  private ArrayList<Team> mTeams;
  private UserPreference mUserPreference;

  private Query mMatchSummariesQuery;
  private Query mTeamsQuery;
  private ValueEventListener mMatchSummariesListener;
  private ValueEventListener mTeamsListener;

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
        mUserPreference.TeamId = "";
      } else {
        try {
          UUID temp = UUID.fromString(preference);
          mUserPreference.TeamId = preference;
        } catch (Exception ex) {
          mUserPreference.TeamId = "";
        }
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

    LogUtils.debug(TAG, "Query: %s", Team.ROOT);
    mTeamsQuery = FirebaseDatabase.getInstance().getReference().child(Team.ROOT);
    mTeamsListener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        mTeams = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          Team team = snapshot.getValue(Team.class);
          if (team != null) {
            team.Id = snapshot.getKey();
            mTeams.add(team);
          }
        }

        mTeams.sort(new SortUtils.ByTeamName());
        matchSummaryQuery();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

        LogUtils.debug(TAG, "++onCancelled(DatabaseError)");
        LogUtils.error(TAG, "%s", databaseError.getDetails());
      }
    };
    mTeamsQuery.addValueEventListener(mTeamsListener);
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

  @Override
  public void onDestroy() {
    super.onDestroy();

    if (mMatchSummariesQuery != null && mMatchSummariesListener != null) {
      mMatchSummariesQuery.removeEventListener(mMatchSummariesListener);
    }

    if (mTeamsQuery != null && mTeamsListener != null) {
      mTeamsQuery.removeEventListener(mTeamsListener);
    }

    mTeams = null;
    mMatchSummaries = null;
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {

    LogUtils.debug(TAG, "++onNavigationItemSelected(%s)", item.getTitle());
    switch (item.getItemId()) {
      case R.id.navigation_menu_home:
        replaceFragment(MatchListFragment.newInstance(mTeams, mMatchSummaries));
        break;
      case R.id.navigation_menu_preferences:
        replaceFragment(UserPreferencesFragment.newInstance(mTeams));
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
    if (mUserPreference != null && mUserPreference.Season > 0) {
      setTitle(String.format(Locale.ENGLISH, "%d", mUserPreference.Season));
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
          mUserPreference.TeamId = "";
        } else {
          try {
            UUID temp = UUID.fromString(preference);
            mUserPreference.TeamId = preference;
          } catch (Exception ex) {
            mUserPreference.TeamId = "";
          }
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
  public void onSelected() {

    LogUtils.debug(TAG, "++onSelected()");
    replaceFragment(TrendFragment.newInstance(mUserPreference));
  }

  private void matchSummaryQuery() {

    LogUtils.debug(TAG, "++matchSummaryQuery()");
    String queryPath = PathUtils.combine(MatchSummary.ROOT, mUserPreference.Season);
    LogUtils.debug(TAG, "Query: %s", queryPath);
    mMatchSummariesQuery = FirebaseDatabase.getInstance().getReference().child(queryPath).orderByChild("MatchDay");
    mMatchSummariesListener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        mMatchSummaries = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          MatchSummary matchSummary = snapshot.getValue(MatchSummary.class);
          if (matchSummary != null) {
            matchSummary.MatchId = snapshot.getKey();
            mMatchSummaries.add(matchSummary);
          }
        }

        Collections.reverse(mMatchSummaries);
        replaceFragment(MatchListFragment.newInstance(mTeams, mMatchSummaries));
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

        LogUtils.debug(TAG, "++onCancelled(DatabaseError");
        LogUtils.error(TAG, databaseError.getMessage());
      }
    };
    mMatchSummariesQuery.addValueEventListener(mMatchSummariesListener);
  }

  private void replaceFragment(Fragment fragment){

    LogUtils.debug(TAG, "++replaceFragment(Fragment)");
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
