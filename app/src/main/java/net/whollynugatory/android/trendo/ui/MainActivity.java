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
package net.whollynugatory.android.trendo.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import net.whollynugatory.android.trendo.R;
import net.whollynugatory.android.trendo.common.PackagedData;
import net.whollynugatory.android.trendo.common.QueryDataAsync;
import net.whollynugatory.android.trendo.common.Trend;
import net.whollynugatory.android.trendo.db.TrendoRepository;
import net.whollynugatory.android.trendo.db.entity.TeamEntity;
import net.whollynugatory.android.trendo.db.entity.User;
import net.whollynugatory.android.trendo.ui.fragments.CardSummaryFragment;
import net.whollynugatory.android.trendo.ui.fragments.LineChartFragment;
import net.whollynugatory.android.trendo.ui.fragments.MatchListFragment;
import net.whollynugatory.android.trendo.ui.fragments.UserPreferencesFragment;

public class MainActivity extends BaseActivity implements
  CardSummaryFragment.OnCardSummaryListener,
  LineChartFragment.OnLineChartListener,
  MatchListFragment.OnMatchListListener,
  UserPreferencesFragment.OnPreferencesListener {

  private static final String TAG = BASE_TAG + "MainActivity";

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

    mProgressBar = findViewById(R.id.main_progress);

    mProgressBar.setIndeterminate(true);
    Toolbar toolbar = findViewById(R.id.main_toolbar);
    setSupportActionBar(toolbar);

    getSupportFragmentManager().addOnBackStackChangedListener(() -> {
      Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
      if (fragment != null) {
        updateTitleAndDrawer(fragment);
      }
    });

    queryData();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    Log.d(TAG, "++onCreateOptionsMenu(Menu)");
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.d(TAG, "++onDestroy()");
    mPackagedData = null;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {

    Log.d(TAG, "++onOptionsItemSelected(MenuItem)");
    switch (item.getItemId()) {
      case R.id.navigation_menu_home:
        replaceFragment(CardSummaryFragment.newInstance(mPackagedData.Trends));
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

    return true;
  }

  /*
      Fragment Callback Overrides
   */
  @Override
  public void onCardSummaryMatchListClicked() {

    Log.d(TAG, "++onCardSummaryItemClicked()");
    replaceFragment(MatchListFragment.newInstance(mPackagedData.MatchDetails, mUser.TeamId));
  }

  @Override
  public void onCardSummaryTrendClicked(Trend selectedTrend) {

    Log.d(TAG, "++onCardSummaryTrendClicked(Trend)");
    replaceFragment(
      LineChartFragment.newInstance(
        mPackagedData.Trends,
        mPackagedData.TrendsAhead,
        mPackagedData.TrendsBehind,
        selectedTrend));
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
      replaceFragment(MatchListFragment.newInstance(mPackagedData.MatchDetails, mUser.TeamId));
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
//    replaceFragment(TrendFragment.newInstance(mUser, new ArrayList<>(mPackagedData.Teams)));
  }

  @Override
  public void onPreferenceChanged() {

    Log.d(TAG, "++onPreferenceChanged()");
    queryData();
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


  private TeamEntity getTeam(String teamId) {

    for (TeamEntity team : mPackagedData.Teams) {
      if (team.Id.equals(teamId)) {
        return team;
      }
    }

    return new TeamEntity();
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

    if (mUser.TeamId.isEmpty() || mUser.TeamId.equals(BaseActivity.DEFAULT_ID)) {
      mProgressBar.setIndeterminate(false);
      mSnackbar = Snackbar.make(
        findViewById(R.id.main_fragment_container),
        getString(R.string.no_matches),
        Snackbar.LENGTH_LONG);
      mSnackbar.show();
      replaceFragment(UserPreferencesFragment.newInstance(new ArrayList<>(mPackagedData.Teams)));
    } else {
      new QueryDataAsync(
        this,
        TrendoRepository.getInstance(this),
        mUser.TeamId,
        mUser.Year).execute();
    }
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
        setTitle(String.format(Locale.ENGLISH, "%s - %d", getTeam(mUser.TeamId).Name, mUser.Year));
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
