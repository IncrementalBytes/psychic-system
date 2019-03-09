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
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import net.frostedbytes.android.trendo.fragments.CommissionerFragment;
import net.frostedbytes.android.trendo.fragments.DefaultFragment;
import net.frostedbytes.android.trendo.fragments.MatchListFragment;
import net.frostedbytes.android.trendo.fragments.TrendFragment;
import net.frostedbytes.android.trendo.fragments.UserPreferencesFragment;
import net.frostedbytes.android.trendo.models.MatchSummary;
import net.frostedbytes.android.trendo.models.Team;
import net.frostedbytes.android.trendo.models.Trend;
import net.frostedbytes.android.trendo.models.User;
import net.frostedbytes.android.trendo.utils.LogUtils;
import net.frostedbytes.android.trendo.utils.PathUtils;
import net.frostedbytes.android.trendo.utils.SortUtils.ByTablePosition;

public class MainActivity extends BaseActivity implements
  NavigationView.OnNavigationItemSelectedListener,
  UserPreferencesFragment.OnPreferencesListener,
  MatchListFragment.OnMatchListListener,
  TrendFragment.OnTrendListener {

    private static final String TAG = BASE_TAG + MainActivity.class.getSimpleName();

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressBar mProgressBar;

    private ArrayList<MatchSummary> mAllSummaries;
    private ArrayList<Team> mTeams;
    private ArrayList<MatchSummary> mTeamSummaries;
    private User mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.debug(TAG, "++onCreate(Bundle)");
        setContentView(R.layout.activity_match_list);

        mDrawerLayout = findViewById(R.id.main_drawer_layout);
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

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this,
            mDrawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // get parameters from previous activity
        mUser = new User();
        mUser.Id = getIntent().getStringExtra(BaseActivity.ARG_USER_ID);
        mUser.Email = getIntent().getStringExtra(BaseActivity.ARG_EMAIL);
        mUser.FullName = getIntent().getStringExtra(BaseActivity.ARG_USER_NAME);

        // update the navigation header
        mNavigationView = findViewById(R.id.main_navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        View navigationHeaderView = mNavigationView.inflateHeaderView(R.layout.main_navigation_header);
        TextView navigationFullName = navigationHeaderView.findViewById(R.id.navigation_text_full_name);
        navigationFullName.setText(mUser.FullName);
        TextView navigationEmail = navigationHeaderView.findViewById(R.id.navigation_text_email);
        navigationEmail.setText(mUser.Email);
        TextView navigationVersion = navigationHeaderView.findViewById(R.id.navigation_text_version);
        navigationVersion.setText(BuildConfig.VERSION_NAME);

        // grab any user options from firebase
        String queryPath = PathUtils.combine(User.ROOT, mUser.Id);
        FirebaseDatabase.getInstance().getReference().child(queryPath).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    mUser.TeamId = user.TeamId;
                    mUser.Season = user.Season;
                    if (user.IsCommissioner) {
                        MenuItem commissionerMenu = mNavigationView.getMenu().findItem(R.id.navigation_menu_commissioner);
                        if (commissionerMenu != null) {
                            commissionerMenu.setVisible(true);
                        }
                    }
                } else {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String preference = preferences.getString(UserPreferencesFragment.KEY_TEAM_PREFERENCE, getString(R.string.none));
                    if (preference != null && !preference.equals(getString(R.string.none))) {
                        mUser.TeamId = preference;
                    }

                    preference = preferences.getString(UserPreferencesFragment.KEY_SEASON_PREFERENCE, getString(R.string.none));
                    if (preference != null) {
                        try {
                            mUser.Season = Integer.parseInt(preference);
                        } catch (Exception ex) {
                            mUser.Season = 0;
                        }
                    }

                    if ((!mUser.TeamId.isEmpty() && !mUser.TeamId.equals(BaseActivity.DEFAULT_ID)) || mUser.Season > 0) {
                        String queryPath = PathUtils.combine(User.ROOT, mUser.Id);
                        FirebaseDatabase.getInstance().getReference().child(queryPath).setValue(mUser);
                    }
                }

                getTeamData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                LogUtils.debug(TAG, "++onCancelled(DatabaseError)");
                LogUtils.error(TAG, "%s", databaseError.getDetails());
            }
        });
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

        LogUtils.debug(TAG, "++onDestroy()");
        mAllSummaries = null;
        mTeamSummaries = null;
        mTeams = null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        LogUtils.debug(TAG, "++onNavigationItemSelected(%s)", item.getTitle());
        switch (item.getItemId()) {
            case R.id.navigation_menu_commissioner:
                replaceFragment(CommissionerFragment.newInstance());
                break;
            case R.id.navigation_menu_home:
                replaceFragment(MatchListFragment.newInstance(mTeamSummaries));
                break;
            case R.id.navigation_menu_preferences:
                mProgressBar.setIndeterminate(false);
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
        mProgressBar.setIndeterminate(false);
        if (mUser != null && mUser.Season > 0) {
            setTitle(String.format(Locale.ENGLISH, "%s - %d", getTeam(mUser.TeamId).ShortName, mUser.Season));
        } else {
            setTitle(getString(R.string.title_match_summaries));
            missingPreference();
        }
    }

    @Override
    public void onPreferenceChanged() {

        LogUtils.debug(TAG, "++onPreferenceChanged()");
        boolean userDataChanged = false;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPreferences.contains(UserPreferencesFragment.KEY_TEAM_PREFERENCE)) {
            String preference = sharedPreferences.getString(UserPreferencesFragment.KEY_TEAM_PREFERENCE, getString(R.string.none));
            String previousTeam = mUser.TeamId;
            if (preference != null) {
                if (preference.equals(getString(R.string.none))) {
                    mUser.TeamId = "";
                } else {
                    try {
                        UUID temp = UUID.fromString(preference);
                        mUser.TeamId = preference;
                    } catch (Exception ex) {
                        mUser.TeamId = "";
                    }
                }
            } else {
                mUser.TeamId = "";
            }

            if (!mUser.TeamId.equals(previousTeam)) {
                userDataChanged = true;
            }
        }

        if (sharedPreferences.contains(UserPreferencesFragment.KEY_SEASON_PREFERENCE)) {
            String preference = sharedPreferences.getString(UserPreferencesFragment.KEY_SEASON_PREFERENCE, getString(R.string.none));
            int previousSeason = mUser.Season;
            if (preference != null) {
                if (preference.equals(getString(R.string.none))) {
                    mUser.Season = 0;
                } else {
                    try {
                        mUser.Season = Integer.parseInt(preference);
                    } catch (Exception ex) {
                        mUser.Season = 0;
                    }
                }
            } else {
                mUser.Season = 0;
            }

            if (mUser.Season != previousSeason) {
                userDataChanged = true;
            }
        }

        if (userDataChanged) {
            String queryPath = PathUtils.combine(User.ROOT, mUser.Id);
            FirebaseDatabase.getInstance().getReference().child(queryPath).setValue(mUser);
        }

        if (mTeams.size() > 0) {
            if ((mUser.TeamId.isEmpty() || mUser.TeamId.equals(BaseActivity.DEFAULT_ID)) || mUser.Season == 0) {
                missingPreference();
            } else {
                getAggregateData();
                getMatchSummaryData();
            }
        } else {
            replaceFragment(DefaultFragment.newInstance(getString(R.string.error_no_data)));
        }
    }

    @Override
    public void onSelected() {

        LogUtils.debug(TAG, "++onSelected()");
        replaceFragment(TrendFragment.newInstance(mUser, mTeams));
    }

    @Override
    public void onTrendQueryFailure() {

        LogUtils.debug(TAG, "++onTrendQueryFailure()");
        Snackbar.make(findViewById(R.id.main_fragment_container), getString(R.string.no_trends), Snackbar.LENGTH_LONG);
        replaceFragment(MatchListFragment.newInstance(mTeamSummaries));
    }

    private void getAggregateData() {

        LogUtils.debug(TAG, "++getAggregateData()");
        mProgressBar.setIndeterminate(true);
        String aggregatePath = PathUtils.combine(Trend.AGGREGATE_ROOT, mUser.Season);
        LogUtils.debug(TAG, "Query: %s", aggregatePath);
        FirebaseDatabase.getInstance().getReference().child(aggregatePath).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                LogUtils.debug(TAG, "Data changed under %s", aggregatePath);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String teamId = snapshot.getKey();
                    long tablePosition = (long) snapshot.getValue();
                    for (Team team : mTeams) {
                        if (team.Id.equals(teamId)) {
                            team.TablePosition = tablePosition;
                            break;
                        }
                    }
                }

                // figure out which teams are ahead and behind the user's preferred team (based on conference)
                if (!mUser.TeamId.isEmpty() && !mUser.TeamId.equals(BaseActivity.DEFAULT_ID)) {
                    mTeams.sort(new ByTablePosition());
                    int targetIndex = 0;
                    for (; targetIndex < mTeams.size(); targetIndex++) {
                        if (mTeams.get(targetIndex).Id.equals(mUser.TeamId)) {
                            break;
                        }
                    }

                    int targetConferenceId = mTeams.get(targetIndex).ConferenceId;
                    for (int index = 0; index < targetIndex; index++) {
                        if (mTeams.get(index).ConferenceId == targetConferenceId) { // want the last conference team before target
                            mUser.AheadTeamId = mTeams.get(index).Id;
                        }
                    }

                    if (mTeams.size() > targetIndex + 1) {
                        for (int index = targetIndex + 1; index < mTeams.size(); index++) {
                            if (mTeams.get(index).ConferenceId == targetConferenceId) { // want the first conference team after target
                                mUser.BehindTeamId = mTeams.get(index).Id;
                                break;
                            }
                        }
                    }
                } else {
                    LogUtils.warn(TAG, "User preferences are incomplete.");
                    mProgressBar.setIndeterminate(false);
                    replaceFragment(UserPreferencesFragment.newInstance(mTeams));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                LogUtils.debug(TAG, "++aggregateDataQuery::onCancelled(DatabaseError)");
                LogUtils.error(TAG, "%s", databaseError.getDetails());
            }
        });
    }

    private void getMatchSummaryData() {

        LogUtils.debug(TAG, "getMatchSummaryData()");
        String queryPath = PathUtils.combine(MatchSummary.ROOT, mUser.Season);
        LogUtils.debug(TAG, "Query: %s", queryPath);
        FirebaseDatabase.getInstance().getReference().child(queryPath).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                LogUtils.debug(TAG, "Data changed under %s", queryPath);
                mAllSummaries = new ArrayList<>();
                mTeamSummaries = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MatchSummary matchSummary = snapshot.getValue(MatchSummary.class);
                    if (matchSummary != null) {
                        matchSummary.MatchId = snapshot.getKey();
                        matchSummary.AwayFullName = getTeam(matchSummary.AwayId).FullName;
                        matchSummary.HomeFullName = getTeam(matchSummary.HomeId).FullName;
                        mAllSummaries.add(matchSummary);
                        if (matchSummary.HomeId.equals(mUser.TeamId) || matchSummary.AwayId.equals(mUser.TeamId)) {
                            mTeamSummaries.add(matchSummary);
                        }
                    }
                }

                mAllSummaries.sort((summary1, summary2) -> Integer.compare(summary2.MatchDate.compareTo(summary1.MatchDate), 0));
                LogUtils.debug(TAG, "Size of summary collection: %d", mAllSummaries.size());
                mTeamSummaries.sort((summary1, summary2) -> Integer.compare(summary2.MatchDate.compareTo(summary1.MatchDate), 0));
                LogUtils.debug(TAG, "Size of team summary collection: %d", mTeamSummaries.size());
                replaceFragment(MatchListFragment.newInstance(mTeamSummaries));
                mProgressBar.setIndeterminate(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                LogUtils.debug(TAG, "++matchSummaryQuery::onCancelled(DatabaseError");
                LogUtils.error(TAG, databaseError.getMessage());
            }
        });
    }

    private void getTeamData() {

        LogUtils.debug(TAG, "++getTeamData()");
        FirebaseDatabase.getInstance().getReference().child(Team.ROOT).addListenerForSingleValueEvent(new ValueEventListener() {

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

                if (mTeams.size() == 0) {
                    replaceFragment(DefaultFragment.newInstance(getString(R.string.error_no_data)));
                } else {
                    if (mUser.TeamId.isEmpty() || mUser.TeamId.equals(BaseActivity.DEFAULT_ID)) {
                        missingPreference();
                    } else {
                        getAggregateData();
                        getMatchSummaryData();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                LogUtils.debug(TAG, "++onCancelled(DatabaseError)");
                LogUtils.error(TAG, "%s", databaseError.getDetails());
            }
        });
    }

    private Team getTeam(String teamId) {

        for (Team team : mTeams) {
            if (team.Id.equals(teamId)) {
                return team;
            }
        }

        return new Team();
    }

    private void missingPreference() {

        LogUtils.debug(TAG, "++missingPreference()");
        mProgressBar.setIndeterminate(false);
        Snackbar snackbar = Snackbar.make(
            findViewById(R.id.main_fragment_container),
            getString(R.string.no_matches),
            Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getString(R.string.preferences), v -> {
            snackbar.dismiss();
            mProgressBar.setIndeterminate(false);
            replaceFragment(UserPreferencesFragment.newInstance(mTeams));
        });
        snackbar.show();
    }

    private void replaceFragment(Fragment fragment) {

        LogUtils.debug(TAG, "++replaceFragment()");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void updateTitleAndDrawer(Fragment fragment) {

        LogUtils.debug(TAG, "++updateTitleAndDrawer(Fragment)");
        String fragmentClassName = fragment.getClass().getName();
        if (fragmentClassName.equals(MatchListFragment.class.getName())) {
            setTitle(getString(R.string.title_match_summaries));
        } else if (fragmentClassName.equals(UserPreferencesFragment.class.getName())) {
            setTitle(getString(R.string.title_preferences));
        } else if (fragmentClassName.equals(CommissionerFragment.class.getName())) {
            setTitle(getString(R.string.commissioner));
        } else {
            setTitle(getString(R.string.app_name));
        }
    }
}
