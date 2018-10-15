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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;
import net.frostedbytes.android.trendo.fragments.MatchListFragment;
import net.frostedbytes.android.trendo.fragments.TrendFragment;
import net.frostedbytes.android.trendo.fragments.UserPreferencesFragment;
import net.frostedbytes.android.trendo.models.MatchSummary;
import net.frostedbytes.android.trendo.models.Team;
import net.frostedbytes.android.trendo.models.Trend;
import net.frostedbytes.android.trendo.models.UserPreference;
import net.frostedbytes.android.trendo.utils.LogUtils;
import net.frostedbytes.android.trendo.utils.PathUtils;
import net.frostedbytes.android.trendo.utils.SortUtils;
import net.frostedbytes.android.trendo.utils.SortUtils.ByTablePosition;

public class MainActivity extends BaseActivity implements
  NavigationView.OnNavigationItemSelectedListener,
  UserPreferencesFragment.OnPreferencesListener,
  MatchListFragment.OnMatchListListener,
  TrendFragment.OnTrendListener {

    private static final String TAG = BASE_TAG + MainActivity.class.getSimpleName();

    private DrawerLayout mDrawerLayout;
    private ProgressBar mProgressBar;

    private ArrayList<MatchSummary> mAllSummaries;
    private ArrayList<Team> mTeams;
    private ArrayList<MatchSummary> mTeamSummaries;
    private UserPreference mUserPreference;

    private Query mAggregateQuery;
    private Query mMatchSummariesQuery;
    private Query mTeamsQuery;

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
            if (preference != null && preference.equals(getString(R.string.none))) {
                mUserPreference.TeamId = "";
            } else if (preference != null){
                try {
                    UUID temp = UUID.fromString(preference);
                    mUserPreference.TeamId = preference;
                } catch (Exception ex) {
                    mUserPreference.TeamId = "";
                }
            } else {
                mUserPreference.TeamId = "";
            }
        }

        mTeamsQuery = FirebaseDatabase.getInstance().getReference().child(Team.ROOT);
        mTeamsQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                LogUtils.debug(TAG, "Data changed under %s", Team.ROOT);
                mTeams = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Team team = snapshot.getValue(Team.class);
                    if (team != null) {
                        team.Id = snapshot.getKey();
                        mTeams.add(team);
                    }
                }

                // BUGBUG: what if teams is still empty?
                mTeams.sort(new SortUtils.ByTeamName());
                LogUtils.debug(TAG, "Size of team collection: %d", mTeams.size());
                if (mUserPreference.TeamId.isEmpty() || mUserPreference.TeamId.equals(BaseActivity.DEFAULT_ID)) {
                    mProgressBar.setIndeterminate(false);
                    replaceFragment(UserPreferencesFragment.newInstance(mTeams));
                } else {
                    aggregateDataQuery();
                    matchSummaryQuery();
                }
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
        mAggregateQuery = null;
        mMatchSummariesQuery = null;
        mTeamsQuery = null;
        mAllSummaries = null;
        mTeamSummaries = null;
        mTeams = null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        LogUtils.debug(TAG, "++onNavigationItemSelected(%s)", item.getTitle());
        switch (item.getItemId()) {
            case R.id.navigation_menu_home:
                replaceFragment(MatchListFragment.newInstance(mTeamSummaries));
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
        mProgressBar.setIndeterminate(false);
        if (mUserPreference != null && mUserPreference.Season > 0) {
            setTitle(String.format(Locale.ENGLISH, "%d", mUserPreference.Season));
        } else {
            setTitle(getString(R.string.title_match_summaries));
            missingPreference();
        }
    }

    @Override
    public void onPreferenceChanged() {

        LogUtils.debug(TAG, "++onPreferenceChanged()");
        if (mUserPreference != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if (sharedPreferences.contains(UserPreferencesFragment.KEY_TEAM_PREFERENCE)) {
                String preference = sharedPreferences.getString(UserPreferencesFragment.KEY_TEAM_PREFERENCE, getString(R.string.none));
                if (preference != null && preference.equals(getString(R.string.none))) {
                    mUserPreference.TeamId = "";
                } else if (preference != null){
                    try {
                        UUID temp = UUID.fromString(preference);
                        mUserPreference.TeamId = preference;
                    } catch (Exception ex) {
                        mUserPreference.TeamId = "";
                    }
                } else {
                    mUserPreference.TeamId = "";
                }
            }
        }

        if (mUserPreference == null || mUserPreference.TeamId.isEmpty() || mUserPreference.TeamId.equals(BaseActivity.DEFAULT_ID)) {
            missingPreference();
        } else {
            aggregateDataQuery();
            matchSummaryQuery();
        }
    }

    @Override
    public void onSelected() {

        LogUtils.debug(TAG, "++onSelected()");
        replaceFragment(TrendFragment.newInstance(mUserPreference, mTeams));
    }

    @Override
    public void onTrendQueryFailure() {

        LogUtils.debug(TAG, "++onTrendQueryFailure()");
        Snackbar.make(findViewById(R.id.main_fragment_container), getString(R.string.no_trends), Snackbar.LENGTH_LONG);
        replaceFragment(MatchListFragment.newInstance(mTeamSummaries));
    }

    private void aggregateDataQuery() {

        LogUtils.debug(TAG, "++aggregateDataQuery()");
        String aggregatePath = PathUtils.combine(Trend.AGGREGATE_ROOT, mUserPreference.Season);
        LogUtils.debug(TAG, "Query: %s", aggregatePath);
        mAggregateQuery = FirebaseDatabase.getInstance().getReference().child(aggregatePath);
        mAggregateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
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
                if (!mUserPreference.TeamId.isEmpty() && !mUserPreference.TeamId.equals(BaseActivity.DEFAULT_ID)) {
                    mTeams.sort(new ByTablePosition());
                    int targetIndex = 0;
                    for (; targetIndex < mTeams.size(); targetIndex++) {
                        if (mTeams.get(targetIndex).Id.equals(mUserPreference.TeamId)) {
                            break;
                        }
                    }

                    int targetConferenceId = mTeams.get(targetIndex).ConferenceId;
                    for (int index = 0; index < targetIndex; index++) {
                        if (mTeams.get(index).ConferenceId == targetConferenceId) { // want the last conference team before target
                            mUserPreference.AheadTeamId = mTeams.get(index).Id;
                        }
                    }

                    if (mTeams.size() > targetIndex + 1) {
                        for (int index = targetIndex + 1; index < mTeams.size(); index++) {
                            if (mTeams.get(index).ConferenceId == targetConferenceId) { // want the first conference team after target
                                mUserPreference.BehindTeamId = mTeams.get(index).Id;
                                break;
                            }
                        }
                    }
                } else {
                    LogUtils.warn(TAG, "User preferences are incomplete.");
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

    private String getTeamName(String teamId) {
        for (Team team : mTeams) {
            if (team.Id.equals(teamId)) {
                return team.FullName;
            }
        }
        return getString(R.string.not_available);
    }

    private void matchSummaryQuery() {

        LogUtils.debug(TAG, "++matchSummaryQuery()");
        String queryPath = PathUtils.combine(MatchSummary.ROOT, mUserPreference.Season);
        LogUtils.debug(TAG, "Query: %s", queryPath);
        mMatchSummariesQuery = FirebaseDatabase.getInstance().getReference().child(queryPath);
        mMatchSummariesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                LogUtils.debug(TAG, "Data changed under %s", queryPath);
                mAllSummaries = new ArrayList<>();
                mTeamSummaries = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MatchSummary matchSummary = snapshot.getValue(MatchSummary.class);
                    if (matchSummary != null) {
                        matchSummary.MatchId = snapshot.getKey();
                        matchSummary.AwayFullName = getTeamName(matchSummary.AwayId);
                        matchSummary.HomeFullName = getTeamName(matchSummary.HomeId);
                        mAllSummaries.add(matchSummary);
                        if (matchSummary.HomeId.equals(mUserPreference.TeamId) || matchSummary.AwayId.equals(mUserPreference.TeamId)) {
                            mTeamSummaries.add(matchSummary);
                        }
                    }
                }

                mAllSummaries.sort((summary1, summary2) -> Integer.compare(summary2.MatchDate.compareTo(summary1.MatchDate), 0));
                LogUtils.debug(TAG, "Size of summary collection: %d", mAllSummaries.size());
                mTeamSummaries.sort((summary1, summary2) -> Integer.compare(summary2.MatchDate.compareTo(summary1.MatchDate), 0));
                LogUtils.debug(TAG, "Size of team summary collection: %d", mTeamSummaries.size());
                replaceFragment(MatchListFragment.newInstance(mTeamSummaries));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                LogUtils.debug(TAG, "++matchSummaryQuery::onCancelled(DatabaseError");
                LogUtils.error(TAG, databaseError.getMessage());
            }
        });
    }

    private void missingPreference() {

        LogUtils.debug(TAG, "++missingPreference()");
        Snackbar snackbar = Snackbar.make(
            findViewById(R.id.main_fragment_container),
            getString(R.string.no_matches),
            Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getString(R.string.preferences), v -> {
            snackbar.dismiss();
            replaceFragment(UserPreferencesFragment.newInstance(mTeams));
        });
        snackbar.show();
    }

    private void replaceFragment(Fragment fragment) {

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
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment_container, fragment);
            fragmentTransaction.addToBackStack(backStateName);
            fragmentTransaction.commit();
        }
    }

    private void updateTitleAndDrawer(Fragment fragment) {

        LogUtils.debug(TAG, "++updateTitleAndDrawer(Fragment)");
        String fragmentClassName = fragment.getClass().getName();
        if (fragmentClassName.equals(MatchListFragment.class.getName())) {
            setTitle(getString(R.string.title_match_summaries));
        } else if (fragmentClassName.equals(UserPreferencesFragment.class.getName())) {
            setTitle(getString(R.string.title_preferences));
        }
    }
}
