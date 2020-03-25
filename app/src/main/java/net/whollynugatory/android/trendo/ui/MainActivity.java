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
import java.util.List;
import java.util.Locale;

import net.whollynugatory.android.trendo.R;
import net.whollynugatory.android.trendo.common.MatchSummaryTableAsync;
import net.whollynugatory.android.trendo.common.Trend;
import net.whollynugatory.android.trendo.common.TrendTableAsync;
import net.whollynugatory.android.trendo.db.TrendoDatabase;
import net.whollynugatory.android.trendo.db.entity.TeamEntity;
import net.whollynugatory.android.trendo.db.repository.MatchDateDimRepository;
import net.whollynugatory.android.trendo.db.repository.MatchSummaryRepository;
import net.whollynugatory.android.trendo.db.repository.TrendRepository;
import net.whollynugatory.android.trendo.db.views.MatchSummaryDetails;
import net.whollynugatory.android.trendo.ui.fragments.BrokerageFragment;
import net.whollynugatory.android.trendo.ui.fragments.CardSummaryFragment;
import net.whollynugatory.android.trendo.ui.fragments.DataFragment;
import net.whollynugatory.android.trendo.ui.fragments.LineChartFragment;
import net.whollynugatory.android.trendo.ui.fragments.MatchListFragment;
import net.whollynugatory.android.trendo.ui.fragments.UserPreferencesFragment;
import net.whollynugatory.android.trendo.utils.PreferenceUtils;

