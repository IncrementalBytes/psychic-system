/*
 * Copyright 2019 Ryan Ward
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.frostedbytes.android.trendo.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import net.frostedbytes.android.trendo.BuildConfig;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.common.PackagedData;
import net.frostedbytes.android.trendo.common.QueryDataAsync;
import net.frostedbytes.android.trendo.db.TrendoRepository;
import net.frostedbytes.android.trendo.db.entity.TeamEntity;
import net.frostedbytes.android.trendo.db.entity.User;
import net.frostedbytes.android.trendo.ui.fragments.CardSummaryFragment;
import net.frostedbytes.android.trendo.ui.fragments.LineChartFragment;
import net.frostedbytes.android.trendo.ui.fragments.MatchListFragment;
import net.frostedbytes.android.trendo.ui.fragments.TrendFragment;
import net.frostedbytes.android.trendo.ui.fragments.UserPreferencesFragment;

public class MainActivity extends BaseActivity implements
  CardSummaryFragment.OnCardSummaryListener,
  LineChartFragment.OnLineChartListener,
  MatchListFragment.OnMatchListListener,
  NavigationView.OnNavigationItemSelectedListener,
  TrendFragment.OnTrendListener,
  UserPreferencesFragment.OnPreferencesListener {

  private static final String TAG = BASE_TAG + "MainActivity";

  private DrawerLayout mDrawerLayout;
  private NavigationView mNavigationView;
  private ProgressBar mProgressBar;
  private Snackbar mSnackbar;

  private PackagedData mPackagedData;
  private User mUser;

  /*
      AppCompatActivity Override(s)
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_main);

    // get parameters from previous activity
    mPackagedData = new PackagedData();
    mUser = new User();
    mUser.Id = getIntent().getStringExtra(BaseActivity.ARG_USER_ID);

    // TODO: validate user?

    // TODO: remove drawer
    mDrawerLayout = findViewById(R.id.main_drawer_layout);
    mProgressBar = findViewById(R.id.main_progress);
    mNavigationView = findViewById(R.id.main_navigation_view);

    mProgressBar.setIndeterminate(true);
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

    // update the navigation header
    mNavigationView.setNavigationItemSelectedListener(this);
    View navigationHeaderView = mNavigationView.inflateHeaderView(R.layout.main_navigation_header);
    TextView navigationVersion = navigationHeaderView.findViewById(R.id.navigation_text_version);
    navigationVersion.setText(BuildConfig.VERSION_NAME);

    queryData();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.d(TAG, "++onDestroy()");
    mPackagedData = null;
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {

    Log.d(TAG, "++onNavigationItemSelected(MenuItem)");
    switch (item.getItemId()) {
      case R.id.navigation_menu_home:
//        List<Trend> trends = mTrendoViewModel.getAllTrendsByTeamAndYear(mUser.TeamId, mUser.Year);
//          if (trends != null && trends.size() > 0) {
//            replaceFragment(CardSummaryFragment.newInstance(new ArrayList<>(trends)));
//          }

        break;
      case R.id.navigation_menu_preferences:
        mProgressBar.setVisibility(View.INVISIBLE);
        replaceFragment(UserPreferencesFragment.newInstance(new ArrayList<>(mPackagedData.Teams)));
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
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    Log.d(TAG, "++onActivityResult(int, int, Intent)");
    if (requestCode == BaseActivity.RC_COMMISSIONER) {
      if (resultCode == RESULT_OK) {
        // HOORAY! Now what?
      } else if (resultCode == RESULT_CANCELED) {
        Snackbar.make(findViewById(R.id.main_fragment_container), R.string.err_commissioner_activity, Snackbar.LENGTH_LONG);
//        List<Trend> trends = mTrendoViewModel.getAllTrendsByTeamAndYear(mUser.TeamId, mUser.Year);
//        if (trends != null && trends.size() > 0) {
//          replaceFragment(CardSummaryFragment.newInstance(new ArrayList<>(trends)));
//        }
      }
    }
  }

  /*
      Fragment Callback Overrides
   */
  @Override
  public void onCardSummaryItemClicked() {

    Log.d(TAG, "++onCardSummaryItemClicked()");
  }

  @Override
  public void onCardSummaryLoaded() {

    Log.d(TAG, "++onCardSummaryLoaded()");
    setTitle(String.format(Locale.US, "%s - %d", getTeam(mUser.TeamId).Name, mUser.Year));
  }

  @Override
  public void onLineChartInit(boolean isSuccessful) {

    Log.d(TAG, "++onLineChartInit(boolean)");
    mProgressBar.setIndeterminate(false);
    if (!isSuccessful) {
      Snackbar.make(findViewById(R.id.main_fragment_container), getString(R.string.err_trend_data_load_failed), Snackbar.LENGTH_LONG);
      replaceFragment(MatchListFragment.newInstance(mPackagedData.MatchSummaries, mUser.TeamId));
    }
  }

  @Override
  public void onMatchListPopulated(int size) {

    Log.d(TAG, "++onMatchListPopulated(int)");
    mProgressBar.setIndeterminate(false);
    if (size == 0) {
      // TODO: match list empty
    }
  }

  @Override
  public void onMatchListItemSelected() {

    Log.d(TAG, "++onMatchListItemSelected()");
    replaceFragment(TrendFragment.newInstance(mUser, new ArrayList<>(mPackagedData.Teams)));
  }

  @Override
  public void onPreferenceChanged() {

    Log.d(TAG, "++onPreferenceChanged()");
    int previousYear = mUser.Year;
    String previousTeam = mUser.TeamId;
    getUserPreferences();
    if (previousYear != mUser.Year || !previousTeam.equals(mUser.TeamId)) {
//            Log.d(
//                TAG,
//                "User Preferences Changed | Season Was: %d Now: %d | Team Was: %s Now: %s",
//                previousSeason,
//                mUser.Season,
//                getTeam(previousTeam).FullName,
//                getTeam(mUser.TeamId).FullName);
      if (previousYear != mUser.Year || mPackagedData.MatchSummaries.isEmpty()) {
        getAggregateData();
        getMatchSummaryData();
      } else {
//        mTeamSummaries = new ArrayList<>();
//        for (MatchSummaryEntity matchSummary : mAllSummaries) {
//          if (matchSummary.AwayId.equals(mUser.TeamId) || matchSummary.HomeId.equals(mUser.TeamId)) {
//            mTeamSummaries.add(matchSummary);
//          }
//        }

//                mTeamSummaries.sort((summary1, summary2) -> Integer.compare(summary2.MatchDate.compareTo(summary1.MatchDate), 0));

        // update this new team's immediate opponents
//        getNearestOpponents(false);
//        replaceFragment(MatchListFragment.newInstance(mTeamSummaries, mUser.TeamId));
      }
    }
  }

  @Override
  public void onShowByConference(boolean showByConference) {

    Log.d(TAG, "++onShowByConference(boolean)");
    mProgressBar.setIndeterminate(true);
    getNearestOpponents(showByConference);
    replaceFragment(TrendFragment.newInstance(mUser, new ArrayList<>(mPackagedData.Teams), showByConference));
  }

  @Override
  public void onTrendInit(boolean isSuccessful) {

    Log.d(TAG, "++onTrendFail(boolean)");
    mProgressBar.setIndeterminate(false);
    if (!isSuccessful) {
      Snackbar.make(findViewById(R.id.main_fragment_container), getString(R.string.no_trends), Snackbar.LENGTH_LONG);
      replaceFragment(MatchListFragment.newInstance(mPackagedData.MatchSummaries, mUser.TeamId));
    }
  }

  /*
    Public methods
   */
  public void dataQueryComplete(PackagedData packagedData) {

    Log.d(TAG, "++dataQueryComplete(PackageData)");
    mPackagedData = packagedData;
    if (mUser.TeamId.equals(BaseActivity.DEFAULT_ID)) {
      mProgressBar.setIndeterminate(false);
      mSnackbar = Snackbar.make(
        findViewById(R.id.main_fragment_container),
        getString(R.string.no_matches),
        Snackbar.LENGTH_LONG);
      mSnackbar.show();
      replaceFragment(UserPreferencesFragment.newInstance(new ArrayList<>(mPackagedData.Teams)));
    } else if (packagedData.Trends.size() == 0) {
      // TODO: handle no trends for team/year
      Log.w(TAG, "No trends data found for " + mUser.TeamId);
    } else {
      replaceFragment(CardSummaryFragment.newInstance(mPackagedData.Trends));
    }
  }

  /*
      Private Methods
   */
  // TODO: is this still necessary?
  private void getAggregateData() {

    Log.d(TAG, "++getAggregateData()");
//        mProgressBar.setVisibility(View.VISIBLE);
//        String aggregatePath = PathUtils.combine(Trend.AGGREGATE_ROOT, mUser.Season);
//        Log.d(TAG, "Query: " + aggregatePath);
//        FirebaseDatabase.getInstance().getReference().child(aggregatePath).addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                Log.d(TAG, "Data changed under " + aggregatePath);
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String teamId = snapshot.getKey();
//                    long tablePosition = (long) snapshot.getValue();
//                    for (Team team : mTeams) {
//                        if (team.Id.equals(teamId)) {
//                            team.TablePosition = tablePosition;
//                            break;
//                        }
//                    }
//                }
//
//                // figure out which teams are ahead and behind the user's preferred team (based on conference)
//                if (!mUser.TeamId.isEmpty() && !mUser.TeamId.equals(BaseActivity.DEFAULT_ID)) {
//                    getNearestOpponents(false);
//                } else {
//                    Log.w(TAG, "User preferences are incomplete.");
//                    mProgressBar.setIndeterminate(false);
//                    replaceFragment(UserPreferencesFragment.newInstance(mTeams));
//                }
//
//                Log.d(TAG, "Finished querying aggregate data.");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                Log.d(TAG, "++aggregateDataQuery::onCancelled(DatabaseError)");
//                Log.e(TAG, databaseError.getDetails());
//            }
//        });
  }

  // TODO: is this still necessary?
  private void getMatchSummaryData() {

    Log.d(TAG, "getMatchSummaryData()");
//        String queryPath = PathUtils.combine(MatchSummary.ROOT, mUser.Season);
//        Log.d(TAG, "Query: " + queryPath);
//        FirebaseDatabase.getInstance().getReference().child(queryPath).addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                Log.d(TAG, "Data changed under " + queryPath);
//                mAllSummaries = new ArrayList<>();
//                mTeamSummaries = new ArrayList<>();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    MatchSummary matchSummary = snapshot.getValue(MatchSummary.class);
//                    if (matchSummary != null) {
//                        matchSummary.Id = snapshot.getKey();
//                        matchSummary.AwayFullName = getTeam(matchSummary.AwayId).FullName;
//                        matchSummary.HomeFullName = getTeam(matchSummary.HomeId).FullName;
//                        matchSummary.IsRemote = true;
//                        mAllSummaries.add(matchSummary);
//                        if (matchSummary.HomeId.equals(mUser.TeamId) || matchSummary.AwayId.equals(mUser.TeamId)) {
//                            mTeamSummaries.add(matchSummary);
//                        }
//                    }
//                }
//
//                mAllSummaries.sort((summary1, summary2) -> Integer.compare(summary2.MatchDate.compareTo(summary1.MatchDate), 0));
//                Log.d(TAG, "Size of summary collection: " + mAllSummaries.size());
//                mTeamSummaries.sort((summary1, summary2) -> Integer.compare(summary2.MatchDate.compareTo(summary1.MatchDate), 0));
//                Log.d(TAG, "Size of team summary collection: " + mTeamSummaries.size());
//                mProgressBar.setIndeterminate(false);
//                replaceFragment(MatchListFragment.newInstance(mTeamSummaries, mUser.TeamId));
//                Log.d(TAG, "Finished querying match summary data.");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                Log.d(TAG, "++matchSummaryQuery::onCancelled(DatabaseError");
//                Log.e(TAG, databaseError.getMessage());
//            }
//        });
  }

  // TODO: is this still necessary?
  private void getNearestOpponents(boolean isByConference) {

    Log.d(TAG, "++getNearestOpponents(boolean)");
//        mTeams.sort(new ByTablePosition());
//        mUser.AheadTeamId = mUser.BehindTeamId = BaseActivity.DEFAULT_ID;
//        int targetIndex = 0;
//        for (; targetIndex < mTeams.size(); targetIndex++) {
//            if (mTeams.get(targetIndex).Id.equals(mUser.TeamId)) {
//                break;
//            }
//        }
//
//        // get the team that is ahead of selected team
//        int targetConferenceId = mTeams.get(targetIndex).ConferenceId;
//        for (int index = 0; index < targetIndex; index++) {
//            if (!isByConference) {
//                mUser.AheadTeamId = mTeams.get(index).Id;
//            } else if (mTeams.get(index).ConferenceId == targetConferenceId) {
//                mUser.AheadTeamId = mTeams.get(index).Id;
//            }
//        }
//
//        // get the team that is behind the selected team
//        if (mTeams.size() > targetIndex + 1) {
//            for (int index = targetIndex + 1; index < mTeams.size(); index++) {
//                if (!isByConference) {
//                    mUser.BehindTeamId = mTeams.get(index).Id;
//                    break;
//                } else if (mTeams.get(index).ConferenceId == targetConferenceId) {
//                    mUser.BehindTeamId = mTeams.get(index).Id;
//                    break;
//                }
//            }
//        }
  }

  private TeamEntity getTeam(String teamId) {

    for (TeamEntity team : mPackagedData.Teams) {
      if (team.Id.equals(teamId)) {
        return team;
      }
    }

    return new TeamEntity();
  }

  private void getUserPreferences() {

    Log.d(TAG, "++getUserPreferences()");
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    if (sharedPreferences.contains(UserPreferencesFragment.KEY_TEAM_PREFERENCE)) {
      String preference = sharedPreferences.getString(UserPreferencesFragment.KEY_TEAM_PREFERENCE, getString(R.string.none));
      if (preference != null) {
        if (preference.equals(getString(R.string.none))) {
          mUser.TeamId = BaseActivity.DEFAULT_ID;
        } else {
          try {
            //noinspection ResultOfMethodCallIgnored
            UUID.fromString(preference);
            mUser.TeamId = preference;
          } catch (Exception ex) {
            mUser.TeamId = BaseActivity.DEFAULT_ID;
          }
        }
      } else {
        mUser.TeamId = BaseActivity.DEFAULT_ID;
      }
    }

    if (sharedPreferences.contains(UserPreferencesFragment.KEY_YEAR_PREFERENCE)) {
      String preference = sharedPreferences.getString(UserPreferencesFragment.KEY_YEAR_PREFERENCE, getString(R.string.none));
      if (preference != null) {
        if (preference.equals(getString(R.string.none))) {
          mUser.Year = Calendar.getInstance().get(Calendar.YEAR);
        } else {
          try {
            mUser.Year = Integer.parseInt(preference);
          } catch (Exception ex) {
            mUser.Year = Calendar.getInstance().get(Calendar.YEAR);
          }
        }
      } else {
        mUser.Year = Calendar.getInstance().get(Calendar.YEAR);
      }
    }

    if (mUser.TeamId.isEmpty() || mUser.TeamId.equals(BaseActivity.DEFAULT_ID)) {
      mProgressBar.setIndeterminate(false);
      mSnackbar = Snackbar.make(
        findViewById(R.id.main_fragment_container),
        getString(R.string.no_matches),
        Snackbar.LENGTH_LONG);
      mSnackbar.show();
      replaceFragment(UserPreferencesFragment.newInstance(new ArrayList<>(mPackagedData.Teams)));
    } else {
      queryData();
    }
  }

  private void queryData() {

    Log.d(TAG, "++queryData()");
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    if (sharedPreferences.contains(UserPreferencesFragment.KEY_TEAM_PREFERENCE)) {
      String preference = sharedPreferences.getString(UserPreferencesFragment.KEY_TEAM_PREFERENCE, getString(R.string.none));
      if (preference != null) {
        if (preference.equals(getString(R.string.none))) {
          mUser.TeamId = BaseActivity.DEFAULT_ID;
        } else {
          try {
            //noinspection ResultOfMethodCallIgnored
            UUID.fromString(preference);
            mUser.TeamId = preference;
          } catch (Exception ex) {
            mUser.TeamId = BaseActivity.DEFAULT_ID;
          }
        }
      } else {
        mUser.TeamId = BaseActivity.DEFAULT_ID;
      }
    }

    if (sharedPreferences.contains(UserPreferencesFragment.KEY_YEAR_PREFERENCE)) {
      String preference = sharedPreferences.getString(UserPreferencesFragment.KEY_YEAR_PREFERENCE, getString(R.string.none));
      if (preference != null) {
        if (preference.equals(getString(R.string.none))) {
          mUser.Year = Calendar.getInstance().get(Calendar.YEAR);
        } else {
          try {
            mUser.Year = Integer.parseInt(preference);
          } catch (Exception ex) {
            mUser.Year = Calendar.getInstance().get(Calendar.YEAR);
          }
        }
      } else {
        mUser.Year = Calendar.getInstance().get(Calendar.YEAR);
      }
    }

    new QueryDataAsync(this, TrendoRepository.getInstance(this), mUser.TeamId, mUser.Year).execute();
  }

  private void replaceFragment(Fragment fragment) {

    Log.d(TAG, "++replaceFragment()");
    if (mSnackbar != null && mSnackbar.isShown()) {
      mSnackbar.dismiss();
    }

    updateTitleAndDrawer(fragment);
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.main_fragment_container, fragment);
    fragmentTransaction.commitAllowingStateLoss();
  }

  private void updateTitleAndDrawer(Fragment fragment) {

    Log.d(TAG, "++updateTitleAndDrawer(Fragment)");
    String fragmentClassName = fragment.getClass().getName();
    if (fragmentClassName.equals(MatchListFragment.class.getName())) {
      if (mUser != null && mUser.Year > 0) {
        setTitle(String.format(Locale.ENGLISH, "%s - %d", getTeam(mUser.TeamId).ShortName, mUser.Year));
      } else {
        setTitle(getString(R.string.title_match_summaries));
      }
    } else if (fragmentClassName.equals(UserPreferencesFragment.class.getName())) {
      setTitle(getString(R.string.title_preferences));
    } else {
      setTitle(getString(R.string.app_name));
    }
  }
}
