/*
 * Copyright 2020 Ryan Ward
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
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.whollynugatory.android.trendo.R;
import net.whollynugatory.android.trendo.common.PackagedData;
import net.whollynugatory.android.trendo.common.Trend;
import net.whollynugatory.android.trendo.db.entity.TeamEntity;
import net.whollynugatory.android.trendo.db.entity.User;
import net.whollynugatory.android.trendo.ui.fragments.BrokerageFragment;
import net.whollynugatory.android.trendo.ui.fragments.CardSummaryFragment;
import net.whollynugatory.android.trendo.ui.fragments.DataFragment;
import net.whollynugatory.android.trendo.ui.fragments.LineChartFragment;
import net.whollynugatory.android.trendo.ui.fragments.MatchListFragment;
import net.whollynugatory.android.trendo.ui.fragments.UserPreferencesFragment;

public class MainActivity extends BaseActivity implements
  BrokerageFragment.OnBrokerageListener,
  CardSummaryFragment.OnCardSummaryListener,
  DataFragment.OnDataListener,
  LineChartFragment.OnLineChartListener,
  MatchListFragment.OnMatchListListener {

  private static final String TAG = BASE_TAG + "MainActivity";

  private Snackbar mSnackbar;

  private PackagedData mPackagedData;
  private int mQueryAttempts;
  private Map<String, Boolean> mSeasonalData = new HashMap<>();
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
    mQueryAttempts++;
    mUser = new User();
    mUser.Uid = getIntent().getStringExtra(BaseActivity.ARG_UID);
    String[] seasons = getResources().getStringArray(R.array.seasons);
    for (String season : seasons) {
      mSeasonalData.put(season, false);
    }

    // TODO: validate user?

    replaceFragment(DataFragment.newInstance());

    Toolbar toolbar = findViewById(R.id.main_toolbar);
    setSupportActionBar(toolbar);
    getSupportFragmentManager().addOnBackStackChangedListener(() -> {
      Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
      if (fragment != null) {
        String fragmentClassName = fragment.getClass().getName();
        if (fragmentClassName.equals(MatchListFragment.class.getName())) {
          if (mUser != null && mUser.Year > 0) {
            setTitle(String.format(Locale.ENGLISH, "%s - %d", getTeam(mUser.TeamId).Name, mUser.Year));
          } else {
            setTitle(getString(R.string.title_match_summaries));
          }
        } else if (fragmentClassName.equals(UserPreferencesFragment.class.getName())) {
          setTitle(getString(R.string.title_preferences));
        } else if (fragmentClassName.equals(LineChartFragment.class.getName())) {
          if (mUser != null && mUser.Year > 0) {
            setTitle(String.format(Locale.ENGLISH, "%s - %d", getTeam(mUser.TeamId).Name, mUser.Year));
          } else {
            setTitle(getString(R.string.title_trend_chart));
          }
        } else if (fragmentClassName.equals(CardSummaryFragment.class.getName())) {
          if (mUser != null && mUser.Year > 0) {
            setTitle(String.format(Locale.ENGLISH, "%s - %d", getTeam(mUser.TeamId).Name, mUser.Year));
          } else {
            setTitle(getString(R.string.title_summary));
          }
        } else {
          setTitle(getString(R.string.app_name));
        }
      }
    });
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
        replaceFragment(BrokerageFragment.newInstance());
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
  public void onBrokerageTeamsRetrieved(List<TeamEntity> teamEntityList) {

    Log.d(TAG, "++onBrokerageTeamsRetrieved(List<TeamEntity>)");
    replaceFragment(UserPreferencesFragment.newInstance(new ArrayList<>(teamEntityList)));
  }

  @Override
  public void onCardSummaryMatchListClicked() {

    Log.d(TAG, "++onCardSummaryItemClicked()");
    replaceFragment(MatchListFragment.newInstance());
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
  public void onConferenceDataExists() {

    Log.d(TAG, "++onConferenceDataExists()");
  }

  @Override
  public void onMatchesDataExists(String season) {

    Log.d(TAG, "++onMatchesDataExists(String)");
    if (!mSeasonalData.containsKey(season)) {
      mSeasonalData.put(season, true);
    }

    if (mSeasonalData.containsValue(false)) {
      // continue waiting
    } else {
      replaceFragment(MatchListFragment.newInstance());
    }
  }

  @Override
  public void onTeamDataExists() {

    Log.d(TAG, "++onTeamDataExists()");
    replaceFragment(MatchListFragment.newInstance());
  }

  @Override
  public void onLineChartInit(boolean isSuccessful) {

    Log.d(TAG, "++onLineChartInit(boolean)");
    if (!isSuccessful) {
      Snackbar.make(findViewById(R.id.main_fragment_container), getString(R.string.err_trend_data_load_failed), Snackbar.LENGTH_LONG);
      replaceFragment(MatchListFragment.newInstance());
    }
  }

  @Override
  public void onMatchListPopulated(int size) {

    Log.d(TAG, "++onMatchListPopulated(int)");
  }

  @Override
  public void onMatchListItemSelected() {

    Log.d(TAG, "++onMatchListItemSelected()");
    // TODO: open line chart and highlight match?
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

  private void replaceFragment(Fragment fragment) {

    Log.d(TAG, "++replaceFragment()");
    getSupportFragmentManager()
      .beginTransaction()
      .replace(R.id.main_fragment_container, fragment)
      .addToBackStack(null)
      .commit();
  }
}
