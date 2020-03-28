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

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import net.whollynugatory.android.trendo.R;
import net.whollynugatory.android.trendo.common.ConferenceTableAsync;
import net.whollynugatory.android.trendo.common.MatchSummaryTableAsync;
import net.whollynugatory.android.trendo.common.TeamTableAsync;
import net.whollynugatory.android.trendo.common.TrendTableAsync;
import net.whollynugatory.android.trendo.db.TrendoDatabase;
import net.whollynugatory.android.trendo.db.entity.MatchSummaryEntity;
import net.whollynugatory.android.trendo.db.entity.TeamEntity;
import net.whollynugatory.android.trendo.db.repository.ConferenceRepository;
import net.whollynugatory.android.trendo.db.repository.MatchDateDimRepository;
import net.whollynugatory.android.trendo.db.repository.MatchSummaryRepository;
import net.whollynugatory.android.trendo.db.repository.TeamRepository;
import net.whollynugatory.android.trendo.db.repository.TrendRepository;
import net.whollynugatory.android.trendo.ui.fragments.DataFragment;
import net.whollynugatory.android.trendo.ui.fragments.UserPreferencesFragment;
import net.whollynugatory.android.trendo.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

public class DataActivity extends BaseActivity {

  private static final String TAG = BASE_TAG + "DataActivity";

  private DataFragment mDataFragment;

  /*
  AppCompatActivity Override(s)
 */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_data);

    Toolbar toolbar = findViewById(R.id.data_toolbar);
    setSupportActionBar(toolbar);
    getSupportFragmentManager().addOnBackStackChangedListener(() -> {
      Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.data_fragment_container);
      if (fragment != null) {
        setTitle(getString(R.string.please_wait));
      }
    });

    mDataFragment = DataFragment.newInstance();
    replaceFragment(mDataFragment);
    mDataFragment.setConferencesStatusImage(getResources().getDrawable(R.drawable.ic_progress_dark, getTheme()));
    mDataFragment.setConferencesStatusText(getString(R.string.in_progress));
    new ConferenceTableAsync(this, ConferenceRepository.getInstance(TrendoDatabase.getInstance(this).conferenceDao())).execute();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    Log.d(TAG, "++onCreateOptionsMenu(Menu)");
    getMenuInflater().inflate(R.menu.data, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {

    Log.d(TAG, "++onOptionsItemSelected(MenuItem)");
    switch (item.getItemId()) {
      case R.id.navigation_menu_save:
        replaceFragment(mDataFragment);
        mDataFragment.setConferencesStatusImage(getResources().getDrawable(R.drawable.ic_success_dark, getTheme()));
        mDataFragment.setConferencesStatusText(getString(R.string.complete));
        mDataFragment.setTeamStatusImage(getResources().getDrawable(R.drawable.ic_success_dark, getTheme()));
        mDataFragment.setTeamStatusText(getString(R.string.complete));
        mDataFragment.setMatchSummariesStatusImage(getResources().getDrawable(R.drawable.ic_progress_dark, getTheme()));
        mDataFragment.setMatchSummariesStatusText(getString(R.string.in_progress));
        processMatchSummaries();
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
    Public Method(s)
   */
  public void conferenceTableSynced() {

    Log.d(TAG, "++conferenceTableSynced()");
    mDataFragment.setConferencesStatusImage(getResources().getDrawable(R.drawable.ic_success_dark, getTheme()));
    mDataFragment.setConferencesStatusText(getString(R.string.complete));
    mDataFragment.setTeamStatusImage(getResources().getDrawable(R.drawable.ic_progress_dark, getTheme()));
    mDataFragment.setTeamStatusText(getString(R.string.pending));

    new TeamTableAsync(this, TeamRepository.getInstance(TrendoDatabase.getInstance(this).teamDao())).execute();
  }

  public void matchSummaryTableSynced(List<MatchSummaryEntity> matchSummaries) {

    Log.d(TAG, "++matchSummaryTableSynced(List<MatchSummaryEntity>)");
    mDataFragment.setMatchSummariesStatusImage(getResources().getDrawable(R.drawable.ic_success_dark, getTheme()));
    mDataFragment.setMatchSummariesStatusText(getString(R.string.complete));
    mDataFragment.setTrendsStatusImage(getResources().getDrawable(R.drawable.ic_progress_dark, getTheme()));
    mDataFragment.setTrendsStatusText(getString(R.string.pending));

    new TrendTableAsync(
      this,
      TrendRepository.getInstance(TrendoDatabase.getInstance(this).trendDao()),
      matchSummaries,
      PreferenceUtils.getTeam(this),
      PreferenceUtils.getSeason(this)).execute();
  }

  public void teamTableSynced(List<TeamEntity> teamList) {

    Log.d(TAG, "++teamTableSynced(List<TeamEntity>)");
    mDataFragment.setTeamStatusImage(getResources().getDrawable(R.drawable.ic_success_dark, getTheme()));
    mDataFragment.setTeamStatusText(getString(R.string.complete));
    if (PreferenceUtils.getTeam(this).equals(BaseActivity.DEFAULT_ID) ||
      PreferenceUtils.getTeam(this).length() != BaseActivity.DEFAULT_ID.length()) {
      replaceFragment(UserPreferencesFragment.newInstance(new ArrayList<>(teamList)));
    } else {
      mDataFragment.setMatchSummariesStatusImage(getResources().getDrawable(R.drawable.ic_progress_dark, getTheme()));
      mDataFragment.setMatchSummariesStatusText(getString(R.string.in_progress));
      processMatchSummaries();
    }
  }

  public void trendTableSynced() {

    Log.d(TAG, "++trendTableSynced()");
    mDataFragment.setTrendsStatusImage(getResources().getDrawable(R.drawable.ic_success_dark, getTheme()));
    mDataFragment.setTrendsStatusText(getString(R.string.complete));
    Intent intent = new Intent(DataActivity.this, MainActivity.class);
    startActivity(intent);
    finish();
  }

  private void processMatchSummaries() {

    Log.d(TAG, "++processMatchSummaries()");
    mDataFragment.setTeamStatusImage(getResources().getDrawable(R.drawable.ic_success_dark, getTheme()));
    mDataFragment.setTeamStatusText(getString(R.string.complete));
    mDataFragment.setMatchSummariesStatusImage(getResources().getDrawable(R.drawable.ic_progress_dark, getTheme()));
    mDataFragment.setMatchSummariesStatusText(getString(R.string.pending));

    new MatchSummaryTableAsync(
      this,
      MatchSummaryRepository.getInstance(TrendoDatabase.getInstance(this).matchSummaryDao()),
      MatchDateDimRepository.getInstance(TrendoDatabase.getInstance(this).matchDateDimDao()),
      PreferenceUtils.getSeason(this)).execute();
  }

  private void replaceFragment(Fragment fragment) {

    Log.d(TAG, "++replaceFragment()");
    getSupportFragmentManager()
      .beginTransaction()
      .replace(R.id.data_fragment_container, fragment)
      .addToBackStack(null)
      .commit();
  }
}