public class MainActivity extends BaseActivity implements
  BrokerageFragment.OnBrokerageListener,
  CardSummaryFragment.OnCardSummaryListener,
  LineChartFragment.OnLineChartListener,
  MatchListFragment.OnMatchListListener {

  private static final String TAG = BASE_TAG + "MainActivity";

  private Snackbar mSnackbar;

  private List<MatchSummaryDetails> mMatchSummaryDetails;
  private List<TeamEntity> mTeams;

  /*
    AppCompatActivity Override(s)
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_main);

    mMatchSummaryDetails = new ArrayList<>();
    mTeams = new ArrayList<>();
    Toolbar toolbar = findViewById(R.id.main_toolbar);
    setSupportActionBar(toolbar);
    getSupportFragmentManager().addOnBackStackChangedListener(() -> {
      Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
      if (fragment != null) {
        String fragmentClassName = fragment.getClass().getName();
        if (fragmentClassName.equals(MatchListFragment.class.getName())) {
          setTitle(
            String.format(
              Locale.ENGLISH,
              "%s - %d",
              getTeam(PreferenceUtils.getTeam(this)).ShortName,
              PreferenceUtils.getSeason(this)));
        } else if (fragmentClassName.equals(UserPreferencesFragment.class.getName())) {
          setTitle(getString(R.string.title_preferences));
        } else if (fragmentClassName.equals(LineChartFragment.class.getName())) {
          setTitle(
            String.format(
              Locale.ENGLISH,
              "%s - %d",
              getTeam(PreferenceUtils.getTeam(this)).ShortName,
              PreferenceUtils.getSeason(this)));
        } else if (fragmentClassName.equals(CardSummaryFragment.class.getName())) {
          setTitle(
            String.format(
              Locale.ENGLISH,
              "%s - %d",
              getTeam(PreferenceUtils.getTeam(this)).ShortName,
              PreferenceUtils.getSeason(this)));
        } else {
          setTitle(getString(R.string.app_name));
        }
      }
    });

    String teamId = PreferenceUtils.getTeam(this);
    if (teamId == null || teamId.equals(BaseActivity.DEFAULT_ID) || teamId.length() != BaseActivity.DEFAULT_ID.length()) {
      replaceFragment(BrokerageFragment.newInstance(BrokerageFragment.BrokerageType.Teams));
    } else {
      replaceFragment(BrokerageFragment.newInstance(BrokerageFragment.BrokerageType.Matches));
    }
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
  }

  @Override
  public void onResume() {
    super.onResume();

    Log.d(TAG, "++onResume()");
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {

    Log.d(TAG, "++onOptionsItemSelected(MenuItem)");
    switch (item.getItemId()) {
      case R.id.navigation_menu_home:
        replaceFragment(BrokerageFragment.newInstance(BrokerageFragment.BrokerageType.Matches));
        break;
      case R.id.navigation_menu_preferences:
        replaceFragment(BrokerageFragment.newInstance(BrokerageFragment.BrokerageType.Teams));
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
  public void onBrokerageMatchesMissing() {

    Log.d(TAG, "++onBrokerageMatchesMissing()");
    replaceFragment(DataFragment.newInstance());
    new MatchSummaryTableAsync(
      this,
      MatchSummaryRepository.getInstance(TrendoDatabase.getInstance(this).matchSummaryDao()),
      MatchDateDimRepository.getInstance(TrendoDatabase.getInstance(this).matchDateDimDao()),
      PreferenceUtils.getSeason(this)).execute();
  }

  @Override
  public void onBrokerageMatchesRetrieved(List<MatchSummaryDetails> matchSummaryDetailsList) {

    Log.d(TAG, "++onBrokerageMatchesRetrieved(List<MatchSummaryDetails>)");
    mMatchSummaryDetails = new ArrayList<>(matchSummaryDetailsList);
    replaceFragment(CardSummaryFragment.newInstance());
  }

  @Override
  public void onBrokerageTeamsRetrieved(List<TeamEntity> teamEntityList) {

    Log.d(TAG, "++onBrokerageTeamsRetrieved(List<TeamEntity>)");
    mTeams = new ArrayList<>(teamEntityList);
    replaceFragment(UserPreferencesFragment.newInstance(new ArrayList<>(teamEntityList)));
  }

  @Override
  public void onCardSummaryLoaded() {

    Log.d(TAG, "++onCardSummaryLoaded()");
  }

  @Override
  public void onCardSummaryMatchListClicked() {

    Log.d(TAG, "++onCardSummaryItemClicked()");
    replaceFragment(MatchListFragment.newInstance());
  }


  @Override
  public void onCardSummaryTrendClicked(Trend selectedTrend) {

    Log.d(TAG, "++onCardSummaryTrendClicked(Trend)");
    // TODO:
//    replaceFragment(
//      LineChartFragment.newInstance(
//        mPackagedData.Trends,
//        mPackagedData.TrendsAhead,
//        mPackagedData.TrendsBehind,
//        selectedTrend));
  }

  @Override
  public void onCardSummaryTrendsMissing() {

    Log.d(TAG, "++onCardSummaryTrendsMissing()");
    replaceFragment(DataFragment.newInstance());
    new TrendTableAsync(
      this,
      TrendRepository.getInstance(TrendoDatabase.getInstance(this).trendDao()),
      mMatchSummaryDetails,
      PreferenceUtils.getTeam(this),
      PreferenceUtils.getSeason(this)).execute();
  }

  @Override
  public void onLineChartInit(boolean isSuccessful) {

    Log.d(TAG, "++onLineChartInit(boolean)");
    if (!isSuccessful) {
      showSnackbar(getString(R.string.err_trend_data_load_failed));
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
    Public Method(s)
   */
  public void matchSummaryTableSynced(Integer matchCount) {

    Log.d(TAG, "++matchSummaryTableSynced(Integer)");
    if (matchCount == 0) {
      showSnackbar(getString(R.string.no_matches));
    } else {
      replaceFragment(BrokerageFragment.newInstance(BrokerageFragment.BrokerageType.Matches));
    }
  }

  public void trendTableSynced() {

    Log.d(TAG, "++trendTableSynced()");
    replaceFragment(CardSummaryFragment.newInstance());
  }

  /*
    Private Methods
   */
  private TeamEntity getTeam(String teamId) {

    for (TeamEntity team : mTeams) {
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

  private void showSnackbar(String message) {

    mSnackbar = Snackbar.make(
      findViewById(R.id.main_fragment_container),
      message,
      Snackbar.LENGTH_INDEFINITE);
    mSnackbar.setAction(R.string.dismiss, v -> mSnackbar.dismiss());
    mSnackbar.show();
  }
}
